/*-----------------------------------------------------------------------------*
*
* File:   cticonnect.h
*
* Date:   1/06/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_device.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2006/03/08 14:54:49 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef  __CTICONNECT_H__
#define  __CTICONNECT_H__
#include "yukon.h"

#include <windows.h>

#include "dlldefs.h"
#include "netports.h"

#define  ERR_CTINEXUS_BASE                                   100000
#define  ERR_CTINEXUS_INVALID_HANDLE                         (ERR_CTINEXUS_BASE + 0)
#define  ERR_CTINEXUS_READTIMEOUT                            (ERR_CTINEXUS_BASE + 1)

/* Nexus state flags.  */
#define  CTINEXUS_STATE_NULL                                 0x00000000    // Not connected.
#define  CTINEXUS_STATE_CONNECTED                            0x00000001
#define  CTINEXUS_STATE_WAITING                              0x00000002

/* Nexus definition flags */
#define  CTINEXUS_FLAG_READEXACTLY                           0x00000001
#define  CTINEXUS_FLAG_READANY                               0x00000002

/* Nexus Types */
#define  CTINEXUS_TYPE_SOCKTYPE                              0x00000010
#define  CTINEXUS_TYPE_UDPTYPE                               0x00000020

/* Nexus defines */
#define  CTINEXUS_INFINITE_TIMEOUT                          LONG_MIN    // MUST be negative to keep us from timing the thing!

class IM_EX_CTIBASE CtiConnect
{
public:
    virtual ~CtiConnect () {}; //The pure virtuals below make this an abstract class, the destructor needs a body.

    virtual ULONG CtiGetNexusState   () { return CTINEXUS_STATE_NULL; }
    virtual INT CTINexusClose        () { return 0; }
    virtual INT CTINexusWrite        (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut) { return 0; }
    virtual INT CTINexusRead         (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut) { return 0; }
    virtual INT CTINexusPeek         (VOID *buf, ULONG len, PULONG BRead) { return 0; }
    
    virtual bool CTINexusValid() const { return false; }
};

#endif   // #ifdef  __CTICONNECT_H__

