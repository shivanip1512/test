package com.cannontech.encryption;

import java.util.List;

import com.cannontech.database.db.pao.EncryptedRoute;
import com.cannontech.database.db.security.EncryptionKey;

public interface EncryptedRouteDao {
    
    public List<EncryptedRoute> getAllEncryptedRoutes();
    
    public void addEncryptedRoute(EncryptedRoute encryptedRoute);
    
    public void deleteEncryptedRoute(EncryptedRoute encryptedRoute);
    
    public List<EncryptionKey> getEncryptionKeys();
    
    public void saveNewEncyptionKey(String name, String value) throws CryptoException;
    
    public void deleteEncyptionKey(int encryptionKeyId);

    /**
     * Saves Honeywell public and private key in the EncryptionKey table
     */
    void saveNewHoneywellEncyptionKey(String privateKey, String publicKey);

    /**
     * Gets Honeywell Private and Public keys from the EncryptionKey table
     */
    EncryptionKey getHoneywellEncyptionKey();
    
}
