package se.callisatenterprise.cadec.stream;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.junit.Test;
import se.callistaenterprise.cadec.stream.model.BlobberControl;

import static org.junit.Assert.assertTrue;

public class SerializerTest {

    @Test
    public void test() {

        final Serde<BlobberControl> blobberSerde = new SpecificAvroSerde<>();

        final BlobberControl ctrl = BlobberControl.newBuilder().setAmount(1).setId("blobid").build();

        System.err.println(ctrl);

        blobberSerde.serializer().serialize("", ctrl);


        assertTrue(true);
    }

}
