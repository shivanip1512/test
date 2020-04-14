package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.cannontech.spring.YukonSpringHook;

public class BeatThePeakGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear
{

    private BeatThePeakGearContainer tierGearContainer = new BeatThePeakGearContainer();

    public BeatThePeakGear() {
        super();

        setControlMethod(GearControlMethod.BeatThePeak);
    }

    public BeatThePeakGearContainer getTierGearContainer() {
        return tierGearContainer;
    }

    public void setTierGearContainer(BeatThePeakGearContainer tierGearContainer) {
        this.tierGearContainer = tierGearContainer;
    }

    @Override
    public void add() throws SQLException {
        super.add();

        tierGearContainer.setGearId(getGearID());
        LMGearDao gearDao = YukonSpringHook.getBean("lmGearDao", LMGearDao.class);
        gearDao.insertContainer(tierGearContainer);
    }

    @Override
    public boolean useCustomDbRetrieve() {
        return true;
    }
    
    @Override
    public void retrieve() throws SQLException {
        super.retrieve();

        LMGearDao gearDao = YukonSpringHook.getBean("lmGearDao", LMGearDao.class);
        setTierGearContainer(gearDao.getContainer(getGearID()));
    }

    @Override
    public void update() throws SQLException {
        super.update();

        tierGearContainer.setGearId(getGearID());
        LMGearDao gearDao = YukonSpringHook.getBean("lmGearDao", LMGearDao.class);
        gearDao.updateContainer(tierGearContainer);

    }

    public java.lang.Integer getTimeout() {
        // Timeout is stored in the methodPeriod column
        return getMethodPeriod();
    }

    public java.lang.Integer getResend() {
        // Resend is stored in the methodRate column
        return getMethodRate();
    }

    public void setTimeout(java.lang.Integer timeout) {
        // Timeout is stored in the methodPeriod column
        setMethodPeriod(timeout);
    }

    public void setResend(java.lang.Integer resend) {
        // Resend is stored in the methodRate column
        setMethodRate(resend);
    }
}