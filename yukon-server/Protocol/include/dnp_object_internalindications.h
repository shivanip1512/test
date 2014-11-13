#pragma once

#include "dnp_objects.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class IM_EX_PROT InternalIndications : public Object
{
private:

    bool _value;

protected:

    InternalIndications(int group, int variation) :
        Object(group, variation)
    {
        _valid = false;
        _value = false;
    };

public:

    InternalIndications(int variation) :
        Object(Group, variation)
    {
        _valid = false;
        _value = false;
    };

    enum Variation
    {
        II_InternalIndications = 1
    };

    enum
    {
        Group = 80
    };

    void setValue(bool value);

    virtual int serialize(unsigned char *buf) const;
    virtual int getSerializedLen(void) const;

    virtual int restoreBits(const unsigned char *buf, int bitoffset, int len);
};

}
}
}

