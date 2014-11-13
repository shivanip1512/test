#pragma once

#include "dnp_objects.h"
#include "dnp_object_time.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class IM_EX_PROT BinaryInput : public Object
{
private:
    union bifu  //  binary in flag union, named for Slick's parsing pleasure
    {
        struct biflags
        {
            unsigned char online        : 1;
            unsigned char restart       : 1;
            unsigned char commlost      : 1;
            unsigned char remoteforced  : 1;
            unsigned char localforced   : 1;
            unsigned char chatterfilter : 1;
            unsigned char reserved      : 1;
            unsigned char state         : 1;
        } flags;

        unsigned char raw;
    } _bi;

protected:
    BinaryInput(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    BinaryInput(int variation);

    enum Variation
    {
        BI_SingleBitPacked = 1,
        BI_WithStatus      = 2
    };

    enum
    {
        Group = 1
    };

    virtual int restore(const unsigned char *buf, int len);
    int restoreBits(const unsigned char *buf, int bitoffset, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    void setStateValue(long value);
    void setOnlineFlag(bool online);
    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};

class BinaryInputChange : public BinaryInput
{
protected:
    Time      _time;
    TimeDelay _timeRelative;

public:
    BinaryInputChange(int variation);

    enum Variation
    {
        BIC_WithoutTime      =  1,
        BIC_WithTime         =  2,
        BIC_WithRelativeTime =  3
    };

    enum
    {
        Group = 2
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    void setTime(CtiTime timestamp);

    CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};

}
}
}
