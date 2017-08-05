# TomcatLogAla
Tomcat服务器访问日志分析

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
