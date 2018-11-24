# 使用Filebeat采集日志结合logstash过滤出你想要的日志

filebeat作用是采集特定目录下的日志，并将其发送出去，但是它只能采集并无法对数据进行筛选，这时候就用到logstash了，logstash拥有众多插件可提供过滤筛选功能，由于logstash本身是基于jdk的，所以占用内存较大，而filebeat相较下，占用的内存就不是很多了。有图有真相：  

![](https://ws1.sinaimg.cn/large/006mOQRagy1fxj5ibh86zj31fj0ng0wx.jpg)  

所以可采用如下的方案采集筛选日志：  
1. 每个项目都伴随着一个filebeat采集日志，你只需给他配置日志所在目录(可用正则匹配)即可，它会进行监视，如有新增日志会自动将日志采集发送到你配置的输出里，一般配 置的输出有kafka和redis、logstash、elasticsearch，这里为了筛选格式，采用logstash进行处理。  
2. 配置filebeat多模块，为众多项目配置日志目录路径进行日志采集并发送到logstash筛选过滤在转发给elasticsearch。  

### filebeat.yml配置
参考配置，只列出我用到的，详情见官方文档：  
```
filebeat:
  prospectors:
  - type: log
    //开启监视，不开不采集
    enable: true
    paths:  # 采集日志的路径这里是容器内的path
    - /var/log/elkTest/error/*.log
    # 日志多行合并采集
    multiline.pattern: '^\['
    multiline.negate: true
    multiline.match: after
    # 为每个项目标识,或者分组，可区分不同格式的日志
    tags: ["java-logs"]
    # 这个文件记录日志读取的位置，如果容器重启，可以从记录的位置开始取日志
    registry_file: /usr/share/filebeat/data/registry

output:
  # 输出到logstash中
  logstash:
    hosts: ["120.79.58.138:5044"]
```
注：6.0以上该filebeat.yml需要挂载到/usr/share/filebeat/filebeat.yml,另外还需要挂载/usr/share/filebeat/data/registry 文件，避免filebeat容器挂了后，新起的重复收集日志。  

### logstash.conf配置
我用到的logstash并不是用来采集日志的，而是对日志进行匹配筛选，所以不要跟随项目启动，只需单独启动，暴露5044端口，能接收到filebeat发送日志即可，也就是说，它只是起到一个加工并转发给elasticsearch的作用而已。  
配置参考：  
```
input {
	beats {
	    port => 5044
	}
}
filter {
   if "java-logs" in [tags]{ 
     grok {
        # 筛选过滤
        match => {
	       "message" => "(?<date>\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2},\d{3})\]\[(?<level>[A-Z]{4,5})\]\[(?<thread>[A-Za-z0-9/-]{4,40})\]\[(?<class>[A-Za-z0-9/.]{4,40})\]\[(?<msg>.*)"
        }
       remove_field => ["message"]
     }
     # 不匹配正则则删除，匹配正则用=~
 	 if [level] !~ "(ERROR|WARN|INFO)" {
         # 删除日志
         drop {}
     }
    }
}

output {
	elasticsearch {
		hosts => "elasticsearch:9200"
	}
}

```
备注：  

    #grok 里边有定义好的现场的模板你可以用，但是更多的是自定义模板，规则是这样的，小括号里边包含所有一个key和value，例子：（?<key>value），比如以下的信息，第一个我定义的key是data，表示方法为：?<key> 前边一个问号，然后用<>把key包含在里边去。value就是纯正则了，这个我就不举例子了。这个有个在线的调试库，可以供大家参考，
    http://grokdebug.herokuapp.com/ 
    
参考链接：  
[ELK 之Filebeat 结合Logstash](http://blog.51cto.com/seekerwolf/2110174)  
[Grok Debugger](http://grokdebug.herokuapp.com/)  
[ELK logstash 配置语法(24th)](http://www.ttlsa.com/elk/elk-logstash-configuration-syntax/)  
[Filebeat官方文档](https://www.elastic.co/guide/en/beats/filebeat/6.x/index.html)  
[Logstash官方文档](https://www.elastic.co/guide/en/logstash/6.5/logstash-6-5-0.html)

