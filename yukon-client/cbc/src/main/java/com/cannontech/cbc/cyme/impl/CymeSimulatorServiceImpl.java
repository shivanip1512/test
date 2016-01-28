package com.cannontech.cbc.cyme.impl;

import java.util.HashMap;
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

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.cbc.cyme.CymeDataListener;
import com.cannontech.cbc.cyme.CymeLoadProfileReader;
import com.cannontech.cbc.cyme.CymeSimulatorService;
import com.cannontech.cbc.cyme.CymeWebService;
import com.cannontech.cbc.cyme.exception.CymeConfigurationException;
import com.cannontech.cbc.cyme.exception.CymeSimulationStopException;
import com.cannontech.cbc.cyme.model.SimulationLoadFactor;
import com.cannontech.cbc.cyme.profile.CymeLoadProfile;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.point.stategroup.TrueFalse;

public class CymeSimulatorServiceImpl implements CymeSimulatorService {
    private static final Logger logger = YukonLogManager.getLogger(CymeSimulatorServiceImpl.class);
    
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private CymeWebService cymDISTWebService;
    @Autowired private CymeTaskExecutor cymeTaskExecutor;
    @Autowired private CymeLoadProfileReader cymeLoadProfileReader;
    @Autowired private CymeXMLBuilder cymeXMLBuilder;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private CapbankControllerDao cbcDao;
    @Autowired private CapbankDao capbankDao;
    
    private static final int MINUTES_PER_DAY = 24 * 60;
    
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(5);
    
    //CPARM
    private Duration simulationDelay;
    
    // Keeps track of the individual study threads.
    private Map<PaoIdentifier, CymeStudyRunnable> paoStudyRunnableMap = new HashMap<>();
    
    // Keeps track of the scheduled load profile threads.
    private Map<PaoIdentifier, CymeLoadProfileRunnable> paoLoadProfileRunnableMap = new HashMap<>();
    
    // Runnable class for handling LoadProfile simulations.
    public class CymeLoadProfileRunnable implements Runnable {
    	private boolean complete = false;
    	private boolean runSimulation = false;
    	private boolean simulationRunning = false;
    	
    	private int stepNumber = 1; //Start at 1. The 0th was sent out upon simulation start
    	private SimulationLoadFactor nextLoadFactor = null;
    	private final CymeLoadProfile loadProfile;
    	private final PaoIdentifier busIdentifier;
    	
    	public CymeLoadProfileRunnable(PaoIdentifier busIdentifier, CymeLoadProfile loadProfile) {
    		this.busIdentifier = busIdentifier;
    		this.loadProfile = loadProfile;
    		runSimulation = true;
    		nextLoadFactor = this.loadProfile.getValues().get(0);
    	}
    	
    	/**
    	 * This will cause the next attempt at executing the run method to throw an exception instead,
    	 * causing the runnable to halt.
    	 */
    	public void stop() {
    		// This will cause simulationRunning to be set to false, so we don't need to set it here.
    		runSimulation = false;
    	}
    	
    	public boolean isSimulationRunning() {
    		return simulationRunning;
    	}
    	
    	@Override
    	public void run() {
    		try {
    			//Check if we should continue to run.
    			if (!runSimulation) {
    				//This was true to get this Runnable to execute, it must have changed since runtime 
    				logger.debug("Simulation disabled, halting.");
    				throw new CymeSimulationStopException("Simulation was halted before completing.");
    			}
    			
    			if (complete) {
    				logger.debug("Simulation Complete, halting.");
    				throw new CymeSimulationStopException("Simulation completing normally.");
    			}
    			
    			//Check if its time to execute a new Load Factor
    			if (nextLoadFactor.getTime().isBeforeNow()) {
    				logger.debug("Running next simulation step with Profile Percent:" + nextLoadFactor.getLoad());
    				
    				//Send the New Load Value to the system. The point change will cause a new study to run
    				PaoPointIdentifier paoPointIdentifier = 
    				        new PaoPointIdentifier(busIdentifier, CymeDataListener.CYME_LOAD_FACTOR_IDENTIFIER);
    				LitePoint loadPoint = pointDao.getLitePoint(paoPointIdentifier);
    				simplePointAccessDao.setPointValue(loadPoint, nextLoadFactor.getLoad());
    				
    				//Are we done? Change the status point
    				//TODO Future: This will also need to pay attention to the time.
    				if ( (stepNumber + 1) >= (MINUTES_PER_DAY / loadProfile.getTimeInterval().getMinutes()) ) {
    					complete = true;
    					simulationRunning = false;
    				} else {
    					//Setup the next Load Factor
    					nextLoadFactor = loadProfile.getValues().get(stepNumber++);
    				}
    			}
    		} catch (CymeSimulationStopException e) {
    			stopSimulation();
    			throw new RuntimeException(); // Stops future scheduled executions
    		} catch (Exception e) {
    			logger.error("Uncaught Exception during 24 simulation. Shutting down the simulation.",e);
    			stopSimulation();
    			throw e; // Stops future scheduled executions
    		}
    	}
    	
