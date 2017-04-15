//mqtt
package javacode;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;  
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;  



public class MQTTpub {

  MqttClient client;
  
  public MQTTpub() {}
  
  public void start() {
    try {
      client = new MqttClient("tcp://127.0.0.1:1883", "pahomqttPublish1");
      client.connect();
      MqttMessage message = new MqttMessage();
      message.setPayload("A single message".getBytes());
      
      while(true){
    	  client.publish("ICS", message);
    	  System.out.println("--Pub: "+message.toString());
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