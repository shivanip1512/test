#include "yukon.h"

#include "EmetconWordBase.h"



EmetconWordBase::EmetconWordBase (unsigned char * buf, int size)
{
    _size = size;
    _buffer = new unsigned char [size];
    memcpy(_buffer,buf,size);
}

EmetconWordBase::EmetconWordBase ()
{
    _size = defaultWordSize;
    _buffer = new unsigned char [_size];

    for (int i=0; i < defaultWordSize; i++)
    {
        _buffer[i] = 0;
    }
}

EmetconWordBase::~EmetconWordBase()
{
    delete [] _buffer;
}

unsigned char EmetconWordBase::getType()
{
    unsigned char ret = 0xf0 & _buffer[0];

    return ret;
}
