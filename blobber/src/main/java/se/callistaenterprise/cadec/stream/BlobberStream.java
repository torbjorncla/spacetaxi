package se.callistaenterprise.cadec.stream;

import io.confluent.kafka.serializers.AbstractKafkaAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import se.callistaenterprise.cadec.stream.model.Blobber;
import se.callistaenterprise.cadec.stream.model.BlobberControl;

import javax.annotation.PostConstruct;

@Component
public class BlobberStream {

    @Value("${kafka.topics.blobber.in}")
    private String blobberControlTopic;

    @Value("${kafka.topics.blobber.out}")
    private String blobberOutTopic;

    private Serde<BlobberControl> blobberCtrlSerde = new SpecificAvroSerde<>();


    @Autowired
    private SpecificAvroSerde<Blobber> blobberSerde;

    @Bean
    public KStream<byte[], Blobber> stream(final KStreamBuilder builder) {

        final KStream<byte[], Blobber> s = builder.stream(Serdes.ByteArray(), Serdes.ByteArray(), "WebSocketIn")
                .peek((k,v) -> {
                    System.err.println("KEY: " + new String(k));
                    System.err.println("VALUE: " + new String(v));
                }).mapValues(v -> {
            final Blobber b = Blobber.newBuilder().setColor("#999").setId(1).setPos(2).setSize(3).setSpeed(4).build();
            return b;
        }).through(Serdes.ByteArray(), blobberSerde, blobberOutTopic) //
        .peek((k, v) -> {
            System.err.println("KEYKEY: " + k);
            System.err.println("VVV: " + v.toString());
        });


        return s;

    }

}
