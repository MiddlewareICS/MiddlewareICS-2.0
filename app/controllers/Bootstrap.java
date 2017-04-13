/**
 * 
 */
package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import edu.pc3.sensoract.vpds.api.SensorActAPI;
import edu.pc3.sensoract.vpds.api.request.UserRegisterFormat;
import edu.pc3.sensoract.vpds.constants.Const;
import edu.pc3.sensoract.vpds.model.UserProfileModel;


//mqtt
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;  


import com.mongodb.*;


//为设备信息插入数据库而引入
import java.util.HashMap;
import java.util.Map;
import play.mvc.Before;
import play.mvc.Controller;
import edu.pc3.sensoract.vpds.api.SensorActAPI;
import edu.pc3.sensoract.vpds.api.request.TaskletAddFormat;

//Esper
import com.espertech.esper.client.EPAdministrator;  
import com.espertech.esper.client.EPRuntime;  
import com.espertech.esper.client.EPServiceProvider;  
import com.espertech.esper.client.EPServiceProviderManager;  
import com.espertech.esper.client.EPStatement;  
import com.espertech.esper.client.EventBean;  
import com.espertech.esper.client.UpdateListener;  
  
//Rhino
import java.io.FileReader;   
import java.io.LineNumberReader;   
import org.mozilla.javascript.Context;   
import org.mozilla.javascript.Function;   
import org.mozilla.javascript.Scriptable; 

/**
 * @author samy
 * 
 */
@OnApplicationStart
public class Bootstrap extends Job {


	private String ownerName = null;
	private String ownerPassword = null;
	private String ownerEmail = null;
	private String ownerKey = null;
	private String uploadKey = null;
	private String actuationKey = null;

	public void getOwnerConfiguration() {
		ownerName = Play.configuration.getProperty(Const.OWNER_NAME);
		ownerPassword = Play.configuration.getProperty(Const.OWNER_PASSWORD);
		ownerEmail = Play.configuration.getProperty(Const.OWNER_EMAIL);
		ownerKey = Play.configuration.getProperty(Const.OWNER_OWNERKEY);
		uploadKey = Play.configuration.getProperty(Const.OWNER_UPLOADKEY);
		actuationKey = Play.configuration.getProperty(Const.OWNER_ACTUATIONKEY);
	}

	public void updateOwnerConfigurationFile() {

		// read the conf file and comment the existing lines and add all the new
		// configuration parameters
		//
		String confFileName = Play.applicationPath.getAbsolutePath() + "/conf/"
				+ Const.OWNER_CONFIG_FILENAME;

		System.out.println("Updating " + confFileName);

		File confFile = new File(confFileName);

		// we need to store all the lines
		List<String> lines = new ArrayList<String>();

		// first, read the file and store the changes
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(confFile));
			String line = in.readLine();
			while (line != null) {
				lines.add(line);
				line = in.readLine();
			}
			in.close();

			// comment all the existing lines
			PrintWriter out = new PrintWriter(confFile);
			for (String l : lines)
				out.println("# " + l);

			// write the new parameters
			out.println("# ");
			out.println("# Updated on " + new Date().toString());
			out.println(Const.OWNER_NAME + "=" + ownerName);
			out.println(Const.OWNER_PASSWORD + "=" + ownerPassword);
			out.println(Const.OWNER_EMAIL + "=" + ownerEmail);
			out.println(Const.OWNER_OWNERKEY + "=" + ownerKey);
			out.println(Const.OWNER_UPLOADKEY + "=" + uploadKey);
			out.println(Const.OWNER_ACTUATIONKEY + "=" + actuationKey);

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void verifyOwnerKeys() {

		boolean isChanged = false;

		if (ownerKey == null || ownerKey.isEmpty() || ownerKey.length() != 32) {
			System.out.println("Invalid " + Const.OWNER_OWNERKEY + ":" + ownerKey
					+ " found. New key created!");

			ownerKey = SensorActAPI.userProfile.generateNewKey();
			Play.configuration.setProperty(Const.OWNER_OWNERKEY, ownerKey);

			isChanged = true;
		}

		if (uploadKey == null || uploadKey.isEmpty()
				|| uploadKey.length() != 32) {
			System.out.println("Invalid " + Const.OWNER_UPLOADKEY + ":" + uploadKey
					+ " found. New key created!");

			uploadKey = SensorActAPI.userProfile.generateNewKey();
			Play.configuration.setProperty(Const.OWNER_UPLOADKEY, uploadKey);
			isChanged = true;
		}

		if (actuationKey == null || actuationKey.isEmpty()
				|| actuationKey.length() != 32) {
			System.out.println("Invalid " + Const.OWNER_ACTUATIONKEY + ":"
					+ actuationKey + " found. New key created!");

			actuationKey = SensorActAPI.userProfile.generateNewKey();
			Play.configuration.setProperty(Const.OWNER_ACTUATIONKEY, actuationKey);
			isChanged = true;
		}

		if (isChanged) {
			updateOwnerConfigurationFile();
		}
	}

