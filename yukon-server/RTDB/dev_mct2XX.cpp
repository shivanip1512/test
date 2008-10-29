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
* REVISION     :  $Revision: 1.44 $
* DATE         :  $Date: 2008/10/29 18:16:45 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <windows.h>
#include "devicetypes.h"
#include "dev_mct2XX.h"
#include "logger.h"
#include "porter.h"
#include "pt_numeric.h"
#include "numstr.h"

using Cti::Protocol::Emetcon;


const CtiDeviceMCT2XX::CommandSet CtiDeviceMCT2XX::_commandStore = CtiDeviceMCT2XX::initCommandStore();


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


CtiDeviceMCT2XX::CommandSet CtiDeviceMCT2XX::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::PutConfig_Raw,           Emetcon::IO_Write, 0, -1));  //  this will be filled in by executePutConfig

    //  MCT 2xx common commands
    cs.insert(CommandStore(Emetcon::GetValue_PFCount,        Emetcon::IO_Read,  MCT2XX_PFCountPos,  MCT2XX_PFCountLen));

    cs.insert(CommandStore(Emetcon::PutValue_ResetPFCount,   Emetcon::IO_Write, MCT2XX_PFCountPos,  MCT2XX_PFCountLen));

    cs.insert(CommandStore(Emetcon::GetStatus_Internal,      Emetcon::IO_Read,  MCT2XX_GenStatPos,  MCT2XX_GenStatLen));

    cs.insert(CommandStore(Emetcon::PutStatus_Reset,         Emetcon::IO_Write, MCT2XX_ResetPos,    MCT2XX_ResetLen));

    cs.insert(CommandStore(Emetcon::GetConfig_Multiplier,    Emetcon::IO_Read,  MCT2XX_MultPos,     MCT2XX_MultLen));

    cs.insert(CommandStore(Emetcon::PutConfig_Multiplier,    Emetcon::IO_Write, MCT2XX_MultPos,     MCT2XX_MultLen));

    cs.insert(CommandStore(Emetcon::GetConfig_Options,       Emetcon::IO_Read,  MCT2XX_OptionPos,   MCT2XX_OptionLen));

    cs.insert(CommandStore(Emetcon::GetConfig_Time,          Emetcon::IO_Read,  Memory_TimePos,     Memory_TimeLen));

    cs.insert(CommandStore(Emetcon::PutConfig_TSync,         Emetcon::IO_Write, Memory_TSyncPos,    Memory_TSyncLen));

    cs.insert(CommandStore(Emetcon::PutConfig_UniqueAddress, Emetcon::IO_Write, MCT2XX_UniqueAddressPos, MCT2XX_UniqueAddressLen));

    return cs;
}


