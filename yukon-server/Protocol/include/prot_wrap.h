#pragma once

#include "prot_base.h"

namespace Cti {
namespace Protocols {


struct Wrap : public Interface
{
    virtual bool send( const std::vector<unsigned char> &buf ) = 0;
    virtual bool recv( void ) = 0;

    virtual bool init( void ) = 0;

    virtual void getInboundData( std::vector<unsigned char> &buf ) = 0;

    virtual unsigned getMaximumPayload() const = 0;
};


}
}
