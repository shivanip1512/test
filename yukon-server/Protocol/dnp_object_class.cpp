#include "precompiled.h"


#include "dnp_object_class.h"
#include "logger.h"

using std::endl;

namespace Cti {
namespace Protocols {
namespace DNP {

Class::Class(int variation) : Object(Group, variation)
{

}


int Class::restore(const unsigned char *buf, int len)
{
    return 0;
}


int Class::serialize(unsigned char *buf) const
{
    return 0;
}


int Class::getSerializedLen(void) const
{
    const int variation = getVariation();

    switch(variation)
    {
        case Class0:
        case Class1:
        case Class2:
        case Class3:
        {
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "unknown variation ("<< variation <<")");
        }
    }

    return 0;
}

}
}
}

