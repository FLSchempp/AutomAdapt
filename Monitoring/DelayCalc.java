/**
* AutomAdapt
* End-to-end delay calculation; Monitoring implementation
* @author      Francisco Luque Schempp
* @version     1.0
*/

package uma;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import java.time.Duration;
import java.util.Properties;


public class DelayCalc {

    public static void main(String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "IP_Address:Port";
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "monitoring-fls-a");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "monitoring-fls-c");
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        // Set the commit interval to 500ms so that any changes are flushed frequently. The low latency
        // would be important for anomaly detection.
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500);

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        final StreamsBuilder builder = new StreamsBuilder();

        final KStream<String, Long> s_packets = builder.stream("delay_talker_topic");
        final KStream<String, Long> r_packets = builder.stream("delay_listener_topic");

        ValueJoiner<Long, Long, Long> valueJoiner = (leftValue, rightValue) -> {
                return rightValue - leftValue;
        };

        KStream<String, Long> result_packets = s_packets.join(r_packets,
                valueJoiner,
                JoinWindows.of(Duration.ofSeconds(1)));

        result_packets.to("delay_topic", Produced.with(stringSerde, longSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.cleanUp();
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
