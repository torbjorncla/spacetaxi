FROM kafka-connect:latest
ADD build/libs/*-all.jar /kafka/connectors/websocket.jar
ADD SourceConfig.properties /kafka/connectors/

EXPOSE 8084 8083

ENTRYPOINT exec sh /kafka/run-standalone.sh /kafka/config/connect-standalone.properties /kafka/connectors/SourceConfig.properties
