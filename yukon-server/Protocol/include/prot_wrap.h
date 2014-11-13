#pragma once

#include "prot_base.h"

namespace Cti {
namespace Protocols {


class Wrap : public Interface
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Wrap(const Wrap&);
    Wrap& operator=(const Wrap&);

public:

    Wrap() {}

    virtual bool send( const std::vector<unsigned char> &buf ) = 0;
    virtual bool recv( void ) = 0;

    virtual bool init( void ) = 0;

    virtual void getInboundData( std::vector<unsigned char> &buf ) = 0;

    virtual unsigned getMaximumPayload() const = 0;
};


}
}
