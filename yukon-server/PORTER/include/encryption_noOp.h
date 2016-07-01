#pragma once

#include "EncodingFilter.h"

class NoOpEncryption : public EncodingFilter
{
    public:

        NoOpEncryption();

        bool encode(const unsigned char* const plainText, long bufLen, std::vector<unsigned char>& cipher);
        bool decode(const unsigned char* const cipher , long bufLen, std::vector<unsigned char>& plainText);
};
