FROM openjdk:8-jdk-alpine
RUN wget http://apache.mirrors.spacedump.net/kafka/0.11.0.1/kafka_2.11-0.11.0.1.tgz
RUN tar -zxvf kafka_2.11-0.11.0.1.tgz
RUN rm -f kafka_2.11-0.11.0.1.tgz
RUN mv kafka_2.11-0.11.0.1 kafka
RUN mkdir -p /kafka/connectors
ENV PATH /kafka/bin:/kafka/connectors/:/kafka/config/:$PATH
ENV KAFKA_LOG4J_OPTS "-Dlog4j.configuration=file:/kafka/config/connect-log4j.properties"
ADD connect-standalone.properties /kafka/config
ADD run-standalone.sh /kafka
