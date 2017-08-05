package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.ibm.icu.util.Calendar;

import entity.keyValueBean;
import net.sf.json.JSONArray;

public class interpolation {
	public static JSONArray inter_date(String url) throws Exception{
		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(URI.create(url),conf);
		FSDataInputStream in = null;
		BufferedReader reader=null;
		String line = null;
		List<keyValueBean> list = new ArrayList<>();
		reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(url))));
		Date lastDate = new Date();
		int i=0;
        while((line=reader.readLine())!=null){
        	String[] arr = line.split("\\s+");//匹配多个空格
        	Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            date=sdf.parse(arr[0]);
        	keyValueBean bean = new keyValueBean();
        	bean.setKey(arr[0]);
        	bean.setValue(arr[1]);
        	if(i==0){
        		list.add(bean);
                i++;
                continue;
        	}
        	long minus=date.getTime()-lastDate.getTime();
        	long lastTime=lastDate.getTime();
        	while((minus-=86400000)>0){
				keyValueBean inter_bean = new keyValueBean();
				inter_bean.setKey(sdf.format(new Date(lastTime+=86400000)));
				inter_bean.setValue("0");
				list.add(inter_bean);
                i++;
			}
        	list.add(bean);
            lastDate=date;
            i++;
        }
        reader.close();
        JSONArray ja = JSONArray.fromObject(list);
        System.out.println(ja);
        return ja;
	}

}
