//var swingNames = JavaImporter();    
//swingNames.importPackage(Packages.java.lang);   
//swingNames.importPackage(Packages.javacode);
//
//with (swingNames) {  
 
 System.out.println("### Here is MQTTsub.js ### \n");

 
 // sub
 client = new MQTTsub("tcp://192.168.199.170:1883","ICS","subDevice1");  
 client.start();
 
 client = new MQTTsub("tcp://192.168.199.170:1883","Environment","subDevice2");  
 client.start();
 

 
 
//}    
 