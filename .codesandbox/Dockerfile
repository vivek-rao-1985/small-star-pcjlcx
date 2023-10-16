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


COPY --from=library/docker:latest /usr/local/bin/docker /usr/bin/docker
COPY --from=docker/compose:1.22.0 /usr/local/bin/docker-compose /usr/bin/docker-compose