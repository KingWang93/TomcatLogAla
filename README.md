# TomcatLogAla
Tomcat服务器访问日志分析
- [MongoOraclePerformanceTest](https://github.com/KingWang93/MongoOraclePerformanceTest)
- [TomcatLogAla](https://github.com/KingWang93/TomcatLogAla)
- [LogAlaHive](https://github.com/KingWang93/LogAlaHive)

上述三个项目都是关于Tomcat日志访问分析的项目，逐渐递进

MongoOraclePerformanceTest是基于Oracle做的日志分析，同时该项目还对MondoDb和Oracle做了插入性能测试和查询测试（分别在无索引和有索引的情况下）
TomcatLogAla是基于纯HDFS（没有Hive,也没有用到Hbase）开发的
LogAlaHive是基于Hbase，同时上层利用Hive做查询使用功能

<font size=4 color=red>
主要功能：
基于Tomcat访问日志进行分析，分析了各个时段/每天的访问量统计，以及各个IP访问量的统计

环境：
Hadoop-2.7.2/Hbase-1.1.2/Hive-1.2.1/Zookeeper-3.4.6/Mysql/VMware/Ubuntu14.0

处理流程：
(1)日志文件数据上传至HDFS
(2)利用Map/Reduce，根据正则表达式进行统计（类似于词频统计）
(3)利用Partitioner类，根据正则匹配，将IP访问量统计结果，每天访问量统计结果，时间段统计结果，访问方法比例统计结果输出到不同的文件（这时候结果还没有排序）
(4)对HDFS输出结果进行排序
(5)利用Echart进行结果展示

具体细节，请参考博客地址里的链接
http://blog.csdn.net/wk1134314305/article/details/61965135
（链接中有该项目的文档说明和代码，以及相应的软件安装环境）。
