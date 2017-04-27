//mqtt
package javacode;

import java.io.FileReader;
import java.io.LineNumberReader;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;  

import javacode.*;


public class MQTTpub {

  MqttClient client;
  private String HOST;
  private String TOPIC;
  private String CONTENT;
  private String ClientId;
//  public String JSpath = System.getProperty("user.dir") + "/public/customscripts/toDevice.js";
  
  public MQTTpub(String host, String topic, String content, String clientid) {
	HOST=host;
  	TOPIC=topic;
  	CONTENT=content;
  	ClientId = clientid;
  }
  
  public void start() {
    try {
      client = new MqttClient(HOST, ClientId);
      client.connect();
      MqttMessage message = new MqttMessage();
//      String content = new API().getJsContent(JSpath);
      message.setPayload(CONTENT.getBytes());
      
      while(true){
    	  client.publish(TOPIC, message);
    	  System.out.println("\n-- Server Pub: "+TOPIC+"\n"+message.toString());
    	  try{
    		  	Thread.sleep(5000); 		  	
    	  }catch(InterruptedException e){
    			System.err.println("Interrupted");
    	  }
      }     
//      client.disconnect();       
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }

}