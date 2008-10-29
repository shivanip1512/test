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
* REVISION     :  $Revision: 1.33 $
* DATE         :  $Date: 2008/10/29 18:16:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>

#include "devicetypes.h"
#include "dev_mct210.h"
#include "logger.h"
#include "numstr.h"
#include "porter.h"
#include "pt_numeric.h"
#include "utility.h"

using Cti::Protocol::Emetcon;


const CtiDeviceMCT210::CommandSet CtiDeviceMCT210::_commandStore = CtiDeviceMCT210::initCommandStore();


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


CtiDeviceMCT210::CommandSet CtiDeviceMCT210::initCommandStore()
{
    CommandSet cs;

    //  MCT 210 commands
    cs.insert(CommandStore(Emetcon::GetValue_KWH,           Emetcon::IO_Read,  MCT210_MReadPos,    MCT210_MReadLen));
    cs.insert(CommandStore(Emetcon::Scan_Accum,             Emetcon::IO_Read,  MCT210_MReadPos,    MCT210_MReadLen));

    cs.insert(CommandStore(Emetcon::PutValue_KYZ,           Emetcon::IO_Write, MCT210_PutMReadPos, MCT210_PutMReadLen));

    cs.insert(CommandStore(Emetcon::GetValue_Demand,        Emetcon::IO_Read,  MCT210_DemandPos,   MCT210_DemandLen));
    cs.insert(CommandStore(Emetcon::Scan_Integrity,         Emetcon::IO_Read,  MCT210_DemandPos,   MCT210_DemandLen));
    cs.insert(CommandStore(Emetcon::GetStatus_Disconnect,   Emetcon::IO_Read,  MCT210_StatusPos,   MCT210_StatusLen));
    cs.insert(CommandStore(Emetcon::GetStatus_Internal,     Emetcon::IO_Read,  MCT210_GenStatPos,  MCT210_GenStatLen));

    cs.insert(CommandStore(Emetcon::PutStatus_Reset,        Emetcon::IO_Write, MCT210_ResetPos,    MCT210_ResetLen));

    cs.insert(CommandStore(Emetcon::GetConfig_Multiplier,   Emetcon::IO_Read,  MCT210_MultPos,     MCT210_MultLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Multiplier,   Emetcon::IO_Write, MCT210_MultPos,     MCT210_MultLen));

    return cs;
}


bool CtiDeviceMCT210::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    bool found = false;

    CommandSet::iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        bst.Function = itr->function;
        bst.Length   = itr->length;
        bst.IO       = itr->io;

        if( bst.IO == Emetcon::IO_Write )
        {
            bst.IO |= Q_ARMC;
        }

        found = true;
    }
    else    // Look in the parent if not found in the child
    {
        found = Inherited::getOperation(cmd, bst);
    }

    return found;
}


/*
 *  ModelDecode MUST decode all CtiDLCCommand_t which are defined in the initCommandStore object.  The only exception to this
 *  would be a child whose decode was identical to the parent, but whose request was done differently..
 *  This MAY be the case for example in an IED scan.
 */
INT CtiDeviceMCT210::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (Emetcon::GetStatus_Disconnect):
        {
            status = decodeGetStatusDisconnect(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::Control_Connect):
        case (Emetcon::Control_Disconnect):
        {
            CtiRequestMsg newReq(getID(),
                                 "getstatus disconnect noqueue",
                                 InMessage->Return.UserID,
                                 InMessage->Return.GrpMsgID,
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
            status = Inherited::ModelDecode(InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }
            break;
        }
    }

    return status;
}

