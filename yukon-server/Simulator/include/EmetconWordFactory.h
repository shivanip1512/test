#ifndef  __EMETCONWORDFACTORY_H__
#define  __EMETCONWORDFACTORY_H__

#include "EmetconWordBase.h"

class EmetconWordFactory
{
    public:
        EmetconWordFactory();
        EmetconWordBase* getEmetconWord(unsigned char * buf,int size);

};
#endif
