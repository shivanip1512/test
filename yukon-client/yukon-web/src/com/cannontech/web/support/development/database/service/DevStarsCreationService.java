package com.cannontech.web.support.development.database.service;

import com.cannontech.web.support.development.database.objects.DevStars;

public interface DevStarsCreationService {
    void executeEnergyCompanyCreation(DevStars devStars);
    void executeStarsAccountCreation(DevStars devStars);
    boolean isRunning();
    int getPercentComplete();
    boolean doesAccountExist(String string, int energyCompanyId);
}