bool CtiDeviceMCT2XX::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        CtiDeviceMCT2XX::initCommandStore();
    }

    CommandSet::iterator itr = _commandStore.find(CommandStore(cmd));

    if( itr != _commandStore.end() )
    {
        bst.Function = itr->function;             // Copy over the found function!
        bst.Length   = itr->length;              // Copy over the found length!
        bst.IO       = itr->io;                              // Copy over the found io indicator!

        if( bst.IO == Emetcon::IO_Write && bst.Length )
        {
            bst.IO |= Q_ARMC;
        }

        found = true;
    }
    else                                         // Look in the parent if not found in the child!
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
INT CtiDeviceMCT2XX::ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;


    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Accum:
        case Emetcon::GetValue_KWH:         status = decodeGetValueKWH      (InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_Demand:      status = decodeGetValueDemand   (InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::GetStatus_Internal:   status = decodeGetStatusInternal(InMessage, TimeNow, vgList, retList, outList);     break;

        //  Parent method.
        case Emetcon::GetConfig_Model:      status = decodeGetConfigModel   (InMessage, TimeNow, vgList, retList, outList);     break;

        case Emetcon::GetConfig_Options:    status = decodeGetConfigOptions (InMessage, TimeNow, vgList, retList, outList);     break;

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


INT CtiDeviceMCT2XX::decodeGetValueKWH(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if( InMessage->Sequence == Emetcon::Scan_Accum )
    {
        setScanFlag(ScanRateAccum, false);
    }

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Accumulator Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT    j;
        ULONG  mread = 0;
        double Value;
        string resultString;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;
        CtiPointSPtr    pPoint;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        for(j = 0; j < 3; j++)
        {
            mread = (mread << 8) + InMessage->Buffer.DSt.Message[j];
        }

        pPoint = getDevicePointOffsetTypeEqual( 1, PulseAccumulatorPointType);

        if(pPoint)
        {
            CtiTime pointTime;

            Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM((DOUBLE)mread);

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, PulseAccumulatorPointType, resultString, TAG_POINT_MUST_ARCHIVE);
            if(pData != NULL)
            {
                pointTime -= pointTime.seconds() % 300;
                pData->setTime( pointTime );
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
        else
        {
            resultString = getName() + " / Meter Reading = " + CtiNumStr(mread) + "  --  POINT UNDEFINED IN DB";
            ReturnMsg->setResultString(resultString);
        }

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT CtiDeviceMCT2XX::decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn =  InMessage->EventCode & 0x3fff;
    DSTRUCT *DSt  = &InMessage->Buffer.DSt;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Demand Decode for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateGeneral, false);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!
        INT    demand_interval;
        ULONG  pulses;
        double Value;
        string resultString;
        PointQuality_t quality;
        bool bad_data;

        CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
        CtiPointDataMsg *pData     = NULL;
        CtiPointSPtr    pPoint;

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        pulses = MAKEUSHORT(DSt->Message[1], DSt->Message[0]);

        demand_interval = getDemandInterval();

        //  look for first defined DEMAND accumulator
        pPoint = getDevicePointOffsetTypeEqual( 1, DemandAccumulatorPointType );

        if( checkDemandQuality(pulses, quality, bad_data) )
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
                Value = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM(Value);
            }
        }

        if(pPoint)
        {
            CtiTime pointTime;

            resultString = getName() + " / " + pPoint->getName() + " = " + CtiNumStr(Value,
                                                                                     boost::static_pointer_cast<CtiPointNumeric>(pPoint)->getPointUnits().getDecimalPlaces());

            pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), Value, NormalQuality, DemandAccumulatorPointType, resultString);
            //  correct to beginning of interval

            if(pData != NULL)
            {
                pointTime -= pointTime.seconds() % getDemandInterval();
                pData->setTime( pointTime );
                ReturnMsg->PointData().push_back(pData);
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


INT CtiDeviceMCT2XX::decodeGetStatusInternal( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg      *pData = NULL;

    INT ErrReturn  = InMessage->EventCode & 0x3fff;
    unsigned char *geneBuf = InMessage->Buffer.DSt.Message;

    ULONG pulseCount = 0;
    string resultString;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

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

INT CtiDeviceMCT2XX::decodeGetConfigModel(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    DSTRUCT *DSt   = &InMessage->Buffer.DSt;

    if( !(status = decodeCheckErrorReturn(InMessage, retList, outList)) )
    {
        // No error occured, we must do a real decode!

        INT ssp;
        char rev;
        char temp[80];

        string sspec;
        string options("Options:\n");

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


        ssp = InMessage->Buffer.DSt.Message[4] * 256 + InMessage->Buffer.DSt.Message[0];
        rev = 64 + InMessage->Buffer.DSt.Message[1];

        sspec = "\nSoftware Specification " + CtiNumStr(ssp) + string("  Rom Revision ") + rev + "\n";

        if( InMessage->Buffer.DSt.Message[2] & 0x01 )
        {
            options+= string("  Latched loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x02 )
        {
            options+= string("  Timed loads\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x40 )
        {
            options+= string("  Extended addressing\n");
        }
        if( InMessage->Buffer.DSt.Message[2] & 0x80 )
        {
            options+= string("  Metering of basic kWh\n");
        }

        if( InMessage->Buffer.DSt.Message[3] & 0x01 )
        {
            options+= string("  Time-of-demand\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x04 )
        {
            options+= string("  Load survey\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x08 )
        {
            options+= string("  Full group address support\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x10 )
        {
            options+= string("  Feedback load control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x40 )
        {
            options+= string("  Volt/VAR control\n");
        }
        if( InMessage->Buffer.DSt.Message[3] & 0x80 )
        {
            options+= string("  Capacitor control\n");
        }

        if( (ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
            }

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( sspec + options );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }


    return status;
}


INT CtiDeviceMCT2XX::decodeGetConfigOptions(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT status = NORMAL;

    INT ErrReturn         = InMessage->EventCode & 0x3fff;
    unsigned char *optBuf = InMessage->Buffer.DSt.Message;

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        string options;

        CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;
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

