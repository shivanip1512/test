//Factory to get words here.
#include "yukon.h"

#include "EmetconWordFactory.h"
#include "EmetconWordBase.h"
#include "EmetconWordD1.h"
#include "EmetconWordB.h"
#include "EmetconWordC.h"

EmetconWordFactory::EmetconWordFactory(){}

EmetconWordBase* EmetconWordFactory::getEmetconWord(unsigned char * bWordBuffer, int size)
{
    EmetconWordBase * word = NULL;

    if ((bWordBuffer[0] & 0xf0) == 0xa0) { //Bword
        word = new EmetconWordB(bWordBuffer,size);
    } 
    else if ((bWordBuffer[0] & 0xf0) == 0xd0) //Dword
    {
        word = new EmetconWordD1();
    }
    else if ((bWordBuffer[0] & 0xf0) == 0xc0) //Dword
    {
        word = new EmetconWordC(bWordBuffer,size);
    }

    return word;
}

