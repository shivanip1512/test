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
    std::copy(plainText, plainText + bufLen, cipher.begin());

    return true;
}

bool NoOpEncryption::decode(const unsigned char* const cipher , long bufLen, vector<unsigned char>& plainText)
{
    plainText.resize(bufLen);
    std::copy(cipher, cipher + bufLen, plainText.begin());

    return true;
}
