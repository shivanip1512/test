#ifndef  __EMETCONWORDB_H__
#define  __EMETCONWORDB_H__

#include "EmetconWordBase.h"

class EmetconWordB : public EmetconWordBase
{
    public:
        EmetconWordB(unsigned char * buf, int size);

        int getFunction();
        int getAddress();
        int numberOfWords();
        int getIO();
};


#endif
