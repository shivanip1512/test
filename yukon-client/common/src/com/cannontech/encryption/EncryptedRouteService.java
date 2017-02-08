package com.cannontech.encryption;

import java.security.cert.X509Certificate;

public interface EncryptedRouteService {

    /**
     * Generates a certificate for Honeywell.
     * 
     * @return X509Certificate, certificate object
     * @throws CertificateGenerationFailedException, exception to wrap any certificate generation exception
     */
    public X509Certificate generateHoneywellCertificate() throws CertificateGenerationFailedException;
}
