# 指定基础镜像，在其上进行定制
FROM fancybing/java:serverjre-8

# 维护者信息
MAINTAINER zk 247213579@qq.com

# 参数
ARG JAR_FILE

# 环境变量
ENV WORK_PATH="/opt/services"

# 从上下文目录中复制文件或者目录到容器里中指定的路径, 该路径不用事先建好，路径不存在则会自动创建
COPY target/${JAR_FILE} ${WORK_PATH}/

WORKDIR ${WORK_PATH}

# 指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT exec java -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -jar ${JAR_FILE}