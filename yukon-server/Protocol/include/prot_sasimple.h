
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/14 18:16:32 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"


class IM_EX_PROT CtiProtocolSASimple : public CtiProtocolBase
{
private:
protected:
public:
    CtiProtocolSASimple();
    CtiProtocolSASimple( const CtiProtocolSASimple& aRef );
    ~CtiProtocolSASimple();
};

#endif // #ifndef __PROT_SASIMPLE_H__
