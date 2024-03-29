/**
* AutomAdapt
* Listener implementation
* @author      Francisco Luque Schempp
* @version     1.0
*/

package uma;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.sample.Loop;
import org.pcap4j.util.NifSelector;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class Listener {
    // Set the brokers (bootstrap servers)
    private static final String brokers = "IP_ADDRESS:PORT";
    // Set the topics
    private static final String delay_topic = "delay_listener_topic";
    private static final String throughput_topic = "throughput_listener_topic";
    // Set the variables
    private static final String COUNT_KEY = Loop.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 0);
    private static final String READ_TIMEOUT_KEY = Loop.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]
    private static final String SNAPLEN_KEY = Loop.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]


    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        // Set properties used to configure the producer
        Properties properties = new Properties();
        // Set the brokers (bootstrap servers)
        properties.setProperty("bootstrap.servers", brokers);
        // Set how to serialize key/value pairs
        properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.LongSerializer");
        // Kafka producer declaration
        KafkaProducer<String, Long> producer = new KafkaProducer<>(properties);
        // Set the network interface
        PcapNetworkInterface nif;
        try {
            nif = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (nif == null) {
            return;
        }

        System.out.println(nif.getName() + "(" + nif.getDescription() + ")");

        final PcapHandle handle_sender = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        // Set a filter to only listen to 802.1Q packets
        String filter = "ether proto 0x8100";
        handle_sender.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

        PacketListener listener =
                packet_sent -> {
                    int index = packet_sent.getPayload().getPayload().getPayload().toString().lastIndexOf(":" ) + 2;
                    String packet_id = packet_sent.getPayload().getPayload().getPayload().toString().substring(index,index + 11);
                    long packet_timestamp = TimestampToEpochNano(handle_sender.getTimestamp());
                    long packet_length = packet_sent.length();
                    try {
                        //Time producer - insert the packet id and timestamp
                        producer.send(new ProducerRecord<>(delay_topic,0, packet_id, packet_timestamp)).get();
                        //Data producer - insert the packet id and packet length
                        producer.send(new ProducerRecord<>(throughput_topic, 0, packet_id, packet_length)).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                };

        try {
            handle_sender.loop(COUNT, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handle_sender.close();
    }

    public static long TimestampToEpochNano(Timestamp timestamptoconvert) {
        StringBuilder aux_timestamp = new StringBuilder(timestamptoconvert.toInstant().getNano() + "");
        for (int i = aux_timestamp.length(); i<9; i++) {
            aux_timestamp.insert(0, "0");
        }
        return Long.parseLong((timestamptoconvert.toInstant().getEpochSecond()+"") + aux_timestamp);
    }


}
