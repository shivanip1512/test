/*
 * Created on Dec 31, 2003
 */
package com.cannontech.tags.test;

import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.tags.TagManager;

/**
 * @author aaron
 */
public class TagListener {

	public static void main(String[] args) throws Exception {

		while(true) {
			CTILogger.info("Starting Tag Dump");
			Iterator ptIter = TagManager.getInstance().getPointIDs().iterator();
			while(ptIter.hasNext()) {
				Integer ptID = (Integer) ptIter.next();
				Iterator tIter = TagManager.getInstance().getTags(ptID.intValue()).iterator();
				
				while(tIter.hasNext()) {
					CTILogger.info(tIter.next());
				}
			}
			CTILogger.info("Done Tag Dump");
			Thread.sleep(2000);
		}
	}
}
