#include "yukon.h"

#include "EmetconWordB.h"
#include "SharedFunctions.h"

EmetconWordB::EmetconWordB(unsigned char * buf, int size) : EmetconWordBase(buf,size)
{
}

int EmetconWordB::getFunction()
{
    int function = 0;
    function = (_buffer[4] << 4 & 0xf0) | (_buffer[5] >> 4 & 0x0f);
    
    return function;
}

int EmetconWordB::getIO()
{
    int function = 0;
    int val = 0;
    val = (_buffer[5]>>2) & 0x03;

    switch (val)
    {
        case 0:
            function = WRITE;
            break;
        case 1:
            function = READ;
            break;
        case 2:
            function = FUNC_WRITE;
            break;
        case 3:
            function = FUNC_READ;
            break;
    }

    return function;
}

int EmetconWordB::getAddress()
{
    int setmctAddress = 0;
    setmctAddress = ( (_buffer[1] & 0x0f) << 20 |
                       _buffer[2]  << 12 |
                       _buffer[3]  << 4  |
                       _buffer[4] >> 4) >> 2;
    return setmctAddress;
}

int EmetconWordB::numberOfWords()
{
    int WTF = 0;
    if((_buffer[4] & 0x10) == 0x10) {  WTF = 1;   }
    if((_buffer[4] & 0x20) == 0x20) {  WTF = 2;   }
    if((_buffer[4] & 0x30) == 0x30) {  WTF = 3;   }
    return WTF;
}
