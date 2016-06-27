import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


//DONT SEND BACK THE COUNT

public class TagCounter {
		public static ObjectOutputStream outputStream;  

		public void setOutputStream(ObjectOutputStream o){
			outputStream = o;
		}
		
		public static void readFile(Path p, ObjectOutputStream out) {
	        try{
	        	System.out.println("In ReadFile()");
	            Configuration conf = new Configuration();
	            Path path = new Path(p + "/part-r-00000");
	            conf.set("fs.defaultFS", "hdfs://localhost:9000");
	            FileSystem fs = FileSystem.get(conf);
	            BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
	            String line;
	            line=br.readLine();
	            while (line != null){
	                    //out.writeObject(line);
	                    System.out.println(line);
	                    line=br.readLine();
	            	}
	        }catch(Exception e){
	        		e.printStackTrace(System.out);
	        }
		}


	public static class TagMapper
 extends Mapper<Object, Text, Text, IntWritable>{

 private final static IntWritable one = new IntWritable(1);
 private Text tag = new Text();

 	public void map(Object key, Text value, Context context
                 ) throws IOException, InterruptedException {
 	
 		StringTokenizer itr = new StringTokenizer(value.toString());
 		while (itr.hasMoreTokens()) {
 			if (itr.nextToken().contains("$"))
 			{
 				tag.set(itr.nextToken());
 	        	context.write(tag, one);
 			}
 		}
 	}
 }


public static class TagReducer
    extends Reducer<Text,IntWritable,Text,IntWritable> {
	private IntWritable result = new IntWritable();

 	public void reduce(Text key, Iterable<IntWritable> values,
                    Context context
                    ) throws IOException, InterruptedException {
 	
	 	int sum = 0;
   		for (IntWritable val : values) {
	   	sum += val.get();
   		}
   			result.set(sum);
   			context.write(key, result);
 		}
	}

	public static void main(String args[]) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException
	{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path out = new Path("quizoutput");
		fs.delete(out, true);
		conf.set("fs.default.name", "hdfs://localhost:9000");
		Job job = Job.getInstance(conf, "tag count");
		job.setJarByClass(TagCounter.class);
		job.setMapperClass(TagMapper.class);
		job.setCombinerClass(TagReducer.class); 
		job.setReducerClass(TagReducer.class); 
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, out);
		job.waitForCompletion(true);
	}
	


}

