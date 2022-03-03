package com.cannontech.web.api.dr.ecobee;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;
import com.cannontech.encryption.EcobeeZeusSecurityService;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.web.api.token.AuthenticationException;

import io.jsonwebtoken.Jwts;

public class EcobeeZeusJwtTokenAuthService {
    @Autowired private EcobeeZeusSecurityService ecobeeZeusSecurityService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private EncryptedRouteDao encryptedRouteDao;
    private Logger log = YukonLogManager.getLogger(EcobeeZeusJwtTokenAuthService.class);
    private PublicKey pubKey;
    private PrivateKey privKey;

    @PostConstruct
    public void init() {
        createDBChangeListener();
    }

    private void createDBChangeListener() {
        asyncDynamicDataSource.addDBChangeListener(dbChange -> {
            if (dbChange.getDatabase() == DBChangeMsg.CHANGE_ENCRYPTION_KEY_DB) {
                Optional<EncryptionKey> encryptionKey = encryptedRouteDao.getEncryptionKey(EncryptionKeyType.EcobeeZeus);
                if (encryptionKey.isPresent()) {
                    if (encryptionKey.get().getEncryptionKeyId().equals(dbChange.getId())) {
                        try {
                            pubKey = null;
                            privKey = null;
                            getPublicKey();
                            getPrivateKey();
                        } catch (Exception e) {
                            log.error("Unable to update cache keys ", e);
                        }
                    }
                }
            }
        });
    }

    public void validateEcobeeJwtToken(String jwtToken) {

        try {
            pubKey = getPublicKey();

            Jwts.parser()
                .setSigningKey(pubKey)
                .parse(jwtToken);

        } catch (Exception ex) {
            throw new AuthenticationException("Expired or invalid JWT token");
        }

    }

    /**
     * Convert String PublicKey to PublicKey.
     */
    private PublicKey getPublicKey() throws Exception {
        
        if (pubKey == null) {
            ZeusEncryptionKey encryptionKey = ecobeeZeusSecurityService.getZeusEncryptionKey();
            if (encryptionKey != null) {
                byte[] publicBytes = Base64.decodeBase64(encryptionKey.getPublicKey());
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                pubKey = keyFactory.generatePublic(keySpec);
            }
        }
        return pubKey;
    }

    /**
     * Convert String Private to PrivateKey.
     */
    public PrivateKey getPrivateKey() throws Exception {
        if (privKey == null) {
            ZeusEncryptionKey encryptionKey = ecobeeZeusSecurityService.getZeusEncryptionKey();
            if (encryptionKey != null) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(encryptionKey.getPrivateKey()));
                KeyFactory kf = KeyFactory.getInstance("RSA");
                privKey = kf.generatePrivate(keySpec);
            }
        }
        return privKey;
    }
}
