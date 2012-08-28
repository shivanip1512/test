package com.cannontech.encryption;

import java.util.List;

import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;

public interface EncryptedRouteDao {
    List<EncryptedRoute> getAllEncryptedRoutes();
    void addEncryptedRoute(EncryptedRoute encryptedRoute);
    void deleteEncryptedRoute(EncryptedRoute encryptedRoute);
    List<EncryptionKey> getEncryptionKeys();
    void saveNewEncyptionKey(String name, String value) throws CryptoException;
    void deleteEncyptionKey(int encryptionKeyId);
}
