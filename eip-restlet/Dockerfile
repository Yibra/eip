FROM openjdk:8-jdk-alpine3.9
MAINTAINER chenxin.ma <chenxin.ma@qq.com>
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN mkdir -p /opt/settings
RUN mkdir -p /app
RUN echo "env=Local" > /opt/settings/server.properties
ARG JAR_FILE
ADD target/${JAR_FILE} /app/eip-restlet.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "eip-restlet.jar"]
EXPOSE 8080