/*-----------------------------------------------------------------------------*
*
* File:   dev_mct22X
*
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct22X.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2004/10/12 20:14:16 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include "devicetypes.h"
#include "dev_mct22X.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"

set< CtiDLCCommandStore > CtiDeviceMCT22X::_commandStore;

CtiDeviceMCT22X::CtiDeviceMCT22X() {}

CtiDeviceMCT22X::CtiDeviceMCT22X(const CtiDeviceMCT22X &aRef)
{
   *this = aRef;
}

CtiDeviceMCT22X::~CtiDeviceMCT22X() {}

CtiDeviceMCT22X& CtiDeviceMCT22X::operator=(const CtiDeviceMCT22X &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


bool CtiDeviceMCT22X::initCommandStore()
{
   bool failed = false;

   CtiDLCCommandStore cs;

   cs._cmd     = CtiProtocolEmetcon::GetConfig_GroupAddress;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT2XX_GroupAddrPos,
                            (int)MCT2XX_GroupAddrLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_GoldSilver;
   cs._io      = IO_WRITE | Q_ARMC;
   cs._funcLen = make_pair( (int)MCT2XX_GroupAddrGoldSilverPos,
                            (int)MCT2XX_GroupAddrGoldSilverLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Bronze;
   cs._io      = IO_WRITE | Q_ARMC;
   cs._funcLen = make_pair( (int)MCT2XX_GroupAddrBronzePos,
                            (int)MCT2XX_GroupAddrBronzeLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::PutConfig_GroupAddr_Lead;
   cs._io      = IO_WRITE | Q_ARMC;
   cs._funcLen = make_pair( (int)MCT2XX_GroupAddrLeadPos,
                            (int)MCT2XX_GroupAddrLeadLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::GetValue_Default;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_MReadPos,
                            (int)MCT22X_MReadLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Scan_Accum;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_MReadPos,
                            (int)MCT22X_MReadLen );
   _commandStore.insert( cs );

//  this meter requires you to subtract the current and previous meter readings to get a 5-minute demand value
   cs._cmd     = CtiProtocolEmetcon::GetValue_Demand;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_DemandPos,
                            (int)MCT22X_DemandLen );
   _commandStore.insert( cs );

   cs._cmd     = CtiProtocolEmetcon::Scan_Integrity;
   cs._io      = IO_READ;
   cs._funcLen = make_pair( (int)MCT22X_DemandPos,
                            (int)MCT22X_DemandLen );
   _commandStore.insert( cs );

   return failed;
}


bool CtiDeviceMCT22X::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
   bool found = false;

   if(_commandStore.empty())  // Must initialize!
   {
      CtiDeviceMCT22X::initCommandStore();
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
INT CtiDeviceMCT22X::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    switch(InMessage->Sequence)
    {
        case (CtiProtocolEmetcon::GetValue_Demand):
        case (CtiProtocolEmetcon::Scan_Integrity):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
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



INT CtiDeviceMCT22X::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setMCTScanPending(ScanRateIntegrity, false);    //    resetScanPending();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT    j;
        ULONG  curead, prevrd;
        double Value;
        RWCString resultString;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;
        CtiPointBase    *pPoint;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        curead = (DSt->Message[3] << 16) |
                 (DSt->Message[4] <<  8) |
                  DSt->Message[5];
        prevrd = (DSt->Message[0] << 16) |
                 (DSt->Message[1] <<  8) |
                  DSt->Message[2];

        if( curead < prevrd )
        {
            //  account for rollover
            curead += 10000000;
        }

        //  figure out the difference between current and previous readings
        Value  = curead - prevrd;
        //  turn raw pulses into a demand reading
        Value *= 12;

        //  look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if(pPoint != NULL)
        {
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM(Value);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
            //  correct to beginning of interval

            if(pData != NULL)
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime( pointTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / Demand = " + CtiNumStr((int)Value) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

   return status;
}



