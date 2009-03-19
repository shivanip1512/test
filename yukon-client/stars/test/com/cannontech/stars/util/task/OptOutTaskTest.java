package com.cannontech.stars.util.task;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;

public class OptOutTaskTest {

	private OptOutTask optOutTask;
	
    @Before
    public void setup() {
        optOutTask = new OptOutTask();
    }
	
	@Test
	public void testCancelOptOutEvent() throws Throwable {
		
		// Test with null event
		try {
			optOutTask.cancelOptOutEvent(new LiteStarsLMHardware(), null, new LiteYukonUser());
		} catch (IllegalArgumentException e) {
			// pass - null event is not allowed
		}
	}
	
}
