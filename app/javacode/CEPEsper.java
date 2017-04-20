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
//	public CEPEsper( String EsperL)
//	{
////		className = ClassName;
//		epl = EsperL;
//	}

	public static EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();      
	public static EPAdministrator admin = epService.getEPAdministrator();  
	public static String product = Temperature.class.getName();  
	public static String epl = "select avg(temperature) from " + product + ".win:length_batch(3)";  	 
	public static EPStatement state = admin.createEPL(epl);  
	public static  EPRuntime runtime = epService.getEPRuntime();  
	
 
	//监听事件
	public class TemperatureListener implements UpdateListener  
	{  
	    public void update(EventBean[] newEvents, EventBean[] oldEvents)  
	    {  
	        if (newEvents != null)  
	        {  
	            Double avg = (Double) newEvents[0].get("avg(temperature)");  
	            System.out.println("Average temperature of ICS is " + avg+" and newEvents length is"+newEvents.length);  
	        }  
	    }  
	  
	}
	public TemperatureListener listener = new TemperatureListener();

}




