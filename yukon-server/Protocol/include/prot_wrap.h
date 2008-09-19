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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/09/19 11:40:41 $
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
    virtual bool send( const std::vector<unsigned char> &buf ) = 0;
    virtual bool recv( void ) = 0;

    virtual bool init( void ) = 0;

    virtual void getInboundData( std::vector<unsigned char> &buf ) = 0;

    virtual unsigned getMaximumPayload() const = 0;
};


}
}

#endif
