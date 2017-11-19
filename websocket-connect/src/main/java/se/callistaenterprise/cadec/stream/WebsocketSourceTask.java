package se.callistaenterprise.cadec.stream;

import io.confluent.connect.avro.AvroData;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import se.callistaenterprise.cadec.stream.model.Blobber;
import se.callistaenterprise.cadec.stream.model.BlobberControl;

import java.sql.Blob;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class WebsocketSourceTask extends SourceTask {

    private String version = "";
    private String topic = "";

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("00000000000000000000");
        log.info(" - Started task - ");
        log.info("00000000000000000000");
        this.version = props.get(WebsocketSource.CONFIG_CONNECT_VERSION);
        this.topic = props.get(WebsocketSource.CONFIG_CONNECT_SOURCE_TOPIC);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        final String msg = WebsocketSource.poll();
            if(msg != null) {
                log.info("Producing {} to {}", msg, topic);

                final String key = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                final SourceRecord r = new SourceRecord(null, null, topic, Schema.STRING_SCHEMA, key, Schema.STRING_SCHEMA, msg);

                return Collections.singletonList(r);
            }
        return Collections.emptyList();
    }

    @Override
    public void stop() {
    }
}
