
#pragma warning( disable : 4786)
#ifndef __PROT_SASIMPLE_H__
#define __PROT_SASIMPLE_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_sasimple
*
* Class:
* Date:   4/5/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/03/10 19:22:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"
using namespace Cti;


class IM_EX_PROT CtiProtocolSASimple : public Protocol::Interface
{
private:
protected:
public:
    CtiProtocolSASimple();
    CtiProtocolSASimple( const CtiProtocolSASimple& aRef );
    ~CtiProtocolSASimple();
};

#endif // #ifndef __PROT_SASIMPLE_H__
