FROM consul:1.7.2
MAINTAINER chenxin.ma <chenxin.ma@qq.com>
CMD ["agent", "-dev", "-client", "0.0.0.0"]
ENV JAVA_DISTRIBUTION openjdk
ENV JAVA_VERSION 8
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories
RUN apk add "$JAVA_DISTRIBUTION$JAVA_VERSION" --update && rm -rf /var/cache/apk/* && ln -s "/usr/lib/jvm/java-1.$JAVA_VERSION-openjdk/bin/javac" /usr/bin/javac
RUN mkdir -p /opt/settings
RUN mkdir -p /app
RUN echo "env=Local" > /opt/settings/server.properties
ADD entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ARG JAR_FILE
ADD target/${JAR_FILE} /app/eip-restlet.jar
ENTRYPOINT ["/app/entrypoint.sh"]
EXPOSE 8080