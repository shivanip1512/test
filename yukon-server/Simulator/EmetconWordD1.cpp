#include "yukon.h"

#include "EmetconWordD1.h"
#include "dlldefs.h"
#include "cti_asmc.h"

EmetconWordD1::EmetconWordD1() : EmetconWordBase()
{
    _buffer[0] = 0xd0; //set Dword

    mctAddress = 0;
    numberOfRepeaters = 0;
}

void EmetconWordD1::insertData(unsigned char ptr[], int size)//always 5
{
    if (size != 3)
    {
        //Error
        return;
    }

    _buffer[0] = ((mctAddress >> 12) & 0x01) | (numberOfRepeaters << 1) | 0xd0;//beginning of d word
    _buffer[1] = (mctAddress >> 4) & 0xff;
    
    // First part of byte [2]
    _buffer[2] = ((mctAddress << 4) & 0xf0) | (0x0f & (ptr[0] >> 4));   // data
    _buffer[3] = ((ptr[0] << 4) & 0xf0) | ((ptr[1] >> 4) & 0x0f);// Data
    _buffer[4] = ((ptr[1] << 4) & 0xf0) | ((ptr[2] >> 4) & 0x0f);// Data
    // data ends first half of this byte
    _buffer[5] = (ptr[2] << 4) & 0xf0;

    _buffer[6] = 0x00;
}


void EmetconWordD1::setMctAddress(int addr)
{
    mctAddress = addr;
}

void EmetconWordD1::setNumberOfRepeaters(int num)
{
    numberOfRepeaters = num;
}

void EmetconWordD1::getBytes(unsigned char buf[], int size)
{
    if (size != _size) 
    {
        //error
        return;
    }

    //add PWR ALRM
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

