#include "precompiled.h"
/**
 *
 */

#include "encryption_noOp.h"

using std::vector;

NoOpEncryption::NoOpEncryption()
{

}

bool NoOpEncryption::encode(const unsigned char* const plainText, long bufLen, vector<unsigned char>& cipher)
{
    cipher.resize(bufLen);
    memcpy(&*cipher.begin(),plainText,bufLen);

    return false;
}

bool NoOpEncryption::decode(const unsigned char* const cipher , long bufLen, vector<unsigned char>& plainText)
{
    plainText.resize(bufLen);
    memcpy(&*plainText.begin(),cipher,bufLen);

    return false;
}
