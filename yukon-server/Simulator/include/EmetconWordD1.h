#ifndef  __EMETCONWORDD1_H__
#define  __EMETCONWORDD1_H__

#include "EmetconWordBase.h"

class EmetconWordD1 : public EmetconWordBase
{
    public:
        EmetconWordD1();

        void insertData(unsigned char buf[], int size);
        void setMctAddress(int mctAddress);
        void setNumberOfRepeaters(int num);

        void getBytes(unsigned char buf[], int size);

    private:
        int mctAddress;
        int numberOfRepeaters;
};


#endif
