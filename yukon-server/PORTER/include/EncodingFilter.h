#pragma once

#include <vector>

class EncodingFilter
{
    public:
        virtual bool encode(const unsigned char* const plainText, long bufLen, std::vector<unsigned char>& cipher)=0;
        virtual bool decode(const unsigned char* const cipher , long bufLen, std::vector<unsigned char>& plainText)=0;
};
