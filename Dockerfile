#指定基础镜像，在其上进行定制
FROM fancybing/java:serverjre-8

#维护者信息
MAINTAINER zk 247213579@qq.com

ARG WORK_PATH="/work/services"
ARG JAR_FILE

#复制上下文目录下的target/demo-1.0.0.jar 到容器里
COPY target/$JAR_FILE $WORK_PATH/
RUN ls $WORK_PATH/

WORKDIR $WORK_PATH

#指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT exec java -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -jar $JAR_FILE