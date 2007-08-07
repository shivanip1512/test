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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2007/08/07 19:56:17 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __PROT_SA105_H__
#define __PROT_SA105_H__


#include "cmdparse.h"
#include "dlldefs.h"
#include "dsm2.h"
#include "pointtypes.h"
#include "prot_base.h"


class IM_EX_PROT CtiProtocolSA105 : public Cti::Protocol::Interface
{
public:

    CtiProtocolSA105();
    CtiProtocolSA105( const CtiProtocolSA105& aRef );
    virtual ~CtiProtocolSA105();

private:
protected:

};

#endif // #ifndef __PROT_SA105_H__
