/*-----------------------------------------------------------------------------*
*
* File:   dev_mct210
*
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct210.cpp-arc  $
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2005/02/25 21:56:32 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>

#include "device.h"
#include "devicetypes.h"
#include "dev_mct210.h"
#include "logger.h"
#include "mgr_point.h"
#include "numstr.h"
#include "porter.h"
#include "pt_numeric.h"
#include "utility.h"


set< CtiDLCCommandStore > CtiDeviceMCT210::_commandStore;

CtiDeviceMCT210::CtiDeviceMCT210()
{
}

CtiDeviceMCT210::CtiDeviceMCT210(const CtiDeviceMCT210 &aRef)
{
    *this = aRef;
}

CtiDeviceMCT210::~CtiDeviceMCT210()
{
}

CtiDeviceMCT210& CtiDeviceMCT210::operator=(const CtiDeviceMCT210 &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}


bool CtiDeviceMCT210::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    //  MCT 210 commands
    cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_MReadPos,
                             (int)MCT210_MReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_MReadPos,
                             (int)MCT210_MReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_KYZ;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT210_PutMReadPos,
                             (int)MCT210_PutMReadLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_DemandPos,
                             (int)MCT210_DemandLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::Scan_Integrity;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_DemandPos,
                             (int)MCT210_DemandLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Disconnect;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_StatusPos,
                             (int)MCT210_StatusLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetStatus_Internal;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_GenStatPos,
                             (int)MCT210_GenStatLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_Reset;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT210_ResetPos,
                             (int)MCT210_ResetLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::GetConfig_Multiplier;
    cs._io      = IO_READ;
    cs._funcLen = make_pair( (int)MCT210_MultPos,
                             (int)MCT210_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutConfig_Multiplier;
    cs._io      = IO_WRITE | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT210_MultPos,
                             (int)MCT210_MultLen );
    _commandStore.insert( cs );


    return failed;
}


bool CtiDeviceMCT210::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceMCT210::initCommandStore();
    }

    DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;             // Copy over the found function!
        length = cs._funcLen.second;              // Copy over the found length!
        io = cs._io;                              // Copy over the found io indicator!

        found = true;
    }
    else                                         // Look in the parent if not found in the child!
    {
        found = Inherited::getOperation(cmd, function, length, io);
    }

    return found;
}


/*
 *  ResultDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT210::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (CtiProtocolEmetcon::Control_Conn):
        case (CtiProtocolEmetcon::Control_Disc):
        {
            CtiRequestMsg newReq(getID(),
                                 "getstatus disconnect noqueue",
                                 InMessage->Return.UserID,
                                 InMessage->Return.TrxID,
                                 InMessage->Return.RouteID,
                                 InMessage->Return.MacroOffset,
                                 InMessage->Return.Attempt);

            newReq.setConnectionHandle((void *)InMessage->Return.Connection);

            CtiCommandParser parse(newReq.CommandString());

            CtiDeviceBase::ExecuteRequest(&newReq, parse, vgList, retList, outList);

            break;
        }

        default:
        {
            status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }

    return status;
}

