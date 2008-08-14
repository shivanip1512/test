#ifndef  __EMETCONWORDC_H__
#define  __EMETCONWORDC_H__

#include "EmetconWordBase.h"

class EmetconWordC : public EmetconWordBase
{
    public:
        EmetconWordC(unsigned char * buf, int size);

        void getData(unsigned char buf[]);
};


#endif
