
#pragma warning( disable : 4786)
#ifndef __PROT_SA205_H__
#define __PROT_SA205_H__

/*---------------------------------------------------------------------------------*
*
* File:   prot_sa205
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


class IM_EX_PROT CtiProtocolSA205 : public CtiProtocolBase
{
public:

    CtiProtocolSA205();
    CtiProtocolSA205( const CtiProtocolSA205& aRef );
    virtual ~CtiProtocolSA205();

private:
protected:

};
#endif // #ifndef __PROT_SA205_H__
