/*-----------------------------------------------------------------------------*
*
* File:   slctprt
*
* Class:
* Date:   9/26/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/slctprt.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:18 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __SLCTPRT_H__
#define __SLCTPRT_H__
#pragma warning( disable : 4786)

#include <rw/db/db.h>

#include "dlldefs.h"
#include "dev_base.h"
#include "rte_base.h"
#include "pt_base.h"
#include "port_base.h"

IM_EX_PRTDB CtiPort*       PortFactory(RWDBReader &rdr);

#endif // #ifndef __SLCTPRT_H__
