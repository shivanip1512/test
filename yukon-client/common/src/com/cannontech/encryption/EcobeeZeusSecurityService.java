package com.cannontech.encryption;

import com.cannontech.dr.ecobee.message.ZeusEncryptionKey;

public interface EcobeeZeusSecurityService {

	/**
	 * This method return ZeusEncryptionKey which Contains Public, Private Key and Time Stamp.
	 * throw CryptoException 
	 * return ZeusEncryptionKey.
	 */
	public ZeusEncryptionKey generateZeusEncryptionKey() throws CryptoException;

	/**
	 * This method return ZeusEncryptionKey which Contains Public, Private Key and Time Stamp.
	 * throw Exception
	 * return ZeusEncryptionKey.
	 */
	public ZeusEncryptionKey getZeusEncryptionKey() throws Exception;

}
