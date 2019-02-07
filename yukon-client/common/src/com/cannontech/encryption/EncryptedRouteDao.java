package com.cannontech.encryption;

import java.util.List;
import java.util.Optional;

import org.joda.time.Instant;

import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;

public interface EncryptedRouteDao {
    
    public List<EncryptedRoute> getAllEncryptedRoutes();
    
    public void addEncryptedRoute(EncryptedRoute encryptedRoute);
    
    public void deleteEncryptedRoute(EncryptedRoute encryptedRoute);
    
    public List<EncryptionKey> getEncryptionKeys();
    
    public void saveNewEncryptionKey(String name, String privateKey, String publicKey, EncryptionKeyType type, Instant timeStamp)
            throws CryptoException;
    
    public void deleteEncryptionKey(int encryptionKeyId);

    /**
     * Saves or update Honeywell and Ecobee public and private key in the EncryptionKey table
     */
    void saveOrUpdateEncryptionKey(String privateKey, String publicKey, EncryptionKeyType encryptionKeyType, Instant timestamp);

    /**
     * Gets Honeywell and Ecobee Private and Public keys from the EncryptionKey table
     */
    Optional<EncryptionKey> getEncryptionKey(EncryptionKeyType encryptionKeyType);

}