    	private void stopSimulation() {
    		simulationRunning = false;
    		
    		// Toggle the start simulation point to false,
    		PaoPointIdentifier paoPointIdentifier = 
    		        new PaoPointIdentifier(busIdentifier, CymeDataListener.CYME_START_SIMULATION_IDENTIFIER);
    		LitePoint simulationPoint = pointDao.getLitePoint(paoPointIdentifier);
    		simplePointAccessDao.setPointValue(simulationPoint, TrueFalse.FALSE);
    	}
    }

    // Runnable class for handling individual study simulations.
    public class CymeStudyRunnable implements Runnable {
    	private boolean simulationScheduled = false;
    	private boolean running = false;
    	private boolean needsRerun = false;
    	
    	private final PaoIdentifier busIdentifier;
    	
    	public CymeStudyRunnable(PaoIdentifier busIdentifier) {
    		this.busIdentifier = busIdentifier;
    	}
    	
    	public boolean isSimulationScheduled() {
			return simulationScheduled;
		}
    	
    	public void setSimulationScheduled(boolean simulationScheduled) {
			this.simulationScheduled = simulationScheduled;
		}
    	
    	public boolean isRunning() {
			return running;
		}
    	
    	public void setToRerun() {
    		needsRerun = true;
    	}
    	
    	public void executionComplete() {
    		boolean doRerun = false;
			
    		synchronized (this) {
				running = false;
				doRerun = needsRerun;
			}
    		
    		/*
    		 *  A request may have been submitted while we were running. If so, 
    		 *  schedule this thread for execution again.
    		 */
			if (doRerun) {
				scheduleStudyForExcecution(this);
			}
		}
    	
    	@Override
    	public void run() {
    		logger.debug("Cyme Study Running.");
            
    		synchronized (this) {
    			// Allow new simulations to be scheduled (once running is false, that is.)
    			simulationScheduled = false;
    			
    			// Run until executionComplete is called by the runnable in monitorSimulation.
    			running = true;
    			
    			// We're either a fresh run or a scheduled rerun. Either way this is false.
    			needsRerun = false;
    		}
            
            try {
                String xmlData = cymeXMLBuilder.generateStudy(busIdentifier);
            
                logger.debug("Study data being sent: " + xmlData);
                String simulationId = cymDISTWebService.runSimulation(xmlData);
                if (simulationId != null) {
                    //Create Thread to wait for the results and process.
                    cymeTaskExecutor.monitorSimulation(simulationId, new Instant(), this);
                }
            } catch (CymeConfigurationException e) {
                logger.error(e.getMessage());
            }
    	}
    }
    
    @PostConstruct
    public void initialize() {
        if (isCymeCparmEnabled()) {
            logger.info("CYME IVVC Simulator is initializing.");
            simulationDelay = configurationSource.getDuration("CYME_SIMULATION_DELAY", new Duration(5000));
        } else {
            logger.info("CYME IVVC Simulator is disabled and will not run.");
        }
    }
    
    /**
     * Determine whether CYME is enabled for a specific substation bus.
     * @param busIdentifier the PaoIdentifier of the bus.
     * @return true if CYME is enabled for the bus, false otherwise.
     */
    private boolean isCymeEnabled(PaoIdentifier busIdentifier) {
    	try {
	    	if (busIdentifier.getPaoType() != PaoType.CAP_CONTROL_SUBBUS) {
	    		return false;
	    	}
	    	
	    	PaoPointIdentifier paoPointIdentifier = 
	    	        new PaoPointIdentifier(busIdentifier, CymeDataListener.CYME_ENABLED_IDENTIFIER);
	    	
			LitePoint litePoint = pointDao.getLitePoint(paoPointIdentifier);
	    	
	    	PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
	    	
	        TrueFalse state = TrueFalse.getForAnalogValue((int)pointValue.getValue());
	        return (state == TrueFalse.TRUE);
    	} catch (NotFoundException e) {
    		// Point didn't exist. CYME is definitely not enabled.
    		return false;
    	}
    }
    
