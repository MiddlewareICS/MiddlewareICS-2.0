//var swingNames = JavaImporter();    
//swingNames.importPackage(Packages.java.lang);   
//swingNames.importPackage(Packages.javacode);
//
//with (swingNames) {  
 
 System.out.println("### Here is MQTTsub.js ### \n");

 
 // user does
 client = new MQTTsub("tcp://localhost:1883","ICS");  
 client.start();
 
 
 var JSpath = System.getProperty("user.dir") + "/public/customscripts/toDevice.js";
 var API = new API();
 var content = API.getJsContent(JSpath);
 
 pubClient = new MQTTpub("tcp://localhost:1883","toDevice",content);
 pubClient.start();
//}    
 