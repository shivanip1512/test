/*-----------------------------------------------------------------------------*
*
* File:   trx_info
*
* Class:  CtiTransmitterInfo
* Date:   3/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/trx_info.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/03/13 19:36:18 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TRX_INFO_H__
#define __TRX_INFO_H__
#pragma warning( disable : 4786)

#include <windows.h>

#include "logger.h"
#include "dsm2.h"
#include "queues.h"
#include "mutex.h"
#include "porter.h"

class CtiTransmitterInfo
{
public:

    CtiMutex         _statMux;
    UINT             Status;
    INT              Type;


    REMOTESEQUENCE   RemoteSequence;                   // Used by WELCORTUs to track sequencing...?
    USHORT           FiveMinuteCount;
    ULONG            StageTime;
    ULONG            NextCommandTime;
    ULONG            LCUFlags;
    OUTMESS          *ControlOutMessage;

private:

public:

    CtiTransmitterInfo() :
        Type(-1),
        Status(0),
        StageTime(0),
        FiveMinuteCount(0),
        NextCommandTime(0),
        LCUFlags(0),
        ControlOutMessage(NULL)
    {
        RemoteSequence.Reply   = 0;
    }

    CtiTransmitterInfo(const CtiTransmitterInfo& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiTransmitterInfo()
    {
        if(ControlOutMessage != NULL)
        {
            delete ControlOutMessage;
            ControlOutMessage = NULL;
        }
    }

    CtiTransmitterInfo& operator=(const CtiTransmitterInfo& aRef)
    {
        if(this != &aRef)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        return *this;
    }

    /* Routine to set a status bit with Mutex protection */
    INT SetStatus (USHORT Mask)
    {
        CtiLockGuard< CtiMutex > guard(_statMux);
        Status |= Mask;
        return(NORMAL);
    }

    /* Routine to clear a status bit with Mutex protection */
    INT ClearStatus (USHORT Mask)
    {
        CtiLockGuard< CtiMutex > guard(_statMux);
        Status &= ~Mask;
        return(NORMAL);
    }

    /* Routine to set a status bit with Mutex protection */
    INT GetStatus (USHORT Mask)
    {
        CtiLockGuard< CtiMutex > guard(_statMux);
        return(Status & Mask);
    }

};
#endif // #ifndef __TRX_INFO_H__
