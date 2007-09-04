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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/09/04 16:43:22 $
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

    virtual int  getInboundDataLength( void ) = 0;
    virtual void getInboundData( unsigned char *buf ) = 0;
};


}
}

#endif
