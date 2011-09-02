#pragma once

#include "EncodingFilter.h"
#include <string>

class LantronixEncryptionImpl : public EncodingFilter
{
    public:
        enum{
            UDPHEADERSIZE=18//header is 16(IV) + 2(len)
        };

        union uint_t
        {
            unsigned long ul;
            unsigned char uc[4];
        } uint;

        LantronixEncryptionImpl();

        virtual bool decode(const unsigned char* const cipher,    long bufLen, std::vector<unsigned char>& pText);
        virtual bool encode(const unsigned char* const plainText, long bufLen, std::vector<unsigned char>& cipher);

        void setKey(const std::string key);
        void setIV (const unsigned char iv[]);

        const unsigned char* getKey();
        const unsigned char* getIV();

    private:

        void generateNewIV(char seed);

        bool _staticIv;
        unsigned char _iv[16];
        unsigned char _key[16];
};
