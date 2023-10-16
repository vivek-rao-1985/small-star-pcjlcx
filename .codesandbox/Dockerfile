FROM ubuntu

# Update the package list and install JDK and wget
RUN apt update -y && apt install -y openjdk-17-jdk wget curl software-properties-common

# Install Node.js and npm
RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs

# Set the TOMCAT_VERSION env variable
ENV TOMCAT_VERSION 9.0.82



# Download and extract Tomcat
RUN wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.82/bin/apache-tomcat-9.0.82.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN mv /tmp/apache-tomcat-9.0.82 /opt/tomcat

RUN wget https://repo1.maven.org/maven2/org/json/json/20201115/json-20201115.jar -O /opt/tomcat/lib/json-20201115.jar
RUN wget https://jdbc.postgresql.org/download/postgresql-42.2.5.jar -O /opt/tomcat/lib/postgresql-42.2.5.jar
RUN wget https://repo1.maven.org/maven2/com/auth0/java-jwt/3.18.2/java-jwt-3.18.2.jar -O /opt/tomcat/lib/java-jwt-3.18.2.jar
RUN wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.13.2/jackson-databind-2.13.2.jar -O /opt/tomcat/lib/jackson-databind-2.13.2.jar
RUN wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.13.2/jackson-core-2.13.2.jar -O /opt/tomcat/lib/jackson-core-2.13.2.jar
RUN wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.13.2/jackson-annotations-2.13.2.jar -O /opt/tomcat/lib/jackson-annotations-2.13.2.jar
RUN wget -O /opt/tomcat/lib/jbcrypt-0.4.jar https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
RUN wget -O /opt/tomcat/lib/javafaker-1.0.2.jar https://repo1.maven.org/maven2/com/github/javafaker/javafaker/1.0.2/javafaker-1.0.2.jar
RUN wget -O /opt/tomcat/lib/snakeyaml-1.29.jar https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.29/snakeyaml-1.29.jar
RUN wget -O /opt/tomcat/lib/bucket4j-core-4.10.0.jar https://repo1.maven.org/maven2/com/github/vladimir-bukhtoyarov/bucket4j-core/4.10.0/bucket4j-core-4.10.0.jar
RUN wget -O /opt/tomcat/lib/commons-collections-3.2.1.jar https://repo1.maven.org/maven2/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar


COPY --from=library/docker:latest /usr/local/bin/docker /usr/bin/docker
COPY --from=docker/compose:1.22.0 /usr/local/bin/docker-compose /usr/bin/docker-compose