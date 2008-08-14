#ifndef  __EMETCONWORDBASE_H__
#define  __EMETCONWORDBASE_H__

const int defaultWordSize = 7;

class EmetconWordBase
{
    public:
        EmetconWordBase(unsigned char * buf, int size);
        EmetconWordBase();
        ~EmetconWordBase();

        unsigned char getType();

    protected:
        unsigned char * _buffer;
        int _size;
};


#endif
