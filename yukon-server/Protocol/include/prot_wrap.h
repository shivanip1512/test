/*-----------------------------------------------------------------------------*
*
* File:   prot_wrap
*
* Class:  Cti::Protocol::Wrap
* Date:   2007/08/30
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/03/31 21:17:35 $
*
* Copyright (c) 2007 Cannon Technologies. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_WRAP_H__
#define __PROT_WRAP_H__
#pragma warning( disable : 4786)

#include "prot_base.h"

namespace Cti       {
namespace Protocol  {


class Wrap : public Interface
{
public:
    virtual bool send( const unsigned char *payload, unsigned len ) = 0;
    virtual bool recv( void ) = 0;

    virtual bool init( void ) = 0;

    virtual unsigned getInboundDataLength( void ) const = 0;
    virtual void getInboundData( unsigned char *buf ) = 0;

    virtual unsigned getMaximumPayload() const = 0;
};


}
}

#endif
