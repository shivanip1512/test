package com.cannontech.cbc.cyme.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.BankState;
import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.cbc.cyme.CymeConfigurationException;
import com.cannontech.cbc.cyme.CymeLoadProfileReader;
import com.cannontech.cbc.cyme.CymePointDataCache;
import com.cannontech.cbc.cyme.CymeSimulationListener;
import com.cannontech.cbc.cyme.CymeSimulatorService;
import com.cannontech.cbc.cyme.CymeWebService;
import com.cannontech.cbc.cyme.profile.CymeLoadProfile;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.Lists;

public class CymeSimulatorServiceImpl implements CymeSimulatorService, CymeSimulationListener {

    private static final Logger logger = YukonLogManager.getLogger(CymeSimulatorServiceImpl.class);
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private PointDao pointDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private CymeWebService cymDISTWebService;
    @Autowired private CymeTaskExecutor cymeTaskExecutor;
    @Autowired private CymeLoadProfileReader cymeLoadProfileReader;
    @Autowired private CymePointDataCache cymePointDataCache;
    @Autowired private FeederDao feederDao;
    
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    
    //CPARM
    private Duration simulationDelay;
    
    //Control flags for Runnables
    private boolean simulationScheduled = false;
    private boolean simulationRunning = false;
    private boolean runSimulation = false;
    
    private YukonPao substationBusPao = null;
    
