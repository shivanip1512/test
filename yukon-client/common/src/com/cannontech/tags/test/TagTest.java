/*
 * Created on Dec 30, 2003
 */
package com.cannontech.tags.test;

import java.util.Iterator;
import java.util.Set;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ThreadPool;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.tags.Tag;
import com.cannontech.tags.TagManager;

/**
 * @author aaron
  */
public class TagTest implements Runnable {

	class TagRunner implements Runnable {
		int pointid;
		TagRunner(int id) {
			pointid=id;
		}
		
		public void run() {
			try {
			
			TagManager tm = TagManager.getInstance();
			Thread.sleep(7000);
			CTILogger.info("Creating tag 1 for point id: " + pointid);
			tm.createTag(pointid, -1, CtiUtilities.getUserName(), "Tag 1 by TagTest for id" + pointid, "refstrhere", "forstrhere");
			
			Thread.sleep(7000);
			
			CTILogger.info("Creating tag 2 for point id: " + pointid);
			tm.createTag(pointid, -1, CtiUtilities.getUserName(), "Tag 2 by TagTest for id" + pointid, "refstrhere", "forstrhere");

			Set tagSet = tm.getTags(pointid);
			if(tagSet.size() != 2) {
				CTILogger.error("Point id " + pointid + " has " + tagSet.size() + " tags on it when it should have exactly 2");
			}

			Iterator iter = tagSet.iterator();
			while(iter.hasNext()) {
				Thread.sleep(7000);
				Tag t = (Tag) iter.next();
				t.setDescriptionStr("Tag " + t.getInstanceID() + " updated by TagTest for id " + pointid);
				CTILogger.info("Updating tag " + t.getInstanceID() + " for point id: " + pointid);
				tm.updateTag(t, CtiUtilities.getUserName());
				
			}
			
			tagSet = tm.getTags(pointid);
			iter = tagSet.iterator();
			
			while(iter.hasNext()) {
				Thread.sleep(7000);
				Tag t = (Tag) iter.next();
				t.setDescriptionStr("Tag " + t.getInstanceID() + " removed by TagTest for id " + pointid);
				CTILogger.info("Removing tag " + t.getInstanceID() + " for point id: " + pointid);
				tm.removeTag(t, CtiUtilities.getUserName());
			}
			

			tagSet = tm.getTags(pointid);
			if(tagSet.size() > 0) {
				CTILogger.error("Point id " + pointid + " has " + tagSet.size() + " tags on it when it should have exactly none");
			}
			else {
				CTILogger.info("Removed all tags from point id: " + pointid);
			}
			
			}
			catch(Exception e) {
				CTILogger.error("", e);
			}
		} 
	}
	public static void main(String[] args) throws InterruptedException{
while(true) {

		TagTest tt = new TagTest();
		tt.run();
		Thread.sleep(5000);
}

	}
	
	public void run() {
		try {
		
		ThreadPool thrPool = new ThreadPool(5);		
		Iterator i = DefaultDatabaseCache.getInstance().getAllPoints().iterator();
		while(i.hasNext()) {
			LitePoint lp = (LitePoint) i.next();
			thrPool.enqueueRunnable(new TagRunner(lp.getPointID()));
			Thread.sleep(500);	
		}
		Thread poolThr = new Thread(thrPool);
		poolThr.setDaemon(true);
		poolThr.start();
		
		Thread.sleep(0);
		
		thrPool.stop();
		thrPool.join();
		
		Thread.sleep(10000);
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