    @Override
    public void startLoadProfileSimulation(final YukonPao substationBusPao) {
        if (isCymeEnabled(substationBusPao.getPaoIdentifier())) {
            // Grab simulation Data. From File for now
            String path = configurationSource.getString(MasterConfigString.CYME_SIM_FILE, null );
            if (path == null) {
                throw new UnknownKeyException("Missing File path Cparm CYME_SIM_FILE. Could not continue simulation.");
            }
        	final CymeLoadProfile loadProfile = cymeLoadProfileReader.readFromFile(path);
        	
        	synchronized (paoLoadProfileRunnableMap) {
            	CymeLoadProfileRunnable simulationThread = paoLoadProfileRunnableMap.get(substationBusPao);
            	if (simulationThread != null && simulationThread.isSimulationRunning()) {
            		// The bus already has a thread in the middle of a 24-hour simulation.
            		logger.info("Simulation is in progress. Cannot start new one.");
            	} else {
            		try {
    	        		// We either don't have a thread at all or the thread we have isn't running.
    	        		logger.info("Starting Simulation.");
    	        		
    	        		// Create a new thread.
    	        		simulationThread = new CymeLoadProfileRunnable(substationBusPao.getPaoIdentifier(), loadProfile);
    	        		
    	        		// Keep track of it.
    	        		paoLoadProfileRunnableMap.put(substationBusPao.getPaoIdentifier(), simulationThread);
    	        		
    	        		// Hack.
    	        		simulationThread.simulationRunning = true;
    	        		
    	        		// Run it every 5 seconds.
    	                scheduledExecutor.scheduleAtFixedRate(simulationThread, 0, 5, TimeUnit.SECONDS);
            		} catch (Exception e) {
                        //intercepting all exception while setting up the simulation in order to make sure we reset flags.
                        logger.error("There was an exeption while starting up the 24 hour load simulation.", e);
                        simulationThread.stop();
                        throw e;
                    }
            	}
        	}
        } 
    }
    
