package javacode;


//Esper
import com.espertech.esper.client.EPAdministrator;  
import com.espertech.esper.client.EPRuntime;  
import com.espertech.esper.client.EPServiceProvider;  
import com.espertech.esper.client.EPServiceProviderManager;  
import com.espertech.esper.client.EPStatement;  
import com.espertech.esper.client.EventBean;  
import com.espertech.esper.client.UpdateListener;  




public class CEPEsper
{
	
//	public String className;  
//	public static String epl ; 
////	构造函数
	

	public static EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();      
	public static EPAdministrator admin = epService.getEPAdministrator();  
	public static EPStatement state ; 
	public static  EPRuntime runtime = epService.getEPRuntime();  
	
//	构造函数
	public CEPEsper( String EsperL)
	{
		state = admin.createEPL(EsperL);
	}	
	
	
 
	//监听事件
	public class TemperatureListener implements UpdateListener  
	{  
	    public void update(EventBean[] newEvents, EventBean[] oldEvents)  
	    {  
	        if (newEvents != null)  
	        {  
	            Double avg = (Double) newEvents[0].get("avg(temperature)");  
	            System.out.println("\n@@  CEP:Average temperature of ICS is " + avg+" and newEvents length is"+newEvents.length);  
	        }  
	    }  
	  
	}
	public TemperatureListener listener = new TemperatureListener();

}




