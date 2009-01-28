/*-----------------------------------------------------------------------------*
*
* File:   ctilocalconnect.h
*
* Date:   1/11/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_710.cpp-arc  $
* REVISION     :  $Revision: 1.8.4.1 $
* DATE         :  $Date: 2008/11/13 17:23:50 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef  __CTILOCALCONNECT_H__
#define  __CTILOCALCONNECT_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <limits.h>
#include <queue>

#include <rw\thr\mutex.h>

#include "critical_section.h"
#include "dlldefs.h"
#include "netports.h"
#include "cticonnect.h"

#include "fifo_multiset.h"

template <class Outbound, class Inbound>
class IM_EX_CTIBASE CtiLocalConnect : public CtiConnect
{
private:

    typedef fifo_multiset<Outbound *, ptr_priority_sort<Outbound> > queue_t;

    queue_t _outQueue;
    CtiLocalConnect<Inbound, Outbound> *_directConnection;
    ULONG _nexusState;

    CtiCriticalSection _crit;

    ULONG                   NexusType;        // What is this connection??
    CHAR                    Name[64];         // Text Description of connection

public:

    CtiLocalConnect() {};

    ~CtiLocalConnect();

    enum ReadFlags
    {
        NOFLAG = 0,
        MESSAGE_PEEK
    };

    ULONG CtiGetNexusState ();
    INT   CTINexusClose    ();
    INT   CTINexusWrite    (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut);
    INT   CTINexusRead     (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut);
    INT   CTINexusPeek     (VOID *buf, ULONG len, PULONG BRead);

    bool  CTINexusValid    () const;

    int   CtiLocalConnectOpen ();
    INT   CtiLocalConnectRead (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut, int flags = NOFLAG);

    bool setMatchingConnection( CtiLocalConnect<Inbound, Outbound> &connection );

    void purgeRequest(int request);
};

#include "dsm2.h"

template class IM_EX_CTIBASE CtiLocalConnect<CtiOutMessage, INMESS>;
template class IM_EX_CTIBASE CtiLocalConnect<INMESS, CtiOutMessage>;

#endif   // #ifdef  __CTILOCALCONNECT_H__

