/*
 * Created on Aug 7, 2003
 */
package com.cannontech.datagenerator;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Replays historical point data from a binary file created with PointChangeRecorder
 * @author aaron
 */
public class PointChangePlayer2 {

	private static final String USAGE = "Replays point history from a binary file written by com.cannontech.datagenerator.PointChangeRecorder.\nPointChangePlayer2 file [idoffset]";
	
	//Points with these offset will have the point data load profile flag set
	private static final int[] loadProfileOffsets =
	{
		15, 35, 55, 62, 64, 66, 72, 74, 76 
	};
	
	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.out.println(USAGE);
			System.exit(-1);	
		}

		int idOffset = Integer.parseInt(args[1]);
		PointData[] pData = loadChanges(args[0], idOffset);
				
		CTILogger.info("Loaded " + pData.length + " point changes");
		
		while(true) {		
			int cur = 0;
			long recordedBase = calcBase(pData[0].getPointDataTimeStamp().getTime());
			long realBase = ServletUtil.getToday().getTime();
			long now = System.currentTimeMillis();

			//find our starting point in the first day of the recorded point data
			while( (pData[cur].getPointDataTimeStamp().getTime() - recordedBase) < (now - realBase)) cur++;
		
			while(++cur != pData.length) {			
				sleepUntil(realBase + (pData[cur].getPointDataTimeStamp().getTime() - recordedBase));
			
				int count = 0;			
				now = System.currentTimeMillis();
				while( cur != pData.length &&
						(pData[cur].getPointDataTimeStamp().getTime() - recordedBase) <= (now - realBase)) {
					writeOut(pData[cur], now);
					count++;
					cur++;
				}
			
				CTILogger.info("Sent " + count + " point data messages to dispatch");
			}
		}
	}
	
	private static void writeOut(PointData pd, long ts) {
		
		boolean lpFlag = setLPFlag(pd.getId());
				
		PointData msg = new PointData();
		msg.setId(pd.getId());
		msg.setTime(new Date(ts));
		msg.setTimeStamp(new Date(ts));
		msg.setPointQuality(pd.getPointQuality());
		msg.setValue(pd.getValue());
		msg.setType(pd.getType());	
		
		if(lpFlag)	
			msg.setTags(0x00008000);
			
		CTILogger.info("id: " + msg.getId() + " val: " + msg.getValue() + " timestamp: " + msg.getPointDataTimeStamp() + " lp: " + lpFlag);
		ConnPool.getInstance().getDefDispatchConn().write(msg);
	}
	
	private static void sleepUntil(long ts) {
		try {
			long sleepFor = Math.max(ts - System.currentTimeMillis(), 0);
			CTILogger.info("Sleeping for " + sleepFor + " millis");
			Thread.sleep(sleepFor);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
		return;
	}
	private static long calcBase(long ts) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(ts);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		return cal.getTimeInMillis();
	}
		
	private static PointData[] loadChanges(String fileName, int idOffset) throws Exception {
		
		ArrayList changeList = new ArrayList();
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		try {
	
		while(true) {				
			int id = dis.readInt() + idOffset;
			int ts = (int) (dis.readLong() / 1000L);
			int quality = dis.readInt();
			double value = dis.readDouble();
						
			LitePoint lp = DaoFactory.getPointDao().getLitePoint(id);
			if(lp == null) {
				CTILogger.info("Point id: " + id + " not found, skipping");
				continue;
			}
			
			if(id == -1)
				break;
			
			
			
			PointData pData = new PointData();
			
			pData.setId(id);
			pData.setTime(new Date((long)ts * 1000L));
			pData.setPointQuality(PointQuality.getPointQuality(quality));
			pData.setValue(value);
			
			pData.setType(lp.getPointType());
		
			changeList.add(pData);
		}	
		}
		catch(EOFException e) {
			
		}
		
		dis.close();
		PointData[] retVal = new PointData[changeList.size()];
		changeList.toArray(retVal);
		return retVal;
	}
	
	private static boolean setLPFlag(int pointID) {
		LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointID);
		if(lp == null) {
			CTILogger.warn("couldn't find point id: " + lp.getLiteID());
			return false;
		}
		LiteYukonPAObject paObj = DaoFactory.getPaoDao().getLiteYukonPAO(lp.getPaobjectID());
		if(paObj == null) {
			CTILogger.warn("couldn't find paoobject id: " + lp.getPaobjectID());
			return false;
		}
		
		return  paObj.getPaoType().getPaoClass() == PaoClass.METER &&
				Arrays.binarySearch(loadProfileOffsets, lp.getPointOffset()) != -1;
	}
}
