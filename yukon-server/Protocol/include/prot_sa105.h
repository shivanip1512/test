
#pragma warning( disable : 4786)
#ifndef __PROT_SA105_H__
#define __PROT_SA105_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_sa105
*
* Class:
* Date:   4/5/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/29 19:58:50 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
//using namespace std;

#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"

class IM_EX_PROT CtiProtocolSA105 : public CtiProtocolBase
{
public:

    CtiProtocolSA105();
    CtiProtocolSA105( const CtiProtocolSA105& aRef );
    virtual ~CtiProtocolSA105();

private:
protected:

};

#endif // #ifndef __PROT_SA105_H__
