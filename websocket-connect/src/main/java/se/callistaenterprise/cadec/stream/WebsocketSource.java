package se.callistaenterprise.cadec.stream;

import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import se.callistaenterprise.cadec.stream.websocket.SocketServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class WebsocketSource extends SourceConnector {

    private SocketServer socketServer;

    private static EvictingQueue<String> QUEUE;

    private String version = "1";
    private String brokers;
    private String topic;

    private String path;
    private int port;
    private int maxSize;

    //Websocket
    public final static String CONFIG_WEBSOCKET_PORT = "websocket.port";
    public final static String CONFIG_WEBSOCKET_PATH = "websocket.path";
    public final static String CONFIG_WEBSOCKET_QUEUE_MAX = "websocket.queue_max";

    //Connect
    public final static String CONFIG_CONNECT_VERSION = "source.version";
    public final static String CONFIG_CONNECT_BROKERS = "source.brokers";
    public final static String CONFIG_CONNECT_SOURCE_TOPIC = "source.topic";

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public void start(Map<String, String> props) {

        this.version = props.get(CONFIG_CONNECT_VERSION);
        this.brokers = props.get(CONFIG_CONNECT_BROKERS);
        this.topic = props.get(CONFIG_CONNECT_SOURCE_TOPIC);

        this.path = props.get(CONFIG_WEBSOCKET_PATH);
        this.port = Integer.valueOf(props.get(CONFIG_WEBSOCKET_PORT));
        this.maxSize = Integer.valueOf(props.get(CONFIG_WEBSOCKET_QUEUE_MAX));

        if(QUEUE == null) {
            QUEUE = EvictingQueue.create(this.maxSize);
        }

        socketServer = new SocketServer(port, path);
        CompletableFuture.runAsync(() -> {
            try {
                socketServer.start();
            } catch (Exception err) {
                log.error("Could not start netty", err);
            }
        });
    }

    @Override
    public Class<? extends Task> taskClass() {
        return WebsocketSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return IntStream.of(maxTasks).mapToObj(i -> {
            final Map<String, String> conf = new HashMap<>();
            conf.put(CONFIG_CONNECT_SOURCE_TOPIC, this.topic);
            conf.put(CONFIG_CONNECT_VERSION, this.version);
            return conf;
        }).collect(Collectors.toList());
    }

    @Override
    public void stop() {
        if(socketServer != null) {
            this.socketServer.stop();
        }
    }

    @Override
    public ConfigDef config() {
        final ConfigDef c = new ConfigDef();
        c.define(CONFIG_WEBSOCKET_PATH, ConfigDef.Type.STRING, "/source", ConfigDef.Importance.HIGH, "Path to listen to socket connection");
        c.define(CONFIG_WEBSOCKET_PORT, ConfigDef.Type.INT, 21015, ConfigDef.Importance.HIGH, "Socket port");
        c.define(CONFIG_WEBSOCKET_QUEUE_MAX, ConfigDef.Type.INT, 512, ConfigDef.Importance.HIGH, "Max queue size");
        c.define(CONFIG_CONNECT_BROKERS, ConfigDef.Type.STRING, "localhost:9092", ConfigDef.Importance.HIGH, "Kafka brokers , seperated");
        c.define(CONFIG_CONNECT_VERSION, ConfigDef.Type.STRING, "1.0.0", ConfigDef.Importance.HIGH, "Source connector version");
        c.define(CONFIG_CONNECT_SOURCE_TOPIC, ConfigDef.Type.STRING, "WebSocketSource", ConfigDef.Importance.HIGH, "Source topic");
        return c;
    }

    public static void put(final String msg) {
        log.info("Added to queue: {}", msg);
        QUEUE.add(msg);
    }

    public static String poll() {
        return QUEUE.poll();
    }
}
