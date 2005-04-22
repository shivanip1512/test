/*-----------------------------------------------------------------------------*
*
* File:   dev_mct2XX
*
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_mct2XX.cpp-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2005/04/22 19:00:28 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "device.h"
#include "devicetypes.h"
#include "dev_mct2XX.h"
#include "logger.h"
#include "mgr_point.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"

using Cti::Protocol::Emetcon;


set< CtiDLCCommandStore > CtiDeviceMCT2XX::_commandStore;


CtiDeviceMCT2XX::CtiDeviceMCT2XX( ) { }

CtiDeviceMCT2XX::CtiDeviceMCT2XX( const CtiDeviceMCT2XX &aRef )
{
    *this = aRef;
}

CtiDeviceMCT2XX::~CtiDeviceMCT2XX( ) { }

CtiDeviceMCT2XX& CtiDeviceMCT2XX::operator=(const CtiDeviceMCT2XX& aRef)
{
   if( this != &aRef )
   {
       Inherited::operator=( aRef );
   }

   return *this;
}


bool CtiDeviceMCT2XX::initCommandStore()
{
    bool failed = false;

    CtiDLCCommandStore cs;

    cs._cmd     = Emetcon::PutConfig_Raw;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( 0, 0 );  //  this will be filled in by executePutConfig
    _commandStore.insert( cs );

    //  MCT 2xx common commands
    cs._cmd     = Emetcon::GetValue_PFCount;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT2XX_PFCountPos,
                             (int)MCT2XX_PFCountLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutValue_ResetPFCount;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_PFCountPos,
                             (int)MCT2XX_PFCountLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetStatus_Internal;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT2XX_GenStatPos,
                             (int)MCT2XX_GenStatLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutStatus_Reset;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_ResetPos,
                             (int)MCT2XX_ResetLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Multiplier;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT2XX_MultPos,
                             (int)MCT2XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_Multiplier;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT2XX_MultPos,
                             (int)MCT2XX_MultLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Options;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT2XX_OptionPos,
                             (int)MCT2XX_OptionLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::GetConfig_Time;
    cs._io      = Emetcon::IO_Read;
    cs._funcLen = make_pair( (int)MCT2XX_TimePos,
                             (int)MCT2XX_TimeLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_TSync;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( (int)MCT_TSyncPos,
                             (int)MCT_TSyncLen );
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_UniqueAddr;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair((int)MCT2XX_UniqAddrPos,
                            (int)MCT2XX_UniqAddrLen);
    _commandStore.insert( cs );

    cs._cmd     = Emetcon::PutConfig_Raw;
    cs._io      = Emetcon::IO_Write | Q_ARMC;
    cs._funcLen = make_pair( 0, 0 );
    _commandStore.insert( cs );

    return failed;
}


bool CtiDeviceMCT2XX::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceMCT2XX::initCommandStore();
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
INT CtiDeviceMCT2XX::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case (Emetcon::Scan_Accum):
        case (Emetcon::GetValue_Default):
        {
            status = decodeGetValueKWH(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::Scan_Integrity):
        case (Emetcon::GetValue_Demand):
        {
            status = decodeGetValueDemand(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetStatus_Internal):
        {
            status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);
            break;
        }

        case (Emetcon::GetConfig_Model):
        {
            status = decodeGetConfigModel(InMessage, TimeNow, vgList, retList, outList);  // Parent method.
            break;
        }

        case (Emetcon::GetConfig_Options):
        {
            status = decodeGetConfigOptions(InMessage, TimeNow, vgList, retList, outList);
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


INT CtiDeviceMCT2XX::decodeGetValueKWH(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    //  ACH:  are these necessary?  /mskf
    resetScanFreezePending();
    resetScanFreezeFailed();

    setMCTScanPending(ScanRateAccum, false);

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT    j;
        ULONG  mread = 0;
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

        for(j = 0; j < 3; j++)
        {
            mread = (mread << 8) + InMessage->Buffer.DSt.Message[j];
        }

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType);

        if(pPoint != NULL)
        {
            RWTime pointTime;

            Value = ((CtiPointNumeric*)pPoint)->computeValueForUOM((DOUBLE)mread);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     ((CtiPointNumeric *)pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString);
            if(pData != NULL)
            {
                pointTime -= pointTime.seconds() % 300;
                pData->setTime( pointTime );
                ReturnMsg->PointData().insert(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / KYZ 1 = " + CtiNumStr(mread) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT2XX::decodeGetValueDemand(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if( getMCTDebugLevel(MCTDebug_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setMCTScanPending(ScanRateIntegrity, false);
    resetScanPending();

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT    demand_interval;
        ULONG  pulses;
        double Value;
        RWCString resultString;
        PointQuality_t quality;
        bool bad_data;

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

        // 2 byte demand value.  Upper 2 bits are error indicators.
        pulses = MAKEUSHORT(DSt->Message[1], (DSt->Message[0] & 0x3f) );

        demand_interval = getDemandInterval();

        //  look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if( checkDemandQuality( pulses, quality, bad_data ) )
        {
            Value = 0.0;
        }
        else
        {
            //  if no fatal problems with the quality,
            //    adjust for the demand interval
            Value = pulses * (3600 / demand_interval);

            if( pPoint )
            {
                //    and the UOM
                Value = ((CtiPointNumeric *)pPoint)->computeValueForUOM(Value);
            }
        }

        if(pPoint != NULL)
        {
            RWTime pointTime;

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


INT CtiDeviceMCT2XX::decodeGetStatusInternal( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    ULONG pulseCount = 0;
    RWCString resultString;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        resultString  = getName() + " / Internal Status:\n";

            //  TOU
        if( getType() == TYPEMCT224 || getType() == TYPEMCT226 )
        {
            if( geneBuf[2] )        resultString += "  Waiting for Time Sync\n";
            else                    resultString += "  In Time Sync\n";

            if( geneBuf[5] )        resultString += "  Time-Of-Use Metering Active\n";
            else                    resultString += "  Time-Of-Use Metering Halted\n";

            if( geneBuf[3] )        resultString += "  Base Rate Only\n";
            else                    resultString += "  TOU Rates in Effect\n";
        }   //  LP
        else if( getType() == TYPEMCT240 || getType() == TYPEMCT242 ||
                 getType() == TYPEMCT248 || getType() == TYPEMCT250 )
        {
            if( geneBuf[2] )        resultString += "  Waiting for Time Sync\n";
            else                    resultString += "  In Time Sync\n";

            if( geneBuf[5] )        resultString += "  Load Survey Active\n";
            else                    resultString += "  Load Survey Halted\n";

            if( geneBuf[8] & 0x10 ) resultString += "  Load Survey configuration error\n";
        }

        if( geneBuf[6] & 0x01 ) resultString += "  Reading Overflow\n";
        if( geneBuf[6] & 0x02 ) resultString += "  Long Power Fail\n";
        if( geneBuf[6] & 0x04 ) resultString += "  Short Power Fail/Reset\n";
        if( geneBuf[6] & 0x08 ) resultString += "  Tamper latched\n";
        if( geneBuf[6] & 0x10 ) resultString += "  Self Test Error\n";
        if( geneBuf[7] & 0x01 ) resultString += "  NovRam Fault\n";
        if( geneBuf[7] & 0x02 ) resultString += "  \n";
        if( geneBuf[7] & 0x04 ) resultString += "  Bad opcode\n";
        if( geneBuf[7] & 0x08 ) resultString += "  Power Fail Detected By Hardware\n";
        if( geneBuf[7] & 0x10 ) resultString += "  Deadman/Watchdog Reset\n";
        if( geneBuf[7] & 0x20 ) resultString += "  Software Interrupt (malfunction)\n";
        if( geneBuf[7] & 0x40 ) resultString += "  NM1 Interrupt (malfunction)\n";
        if( geneBuf[7] & 0x80 ) resultString += "  \n";

        if( geneBuf[8] & 0x40 ) resultString += "  Cold Load Flag\n";
        if( geneBuf[8] & 0x80 ) resultString += "  Frequency Fault\n";

        if( (getType() == TYPEMCT213 || getType() == TYPEMCT210) &&  //  if it's a 21x
            !((geneBuf[6] & 0x3F) | geneBuf[7] | geneBuf[8]) )       //    and all of these bits are zero
                resultString += "  Normal Operating Mode\n";

        ReturnMsg->setResultString(resultString);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


//
//  This code handles the decode for all 2XX series model configs..
//

INT CtiDeviceMCT2XX::decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if( !(status = decodeCheckErrorReturn(InMessage, retList, outList)) )
    {
        // No error occured, we must do a real decode!

        INT ssp;
        char rev;
        char temp[80];

        RWCString sspec;
        RWCString options("Options:\n");

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp = InMessage->Buffer.DSt.Message[4] * 256 + InMessage->Buffer.DSt.Message[0];
        rev = 64 + InMessage->Buffer.DSt.Message[1];

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + "  Rom Revision " + RWCString::RWCString(rev) + "\n";

        if( InMessage->Buffer.DSt.Message[2] & 0x01 )
        {
            options+= RWCString("  Latched loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x02 )
        {
            options+= RWCString("  Timed loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x40 )
        {
            options+= RWCString("  Extended addressing\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x80 )
        {
            options+= RWCString("  Metering of basic kWh\n");
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x01 )
        {
            options+= RWCString("  Time-of-demand\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options+= RWCString("  Load survey\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x08 )
        {
            options+= RWCString("  Full group address support\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x10 )
        {
            options+= RWCString("  Feedback load control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x40 )
        {
            options+= RWCString("  Volt/VAR control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x80 )
        {
            options+= RWCString("  Capacitor control\n");
        }

        if( (ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }


    return status;
}


INT CtiDeviceMCT2XX::decodeGetConfigOptions(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;

    INT ErrReturn         = InMessage->EventCode & 0x3fff;
    unsigned char *optBuf = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        RWCString options;

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        options = "Device \"" + getName() + "\" configuration:\n";

        if( optBuf[0] == 0 )
            options += "  Normal Addressing\n";
        else if( optBuf[0] == 1 )
            options += "  Unique Addressing Only\n";
        else if( optBuf[0] == 3 )
            options += "  Unique or Universal Only\n";

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }


    return status;
}

