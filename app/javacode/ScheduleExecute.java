package javacode;

import org.quartz.Job;
import org.quartz.JobDataMap;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduleExecute implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
//		JobDataMap
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		
		String path = (String) jobDataMap.get("Script");
		System.out.println(path + "\n");
		
	}

}