package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import entity.keyValueBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class sort {
	 public static JSONArray getRank(String url,boolean isasc,boolean iskey) throws Exception
	 {
		  Configuration conf = new Configuration();
		  //ip统计结果排序
		  FileSystem hdfs = FileSystem.get(URI.create(url),conf);
		  FSDataInputStream in = null;
		  BufferedReader reader=null;
		  String line = null;
		  LinkedList<keyValueBean> list = new LinkedList<>();
	        	reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(url))));
	            while((line=reader.readLine())!=null){
	            	String[] arr = line.split("\\s+");//匹配多个空格
	            	keyValueBean bean = new keyValueBean();
		            bean.setKey(arr[0]);
		            bean.setValue(arr[1]);
		            sort(bean,list,isasc,iskey);//按照value降序
	            }
	            reader.close();
	            JSONArray ja = JSONArray.fromObject(list);
	            System.out.println(ja);
	            return ja;
	 }
	 /**
	  * 插入排序
	  * @param o	插入对象，为bean，两个String成员
	  * @param list		被插入的链表
	  * @param isasc	是否为升序，true：升序；false：降序
	  * @param iskey	按照什么排序，true：按照key；false：按照value
	  */
	 public static void sort(keyValueBean o,LinkedList<keyValueBean> list,boolean isasc,boolean iskey){
		 boolean orderby=false;
		 if(list.size()==0){
			 list.addFirst(o);
		 }else{
			 boolean insertflag = false;
				 for(int i=0;i<list.size();i++){
					 String s1=iskey?o.getKey():o.getValue();
					 String s2=iskey?list.get(i).getKey():list.get(i).getValue();
					 orderby = isasc?Integer.parseInt(s1)<Integer.parseInt(s2):Integer.parseInt(s1)>Integer.parseInt(s2);
					 if(orderby){
						 list.add(i, o);
						 insertflag = true;
						 break;
					 }
				 }
			 if(!insertflag){
				 list.addLast(o);
			 }
		 }
	 }
}
