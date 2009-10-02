#pragma once

#include "dnp_objects.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class IM_EX_PROT InternalIndications : public Object
{
protected:

    InternalIndications(int group, int variation) :
        Object(group, variation)
    {
        _valid = false;
    };

public:

    InternalIndications(int variation) :
        Object(Group, variation)
    {
        _valid = false;
    };

    enum Variation
    {
        II_InternalIndications = 1
    };

    enum
    {
        Group = 80
    };

    virtual int restoreBits(const unsigned char *buf, int bitoffset, int len);
};

}
}
}

