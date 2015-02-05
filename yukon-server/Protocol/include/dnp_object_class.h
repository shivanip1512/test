#pragma once

#include "dnp_objects.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class Class : public Object
{
public:
    Class(int variation);

    enum Variation
    {
        Class0 = 1,
        Class1 = 2,
        Class2 = 3,
        Class3 = 4
    };

    enum Group
    {
        Group = 60
    };

    int restore(const unsigned char *buf, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;
};

}
}
}
