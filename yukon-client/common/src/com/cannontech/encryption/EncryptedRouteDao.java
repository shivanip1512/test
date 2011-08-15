package com.cannontech.encryption;

import java.util.List;

import com.cannontech.database.db.pao.EncryptedRoute;

public interface EncryptedRouteDao {
    
    List<EncryptedRoute> getAllEncryptedRoutes();

    void saveEncryptedRoute(EncryptedRoute encryptedRoute);

}