    @Override
    public void stopLoadProfileSimulation(YukonPao substationBusPao) {
        CymeLoadProfileRunnable loadProfileThread = null;
        
        synchronized (paoLoadProfileRunnableMap) {
            loadProfileThread = paoLoadProfileRunnableMap.get(substationBusPao);
        }

        if (loadProfileThread != null) {
    		logger.info("Stopping Simulation.");
    		loadProfileThread.stop();
    	} else {
    		logger.debug("No simulation exists for substation bus with id " + substationBusPao.getPaoIdentifier().getPaoId());
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
        if (!isCymeCparmEnabled()) {
            return;
        }
        
        PaoIdentifier paoIdentifier = new PaoIdentifier(regulatorId, PaoType.LOAD_TAP_CHANGER);
        LitePoint point = extraPaoPointAssignmentDao.getLitePoint(paoIdentifier, RegulatorPointMapping.TAP_POSITION);
        
        // get Current Value to change
        try{
            PointValueQualityHolder tapValue = asyncDynamicDataSource.getPointValue(point.getLiteID());
            simplePointAccessDao.setPointValue(point, tapValue.getValue() + tapChange);

            //Determine Bus
            Zone zone = zoneDao.getZoneByRegulatorId(regulatorId);
            scheduleCymeStudy(new PaoIdentifier(zone.getSubstationBusId(), PaoType.CAP_CONTROL_SUBBUS));
        } catch (DynamicDataAccessException e) {
            logger.error("Error returned requsting the Tap Position for Regulator with Id: " + point.getLiteID());
            return;
        }        
        
    }
    
    @Override
    public void handleOpenBank(int bankId) {
        logger.debug("Open Bank Operation received for bankId: " + bankId);
        if (!isCymeCparmEnabled()) {
            return;
        }
        scheduleBankStudy(bankId);
    }

    @Override
    public void handleCloseBank(int bankId) {
        logger.debug("Close Bank Operation received for bankId: " + bankId);
        if (!isCymeCparmEnabled()) {
            return;
        }        
        scheduleBankStudy(bankId);
    }
    
    private void scheduleBankStudy(int bankId) {
        // Cyme enabled already checked. Just get it scheduled
        PaoType paoType = paoDao.getYukonPao(bankId).getPaoIdentifier().getPaoType();
        if (paoType == PaoType.CAPBANK) { 
            PaoIdentifier parentBus = capbankDao.getParentBus(bankId);
            scheduleCymeStudy(parentBus);
        } else {
            logger.warn("Received Bank Operation for unsupported device type: " + paoType.getDbString() );
            return;
        }   
    }
    
    @Override
    public void handleScanDevice(int deviceId) {
        logger.debug("Scan Operation received for deviceId: " + deviceId);
        if (!isCymeCparmEnabled()) {
            return;
        }
        
        PaoType paoType = paoDao.getYukonPao(deviceId).getPaoIdentifier().getPaoType();
        
        if (paoType.isCbc()) {
            PaoIdentifier parentBus = cbcDao.getParentBus(deviceId);
            scheduleCymeStudy(parentBus);
        } else {
            logger.warn("Received scan for unsupported device type: " + paoType.getDbString() );
            return;
        }        
    }
    
    @Override
    public void handleRefreshSystem(int subbusId) {
        logger.debug("Refresh System request received.");
        
        PaoIdentifier busIdentifier = paoDao.getYukonPao(subbusId).getPaoIdentifier();
        
        if (!isCymeCparmEnabled()) {
            logger.warn("Cyme is not enabled. Not processing refresh request.");
            return;
        } else if (busIdentifier.getPaoType() != PaoType.CAP_CONTROL_SUBBUS) {
            logger.warn("Received refresh system request for a non-subbus type. Canceling refresh.");
            return;
        }
        
        // Set regulators to Remote Mode
        List<Zone> zones = zoneDao.getZonesBySubBusId(subbusId);
        for (Zone zone : zones) {
            for (RegulatorToZoneMapping regulator : zone.getRegulators()) {
                int paoId = regulator.getRegulatorId();
                YukonPao regulatorPao = paoDao.getYukonPao(paoId);
                LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(regulatorPao, RegulatorPointMapping.AUTO_REMOTE_CONTROL);
                
                simplePointAccessDao.setPointValue(litePoint, TrueFalse.TRUE);
            }
        }
        
        // This will cause the rest of the points to get updated values.
        scheduleCymeStudy(busIdentifier);
    }
    
    /**
     * Schedule a CYME study for execution for a particular bus.
     * @param substationBusPao the subbus the study will be executed for.
     */
    private void scheduleCymeStudy(final YukonPao substationBusPao) {
    	final PaoIdentifier busIdentifier = substationBusPao.getPaoIdentifier();
    	
        if (isCymeEnabled(busIdentifier)) {
            CymeStudyRunnable cymeStudy = null;
            
            synchronized (paoStudyRunnableMap) {
                cymeStudy = paoStudyRunnableMap.get(busIdentifier);
            	
            	if (cymeStudy == null) {
            		// This bus hasn't had a simulation yet. 
            		cymeStudy = new CymeStudyRunnable(busIdentifier);
            		paoStudyRunnableMap.put(busIdentifier, cymeStudy);
            	}
            }
        	
        	// Check if there's a simulation already scheduled for the thread.
        	if (!cymeStudy.isSimulationScheduled()) {
        		if (cymeStudy.isRunning()) {
        			// There's already a thread running. Schedule him to rerun once he's finished.
        			logger.debug("Attempted to schedule a study while a previous study was still running. " +
        						 "Scheduling a new study following completion of the current one.");
        			
        			cymeStudy.setToRerun();
        		} else {
	        		// Our thread is not running. It's safe to schedule another execution.
	        		scheduleStudyForExcecution(cymeStudy);
        		}
        	} else {
        		logger.debug("Another simulation already scheduled for BusId " + busIdentifier.getPaoId() + 
        					 ", this change will be reflected in the scheduled simulation.");
        	}
        } else {
        	logger.debug("CYME is either not configured or not enabled for BusId " + busIdentifier.getPaoId());
        }
    }
    
    /**
     * Sets the studyRunnable to execute in five seconds. 
     * @param studyRunnable
     */
    private void scheduleStudyForExcecution(CymeStudyRunnable studyRunnable) {
		studyRunnable.setSimulationScheduled(true);
		
		logger.debug("Cyme Study Scheduled. Will run in " + simulationDelay.getMillis() + " " + TimeUnit.MILLISECONDS.name());
		
		// This will wait a period of time before running the simulation to allow for multiple system changes to happen at once.
		scheduledExecutor.schedule(studyRunnable, simulationDelay.getMillis(), TimeUnit.MILLISECONDS);
    }
   
    /**
     * Checks the value of the CYME_ENABLED Cparm.
     * @return the value of the CYME_ENABLED Cparm in Master.cfg.
     */
    private boolean isCymeCparmEnabled() {
        boolean enabled = configurationSource.getBoolean(MasterConfigBoolean.CYME_ENABLED,false);
        if (!enabled) {
            logger.debug("CYME_ENABLED CPARM is false.");
        }
        return enabled;
    }
}