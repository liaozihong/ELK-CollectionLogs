## 利用Filebeat采集日志  

目录介绍：  

- Filebeat 存放Filebeat的配置，主要在于采集和发送，有两个文件，分别是基于Logstash的和基于Ingest Node的。
- Ingest Node 存放一个Spring-Logs例子的管道，具体描述参照filebeat结合Ingest Node采集特定格式的日志.md 
- Logstash 存放关于Logstash的配置，和启动Logstash的docker-compose，具体参照Filebeat结合Logstash采集你想要的日志.md
- Spring-Logs 一个用于生成错误日志的demo，使用Docker构建了，附带Filebeat的容器，需要将对应要测试filebeat.yml挂载进去

1. 构建Spirng-Logs项目
```
gradle clean build -x test docker
```
上面构建命令失败的话，可采取下面的命令
```
gradle clean build -x test
docker build -t Spring-Logs:latest .
```
2(logstash方案). 启动Logstash容器
编辑logstash.conf 文件，更改成自己的es ip地址和logstash.conf文件的挂载路径。接着运行：
```
docker-compose up -d
```
注意：由于logstash启动较慢，处理转发日志可能需要等一会

2(ingest方案).在elasticsearch上创建相应管道    
编辑filebeat-ingestnode.yml，将管道名改成刚创建的那个，在发送到自己的es 地址上。

3. 启动项目前更改docker-compose，查看对应的挂载文件是否正确，并且自己替换对应的Logstash ip地址，检查无误后使用下面命令运行：
```
docker-compose up -d
# 查看日志
docker-compose logs -f
```
访问localhost:8080,生成错误日志，登录kibana，查看es是否生成索引为logstash-* / filebeat-* 的日志，有则创建查看即可。  

日志收集流程查看  
参看对应方案的两个文档。  