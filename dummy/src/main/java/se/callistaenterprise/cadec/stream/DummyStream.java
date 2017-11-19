package se.callistaenterprise.cadec.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import se.callistaenterprise.cadec.stream.model.BlobberControl;

@Component
public class DummyStream {

    @Value("${kafka.topics.dummy.in}")
    private String dummyInTopic;

    @Value("${kafka.topics.dummy.out}")
    private String dummyOutTopic;

    private final Serde<BlobberControl> blobberCtrlSerde = new SpecificAvroSerde<>();

    @Bean
    public KStream<String, String> stream(KStreamBuilder builder) {
        builder.stream(Serdes.String(), blobberCtrlSerde, "BlobberControl").print();
        KStream<String, String> stream = builder.stream(dummyInTopic);
        stream.mapValues(v -> v.toUpperCase()).peek((k,v) -> {
            System.err.println(v);
        }).to(dummyOutTopic);
        return stream;
    }
}
