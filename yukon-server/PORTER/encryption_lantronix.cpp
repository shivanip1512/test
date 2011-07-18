#include "precompiled.h"

#include "encryption_lantronix.h"
#include "logger.h"

#include "openssl\aes.h"
#include "openssl\evp.h"

#include <cstdlib>

using namespace std;

LantronixEncryptionImpl::LantronixEncryptionImpl()
{
    _staticIv = false;
    uint.ul = 0;
    memset(_iv,0,16);
    memset(_key,0,16);
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
    int dataLength = (cipher[16]<<8)+cipher[17];

    //Allocate enough space for the plaintext plus padding
    plainText.resize(bufLen);

    //IV is in buf at beginning, Copying it out.
    unsigned char iv[16];
    memcpy(iv,cipher,16);

    AES_KEY aeskey;
    AES_set_decrypt_key(_key,128,&aeskey);
    AES_cbc_encrypt(cipher+UDPHEADERSIZE,&*plainText.begin(),bufLen,&aeskey,iv, AES_DECRYPT);

    //Shrink to fit.
    plainText.resize(dataLength);

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
bool LantronixEncryptionImpl::encode(const unsigned char* const pText, long pTextLen, std::vector<unsigned char>& cText)
{
    //Setup a new IV (or use a preset one if set)
    generateNewIV(pText[1]);

    int size = ((pTextLen+15)/16*16)+UDPHEADERSIZE;

    //Calculate size for ciphertext including padding. AND UDP header
    cText.resize(size);

    //Copy in IV
    memcpy(&*cText.begin(),_iv,16);

    //Set plaintext length into buffer
    cText[16] = pTextLen >> 8;
    cText[17] = pTextLen & 0xff;

    AES_KEY aeskey;
    AES_set_encrypt_key(_key,128,&aeskey);
    AES_cbc_encrypt(pText,&*(cText.begin()+UDPHEADERSIZE),size-UDPHEADERSIZE,&aeskey,_iv, AES_ENCRYPT);

    return true;
}
/**
 * The key passed in should be a 32 character long string in
 * HEX. This will get boiled down to a 16 byte key, each 2 hex
 * characters getting into one char slot.
 *
 * @param key
 */
void LantronixEncryptionImpl::setKey(const string key)
{
    char *end;
    for (int i = 0; i < 16; i+=4)
    {
        string tester = key.substr((i/4)*8, 8);
        //Grabbing 8 bytes from the strong and converting to a long.
        uint.ul = strtol(key.substr((i/4)*8, 8).c_str() + 0, &end, 16);

        //Passing the bytes into the _key array.
        _key[i] =   uint.uc[3];
        _key[i+1] = uint.uc[2];
        _key[i+2] = uint.uc[1];
        _key[i+3] = uint.uc[0];
    }
}

/**
 * This is here for unit testing.
 */
const unsigned char * LantronixEncryptionImpl::getKey()
{
    return _key;
}

/**
 * This function differ's from the setKey. It will copy the iv
 * passed in directly, so it should be 16 bytes long.
 *
 * Once set is called, generateNewIV will no longer change the
 * IV.
 *
 * @param iv
 */
void LantronixEncryptionImpl::setIV(const unsigned char iv[])
{
    _staticIv = true;
    memcpy(_iv,iv,16);
}

/**
 * This is here for unit testing.
 */
const unsigned char * LantronixEncryptionImpl::getIV()
{
    return _iv;
}

/**
 * Generates a new IV if one has not been set, uses rand()
 * seeded by the character passed in.
 *
 * @param seed
 */
void LantronixEncryptionImpl::generateNewIV(char seed)
{
    if (!_staticIv)
    {
        srand((int)seed);

        for(int i = 0; i < 16; i+=4)
        {
            uint.ul = rand();

            _iv[i] =   uint.uc[3];
            _iv[i+1] = uint.uc[2];
            _iv[i+2] = uint.uc[1];
            _iv[i+3] = uint.uc[0];
        }
    }
}
