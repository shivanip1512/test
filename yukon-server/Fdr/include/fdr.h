#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   fdr
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/09/15 21:09:16 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __FDR_H__
#define __FDR_H__

#include "pointdefs.h"

#define  FDR_VALMET "VALMET"
#define  FDR_VALMETMULTI "VALMETMULTI"
#define  FDR_ACS    "ACS"
#define  FDR_SYSTEM "SYSTEM"

#define  FDR_INTERFACE_SEND "Send"
#define  FDR_INTERFACE_SEND_FOR_CONTROL  "Send for control"
#define  FDR_INTERFACE_RECEIVE "Receive"
#define  FDR_INTERFACE_RECEIVE_FOR_CONTROL "Receive for control"
#define  FDR_INTERFACE_RECEIVE_FOR_ANALOG_OUTPUT "Receive for Analog Output"
#define  FDR_INTERFACE_LINK_STATUS "Link Status"

#define FDR_CONNECTED       OPENED
#define FDR_NOT_CONNECTED   CLOSED

#define FDR_NOT_CONNECTED_TO_DISPATCH 1
#define FDR_CLIENT_NOT_REGISTERED 2

// global just makes it that much easier   
typedef enum {
     NotReloaded=0,
     DbChange,
     Periodic,
     Initial,
     ForceReload
} FDRDbReloadReason;




#endif //  #ifndef __FDR_H__
