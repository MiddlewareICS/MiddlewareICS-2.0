/*
 * Copyright (c) 2012, Indraprastha Institute of Information Technology,
 * Delhi (IIIT-D) and The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. Neither the names of the Indraprastha Institute of Information
 *    Technology, Delhi and the University of California nor the names
 *    of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE IIIT-D, THE REGENTS, AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE IIITD-D, THE REGENTS
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */
/*
 * Name: LuaScriptTasklet.java
 * Project: SensorAct-VPDS
 * Version: 1.0
 * Date: 2012-07-20
 * Author: Pandarasamy Arjunan
 */

package edu.pc3.sensoract.vpds.tasklet;

import java.util.Date;
import java.util.Enumeration;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;

import edu.pc3.sensoract.vpds.model.TaskletModel;

//@org.quartz.DisallowConcurrentExecution
@org.quartz.PersistJobDataAfterExecution
public class LuaScriptTasklet implements InterruptableJob {

	private static ScriptEngine luaEngine = null;
	//private static TaskletModel
	
	// private static LuaJavaMapper luaJavaMapper = new LuaJavaMapper();
	//private String luaScript = null;

	public static final Logger LOG = LuaToJavaFunctionMapper.LOG;
	
	public static String PARAM_TIME = "PARAM_TIME";
	public static String PARAM_VALUE = "PARAM_VALUE";
	
	public static String TASKLETINFO = "TASKLETINFO";
	public static String TASKLET_NAME = "TASKLET_NAME";
	public static String VPDS = "VPDS";
	
	
	static {
	    for (Enumeration appenders=LOG.getAllAppenders(); appenders.hasMoreElements(); )  {
	        Appender appender = (Appender) appenders.nextElement();
	        System.out.println(appender.getName());
	    }
	}
	
	static {
		luaEngine = new ScriptEngineManager().getEngineByName("Lua");
		//LuaToJavaFunctionMapper luaToJavaFunctionMapper = new LuaToJavaFunctionMapper();
		//luaEngine.put(VPDS, luaToJavaFunctionMapper);
	}

	/*
	public String getLuaScript() {
		return luaScript;
	}

	public void setLuaScript(String luaScript) {
		this.luaScript = luaScript;
	}
*/
	
	public void execute(JobExecutionContext context) {
		
		long t1 = new Date().getTime();
	
		JobKey jobKey = context.getJobDetail().getKey();
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();		
		TaskletModel tasklet = (TaskletModel) dataMap.get(TASKLETINFO);
		
		//System.out.println("data map.......................");
		//for(String k : dataMap.getKeys()) {
			//System.out.println(k + " " + dataMap.get(k));
		//}
		
		// Get the params for event based tasklet
		String param_time  = (String) dataMap.get(PARAM_TIME);
		String param_value  = (String) dataMap.get(PARAM_VALUE);
		
		//TODO: validate the tasklet
		
		LOG.info(jobKey.toString() + " started..." );
		
		if(param_time != null && param_value != null) {
			LOG.info(jobKey.toString() + " parameters " + param_time + " " + param_value);	
		}
		
		try {
			//ScriptEngine luaEngineLocal = new ScriptEngineManager().getEngineByName("Lua");
			
			ScriptEngine luaEngineLocal = new ScriptEngineManager().getEngineByName("Lua");			
			// check which scripting language
			if(tasklet.execute.startsWith("# python") || tasklet.execute.startsWith("#python")) {
				luaEngineLocal = new ScriptEngineManager().getEngineByName("python");
				LOG.info(jobKey.toString() + " python script identified..." );
			}
			
			LuaToJavaFunctionMapper luaToJavaFunctionMapper = new LuaToJavaFunctionMapper(context);
			//luaEngineLocal.put(VPDS, luaToJavaFunctionMapper);
			
			// Object email = dataMap.get("email");

			// System.out.println("****before : " + t1);
			// System.out.println("waiting for 10secs");
			// Thread.sleep(10000);

			// luaEngine = new ScriptEngineManager().getEngineByName("Lua");
			// long t2 = new Date().getTime();
			// System.out.print("Lua : " + (t2 - t1));

			ScriptContext newContext = new SimpleScriptContext();
			Bindings newScope = newContext
					.getBindings(ScriptContext.ENGINE_SCOPE);

			// LuaToJavaFunctionMapper luaToJavaFunctionMapper = new
			// LuaToJavaFunctionMapper(context);
			// newScope.put("PDS", luaToJavaFunctionMapper);
			// luaEngine.put("PDS", LuaToJavaFunctionMapper.class);

			// luaEngine.put("PDS", LuaJavaMapper.class);
			// luaEngine.put("email", email);
			
			newScope.put(TASKLET_NAME, jobKey.toString());
			
			newScope.put(VPDS, luaToJavaFunctionMapper);
			
			newScope.putAll(tasklet.input);
			newScope.putAll(tasklet.param);
			
			//export the params received from event based tasklet
			newScope.put(PARAM_TIME, param_time);
			newScope.put(PARAM_VALUE, param_value);
			
			/*
			Set keys = tasklet.input.keySet();			
			for (int i = 0; i < keys.length; ++i) {
				// System.out.println(keys[i] + ":---------------:" +
				// dataMap.get(keys[i]));
				newScope.put(keys[i], dataMap.get(keys[i]));
				//System.out.println("Key " + keys[i] + "Value: " + dataMap.get(keys[i]));
			}
			*/
			
			long t3 = new Date().getTime();

			int ccount = 0;
			try {
				ccount = TaskletScheduler.scheduler.getCurrentlyExecutingJobs()
						.size();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			long e1 = new Date().getTime();
			luaEngineLocal.eval(tasklet.execute, newScope);			
			long e2 = new Date().getTime();
			// System.out.print(" eval : " + (e2 - e1));
			

			// System.out.println("e1:" + e1 + " e2:"+e2);
			// System.out.println("script done..");

			// Double val = (Double) luaEngine.get("val");
			// System.out.println("val : " + val);

			// dataMap.put("val", val);

			// System.out.println("****after : " + t2);

			// System.out.println(key.getName() + " " + (t3 - t1)
			// + ", " + (e2 - e1)
			// + ", " + (e2 - t1)
			// + ", " + ccount
			// + ",   " + val);

//			SensorActLogger.info(jobKey.getName() + " export: " + (t3 - t1)
	//				+ " lua: " + (e2 - e1) + " total: " + (e2 - t1)
		//			+ " #threads: " + ccount);
			// System.out.println( key.getName() + ": " + (e2 - t1) + " " +
			// ccount);
			//TaskletScheduler.updateElapsed((e2 - t1));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error while running lua script for *********** " + jobKey.getName());
			LOG.info(jobKey.toString() + " execute " + e.fillInStackTrace() );
			e.printStackTrace();
		}		
		LOG.info(jobKey.toString() + " ended...." );
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// TODO Auto-generated method stub
		System.out.println("Interrupted..");
	}

}
