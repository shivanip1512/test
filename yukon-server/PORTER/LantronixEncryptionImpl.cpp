#include "yukon.h"

#include "LantronixEncryptionImpl.h"
#include "openssl\aes.h"
#include "openssl\evp.h"

LantronixEncryptionImpl::LantronixEncryptionImpl()
{
	_staticIv = false;
}

int LantronixEncryptionImpl::decode(const unsigned char* const cipher, long bufLen, unsigned char *& plainText)
{	
	int dLen = (cipher[16]<<8)+cipher[17];
	unsigned char* inbuf = new unsigned char [((dLen+15)/16)*16];
	int inLen = 0;
	int tmplen = 0;
	//IV is in buf at beginning
	unsigned char iv[16];
	memcpy(iv,cipher,16);

	EVP_CIPHER_CTX ctx;
	EVP_CIPHER_CTX_init(&ctx);
	EVP_DecryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, _key, iv);
	if (!EVP_DecryptUpdate(&ctx, inbuf, &inLen, cipher+18, bufLen-18))
	{
		/* Error */
		delete [] plainText;
		plainText = NULL;
		return 0;
	}

	if (!EVP_DecryptFinal_ex(&ctx, inbuf + inLen, &tmplen))
	{
		/* Error */
		delete [] plainText;
		plainText = NULL;
		return 0;
	}

	/* Put the decoded buffer into place. bufLen > inLen */
	plainText = inbuf;
	
	return dLen;
}

int LantronixEncryptionImpl::encode(const unsigned char* const pText, long pTextLen, unsigned char *& cText)
{
	
	int outlen = 0, tmplen = 0;
	EVP_CIPHER_CTX ctx;

	generateNewIV();
	int t = ((pTextLen+15)/16*16+18);
	cText = new unsigned char [((pTextLen+15)/16*16)+18];

	EVP_CIPHER_CTX_init(&ctx);
	EVP_EncryptInit_ex(&ctx, EVP_aes_128_cbc(), NULL, _key, _iv);

	memcpy(cText,_iv,16);
	cText[16] = pTextLen >> 8;
	cText[17] = pTextLen & 0xff;

	if (!EVP_EncryptUpdate(&ctx, cText+18, &outlen, pText, pTextLen))
	{
		delete [] cText;
		cText = NULL;
		return 0;
	}

	if (!EVP_EncryptFinal_ex(&ctx, cText+18 + outlen, &tmplen))
	{
		delete [] cText;
		cText = NULL;
		return 0;
	}
	outlen += tmplen;
	EVP_CIPHER_CTX_cleanup(&ctx);
	
	return outlen+UPDHEADERSIZE;
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

void LantronixEncryptionImpl::generateNewIV()
{
	if (!_staticIv)
	{
		for(int i = 0; i < 16; i++)
		{
			_iv[i] = (char)i;
		}
	}
}
