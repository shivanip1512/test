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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/06/01 15:15:23 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


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

CtiDeviceMCT210::CtiDeviceMCT210() {  }

CtiDeviceMCT210::CtiDeviceMCT210(const CtiDeviceMCT210 &aRef)
{
   *this = aRef;
}

CtiDeviceMCT210::~CtiDeviceMCT210() {  }

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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

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

        case (CtiProtocolEmetcon::GetValue_Frozen):
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


INT CtiDeviceMCT210::decodeGetStatusDisconnect(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    RWCString discStr, resultStr;
    double Value;
    CtiPointBase *pPoint;

    //  ACH:  are these needed?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT disc;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;

        decodeStati(disc, MCT_STATUS_DISCONNECT, InMessage->Buffer.DSt.Message);

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        /*
         *  Now sweep the points on this device looking for any status points!  Each gets updated to dispatch via retList.
         */

        Value = CLOSED;

        switch(disc)
        {
            case 0:
            {
                Value = CLOSED;
                discStr = "SERVICE CONNECT ENABLED";
                break;
            }
            case 3:
            {
                Value = OPENED;
                discStr = "SERVICE DISCONNECTED";
                break;
            }
            default:
            {
                Value = INVALID;
                discStr = "UNKNOWN / No Disconnect Status";
                break;
            }
        }

        LockGuard guard(monitor());               // Lock the MCT device!

        pPoint = getDevicePointOffsetTypeEqual(1, StatusPointType);

        if(pPoint != NULL)
        {
            RWRecursiveLock<RWMutexLock>::LockGuard pGuard( pPoint->getMux() );

            resultStr = getName() + " / " + pPoint->getName() + " / " + discStr;

            /*
            *  Send this value to requestor via retList.
            */

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, StatusPointType, resultStr, TAG_POINT_MUST_ARCHIVE);

            if(pData != NULL)
            {
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultStr = getName( ) + " / " + discStr + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultStr);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

void CtiDeviceMCT210::decodeStati(INT &stat, INT which, BYTE *Data)
{
    switch(which)
    {
        case MCT_STATUS_DISCONNECT:
        {
            if(getType() == TYPEMCT213)
            {
                switch( Data[0] & (STATUS2_BIT | STATUS1_BIT))
                {
                    case STATUS2_BIT:
                        stat = 0;               // Connected.
                        break;

                    case STATUS1_BIT:
                        stat = 3;               // Disconnected. (keep it like the 310 - 4 state.)
                        break;

                    //  will catch all malformed cases (no bits, all bits)
                    default:
                        stat = -1;
                        break;
                }
            }

            break;
        }

        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

