#include "yukon.h"

#include "EmetconWordDn.h"
#include "dlldefs.h"
#include "cti_asmc.h"

EmetconWordDn::EmetconWordDn() : EmetconWordBase()
{
    _buffer[0] = 0xd0; //set Dword
}

void EmetconWordDn::insertData(unsigned char ret [], int size)//always 5
{
    if (size > _size)
    {
        //Error
        return;
    }

    _buffer[0] += (ret[0] >> 4 & 0x0f);
    int i = 1;
    for (i = 1; i < size; i++)
    {
        _buffer[i] = (ret[i-1] << 4) | (ret[i] >> 4);
    }

    _buffer[i] = (ret[i-1] << 4);
}

void EmetconWordDn::getBytes(unsigned char buf[], int size)
{
    if (size != _size) 
    {
        //error
        return;
    }

    //add STAT
    //leave Zero for now. (unused according to manual)

    //Add BCH 
    unsigned char BCH = BCHCalc_C (_buffer, 46);
    _buffer[_size-2] |= BCH >> 6;
    _buffer[_size-1] = BCH << 2;

    for (int i = 0; i < _size; i++)
    {
        buf[i] = _buffer[i];
    }
}