	public void addOwnerProfile() {

		// TODO: validated ownerprofile attributes
		UserRegisterFormat owner = new UserRegisterFormat();
		owner.username = ownerName;
		try {
			owner.password = SensorActAPI.userProfile
					.getHashCode(ownerPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		owner.email = ownerEmail;

		UserProfileModel.deleteAll();
		SensorActAPI.userProfile.addUserProfile(owner, ownerKey);
	}

//	启动MQTTsub
	public class mqttSub {  
		  
	    public static final String HOST = "tcp://localhost:1883";  
	    public static final String TOPIC = "ICS";  
	    private MqttClient client;  
	    private MqttConnectOptions options;  
	    
	    private void start() {  
	        try {  
	            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存  
	            client = new MqttClient("tcp://127.0.0.1:1883", "pahomqttPublish1"); 
	            
	            // MQTT的连接设置  
	            options = new MqttConnectOptions();  
	            
	            // 设置回调  
	            client.setCallback(new PushCallback()); 
	            
	            MqttTopic topic = client.getTopic(TOPIC);  
	            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息  
//	            options.setWill(topic, "close".getBytes(), 2, true);  
	  
	            client.connect(options);  
	            
	            //订阅消息  
	            int[] Qos  = {0};  
	            String[] topic1 = {TOPIC}; 
	            
	            //订阅
	        	client.subscribe(topic1, Qos);
	                
	        } catch (Exception e) {  
	            System.out.println("Error");
	            e.printStackTrace();  

	        }  
	    }  
	    
	    //回调函数    
	    public class PushCallback implements MqttCallback {  
	    	  
	    	public void connectionLost(Throwable cause) {  
		        // 连接丢失后，一般在这里面进行重连  
		        System.out.println("连接断开，可以做重连");  
		    }    
		    public void deliveryComplete(IMqttDeliveryToken token) {  
		        System.out.println("deliveryComplete---------" + token.isComplete());  
		    }  
		  
		    public void messageArrived(String topic, MqttMessage message) throws Exception {  
		        // subscribe后得到的消息会执行到这里面  
		        System.out.println("接收消息主题 : " + topic);  
		        System.out.println("接收消息Qos : " + message.getQos());  
		        System.out.println("接收消息内容 : " + new String(message.getPayload()));
		        
		        //Esper
//		        myEsper();
		        Apple apple1 = new Apple();  
		        apple1.setId(1);  
		        apple1.setPrice(5);  
		        runtime.sendEvent(apple1);  

		        Apple apple2 = new Apple();  
		        apple2.setId(2);  
		        apple2.setPrice(2);  
		        runtime.sendEvent(apple2);  

		        Apple apple3 = new Apple();  
		        apple3.setId(3);  
		        apple3.setPrice(5);  
		        runtime.sendEvent(apple3);  
		        
		        //添加设备
		        SensorActAPI.deviceAdd.doProcess(new String(message.getPayload()));

		        
		    }   
	    } 
	    
	}  
	
	
//	Esper
	public class Apple  
	{  
	    private int id;  
	    private int price;  
	  
	    public int getId()  
	    {  
	        return id;  
	    }  
	  
	    public void setId(int id)  
	    {  
	        this.id = id;  
	    }  
	  
	    public int getPrice()  
	    {  
	        return price;  
	    }  
	  
	    public void setPrice(int price)  
	    {  
	        this.price = price;  
	    }  
	}  
	//监听事件
	public class AppleListener implements UpdateListener  
	{  
	    public void update(EventBean[] newEvents, EventBean[] oldEvents)  
	    {  
	        if (newEvents != null)  
	        {  
	            Double avg = (Double) newEvents[0].get("avg(price)");  
	            System.out.println("Average temperature of ICS is " + avg+" and newEvents length is"+newEvents.length);  
	        }  
	    }  
	  
	}  
	//esper 变量
	public EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();      
	public EPAdministrator admin = epService.getEPAdministrator();  
	public String product = Apple.class.getName();  
	public String epl = "select avg(price) from " + product + ".win:length_batch(3)";  
	public EPStatement state = admin.createEPL(epl);  
	public EPRuntime runtime = epService.getEPRuntime();  
	
	
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
	            return sb.toString();   
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
	
	public void doJob() {

		getOwnerConfiguration();
		verifyOwnerKeys();
		addOwnerProfile();
		
		//esper
		state.addListener(new AppleListener()); 
		
		//mqtt
		mqttSub client = new mqttSub();  
        client.start();

        //rhino
        String filename = System.getProperty("user.dir") + "/public/customscripts/custom1.js";
        JSExploration jsExploration = new JSExploration();   	
        Object result = jsExploration.runJavaScript(filename); 
        
		// Play.configuration.list(System.out);

	}

}
