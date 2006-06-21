package com.cannontech.web.editor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.web.util.JSFComparators;

public class CapBankEditorForm extends DBEditorForm {

 private List unassignedPoints = null;
 private List assignedPoints = null;
 private MultiDBPersistent monitorPointsVector = null;
 private List columns;
 
 
	public CapBankEditorForm() {
		super();
	}

	
	protected void checkForErrors() throws Exception {
		// TODO Auto-generated method stub
	}
	
	public void update() {

		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setDetail("Database UPDATE successful");
		List points = new ArrayList();
		for (Iterator iter = assignedPoints.iterator(); iter.hasNext();) {
			CapBankMonitorPointParams point = (CapBankMonitorPointParams) iter.next();
			CCMonitorBankList monitorPoint = new CCMonitorBankList(point);
			points.add(monitorPoint);
		}
		((CapBank)getDbPersistent()).setCcMonitorBankList(points);
		try {
			updateDBObject(getDbPersistent(), facesMessage);			
		} 		
		catch (TransactionException e) {
            String errorString = e.getMessage();
            facesMessage.setDetail(errorString);
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		} finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_update",
					facesMessage);
		}

	}

	

	public List getAssignedPoints() {		
		if(assignedPoints == null)
			assignedPoints = new ArrayList();
		return assignedPoints;		
	}


	public List getUnassignedPoints() {
		if (unassignedPoints == null) {		
			unassignedPoints = new ArrayList();
			CapBank capBank = ((CapBank) getDbPersistent());			
			int controlDeviceId = capBank.getCapBank().getControlDeviceID().intValue();
			if (controlDeviceId > 0) {
				LitePoint[] allPoints = DaoFactory.getPaoDao()
						.getLitePointsForPAObject(controlDeviceId);
			for (int i = 0; i < allPoints.length; i++) {
				LitePoint point = allPoints[i];			
				if (point.getUofmID() == PointUnits.UOMID_VOLTS)
					{							
						CapBankMonitorPointParams monitorPoint = new CapBankMonitorPointParams(point);
						//set the cap bank id to replace the CBC if with Cap Bank Id
						monitorPoint.setCapBankId(capBank.getCapBank().getDeviceID().intValue());
						//set the feeder limits by default
						setDefaultFeederLimits(capBank, monitorPoint);
						
						if(!isPointAssigned (monitorPoint)) {
							unassignedPoints.add(monitorPoint);
						}								
					}
				}				
			}
					Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);
			}
			return unassignedPoints;		
	}


	private void setDefaultFeederLimits(CapBank capBank, CapBankMonitorPointParams monitorPoint) {
		int fdrId = com.cannontech.database.db.capcontrol.CapBank.getParentFeederID( capBank.getPAObjectID().intValue());
		if (fdrId != 0 ){	
			LiteYukonPAObject liteFeeder = DaoFactory.getPaoDao().getLiteYukonPAO(fdrId);		
			CapControlFeeder feeder = (CapControlFeeder) LiteFactory.convertLiteToDBPers( liteFeeder);
			Connection conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			try {
				
				feeder.setDbConnection( conn );						
				feeder.retrieve();
			} catch (SQLException e) {
	
			}
			int stratId = feeder.getCapControlFeeder().getStrategyID().intValue();
			
			CapControlStrategy strategy = new CapControlStrategy();
			strategy.setStrategyID(new Integer ( stratId));
			
			
			try {
				strategy.setDbConnection( conn );
				strategy.retrieve();
	
				monitorPoint.setLowerBandwidth(strategy.getPeakLag().floatValue() );
				monitorPoint.setUpperBandwidth(strategy.getPeakLead().floatValue() );
				
			} catch (SQLException e) {
			    CTILogger.info("CapBankEditorForm -- setDefaultFeederLimits" + e.getMessage());
			}
			finally {
				feeder.setDbConnection( null );
				strategy.setDbConnection( null );
				
				try {
				if( conn != null ) conn.close();
				} catch( java.sql.SQLException e2 ) { 
	                CTILogger.info("CapBankEditorForm -- setDefaultFeederLimits" + e2.getMessage());
	
	            }			
			}
		}
	}


	public void treeSwapRemoveAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();

		String swapType = (String) paramMap.get("swapType");
		int elemId = new Integer((String) paramMap.get("id")).intValue();
		
		if ("CapBankPoint".equalsIgnoreCase(swapType)) {			
			if (unassignedPoints != null) {
				for (Iterator iter = getAssignedPoints().iterator(); iter.hasNext();) {
					CapBankMonitorPointParams monitorPoint = (CapBankMonitorPointParams) iter.next();
					if (monitorPoint.getPointId() == elemId) {
						getAssignedPoints().remove(monitorPoint);
						getUnassignedPoints().add(monitorPoint);
						break;
					}				
				}
			Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);	
			}
		}
	}	


	public void treeSwapAddAction() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();

		String swapType = (String) paramMap.get("swapType");
		int elemId = new Integer((String) paramMap.get("id")).intValue();
		if (unassignedPoints != null) {
			if ("CapBankPoint".equalsIgnoreCase(swapType)) {
					for (Iterator iter = getUnassignedPoints().iterator(); iter.hasNext();) {
						CapBankMonitorPointParams monitorPoint = (CapBankMonitorPointParams) iter.next();
						monitorPoint.setDisplayOrder(getAssignedPoints().size() + 1);
						if (monitorPoint.getPointId() == elemId) {
							getUnassignedPoints().remove(monitorPoint);
							getAssignedPoints().add(monitorPoint);							
							break;
						}				
					}
					Collections.sort(unassignedPoints, JSFComparators.monitorPointComparator);
					Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
				}
		}		
	}

	// hook into capcontrol form
	public void init(DBPersistent dbPersistent) {
		if (getDbPersistent() == null) {
			if (dbPersistent instanceof CapBank) {
			setDbPersistent(dbPersistent);
			//set the assigned points
			CapBank capBank = ((CapBank)getDbPersistent());
			assignedPoints = DaoFactory.getPointDao().getCapBankMonitorPoints(capBank);
			Collections.sort(assignedPoints, JSFComparators.monitorPointDisplayOrderComparator);
			}
		}
	}


	public MultiDBPersistent getMonitorPointsVector() {
		if ( monitorPointsVector == null ) {
			monitorPointsVector = new MultiDBPersistent();
		}			
		return monitorPointsVector;
	}
	
	/**
	 * checks to see if the point is on the assigned list and populates unassigned list
	 * @param monitorPoint
	 */		
	private boolean isPointAssigned (CapBankMonitorPointParams monitorPoint) {
		for (Iterator iter = getAssignedPoints().iterator(); iter.hasNext();) {
			CapBankMonitorPointParams assignedPoint = (CapBankMonitorPointParams) iter.next();
			if (assignedPoint.getPointId() == monitorPoint.getPointId()){
				return true;
			}			
		}				
		return false;
	}
		
}
