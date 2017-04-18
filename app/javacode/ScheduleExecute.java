package javacode;

import java.io.FileReader;
import java.io.LineNumberReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.quartz.Job;
import org.quartz.JobDataMap;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;





public class ScheduleExecute implements Job {

//	Rhino
	public class JSExploration   
	{   
	    private Context cx;   
	  
	    private Scriptable scope;   
	  
	    public JSExploration()   
	    {   
	        this.cx = Context.enter();   
	        this.scope = cx.initStandardObjects();   
	    }   
	  
	    //  运行js
	    public Object runJavaScript(String filename)   
	    {   
	        String jsContent = this.getJsContent(filename);   
	        Object result = cx.evaluateString(scope, jsContent, filename, 1, null);   
	        return result;   
	    }   
	  
	 // 从js文件读出内容，返回String
	    private String getJsContent(String filename)   
	    {   
	        LineNumberReader reader;   
	        try  
	        {   
	            reader = new LineNumberReader(new FileReader(filename));   
	            String s = null;   
	            StringBuffer sb = new StringBuffer();   
	            while ((s = reader.readLine()) != null)   
	            {   
	                sb.append(s).append("\n");   
	            }  
	            
	            String ScriptFront = "var swingNames = JavaImporter(); \n swingNames.importPackage(Packages.java.lang); \n  swingNames.importPackage(Packages.javacode); \n with (swingNames) {  \n";
	            String ScriptEnd = "}";
	            return ScriptFront+sb.toString()+ScriptEnd;   
	        }   
	        catch (Exception e)   
	        {   
	            // TODO Auto-generated catch block   
	            e.printStackTrace();   
	            return null;   
	        }   
	    }   
	    
	    public Scriptable getScope()   
	    {   
	        return scope;   
	    }   
	} 
	
	
	
//	调度的作业
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
//		JobDataMap
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//		脚本路径
		String path = (String) jobDataMap.get("Path");
		System.out.println(path + "  &&&&  \n");
//		运行脚本
        JSExploration jsExploration = new JSExploration();   	
        Object result = jsExploration.runJavaScript(path); 
	}

}