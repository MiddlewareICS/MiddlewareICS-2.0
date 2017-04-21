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
  public String JSpath = System.getProperty("user.dir") + "/public/customscripts/toDevice.js";
  
  public MQTTpub(String host, String topic) {
	HOST=host;
  	TOPIC=topic;
  }
  
  public void start() {
    try {
      client = new MqttClient("tcp://127.0.0.1:1883", "pahomqttPublish2");
      client.connect();
      MqttMessage message = new MqttMessage();
      String content = new API().getJsContent(JSpath);
      message.setPayload(content.getBytes());
      
      while(true){
    	  client.publish("toDevice", message);
    	  System.out.println("-- Server Pub: "+message.toString());
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