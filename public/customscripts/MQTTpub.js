 


 System.out.println("### Here is MQTTpub.js ### \n");
 
 
 
 //pub 
//pub scripts
var JSpath = System.getProperty("user.dir") + "/public/customscripts/toDevice.js";
var API = new API();
var content = API.getJsContent(JSpath);
pubClient = new MQTTpub("tcp://127.0.0.1:1883","toDevice",content,"pubDeviceScripts1");
pubClient.start();

