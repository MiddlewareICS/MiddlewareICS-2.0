//mqtt
package javacode;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import edu.pc3.sensoract.vpds.api.SensorActAPI;  

import javacode.*;
import controllers.Bootstrap;

//mqtt sub
	public class MQTTsub {  
		  
//	    public static final String HOST = "tcp://localhost:1883";  
//	    public static final String TOPIC = "ICS"; 
		private String HOST;
		private String TOPIC;
	    private MqttClient client;  
	    private MqttConnectOptions options;  

	    public MQTTsub(String host, String topic){
	    	HOST=host;
	    	TOPIC=topic;
	    }
	    
	    public void start() {  
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
		        // System.out.println("\n-- 接收信息主题 : " + topic);  
//		        System.out.println("接收消息Qos : " + message.getQos());  
		        System.out.println("\n-- Server Sub : " + topic + new String(message.getPayload()));
		        
		        
		        //Esper
//		        myEsper();
		        Temperature temperature1 = new Temperature();  
		        temperature1.setId(1);  
		        temperature1.setTemperature(20); 
		        Bootstrap.myEsper.runtime.sendEvent(temperature1);  

		        Temperature temperature2 = new Temperature();   
		        temperature2.setId(2);  
		        temperature2.setTemperature(25);  
		        Bootstrap.myEsper.runtime.sendEvent(temperature2);  

		        Temperature temperature3 = new Temperature();   
		        temperature3.setId(3);  
		        temperature3.setTemperature(15);  
		        Bootstrap.myEsper.runtime.sendEvent(temperature3); 
		        
		        //添加设备
		        SensorActAPI.deviceAdd.doProcess(new String(message.getPayload()));
		        
		    }   
	    } 
	    
	}  