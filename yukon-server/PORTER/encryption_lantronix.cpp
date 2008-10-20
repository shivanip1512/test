#include "yukon.h"

#include "encryption_lantronix.h"
#include "openssl\aes.h"
#include "openssl\evp.h"

#include <cstdlib>

LantronixEncryptionImpl::LantronixEncryptionImpl()
{
	_staticIv = false;
}
/**
 * Decode a cipher text. The IV for the decryption is in the 
 * first 16 bytes of the buffer, the following two are the 
 * length of the resulting decoded text. 
 *  
 * requires the Key to be set. 
 * 
 * @param cipher 
 * @param bufLen 
 * @param plainText 
 * 
 * @return int 
 */
bool LantronixEncryptionImpl::decode(const unsigned char* const cipher, long bufLen, vector<unsigned char>& plainText)
{	
	//Grabbing the length of the plaintext from the buffer. Byte 16 and 17
	int dLen = (cipher[16]<<8)+cipher[17];

	//Allocate enough space for the plaintext plus padding
	plainText.resize(dLen);

	int inLen = 0;
	int tmplen = 0;

	//IV is in buf at beginning, Copying it out.
	unsigned char iv[16];
	memcpy(iv,cipher,16);

	EVP_CIPHER_CTX ctx;
	EVP_CIPHER_CTX_init(&ctx);
	EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, _key, iv);
	if (!EVP_DecryptUpdate(&ctx, plainText.begin(), &inLen, cipher+18, bufLen-18))
	{
		/* Error */
		plainText.clear();
		return 0;
	}

	if (!EVP_DecryptFinal_ex(&ctx, plainText.begin() + inLen, &tmplen))
	{
		/* Error */
		plainText.clear();
		return 0;
	}
	
	return true;
}

/**
 * Encode a message. Key needs to be set before hand. IV is 
 * generated 
 * 
 * @param pText 
 * @param pTextLen 
 * @param cText 
 * 
 * @return int 
 */
bool LantronixEncryptionImpl::encode(const unsigned char* const pText, long pTextLen, vector<unsigned char>& cText)
{
	
	int outlen = 0, tmplen = 0;
	EVP_CIPHER_CTX ctx;

	//Setup a new IV (or use a preset one if set)
	generateNewIV(pText[1]);

	//Calculate size for ciphertext including padding. AND UDP header
	cText.resize(((pTextLen+15)/16*16)+UDPHEADERSIZE);

	EVP_CIPHER_CTX_init(&ctx);
	EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, _key, _iv);

	//Copy in IV
	memcpy(cText.begin(),_iv,16);

	//Set plaintext length into buffer
	cText[16] = pTextLen >> 8;
	cText[17] = pTextLen & 0xff;

	if (!EVP_EncryptUpdate(&ctx, cText.begin()+18, &outlen, pText, pTextLen))
	{
		cText.clear();
		return 0;
	}

	if (!EVP_EncryptFinal_ex(&ctx, cText.begin()+18 + outlen, &tmplen))
	{
		cText.clear();
		return 0;
	}

	outlen += tmplen;
	EVP_CIPHER_CTX_cleanup(&ctx);
	
	return true;
}

void LantronixEncryptionImpl::setKey(string key)
{
	memcpy(_key,key.c_str(),16);
}

void LantronixEncryptionImpl::setIV(unsigned char iv[])
{
	_staticIv = true;
	memcpy(_iv,iv,16);
}

void LantronixEncryptionImpl::generateNewIV(char seed)
{
	if (!_staticIv)
	{
		srand((int)seed);
		for(int i = 0; i < 16; i++)
		{
			_iv[i] = (char)rand();
		}
	}
}
