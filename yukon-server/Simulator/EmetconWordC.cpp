#include "yukon.h"

#include "EmetconWordC.h"

EmetconWordC::EmetconWordC(unsigned char * buf, int size) : EmetconWordBase(buf,size)
{
}

void EmetconWordC::getData(unsigned char ret [])//always 6
{
    for(int i = 0; i < 6; i++)
    {
        ret[i] = (_buffer[i] << 4 & 0xf0) | (_buffer[i+1] >> 4 & 0x0f);
    }
}
