import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class QuizSearcher {
	
	public static void readFile(ObjectOutputStream out) {
        try{
        	System.out.println("In ReadFile()");

            Path path = new Path("Project4Output/output/part-r-00000");
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", "hdfs://localhost:9000");
            FileSystem fs = FileSystem.get(conf);
            BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
            String line;
            line=br.readLine();
            while (line != null){
                   // out.writeObject(line);
                    System.out.println(line);
                    line=br.readLine();
            	}
        }catch(Exception e){
        		e.printStackTrace(System.out);
        }
	}

	
	public static ObjectOutputStream outputStream;  

	public void setOutputStream(ObjectOutputStream o){
		outputStream = o;
	}

	 public static class QuizSearchMapper extends Mapper<Object, Text, Text, Text>{
		  
		 public ObjectOutputStream out;  
		 
			public void setOutputStream(ObjectOutputStream o){
				out = o;
			}

		  String requestTag;
		  
		  protected void setup(Context context) throws IOException, InterruptedException {
		        Configuration conf = context.getConfiguration();

		        requestTag = new String();
		        requestTag = conf.get("requestedtag");
		    }	  
		  
		  
		  private Text tag = new Text();
		  private Text quizContent = new Text();
		  private String quiz = "";
		  private String tagString;
		  
		  public void map(Object key, Text value, Context context
		                    ) throws IOException, InterruptedException {
			  StringTokenizer itr = new StringTokenizer(value.toString());
			  while (itr.hasMoreTokens()) {
				  if (itr.nextToken().contains("$"))
				  {
					  tagString = itr.nextToken();
					  System.out.println("TAGSTRING: " + tagString);
					  System.out.println("REQUEST TAG: " + requestTag);
				  }
				  if (tagString.equalsIgnoreCase(requestTag))
				  {
					  System.out.println("IN IF. TAGSTRING: " + tagString);
					  tag.set(tagString);

					  while (!quiz.contains("$") && itr.hasMoreTokens())
					  {	  

						  quiz = quiz + " " + itr.nextToken();
						  if (!quiz.contains("$"))
						  {
							  quizContent.set(quiz);
						  }
					  }
			  	      context.write(tag, quizContent);
			  	      quiz = "";

					  //quizContent.set(quiz);
				  }
			  }
		  }
	 }
	 public static class QuizReducer
	    extends Reducer<Text,Text,Text,Text> {
		  public void reduce(Text key, Text value,
	              Context context
	              ) throws IOException, InterruptedException {
			  context.write(key, value);
		  }
	 }
	 
	 public static void main(String args[]) throws IllegalArgumentException, IOException, ClassNotFoundException, InterruptedException
		{
			Configuration conf = new Configuration();
			conf.set("fs.default.name", "hdfs://localhost:9000");
			conf.set("requestedtag", args[1]);
			Job job = Job.getInstance(conf, "tag count");
			FileSystem fs = FileSystem.get(conf);
			//Path out = fs.makeQualified(new Path ("user/ubuntu/Project4Output/output"));
			Path out = new Path ("quizoutput");
			fs.delete(out, true);
			job.setJarByClass(QuizSearcher.class);
			job.setMapperClass(QuizSearchMapper.class);
			job.setCombinerClass(QuizReducer.class); 
			job.setReducerClass(QuizReducer.class); 
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			FileInputFormat.addInputPath(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, out);
			System.out.println("OUTPUT PATH: " + out);
			job.waitForCompletion(true);

		}

}

