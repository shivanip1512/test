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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/04/20 17:15:13 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef  __CTILOCALCONNECT_H__
#define  __CTILOCALCONNECT_H__

#include <windows.h>
#include <limits.h>
#include <queue>

#include <rw\thr\mutex.h>

#include "critical_section.h"
#include "dlldefs.h"
#include "netports.h"
#include "cticonnect.h"

using namespace std;
struct DirectDataKeeper
{
    BYTE *data;
    UINT len;
};

enum ReadFlags
{
    NOFLAG = 0,
    MESSAGE_PEEK
};

class IM_EX_CTIBASE CtiLocalConnect : public CtiConnect
{
private:
    typedef std::queue<DirectDataKeeper> DataKeeperQueue;

    DataKeeperQueue _outQueue;
    CtiLocalConnect *_directConnection;
    ULONG _nexusState;

    CtiCriticalSection _crit;

public:
    CtiLocalConnect()
    {};

    ~CtiLocalConnect();

    ULONG                   NexusType;        // What is this connection??
    CHAR                    Name[64];         // Text Description of connection

    ULONG CtiGetNexusState ();
    INT   CTINexusClose    ();
    INT   CTINexusWrite    (VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut);
    INT   CTINexusRead     (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut);
    INT   CTINexusPeek     (VOID *buf, ULONG len, PULONG BRead);

    bool  CTINexusValid    () const;

    int   CtiLocalConnectOpen    ();
    INT   CtiLocalConnectRead    (VOID *buf, ULONG len, PULONG BRead, LONG TimeOut, int flags = NOFLAG);

    bool setMatchingConnection( CtiLocalConnect &connection );
};

#endif   // #ifdef  __CTILOCALCONNECT_H__

