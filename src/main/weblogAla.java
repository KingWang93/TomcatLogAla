package main;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import net.sf.json.JSONArray;
import part.MyPartitioner;
import util.interpolation;
import util.sort;


public class weblogAla {
	public static class weblogAlaMapper extends MapReduceBase implements Mapper<Object, Text, Text, IntWritable> {
        private IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        	Pattern p = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s-\\s-\\s\\[(.*)\\s.*\\]\\s\\\"(GET|POST)\\s([^?]+)\\??.*\\s(.*)\\\"\\s(\\d*)\\s(\\d*)");
 			Matcher matcher = p.matcher(value.toString());
 			SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
 			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
 			if(matcher.find())
 	        {
 	        	if(matcher.group(4).length()<100){
 	        		for(int i=1;i<=3;i++){
 	        			if(i==2){
 	        				try {
 	        					System.out.println(sdf.format(df.parse(matcher.group(i))));
								word.set(sdf.format(df.parse(matcher.group(i))));
								output.collect(word, one);
								word.set(String.valueOf(Integer.parseInt(df.format(df.parse(matcher.group(i))).substring(12,14))));
								output.collect(word, one);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}//日期
 	        			}else{
 	        				System.out.println(matcher.group(i));
 	        				word.set(matcher.group(i));
 	        				output.collect(word, one);
 	        			}
 	        		}
 	        	}
 	        }
        }
    }

    public static class weblogAlaReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            result.set(sum);
            output.collect(key, result);
        }
    }
    
   


    public static void main(String[] args) throws Exception {
        String input = "hdfs://192.168.182.133:9000/user/hadoop/tomcat_log";
        String output = "hdfs://192.168.182.133:9000/user/hadoop/weblog_output";

        Configuration conf1 = new Configuration();  
        FileSystem fileSystem = FileSystem.get(new URI(output), conf1);  
        fileSystem.delete(new Path(output), true); 
    	
        JobConf conf = new JobConf(weblogAla.class);
     // 首先删除输出目录已生成的文件
    	FileSystem fs = FileSystem.get(conf);
    	Path outPath = new Path("hdfs://192.168.182.133:9000/user/hadoop/weblog_output");
    	if (fs.exists(outPath)) {
    		fs.delete(outPath, true);
    	}
        conf.setJobName("weblogAla");
        conf.addResource("core-site.xml");//classpath:/hadoop/
        conf.addResource("hdfs-site.xml");
        conf.addResource("mapred-site.xml");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(weblogAlaMapper.class);
        conf.setCombinerClass(weblogAlaReducer.class);
        conf.setReducerClass(weblogAlaReducer.class);
        conf.setPartitionerClass(MyPartitioner.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);
        
        conf.setNumReduceTasks(4);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        JobClient.runJob(conf);
        
        System.out.println("IP访问排名:");
        JSONArray ipArray=sort.getRank("hdfs://192.168.182.133:9000/user/hadoop/weblog_output/part-00001",false,false);//IP排名
        System.out.println(ipArray);
        
        System.out.println("每天的访问量排名:");
        JSONArray dayAccess=interpolation.inter_date("hdfs://192.168.182.133:9000/user/hadoop/weblog_output/part-00002");//每天的访问量
        System.out.println(dayAccess);
        
        System.out.println("24小时时间段访问排名:");
        JSONArray Access24=sort.getRank("hdfs://192.168.182.133:9000/user/hadoop/weblog_output/part-00003",true,true);//IP排名
        System.out.println(Access24);
        
        System.exit(0);
    }
}
