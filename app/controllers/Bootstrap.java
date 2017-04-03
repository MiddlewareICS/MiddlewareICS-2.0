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
	            options.setWill(topic, "close".getBytes(), 2, true);  
	  
	            client.connect(options);  
	            
	            //订阅消息  
	            int[] Qos  = {1};  
	            String[] topic1 = {TOPIC}; 
	            
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
		        
		        System.out.println("---自动添加设备信息");
		        SensorActAPI.deviceAdd.doProcess(new String(message.getPayload()));
//				System.out.println(new String(message.getPayload()));
		        
		      //数据库
				/*
		        try{	        	
		        	Mongo mongo = new Mongo("127.0.0.1",27017);  
		            DB db =mongo.getDB("study"); 
		            DBCollection users = db.getCollection("person");
		            
		            DBObject user = new BasicDBObject();  
		            user.put("name", "jimmy");  
		            user.put("age", "34");  
		            DBObject address = new BasicDBObject();  
		            address.put("city", "bj");  
		            address.put("street", "bq road");  
		            address.put("mail", "ufpark 68#");   
		            user.put("address", address);  
	  
		            users.insert(user);  
		            
		            // 从集合中查询数据，我们就查询一条，调用findOne即可  
//		            DBObject dbUser = users.findOne();  
//		            System.out.println("插入数据库：");
//		            System.out.println("name" + " : "  + dbUser.get("name") );  
//		            System.out.println("age" + " : "  + dbUser.get("age") );  
//		            DBObject dbAddress = (DBObject)user.get("address");  
//		            System.out.println("city" + " : "  + dbAddress.get("city") );  
//		            System.out.println("street" + " : "  + dbAddress.get("street") );  
//		            System.out.println("mail" + " : "  + dbAddress.get("mail") );  
		            
		        }catch(Exception e){
		        	System.out.println("DB error"); 
		        }
		        */
		        
		    }   
	    } 
	    
	}  
	
	public void doJob() {

		getOwnerConfiguration();
		verifyOwnerKeys();
		addOwnerProfile();
		
		mqttSub client = new mqttSub();  
        client.start();
        
//        System.out.println("aaaa");

		// Play.configuration.list(System.out);

	}

}
