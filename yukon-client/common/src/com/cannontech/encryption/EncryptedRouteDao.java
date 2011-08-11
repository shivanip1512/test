package com.cannontech.encryption;

import java.util.List;

public interface EncryptedRouteDao {
    
    List<EncryptedRoute> getAllEncryptedRoutes();

    void saveEncryptedRoute(EncryptedRoute encryptedRoute);

}
