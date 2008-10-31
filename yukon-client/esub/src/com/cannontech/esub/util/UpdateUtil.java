package com.cannontech.esub.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

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
    
    public static String getLineElementString(int pointID) {
        String text = "";
        try {
            DynamicDataSource dynamicDataSource = (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");        
            
            boolean prev = false;
            
            LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointID);
            
            if(lp.getPointType() == PointTypes.STATUS_POINT || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT)
            {
                PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
        
                if (pData != null) {
                    LiteState ls = DaoFactory.getStateDao().getLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
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
        } 
        catch(NotFoundException nfe) {
            text = nfe.getMessage();
        }
        catch(DynamicDataAccessException ddae) {
            text = ddae.getMessage();
        }
        return text;
    }
	
	public static String getDynamicTextString(int pointID, int displayAttrib, YukonUserContext userContext) {
        String text = "";
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
        			
    			String name = null;
    			try {
    			    name = DaoFactory.getPointDao().getPointName(pointID);
    			}catch(NotFoundException nfe) {
    			    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
    			}
    			if(name != null) 
    				text += name;
    			else 
    				text += UNKNOWN_CHAR;
    			prev = true;                   
    		}
    							
    		if( (displayAttrib & PointAttributes.PAO) != 0 ) {          
    			if( prev ) 
    				text += " ";
    			
    			LitePoint lp = null;
    			//find the pao for this point
    			try {
    			    lp = DaoFactory.getPointDao().getLitePoint(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
    			if(lp != null) {
    				text += DaoFactory.getPaoDao().getYukonPAOName(lp.getPaobjectID());
    				prev = true;
    			}else {
    			    text += "InvalidPointId";
    			    prev = true;
    			}
    		}
    									
    		if( (displayAttrib & PointAttributes.LAST_UPDATE) != 0 ) {
                PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
    			if(pData != null ) {
    				if( prev ) 
    					text += " ";
    
    				Date lastUpdate = pData.getPointDataTimeStamp();
    				text += dateFormattingService.formatDate(lastUpdate, DateFormattingService.DateFormatEnum.BOTH, userContext);
     				prev = true;
    			}
    		}	
    		
    		if( (displayAttrib & PointAttributes.LOW_LIMIT) != 0 ) {
    		    LitePointLimit lpl = null;
                LitePointUnit lpu = null;
    		    try {
    		        lpl = DaoFactory.getPointDao().getPointLimit(pointID);
                    lpu = DaoFactory.getPointDao().getPointUnit(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
    			
    			if( lpl != null && lpu != null) {			
    				DecimalFormat f = new DecimalFormat();
    				f.setMaximumFractionDigits(lpu.getDecimalPlaces());
    				f.setMinimumFractionDigits(lpu.getDecimalPlaces());
    				text += f.format(lpl.getLowLimit());
    				prev = true;
    			}else {
    			    text+= "InvalidPointId";
    			    prev = true;
    			}
    		}
    		
    		if( (displayAttrib & PointAttributes.HIGH_LIMIT) != 0 ) {
    			LitePointLimit lpl = null;
                LitePointUnit lpu = null;
                try {
                    lpl = DaoFactory.getPointDao().getPointLimit(pointID);
                    lpu = DaoFactory.getPointDao().getPointUnit(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
    			if( lpl != null && lpu != null) {
    				DecimalFormat f = new DecimalFormat();
    				f.setMaximumFractionDigits(lpu.getDecimalPlaces());
    				f.setMinimumFractionDigits(lpu.getDecimalPlaces());
    				text += f.format(lpl.getHighLimit());
    				prev = true;
    			}else {
                    text+= "InvalidPointId";
                    prev = true;
                }
    		}
    		
    		if( (displayAttrib & PointAttributes.LIMIT_DURATION) != 0 ) {	
    			// always convert seconds -> minutes!	
    			LitePointLimit lpl = null;
                try {
                    lpl = DaoFactory.getPointDao().getPointLimit(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
    			if( lpl != null ) {
    				int min = lpl.getLimitDuration() / 60;
    				text += Integer.toString(min);		
    				prev = true;	
    			}else {
                    text+= "InvalidPointId";
                    prev = true;
                }
    		}
    		
    		if( (displayAttrib & PointAttributes.MULTIPLIER) != 0 ) {
    		    double mult = -1.0;
    		    try {
    		        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(pointID);
    		        mult = DaoFactory.getPointDao().getPointMultiplier(litePoint);
    		    }catch(NotFoundException e) {
    		        CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", e);
    		    }
    		    if(mult > -1.0) {
                    text += Double.toString(mult);
                    prev = true;
    		    }else {
    		        text += "InvalidPointId";
    		        prev = true;
    		    }
    		}
    		
    		if( (displayAttrib & PointAttributes.DATA_OFFSET) != 0 ) {
    		    int offset = -1;
    		    try {
    		        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(pointID);
    		        offset = DaoFactory.getPointDao().getPointDataOffset(litePoint);
    		    }catch(NotFoundException e ) {
    		        CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", e);
    		    }
    		    if(offset > -1) {
                    text += Integer.toString(offset);
                    prev = true;
    		    }else {
    		        text += "InvalidPointId";
    		        prev = true;
    		    }
    		}
    		
    		if( (displayAttrib & PointAttributes.ALARM_TEXT) != 0 ) {
    			boolean foundOne = false;
    			Iterator sigIter = dynamicDataSource.getSignals(pointID).iterator();			
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
    		    LitePoint lp = null;
    		    try {
    		        lp = DaoFactory.getPointDao().getLitePoint(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
                if(lp != null) {
                    if(lp.getPointType() == PointTypes.STATUS_POINT || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT){
                    
                        PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
                        
                        if (pData != null) {
                            LiteState ls = DaoFactory.getStateDao().getLiteState(lp.getStateGroupID(), (int) pData.getValue()); 
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
                }else {
                    text += "InvalidPointId";
                    prev = true;
                }
            }	
            
            if( (displayAttrib & PointAttributes.CURRENT_STATE) != 0 ) {
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(pointID);
                }catch(NotFoundException nfe) {
                    CTILogger.error("The point (pointId:"+ pointID + ") might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    if(litePoint.getPointType() == PointTypes.STATUS_POINT  || litePoint.getPointType() == PointTypes.CALCULATED_STATUS_POINT) {
                        PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
                
                        if (pData != null) {
                            LiteState ls = DaoFactory.getStateDao().getLiteState(litePoint.getStateGroupID(), (int) pData.getValue()); 
                            if( ls != null ) {          
                                text += ls.getStateRawState();
                                prev = true;
                            }
                        }
                    }else
                    {
                        PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
                        LiteState ls = pointService.getCurrentState(litePoint.getPointID());
                        if( ls != null ) {          
                            text += ls.getStateRawState();
                            prev = true;
                        }
                    }
                }else {
                    text +="0";
                    prev = true;
                }
            }
    					
    		if( !prev )
    			text = "?";
    		
    		if(text.length() == 1) { //workaround for bugin adobe svg geturl function!
    		    text = " " + text;
    		}	
        } 
        catch(NotFoundException nfe) {
            text = nfe.getMessage();
        }
        catch(DynamicDataAccessException ddae) {
            text = ddae.getMessage();
        }
        return text;        
}

	public static String getStateImageName(int pointID) {
		LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointID);
        DynamicDataSource dynamicDataSource = 
            (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
        PointValueHolder pData = dynamicDataSource.getPointValue(pointID);
        LiteYukonImage img;
        if (lp.getPointType() == PointTypes.STATUS_POINT)
        {
    		LiteState ls = DaoFactory.getStateDao().getLiteState(lp.getStateGroupID(), (int) pData.getValue());
    		img = DaoFactory.getYukonImageDao().getLiteYukonImage(ls.getImageID());
        }else 
        {
            PointService pointService = YukonSpringHook.getBean("pointService", PointService.class);
            LiteState ls = pointService.getCurrentState(pointID);
            img = DaoFactory.getYukonImageDao().getLiteYukonImage(ls.getImageID());
        }
		return img.getImageName();
	}	
	
	public static boolean isControllable(int pointID) {
        DynamicDataSource dynamicDataSource = 
            (DynamicDataSource) YukonSpringHook.getBean("dynamicDataSource");
		int tags = dynamicDataSource.getTags(pointID);
        
		return ((tags & Signal.TAG_ATTRIB_CONTROL_AVAILABLE) != 0) &&
				!((tags & Signal.MASK_ANY_CONTROL_DISABLE) != 0);
	}
}
