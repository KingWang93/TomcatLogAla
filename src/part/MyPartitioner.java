package part;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;


public class MyPartitioner implements Partitioner<Text,IntWritable> {
	@Override
	public int getPartition(Text paramKEY, IntWritable paramVALUE, int paramInt) {
		// TODO Auto-generated method stub
		Pattern p1 = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");//IP
		Matcher matcher1 = p1.matcher(paramKEY.toString());
		if(matcher1.find()){
			return 1;
		}
		Pattern p2 = Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$");//日期
		Matcher matcher2 = p2.matcher(paramKEY.toString());
		if(matcher2.find()){
			return 2;
		}
		Pattern p3 = Pattern.compile("^\\d{1,2}$");//24小时
		Matcher matcher3 = p3.matcher(paramKEY.toString());
		if(matcher3.find()){
			return 3;
		}
		return 0;
	}

	@Override
	public void configure(JobConf paramJobConf) {
		// TODO Auto-generated method stub
	}
}