package com.ripple.datasource.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TimeTools
{
	private static Logger logger = LogManager.getLogger( TimeTools.class );
	
	public static String getPeriodOfTime(long beginTime,long endTime)
	{
		if( beginTime == endTime )
		{
			return "0 毫秒";
		}else if( beginTime == endTime )
		{
			return "计时有误";
		}
		long periodOfTime = endTime - beginTime;
		long hourTime = periodOfTime/(60*60*1000);
		long minuteTime = ( periodOfTime%( 60*60*1000 ) )/(60*1000);
		long secondTime = (( periodOfTime%( 60*60*1000 ) )%(60*1000) )/1000;
		long millisecondTime = (( periodOfTime%( 60*60*1000 ) )%(60*1000) )%1000;
		
		StringBuffer timeStr = new StringBuffer();
		if( hourTime > 0 )
		{
			timeStr.append( hourTime ).append( " 时" );
		}
		if( minuteTime > 0 )
		{
			timeStr.append( minuteTime ).append( " 分" );
		}
		if( secondTime > 0 )
		{
			timeStr.append( secondTime ).append( " 秒" );
		}
		if( millisecondTime > 0 )
		{
			timeStr.append( millisecondTime ).append( " 毫秒" );
		}
		return timeStr.toString();
	}
}
