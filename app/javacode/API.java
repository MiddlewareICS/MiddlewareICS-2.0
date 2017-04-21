package javacode;

import java.io.FileReader;
import java.io.LineNumberReader;

public class API
{

	//从js文件读出内容，返回String
	  public String getJsContent(String filename)   
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
	          
	          return sb.toString();   
	      }   
	      catch (Exception e)   
	      {   
	          // TODO Auto-generated catch block   
	          e.printStackTrace();   
	          return null;   
	      }   
	  }   
}