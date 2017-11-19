package se.callistaenterprise.cadec.stream;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.KafkaAdmin;
import se.callistaenterprise.cadec.stream.model.Blobber;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class Config {

    @Value("${kafka.brokers}")
    private String brokers;

    @Value("${kafka.schema_registry}")
    private String schemaRegistryUrl;

    @Value("${kafka.topics.blobber.in}")
    private String blobberInTopic;

    @Value("${kafka.topics.blobber.out}")
    private String blobberOutTopic;

    @Bean
    public KafkaAdmin admin() {
        final Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        return new KafkaAdmin(config);
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig streamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "blobberStreams");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "blobberStreamsClient");
        //props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        return new StreamsConfig(props);
    }

    @Bean
    public SpecificAvroSerde<Blobber> createBlobberSerde() {
        final SpecificAvroSerde<Blobber> blobberSerde = new SpecificAvroSerde<>();
        blobberSerde.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl),
                false);
        return blobberSerde;
    }

    @Bean
    public NewTopic BlobberIn() {
        return new NewTopic(blobberInTopic, 5, (short) 2);
    }

    @Bean
    public NewTopic BlobberOut() {
        return new NewTopic(blobberOutTopic, 5, (short) 2);
    }

    @Bean
    public NewTopic WebSocketIn() { return new NewTopic("WebSocketIn", 5, (short) 2);}

    @Bean
    public NewTopic WebSocketOut(){ return new NewTopic("WebSocketOut", 5, (short) 2);}
}
