//var swingNames = JavaImporter();    
//swingNames.importPackage(Packages.java.lang);   
//swingNames.importPackage(Packages.javacode);
//
//with (swingNames) {  
 
 System.out.println("### Here is MQTTsub.js ### \n");

 var hostname = "tcp://127.0.0.1:1883"
 // sub
 client = new MQTTsub(hostname,"ICS","subDevice1");  
 client.start();
 
 client2 = new MQTTsub(hostname,"Environment","subDevice2");  
 client2.start();
 
 client3 = new MQTTsub(hostname,"RaspberryPiInfo","subDevice3");  
 client3.start();

 
 
//}    
 