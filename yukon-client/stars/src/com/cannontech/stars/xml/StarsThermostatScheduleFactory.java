package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsThermostatSchedule;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsThermostatScheduleFactory {
	
	public static StarsThermostatSchedule newStarsThermostatSchedule(StarsThermostatSchedule sched) {
		StarsThermostatSchedule newSched = new StarsThermostatSchedule();
		newSched.setDay( sched.getDay() );
		newSched.setTime1( sched.getTime1() );
		newSched.setTemperature1( sched.getTemperature1() );
		newSched.setTime2( sched.getTime2() );
		newSched.setTemperature2( sched.getTemperature2() );
		newSched.setTime3( sched.getTime3() );
		newSched.setTemperature3( sched.getTemperature3() );
		newSched.setTime4( sched.getTime4() );
		newSched.setTemperature4( sched.getTemperature4() );
		
		return newSched;
	}

}
