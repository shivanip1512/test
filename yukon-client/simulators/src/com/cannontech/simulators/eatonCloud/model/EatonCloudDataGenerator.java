package com.cannontech.simulators.eatonCloud.model;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpStatus;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;

public abstract class EatonCloudDataGenerator {

    protected int status = HttpStatus.OK.value();
    protected int successPercentage = 100;
    protected int unknownPercentage = 0;
    protected EatonCloudSimulatorDeviceCreateRequest createRequest;
    protected String token1;
    protected String token2;
    protected Date expiryTime1;
    protected Date expiryTime2;
    private static int length = 5;
    private static boolean useLetters = true;
    private static boolean useNumbers = true;
    protected boolean refreshToken = true;
    
    {
        resetToken1();
        resetToken2();
    }

    protected void resetToken1() {
        token1 = RandomStringUtils.random(length, useLetters, useNumbers);
        expiryTime1 = DateUtils.addYears(new Date(), 1);
    }
    
    protected void resetToken2() {
        token2 = RandomStringUtils.random(length, useLetters, useNumbers); 
        expiryTime2 = DateUtils.addYears(new Date(), 1);
    }

    protected Map<PaoType, HardwareType> paoTypeToHardware = Map.of(PaoType.LCR6600C, HardwareType.LCR_6600C, PaoType.LCR6200C,
            HardwareType.LCR_6200C);
    
 
    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreateRequest(EatonCloudSimulatorDeviceCreateRequest request) {
        this.createRequest = request;
    }

    public void setSuccessPercentage(int successPercentage) {
        this.successPercentage = successPercentage;
    }
    
    public void setUnknownPercentage(int unknownPercentage) {
        this.unknownPercentage = unknownPercentage;
    }
    
    public abstract EatonCloudDataGenerator getDataGenerator(EatonCloudVersion version);
    
    public void expireSecrets() {
        expiryTime1 = DateUtils.addMonths(new Date(), +5);
        expiryTime2 = DateUtils.addMonths(new Date(), +5);
    }

    public String getToken2() {
        return token2;
    }

    public String getToken1() {
        return token1;
    }
    
    public Date getExpiryTime1() {
        return expiryTime1;
    }
    
    public Date getExpiryTime2() {
        return expiryTime2;
    }

    public boolean isRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(boolean refreshToken) {
        this.refreshToken = refreshToken;
    }
}
