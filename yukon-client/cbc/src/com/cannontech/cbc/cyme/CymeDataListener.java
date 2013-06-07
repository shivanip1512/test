package com.cannontech.cbc.cyme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.google.common.collect.Sets;

public class CymeDataListener implements DBChangeListener, PointDataListener {
	private static final Logger logger = YukonLogManager.getLogger(CymeDataListener.class);
	
	@Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
	@Autowired private CymeSimulatorService cymeSimulatorService;
	@Autowired private PointDao pointDao;
	@Autowired private SubstationBusDao substationBusDao;
	
	private Map<PaoIdentifier, Set<Integer>> busToPointMappings = new HashMap<>();
	
	public static final PointIdentifier CYME_ENABLED_IDENTIFIER = new PointIdentifier(PointType.Status, 350);
	public static final PointIdentifier CYME_START_SIMULATION_IDENTIFIER = new PointIdentifier(PointType.Status, 351);
	public static final PointIdentifier CYME_LOAD_FACTOR_IDENTIFIER = new PointIdentifier(PointType.Analog, 352);
	
	@PostConstruct
	public void initialize() {
		// Register as a listener for DB Change messages.
		asyncDynamicDataSource.addDBChangeListener(this);

		List<PaoIdentifier> busList = substationBusDao.getAllSubstationBuses();
		
		Set<Integer> allCymePoints = Sets.newHashSet();
		
		for (PaoIdentifier busIdentifier : busList) {
			try {
				allCymePoints.addAll(loadBusPoints(busIdentifier));
			} catch (NotFoundException nfe) {
				logger.debug("Sub bus with id " + busIdentifier.getPaoId() + " is missing the points required for CYME simulation.");
			}
		}
		
		// Register for the necessary points for all the SubBus objects we found that were configured for CYME.
		asyncDynamicDataSource.registerForPointData(this, allCymePoints);
	}

	/**
	 * Load the CYME points for the specified bus.
	 * @param busIdentifier the PaoIdentifier for the bus whose points need to be loaded
	 * @return the set of all of the CYME points for the bus
	 * @throws NotFoundException if any of the points required for CYME configuration are missing for the bus.
	 */
	private Set<Integer> loadBusPoints(PaoIdentifier busIdentifier) throws NotFoundException {
		LitePoint cymeEnabledPoint = pointDao.getLitePoint(new PaoPointIdentifier(busIdentifier, CYME_ENABLED_IDENTIFIER));
		LitePoint startSimPoint = pointDao.getLitePoint(new PaoPointIdentifier(busIdentifier, CYME_START_SIMULATION_IDENTIFIER));
		LitePoint loadFactorPoint = pointDao.getLitePoint(new PaoPointIdentifier(busIdentifier, CYME_LOAD_FACTOR_IDENTIFIER));
		
		Set<Integer> busPoints = Sets.newHashSet();
		busPoints.add(cymeEnabledPoint.getPointID());
		busPoints.add(startSimPoint.getPointID());
		busPoints.add(loadFactorPoint.getPointID());
		
		busToPointMappings.put(busIdentifier, busPoints);
		
		return busPoints;
	}
	
	@Override
	public void pointDataReceived(PointValueQualityHolder pointData) {
		// Find the bus this point belongs to, if any.
		PaoIdentifier busIdentifier = null;
		
		for (PaoIdentifier identifier : busToPointMappings.keySet()) {
			if (busToPointMappings.get(identifier).contains(pointData.getId())) {
				// Got him!
				busIdentifier = identifier;
				break;
			}
		}
		
		if (busIdentifier != null) {
			LitePoint point = pointDao.getLitePoint(pointData.getId());
			PointIdentifier pointIdentifier = PointIdentifier.createPointIdentifier(point);
			if (pointIdentifier.equals(CYME_LOAD_FACTOR_IDENTIFIER)) {
				cymeSimulatorService.handleRefreshSystem(busIdentifier.getPaoId());
			} else if (pointIdentifier.equals(CYME_START_SIMULATION_IDENTIFIER)) {
				TrueFalse value = TrueFalse.getForAnalogValue((int)pointData.getValue());
				if (value == TrueFalse.TRUE) {
					cymeSimulatorService.startLoadProfileSimulation(busIdentifier);
				} else {
					cymeSimulatorService.stopLoadProfileSimulation(busIdentifier);
				}
			}
		}
	}

	@Override
	public void dbChangeReceived(DBChangeMessage dbChange) {
		if (dbChange.getDatabase() == DBChangeMessage.CHANGE_PAO_DB) {
			PaoType paoType = PaoType.getForDbString(dbChange.getObjectType());
			if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
				PaoIdentifier busIdentifier = new PaoIdentifier(dbChange.getId(), paoType);

				// Unregister for the old points.
				Set<Integer> oldBusPoints = busToPointMappings.get(busIdentifier);
				if (oldBusPoints != null) {
					asyncDynamicDataSource.unRegisterForPointData(this, oldBusPoints);
				}
				
				// Remove from the map, re-register if necessary.
				busToPointMappings.remove(busIdentifier);
				
				if (dbChange.getDbChangeType() != DbChangeType.DELETE) {
					// Register for the new points.
					try {
						Set<Integer> updatedBusPoints = loadBusPoints(busIdentifier);
						asyncDynamicDataSource.registerForPointData(this, updatedBusPoints);
						
						// Overwrite whatever was in our store with new info.
						busToPointMappings.put(busIdentifier, updatedBusPoints);
					} catch (NotFoundException nfe) {
						// DBChange message was for a bus isn't configured for CYME.
						logger.debug("Sub bus is missing the points required for CYME simulation.");
					}
				}
			}
		}
	}
}
