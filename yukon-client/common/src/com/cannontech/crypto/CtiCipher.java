package com.cannontech.crypto;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.cannontech.clientutils.CTILogger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CtiCipher
{
	private static CtiCipher localCipher = null;
	private static Cipher ENC_CIPHER_1 = null;
	private static Cipher DEC_CIPHER_1 = null;
	private static SecretKey key = null;


	// 8-byte Salt
	private static final byte[] SALT =
	{
		(byte)0xF2, (byte)0x9B, (byte)0x99, (byte)0x12,
		(byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
	};
	
	private static final int ITER_CNT = 13;

	private static KeySpec keySpec =new PBEKeySpec(
			"smoe_kRAZy-Key FOR_+ NOW THIS is kIND of CrZY wrighT-?".toCharArray(),
			SALT, ITER_CNT );


	/**
	 * 
	 */
	private CtiCipher()
	{
		super();

		try
		{
			SecretKey key = SecretKeyFactory.getInstance(
					"PBEWithMD5AndDES").generateSecret(keySpec);
			
			if( DEC_CIPHER_1 == null )
				DEC_CIPHER_1 = Cipher.getInstance( key.getAlgorithm() );

			if( ENC_CIPHER_1 == null )
				ENC_CIPHER_1 = Cipher.getInstance( key.getAlgorithm() );
			
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec =
				new PBEParameterSpec(SALT, ITER_CNT);
			
			DEC_CIPHER_1.init( Cipher.DECRYPT_MODE, key, paramSpec );
			ENC_CIPHER_1.init( Cipher.ENCRYPT_MODE, key, paramSpec );

		}
		catch( Exception e )
		{
			CTILogger.error( "Unable to create encryption Cipher", e );			
			ENC_CIPHER_1 = null;
			DEC_CIPHER_1 = null;
		}
	}

	private static synchronized CtiCipher getInstance()
	{
		if( localCipher == null )
			localCipher = new CtiCipher();
		
		return localCipher;
	}


	public static String encrypt( String str )
	{
		try
		{
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");
			
			//encrypt it
			byte[] res = getInstance().ENC_CIPHER_1.doFinal( utf8 );
			
			return new BASE64Encoder().encode(res);
		}
		catch( Exception e )
		{
			CTILogger.error( "Unsuccessful string encryption", e );			
		}

		return null;
	}

	public static String decrypt( String str )
	{
		try
		{
			// Decode base64 to get bytes
			byte[] dec = new BASE64Decoder().decodeBuffer(str);
			
			// decrypt it
			byte[] res = getInstance().DEC_CIPHER_1.doFinal( dec );
			
			return new String(res, "UTF8");
		}
		catch( Exception e )
		{
			CTILogger.error( "Unsuccessful string decryption", e );			
		}

		return null;
	}

}
