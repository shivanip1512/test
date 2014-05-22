package com.cannontech.web.dev.database.service;

import com.cannontech.web.dev.database.objects.DevStars;

public interface DevStarsCreationService {
    void executeEnergyCompanyCreation(DevStars devStars);
    void executeStarsAccountCreation(DevStars devStars);
    boolean isRunning();
    int getPercentComplete();
    boolean doesAccountExist(String string, int energyCompanyId);
}
