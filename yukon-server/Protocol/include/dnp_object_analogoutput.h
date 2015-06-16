#pragma once

#include "dnp_objects.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class IM_EX_PROT AnalogOutputStatus : public Object
{
private:
    long   _longValue;
    double _doubleValue;

    union aofu //  analog out flag union, only for Slick's parsing pleasure
    {
        struct
        {
            unsigned char online       : 1;
            unsigned char restart      : 1;
            unsigned char commlost     : 1;
            unsigned char remoteforced : 1;
            unsigned char reserved     : 4;
        };

        unsigned char raw;
    } _flags;

protected:
    AnalogOutputStatus(int group, int variation);

    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    AnalogOutputStatus(int variation);

    enum Variation
    {
        AOS_32Bit = 1,
        AOS_16Bit = 2,
        AOS_SingleFloat = 3,
        AOS_DoubleFloat = 4,
    };

    enum
    {
        Group = 40
    };

    enum
    {
        //  Analog outputs are offset by this amount when they are returned as normal analogs
        AnalogOutputOffset = 10000
    };

    virtual int restore(const unsigned char *buf, int len);
    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


class IM_EX_PROT AnalogOutput : public Object
{
private:
    long   _longValue;
    double _doubleValue;

    unsigned char _status;

protected:
    int restoreVariation(const unsigned char *buf, int len, int variation);
    int serializeVariation(unsigned char *buf, int variation) const;

public:
    AnalogOutput(int variation);

    enum Variation
    {
        AO_32Bit = 1,
        AO_16Bit = 2,
        AO_SingleFloat = 3,
        AO_DoubleFloat = 4,
    };

    enum group
    {
        Group = 41
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
    unsigned char getStatus() const;
    double getValue() const;

    void setControl(double value);
};

}
}
}