    @PostConstruct
    public void initialize() {
        
        boolean cymeEnabled = configurationSource.getBoolean(MasterConfigBooleanKeysEnum.CYME_ENABLED,false);
        
        if (cymeEnabled) {
            String subBusName = null;
            logger.info("CYME IVVC Simulator is initializing.");
            try {
                subBusName = configurationSource.getString(MasterConfigStringKeysEnum.CYME_INTEGRATION_SUBBUS,null);
                if (subBusName == null) {
                    throw new UnknownKeyException("Missing CYME_INTEGRATION_SUBBUS cparm.");
                }
                
                simulationDelay = configurationSource.getDuration("CYME_SIMULATION_DELAY", new Duration(5000));
                
                substationBusPao = paoDao.getYukonPao(subBusName,PaoType.CAP_CONTROL_SUBBUS.getPaoCategory(),
                                                                    PaoType.CAP_CONTROL_SUBBUS.getPaoClass());
                logger.info("CYME Simulator is configured for sub bus with name: " + subBusName);
              
                //register with CymePointDataCache
                cymePointDataCache.registerPointsForSubStationBus(this,substationBusPao.getPaoIdentifier());
                
                return;
            } catch (UnknownKeyException e) {
                logger.error("CYME IVVC Simulator is missing CPARM for subbus. CYME_INTEGRATION_SUBBUS");
            } catch (NotFoundException e) {
                logger.error("CYME Simulator: Subbus is not found in the system. " + subBusName);
            } catch (CymeConfigurationException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.info("CYME integration is disabled.");
            return;
        }
        
        // We didnt return out where we expected, something is wrong
        logger.info("CYME IVVC Simulator is disabled and will not run.");
    }
    
    // Main Loop
    // Task in a scheduled executor.
    public void simulationKickoff() {
        
        if (cymePointDataCache.isCymeEnabled(substationBusPao.getPaoIdentifier().getPaoId())) {
            if( ! simulationRunning) {   
                logger.debug("Starting Simulation.");
                simulationRunning = true;
                
                //Grab simulation Data. From File for now
                String path = configurationSource.getString(MasterConfigStringKeysEnum.CYME_SIM_FILE, null );
                if (path == null) {
                    throw new UnknownKeyException("Missing File path Cparm  CYME_SIM_FILE. Could not continue simulation.");
                }

                final CymeLoadProfile loadProfile = cymeLoadProfileReader.readFromFile(path);
    
                //Prime the System data.
                simulationKickoff();
                
                Runnable simulation = new Runnable() {
                    private int stepNumber = 1;//Start at 1. The 0th was sent out upon simulation start
                    private boolean complete = false;
                    
                    @Override
                    public void run() {
                        if (! runSimulation) {
                            logger.debug("Simulation disabled, halting.");
                            simulationRunning = false;
                            throw new RuntimeException();//this ends future tasks
                        }

                        if (complete) {
                            logger.debug("Simulation Complete, halting.");
                            //Toggle the start simulation point to false,

                            PointIdentifier startSimulation = new PointIdentifier(PointType.Status, 351);
                            LitePoint simulationPoint = pointDao.getLitePoint(new PaoPointIdentifier(substationBusPao.getPaoIdentifier(), startSimulation));
                            
                            simplePointAccessDao.setPointValue(simulationPoint.getLiteID(), TrueFalse.FALSE);
                            throw new RuntimeException();//this ends future tasks
                        }
                        
                        int value = loadProfile.getValues().get(stepNumber++);
                        logger.debug("Running next simulation step with Profile Percent:" + value);
    
                        //Send the New Load Value to the system.
                        PointIdentifier loadFactor = new PointIdentifier(PointType.Analog, 352);
                        LitePoint loadPoint = pointDao.getLitePoint(new PaoPointIdentifier(substationBusPao.getPaoIdentifier(), loadFactor));
                        simplePointAccessDao.setPointValue(loadPoint.getLiteID(), value);
                        scheduleCymeStudy();
                        
                        //Are we done? Change the status point
                        if (stepNumber+1 >= 1440/loadProfile.getTimeInterval().getMinutes()) {
                            complete = true;
                            simulationRunning = false;
                        }
                    }
                };
    
                //Schedule at rate based on loadProfile data
                int minutes = loadProfile.getTimeInterval().getMinutes();
                scheduledExecutor.scheduleAtFixedRate(simulation, 0, minutes, TimeUnit.MINUTES);//TODO fix this to minutes
            } else {
                logger.debug("Simulation is in progress. Cannot start new one.");
            }
        } 
    }

    @Override
    public void handleTapUp(int regulatorId) {
        logger.debug("Raise Tap Operation received for regulatorId: " + regulatorId);
        handleTap(regulatorId,1);
    }
    
    @Override
    public void handleTapDown(int regulatorId) {
        logger.debug("Lower Tap Operation received for regulatorId: " + regulatorId);
        handleTap(regulatorId,-1);
    }

    private void handleTap(int regulatorId, int tapChange) {
        PaoIdentifier paoIdentifier = new PaoIdentifier(regulatorId, PaoType.LOAD_TAP_CHANGER);
        LitePoint point = extraPaoPointAssignmentDao.getLitePoint(paoIdentifier, RegulatorPointMapping.TAP_POSITION);
        
        // get Current Value to change
        PointValueQualityHolder tapValue = cymePointDataCache.getCurrentValue(point.getLiteID());
        
        if (tapValue != null) {
            simplePointAccessDao.setPointValue(point.getLiteID(), tapValue.getValue() + tapChange);
        } else {
            logger.error("Tap Position for Redgulator with Id: " + point.getLiteID() + " was not found in Cache.");
            return;
        }
        
        scheduleCymeStudy();
    }
    
    @Override
    public void handleOpenBank(int bankId) {
        logger.debug("Open Bank Operation received for bankId: " + bankId);
        scheduleCymeStudy();
    }

    @Override
    public void handleCloseBank(int bankId) {
        logger.debug("Close Bank Operation received for bankId: " + bankId);
        scheduleCymeStudy();
    }
    
    @Override
    public void handleScanDevice(int deviceId) {
        logger.debug("Scan Operation received for deviceId: " + deviceId);
        scheduleCymeStudy();
    }
    
    @Override
    public void handleRefreshSystem(int deviceId) {
        logger.debug("Refresh System request received.");
        
        //Set regulators to Remote Mode
        List<Zone> zones = zoneDao.getZonesBySubBusId(deviceId);
        for (Zone zone : zones) {
            for (RegulatorToZoneMapping regulator : zone.getRegulators()) {
                int paoId = regulator.getRegulatorId();
                YukonPao regulatorPao = paoDao.getYukonPao(paoId);
                LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(regulatorPao, RegulatorPointMapping.AUTO_REMOTE_CONTROL);
                
                simplePointAccessDao.setPointValue(litePoint, TrueFalse.TRUE);
            }
        }
        
        //This will cause the rest of the points to get updated values.
        scheduleCymeStudy();
    }
    
    private void scheduleCymeStudy() {
        
        //if is not scheduled
        if ( cymePointDataCache.isCymeEnabled(substationBusPao.getPaoIdentifier().getPaoId()) && !simulationScheduled ) {
            simulationScheduled = true;
            logger.debug("Cyme Study Scheduled. Will run in " + simulationDelay.getMillis() + " " + TimeUnit.MILLISECONDS.name());
            //This will wait a period of time before running the simulation to allow for multiple system changes to happen at once.
            scheduledExecutor.schedule( new Runnable() {
    
                @Override
                public void run() {
                    logger.debug("Cyme Study Running.");
                    simulationScheduled = false;
                    final Instant simulationTime = new Instant();
                    String simulationId = null;
                    
                    Collection<PointPaoIdentifier> paosInSystem = cymePointDataCache.getPaosInSystem();
                    Map<Integer,PointValueQualityHolder> currentPointValues = cymePointDataCache.getCurrentValues();
                    
                    List<String> paoNames = Lists.newArrayList();
                    LiteYukonPAObject subbus = paoDao.getLiteYukonPAO(substationBusPao.getPaoIdentifier().getPaoId());
                    paoNames.add(subbus.getPaoName());
                    List<Integer> feederIds = feederDao.getFeederIdBySubstationBus(substationBusPao);
                    for (Integer feederId : feederIds) {
                        LiteYukonPAObject feeder = paoDao.getLiteYukonPAO(feederId);
                        paoNames.add(feeder.getPaoName());
                    }
                    
                    String xmlData = CymeXMLBuilder.generateStudy(paosInSystem,currentPointValues,paoNames);
                    
                    logger.debug("Study date being sent: " + xmlData);
                    simulationId = cymDISTWebService.runSimulation(xmlData);
    
                    if (simulationId != null) {
                        //Create Thread to wait for the results and process.
                        cymeTaskExecutor.monitorSimulation(simulationId,simulationTime);
                    }
                }
            
            }, simulationDelay.getMillis(), TimeUnit.MILLISECONDS);
        } else if (simulationScheduled) {
            logger.debug("Another simulation already scheduled, this change will be reflected in the scheduled simulation.");
        }
    }

    @Override
    public void notifyNewLoadFactor(PointValueQualityHolder pointData) {
        scheduleCymeStudy();
    }
    
    @Override
    public void notifyNewSimulation(PointValueQualityHolder pointData) {
        TrueFalse state = TrueFalse.getForAnalogValue((int)pointData.getValue());
        logger.debug("Simulation Status update received: " + state.getDisplayValue());

        //If we see this point go to TRUE, Kick off a simulation
        if (state == TrueFalse.TRUE) {
            runSimulation = true;
            simulationKickoff();                                                
        } else {
            runSimulation = false;
        }
    }

    @Override
    public void notifyCbcControl(PointValueQualityHolder pointData) {
        BankState state = PointStateHelper.decodeRawState(BankState.class, pointData.getValue());
        if (state == BankState.OPEN_PENDING || state == BankState.CLOSE_PENDING) {
            logger.debug("OpenPending or ClosePending state detected. Scheduling Simulation.");
            scheduleCymeStudy();
        }
    }
}