package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSetting;
import com.cannontech.database.db.capcontrol.VoltageViolationSetting;
import com.cannontech.user.YukonUserContext;

public interface StrategyDao {

    public List<CapControlStrategy> getAllStrategies();

    /**
     * Returns a List<LiteCapControlStrategy>, lites only contain names and id's.
     * @return List<LiteCapControlStrategy>
     */
    public List<LiteCapControlStrategy> getAllLiteStrategies();

    public boolean delete(int strategyId);

    public boolean deleteStrategyAssignmentsByStrategyId(int strategyId);
    
    public int add(String name);

    public void savePowerFactorCorrectionSetting(CapControlStrategy strategy);
    
    public void saveVoltageViolationSettings(CapControlStrategy strategy);

    public void savePeakSettings(CapControlStrategy strategy);

    /**
     * Get our PowerFactorCorrectionSetting object from the database. If this strategy is not an IVVC strategy, then
     * these values won't exist in the database, so we will return the default settings. This is so that the values are there
     * if the user changes the Control Algorithm to IVVC in the UI.
     */
    public PowerFactorCorrectionSetting getPowerFactorCorrectionSetting(CapControlStrategy strategy);
    
    /**
     * Get our List<VoltageViolationSetting> object's from the database. If this strategy is not an IVVC strategy, then
     * these values won't exist in the database, so we will return the default settings. This is so that the values are there
     * if the user changes the Control Algorithm to IVVC in the UI.
     */
    public List<VoltageViolationSetting> getVoltageViolationSettings(CapControlStrategy strategy);

    public List<PeakTargetSetting> getPeakSettings(CapControlStrategy capControlStrategy);

    public List<String> getAllOtherPaoNamesUsingSeasonStrategy(int strategyId, int excludedPaoId);
    
    public List<String> getAllOtherPaoNamesUsingHolidayStrategy(int strategyId, int excludedPaoId);
    
    public List<String> getAllOtherPaoNamesUsingStrategyAssignment(int strategyId, int excludedPaoId);

    public List<ViewableStrategy> getAllViewableStrategies(YukonUserContext userContext);

    public CapControlStrategy getForId(int strategyId);

    public void update(CapControlStrategy strategy);

    public StrategyLimitsHolder getStrategyLimitsHolder(int strategyId);

}
