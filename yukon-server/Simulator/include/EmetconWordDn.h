#ifndef  __EMETCONWORDC_H__
#define  __EMETCONWORDC_H__

#include "EmetconWordBase.h"

class EmetconWordDn : public EmetconWordBase
{
    public:
        EmetconWordDn();

        void insertData(unsigned char buf[], int size);
        void getBytes(unsigned char buf[], int size);
};


#endif
