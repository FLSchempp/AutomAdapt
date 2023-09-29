/**
* AutomAdapt
* KPI generator; Monitoring implementation
* @author      Francisco Luque Schempp
* @version     1.0
*/

package uma;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import static java.lang.Math.abs;

public class KPIgenerator {
    // Set the brokers (bootstrap servers)
    private static final String brokers = "IP_Address:Port";
    // Set the topics 
    private static final String topicDelay = "delay_topic";
    private static final String topicThroughputS = "throughput_talker_topic";
    private static final String topicThroughputR = "throughput_listener_topic";
    // Set the InfluxDB parameters and create the InfluxDBClient
    static char[] token = "private_token_id".toCharArray();
    private static String org = "UMA";
    private static String bucket = "schempp";
    static InfluxDBClient influxDBClient = InfluxDBClientFactory.create("http://IP_Address:Port", token, org, bucket);
    static WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
    // Set the variables used to calculate the KPIs
    static ArrayList<Long> delay_list = new ArrayList<>();
    static long old_delay = 0;
    static long current_delay = 0;
    static long sum_jitter = 0;
    static Double avg_jitter = 0.0;
    static long sum_bytes_sender = 0;
    static long sum_bytes_receiver = 0;
    static long current_timestamp = 0;

    public static void main(String[] args) throws InterruptedException {
        // Set properties used to configure the producer
        Properties properties = new Properties();
        // Set the brokers (bootstrap servers)
        properties.setProperty("bootstrap.servers", brokers);
        // Set how to serialize key/value pairs
        properties.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer","org.apache.kafka.common.serialization.LongDeserializer");
        properties.put("group.id", "kpi");

        // Create a KafkaConsumer instance and configure it with properties.
        KafkaConsumer<String, Long> delayC = new KafkaConsumer<>(properties);
        delayC.subscribe(Collections.singletonList(topicDelay));
        KafkaConsumer<String, Long> thrSC = new KafkaConsumer<>(properties);
        thrSC.subscribe(Collections.singletonList(topicThroughputS));
        KafkaConsumer<String, Long> thrRC = new KafkaConsumer<>(properties);
        thrRC.subscribe(Collections.singletonList(topicThroughputR));


        while (true) {
            // Poll for records 
            ConsumerRecords<String, Long> delayRecords = delayC.poll(Duration.ofMillis(1000));
            ConsumerRecords<String, Long> thrSRecords = thrSC.poll(Duration.ofMillis(1000));
            ConsumerRecords<String, Long> thrRRecords = thrRC.poll(Duration.ofMillis(1000));

            // If records are found, calculate the KPIs
            delayCalculation(delayRecords);
            jitterCalculation(delayRecords);
            throughputCalculation(thrSRecords, thrRRecords);
            }

        }

    // Calculate the delay and send it to InfluxDB
    private static void delayCalculation(ConsumerRecords<String, Long> dRecords) {
        long time = 0;
        for (ConsumerRecord<String, Long> record : dRecords) {
            time += record.value();
            current_timestamp = record.timestamp();
        }
        if (!dRecords.isEmpty()) {
            toInfluxDB(Point.measurement("kpis")
                            .addTag("test_id", "1")
                            .addField("delay", (time / dRecords.count()))
                            .time(current_timestamp, WritePrecision.MS));
        }
    }

    // Calculate the jitter and send it to InfluxDB
    private static void jitterCalculation(ConsumerRecords<String, Long> jRecords) {
        for (ConsumerRecord<String, Long> record : jRecords) {
            if (old_delay == 0)
                old_delay = record.value();
            else {
                current_delay = record.value();
                delay_list.add(abs(current_delay - old_delay));
                old_delay = current_delay;
            }
            if (delay_list.size() >= 10) {
                for (Long delay:delay_list) {
                    sum_jitter += delay;
                }
                Integer size_list = delay_list.size();
                avg_jitter = (sum_jitter/size_list.doubleValue());
                Point point_test = Point.measurement("kpis")
                        .addTag("test_id", "1")
                        .addField("jitter", avg_jitter.intValue())
                        .time(current_timestamp, WritePrecision.MS);

                writeApi.writePoint(point_test);
                delay_list.clear();
                sum_jitter = 0;
            }
        }
    }

    // Calculate the throughput and packet loss, and send them to InfluxDB
    private static void throughputCalculation(ConsumerRecords<String, Long> sTRecords, ConsumerRecords<String, Long> rTRecords) {
        sum_bytes_sender = 0;
        sum_bytes_receiver = 0;
        long timestp = 0;
        long ploss = 0;
        for (ConsumerRecord<String, Long> record : sTRecords) {
            sum_bytes_sender += record.value();
        }
        for (ConsumerRecord<String, Long> record : rTRecords) {
            sum_bytes_receiver += record.value();
            timestp = record.timestamp();
        }
        if (sum_bytes_sender > 0) {
            toInfluxDB(Point.measurement("kpis")
                    .addTag("test_id", "1")
                    .addField("throughput_sent", (sum_bytes_sender))
                    .time(timestp, WritePrecision.MS));
        }
        if (sum_bytes_receiver > 0) {
            toInfluxDB(Point.measurement("kpis")
                    .addTag("test_id", "1")
                    .addField("throughput_rcv", (sum_bytes_receiver))
                    .time(timestp, WritePrecision.MS));
        }
        if (sum_bytes_sender > 0) {
            ploss = 100 - (sum_bytes_receiver / sum_bytes_sender * 100);
        }
        if (ploss >= 0 && ploss <100) {
            toInfluxDB(Point.measurement("kpis")
                    .addTag("test_id", "1")
                    .addField("packet_loss", ploss)
                    .time(timestp, WritePrecision.MS));
        }
    }

    // Auxiliary function to send the data to InfluxDB
    private static boolean toInfluxDB(Point point_to_add) {
        try {
            writeApi.writePoint(point_to_add);
            return true;
        } catch (InfluxException e) {
            e.printStackTrace();
            return false;
        }
    }

}


