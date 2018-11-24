## 采集日志DEMO

1. 构建项目
```
gradle clean build -x test docker
```
上面构建命令失败的话，可采取下面的命令
```
gradle clean build -x test
docker build -t Spring-Logs:latest .
```
2. 启动Logstash容器
编辑logstash.conf 文件，更改成自己的es ip地址和logstash.conf文件的挂载路径。接着运行：
```
docker-compose up -d
```
注意：由于logstash启动较慢，处理转发日志可能需要等一会

3. 启动项目前更改docker-compose，查看对应的挂载文件是否正确，并且自己替换对应的Logstash ip地址，检查无误后使用下面命令运行：
```
docker-compose up -d
# 查看日志
docker-compose logs -f
```
访问localhost:8080,生成错误日志，登录kibana，查看es是否生成索引为logstash-*的日志，有则创建查看即可。

日志收集流程查看README-zh-cn.md
