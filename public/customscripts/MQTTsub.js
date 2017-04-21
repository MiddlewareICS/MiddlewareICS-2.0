//var swingNames = JavaImporter();    
//swingNames.importPackage(Packages.java.lang);   
//swingNames.importPackage(Packages.javacode);
//
//with (swingNames) {   
 System.out.println("### Here is MQTTsub.js ### \n");

 // user does
 client = new MQTTsub("tcp://localhost:1883","ICS");  
 client.start();
 
 
 pubClient = new MQTTpub();
 pubClient.start();
//}    
 