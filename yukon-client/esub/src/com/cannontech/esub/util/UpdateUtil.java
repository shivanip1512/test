package com.cannontech.esub.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.PointAttributes;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

/**
 * TODO:  find commonolities and merge with displayupdater
 * @author alauinger
 */
public class UpdateUtil {

	private static final String UNKNOWN_CHAR = "?";
	private static final int INVALID_POINTID = -1;
    
    public static String getLineElementString(int pointID) {
        String text = "";
        LitePoint lp = null;
        if(pointID != INVALID_POINTID){
            try {
                try{
                    lp = DaoFactory.getPointDao().getLitePoint(pointID);
                } catch (NotFoundException e ){
                    CTILogger.error("LineElement Error: pointid: " + pointID + " not found.");
                    return text;
                }

                DynamicDataSource dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");        
                
                boolean prev = false;
                
                if(lp.getPointType() == PointTypes.STATUS_POINT || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT)
                {
                    PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
            
                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
                        if( ls != null ) {          
                            text += ls.getStateRawState();
                            prev = true;
                        }
                    }
                }else
                {
                    PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
                    LiteState ls = pointService.getCurrentState(lp.getPointID());
                    if( ls != null ) {          
                        text += ls.getStateRawState();
                        prev = true;
                    }
                }
                
                if( !prev )
                    text = "?";
                
                if(text.length() == 1) { //workaround for bugin adobe svg geturl function!
                    text = " " + text;
                }   
            } catch (DynamicDataAccessException ddae) {
                CTILogger.error("LineElement Error: Unable to get dynamic data for point" + pointID);
            }
        }
        return text;
    }
	
	public static String getDynamicTextString(int pointID, int displayAttrib, YukonUserContext userContext, String referrer) {
        String text = "";
        LitePoint lp = null;

        /* Validate it's not a broken dynamic text: point != -1 and point really exists in db */
        if(pointID == INVALID_POINTID) {
            CTILogger.error("DynamicText Error: not attached to a real point.");
            return "BROKEN DYNAMIC TEXT";
        } else {
            try {
                lp = DaoFactory.getPointDao().getLitePoint(pointID);
            } catch (NotFoundException e) {
                String endMessage = referrer != null ? " on page: " + referrer : ".";
                CTILogger.error("DynamicText Error: pointid: " + pointID + " not found" + endMessage);
                return "BROKENT DYNAMIC TEXT";
            }
        }
        
        /* Seems to be a real point, update this dynamic text element. */
        try {
            DynamicDataSource dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");        
            DateFormattingService dateFormattingService = YukonSpringHook.getBean("dateFormattingService",DateFormattingService.class);
    		boolean prev = false;	
    		if( (displayAttrib & PointAttributes.VALUE) != 0 ) {
                PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
    
    			if (pData != null) {
    				DecimalFormat f = new DecimalFormat();
                    try {
                        LitePointUnit lpu = DaoFactory.getPointDao().getPointUnit(pointID);
        				if (lpu != null) {
        					f.setMaximumFractionDigits(lpu.getDecimalPlaces());
        					f.setMinimumFractionDigits(lpu.getDecimalPlaces());
        				} else {
        					// No point units, could be a status
        					f.setMaximumFractionDigits(0);
        					f.setMinimumFractionDigits(0);
        				}
                    }
                    catch(NotFoundException nfe) {
                        // OK, status points don't have this, for example
                    }
    
    				text = f.format(pData.getValue());
    				prev = true;
    			}
    		}
    							
    		if( (displayAttrib & PointAttributes.UOFM) != 0 ) {
    			if( prev ) 
    				text += " ";
        			
    			LiteUnitMeasure lum = DaoFactory.getUnitMeasureDao().getLiteUnitMeasureByPointID(pointID);
    			if( lum != null ) {
    				text += lum.getUnitMeasureName();
    				prev = true;
    			}            
    		}
    							
    		if( (displayAttrib & PointAttributes.NAME) != 0 ) {
    			if( prev ) 
    				text += " ";
        			
    			String name = DaoFactory.getPointDao().getPointName(pointID);
    			if(name != null) 
    				text += name;
    			else 
    				text += UNKNOWN_CHAR;
    			prev = true;                   
    		}
    							
    		if( (displayAttrib & PointAttributes.PAO) != 0 ) {          
    			if( prev ) 
    				text += " ";
    			
				text += DaoFactory.getPaoDao().getYukonPAOName(lp.getPaobjectID());
				prev = true;
    		}
    									
    		if( (displayAttrib & PointAttributes.LAST_UPDATE) != 0 ) {
                PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
    			if(pData != null ) {
    				if( prev ) 
    					text += " ";
    
    				Date lastUpdate = pData.getPointDataTimeStamp();
    				text += dateFormattingService.format(lastUpdate, DateFormattingService.DateFormatEnum.BOTH, userContext);
     				prev = true;
    			}
    		}	
    		
    		if( (displayAttrib & PointAttributes.LOW_LIMIT) != 0 ) {
    		    LitePointLimit lpl = DaoFactory.getPointDao().getPointLimit(pointID);
                LitePointUnit lpu = DaoFactory.getPointDao().getPointUnit(pointID);
    			
				DecimalFormat f = new DecimalFormat();
				f.setMaximumFractionDigits(lpu.getDecimalPlaces());
				f.setMinimumFractionDigits(lpu.getDecimalPlaces());
				text += f.format(lpl.getLowLimit());
				prev = true;
    		}
    		
    		if( (displayAttrib & PointAttributes.HIGH_LIMIT) != 0 ) {
    			LitePointLimit lpl = DaoFactory.getPointDao().getPointLimit(pointID);
                LitePointUnit lpu = DaoFactory.getPointDao().getPointUnit(pointID);
				DecimalFormat f = new DecimalFormat();
				f.setMaximumFractionDigits(lpu.getDecimalPlaces());
				f.setMinimumFractionDigits(lpu.getDecimalPlaces());
				text += f.format(lpl.getHighLimit());
				prev = true;
    		}
    		
    		if( (displayAttrib & PointAttributes.LIMIT_DURATION) != 0 ) {
    			// always convert seconds -> minutes!	
    			LitePointLimit lpl = DaoFactory.getPointDao().getPointLimit(pointID);
				int min = lpl.getLimitDuration() / 60;
				text += Integer.toString(min);		
				prev = true;	
    		}
    		
    		if( (displayAttrib & PointAttributes.MULTIPLIER) != 0 ) {
    		    double mult = DaoFactory.getPointDao().getPointMultiplier(lp);
                text += Double.toString(mult);
                prev = true;
    		}
    		
    		if( (displayAttrib & PointAttributes.DATA_OFFSET) != 0 ) {
		        int offset = DaoFactory.getPointDao().getPointDataOffset(lp);
                text += Integer.toString(offset);
                prev = true;
    		}
    		
    		if( (displayAttrib & PointAttributes.ALARM_TEXT) != 0 ) {
    			boolean foundOne = false;
    			Iterator<Signal> sigIter = dynamicDataSource.getSignals(pointID).iterator();			
    			while(sigIter.hasNext()) {
    				Signal sig = (Signal) sigIter.next();
    				if((sig.getTags() & Signal.TAG_UNACKNOWLEDGED_ALARM) != 0) {
    					if(!foundOne) {
    						text += sig.getDescription();						
    					}
    					else {
    						text += ", " + sig.getDescription();
    					}
    					foundOne = true;
    				}
    			}
    			
    			if(!foundOne) {
    				text += "NOT IN ALARM";
    			}
    			
    			prev = true;
    		}
    		
    		if( (displayAttrib & PointAttributes.STATE_TEXT) != 0 ) {
                if(lp.getPointType() == PointTypes.STATUS_POINT || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT){
                
                    PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
                    
                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
                        if( ls != null ) {
                            text += ls.getStateText();
                            prev = true;
                        }
                    }
                    
                }else {
                    
                    PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
                    LiteState ls = pointService.getCurrentState(lp.getLiteID());
                    if( ls != null ) {
                        text += ls.getStateText();
                        prev = true;
                    }
                }
            }	
            
            if( (displayAttrib & PointAttributes.CURRENT_STATE) != 0 ) {
                if(lp.getPointType() == PointTypes.STATUS_POINT  || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT) {
                    PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
                    
                    if (pData != null) {
                        LiteState ls = DaoFactory.getStateDao().findLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
                        if( ls != null ) {
                            text += ls.getStateRawState();
                            prev = true;
                        }
                    }
                } else {
                    PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
                    LiteState ls = pointService.getCurrentState(lp.getPointID());
                    if( ls != null ) {
                        text += ls.getStateRawState();
                        prev = true;
                    }
                }
            }
    					
    		if( !prev ) {
    			text = "?";
    		}
    		
    		if(text.length() == 1) { //workaround for bugin adobe svg geturl function!
    		    text = " " + text;
    		}	
        } catch(DynamicDataAccessException ddae) {
            /* Connection to dispatch may be down. */
            text = ddae.getMessage();
            CTILogger.error("DynamicText Error: Unable to get dynamic data for point" + pointID);
        }
        return text;        
    }

	public static String getStateImageName(int pointID, String referrer) {
	    if(pointID == -1) {
	        return Util.DEFAULT_IMAGE_NAME;
	    }
	    try{
    		LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointID);
            DynamicDataSource dynamicDataSource = 
                (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
            PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
            LiteYukonImage img;
            if (lp.getPointType() == PointTypes.STATUS_POINT)
            {
        		LiteState ls = DaoFactory.getStateDao().findLiteState(lp.getStateGroupID(), (int) pData.getValue());
        		img = DaoFactory.getYukonImageDao().getLiteYukonImage(ls.getImageID());
            }else 
            {
                PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
                LiteState ls = pointService.getCurrentState(pointID);
                img = DaoFactory.getYukonImageDao().getLiteYukonImage(ls.getImageID());
            }
    		return img.getImageName();
	    } catch (NotFoundException e){
	        CTILogger.error("StateImage error: pointid: " + pointID + " not found" + referrer != null ? " on page: " + referrer : "" + " ...using default image.");
	        return Util.DEFAULT_IMAGE_NAME;
	    }
	}	
	
	public static boolean isControllable(int pointID) {
        DynamicDataSource dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
		int tags = dynamicDataSource.getTags(pointID);
        
		return ((tags & Signal.TAG_ATTRIB_CONTROL_AVAILABLE) != 0) &&
				!((tags & Signal.MASK_ANY_CONTROL_DISABLE) != 0);
	}
}
