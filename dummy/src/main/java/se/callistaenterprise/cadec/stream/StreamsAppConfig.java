package se.callistaenterprise.cadec.stream;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class StreamsAppConfig {

    @Value("${kafka.brokers}")
    private String brokers;

    @Value("${kafka.topics.dummy.in}")
    private String dummyInTopic;

    @Value("${kafka.topics.dummy.out}")
    private String dummyOutTopic;

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testStreams");
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new StreamsConfig(props);
    }
    @Bean("serdeConfig")
    public Map<String,String> serdeConfig(@Value("${kafka.schema_registry}") String schemaRegistryUrl) {
        return Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
    }

    @Bean
    public NewTopic dummyIn() {
        return new NewTopic(dummyInTopic, 5, (short) 2);
    }

    @Bean
    public NewTopic dummyOut() {
        return new NewTopic(dummyOutTopic, 5, (short) 2);
    }

    @Bean
    public NewTopic BlobberIn() {
        return new NewTopic(blobberInTopic, 5, (short) 2);
    }

    @Bean
    public NewTopic BlobberOut() {
        return new NewTopic(blobberOutTopic, 5, (short) 2);
    }
}
