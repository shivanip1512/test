/*-----------------------------------------------------------------------------*
*
* File:   dev_davis
*
* Date:   6/17/2002
*
* PVCS KEYWORDS:
* REVISION     :  $Revision: 1.12.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:39 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cmdparse.h"
#include "connection.h"
#include "dev_davis.h"
#include "dlldefs.h"

#include "logger.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "numstr.h"
#include "porter.h"
#include "pt_base.h"
#include "pt_status.h"

#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "utility.h"

using std::string;
using std::endl;
using std::list;

#define DEBUG_PRINT_DECODE 0

// DLLIMPORT extern CtiConnection VanGoghConnection;

CtiDeviceDavis::CtiDeviceDavis()
{}

INT CtiDeviceDavis::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    INT status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        status = generateScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority);
    }

    return status;
}


INT CtiDeviceDavis::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    INT nRet = ClientErrors::None;

    CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

    if(pMsg != NULL)
    {
        pMsg->insert( -1 );                 // This is the dispatch token and is unimplemented at this time
        pMsg->insert(CtiCommandMsg::OP_DEVICEID);          // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
        pMsg->insert(getID());        // The id (device or point which failed)
        pMsg->insert(ScanRateGeneral);      // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

        pMsg->insert(
                InMessage.ErrorCode
                    ? InMessage.ErrorCode
                    : ClientErrors::GeneralScanAborted);

        retList.push_back( pMsg );
    }

    return nRet;
}

INT CtiDeviceDavis::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    INT nRet = ClientErrors::None;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    switch(parse.getCommand())
    {
    case LoopbackRequest:
    case ScanRequest:
    case GetValueRequest:
        {
            nRet = generateScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
    case ControlRequest:
    case GetStatusRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr),
                                             string("Davis Devices do not support this command (yet?)"),
                                             nRet,
                                             OutMessage->Request.RouteID,
                                             OutMessage->Request.RetryMacroOffset,
                                             OutMessage->Request.Attempt,
                                             OutMessage->Request.GrpMsgID,
                                             OutMessage->Request.UserID,
                                             OutMessage->Request.SOE,
                                             CtiMultiMsg_vec()));

            break;
        }
    }

    return nRet;
}


/* Routine to output Forced scan to a WelCo device */
INT CtiDeviceDavis::generateScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    if(OutMessage)
    {
        /* Load the sectn to scan the stati */
        OutMessage->Buffer.OutMessage[7] = 'L';
        OutMessage->Buffer.OutMessage[8] = 'O';
        OutMessage->Buffer.OutMessage[9] = 'O';
        OutMessage->Buffer.OutMessage[10] = 'P';
        OutMessage->Buffer.OutMessage[11] = 0xff;
        OutMessage->Buffer.OutMessage[12] = 0xff;

        OutMessage->Buffer.OutMessage[13] = 0x0d;

        /* Load all the other stuff that is needed */
        OutMessage->DeviceID = getID();
        OutMessage->Port = getPortID();

        OutMessage->TimeOut = 2;
        OutMessage->OutLength = 7;
        OutMessage->InLength = 19;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence = 0;
        OutMessage->Retry = 2;
        EstablishOutMessagePriority( OutMessage, ScanPriority );


        /* Message is loaded so send it to porter */
        outList.push_back(OutMessage);
        OutMessage = NULL;
    }

    return ClientErrors::None;
}


/* Routine to decode returned Davis message and update database */
INT CtiDeviceDavis::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    /* Misc. definitions */
    ULONG i;
    const char *MyInMessage;

    /* Variables for decoding Davis Message */
    DOUBLE PValue;

    DOUBLE InsideTempF, OutsideTempF, WindSpeedMPH, BarometerInches, InsideHumidity, OutsideHumidity;
    DOUBLE BarometerMillibars, HeatIndexF, OutsideTempC;
    DOUBLE WindChillF, DewPointC, WetBulbC;
    DOUBLE T2, T3, RH2, RH3, DPVP, Logs, VP;

    string tStr;
    CtiPointDataMsg      *pData = NULL;
    CtiReturnMsg         *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi


    if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr)) == NULL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

        return ClientErrors::MemoryAccess;
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);


    MyInMessage = (const PCHAR)(InMessage.Buffer.InMessage + 2);

    /* Walk through the points and decode them */
    for (i = 1; i <= 8; i++)
    {
        /* Check if this point exists */
        CtiPointAnalogSPtr pAnalog = boost::static_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(i, AnalogPointType));

        if(pAnalog)
        {
            /* Point numbers are as follows
                1   =   Inside Temp, F
                2   =   Outside Temp, F
                3   =   Wind Speed, MPH
                4   =   Wind Direction, Degrees
                5   =   Barometric Presure, Inches
                6   =   InSide Humidity, Percent
                7   =   OutSide Humidity, Percent
                8   =   Total Rainfall, Inches
            */

            /* Decode the value and store those important to calculated values... */
            switch (i)
            {
                case 1:
                    /* Inside Temperature */
                    PValue = InsideTempF = (DOUBLE) *((const PSHORT)(MyInMessage)) / 10.0;
                    break;

                case 2:
                    /* Outside Temperature */
                    PValue = OutsideTempF = (DOUBLE) *((const PSHORT)(MyInMessage + 2)) / 10.0;
                    break;

                case 3:
                    /* Wind Speed */
                    PValue = WindSpeedMPH = (DOUBLE) *((const PBYTE)(MyInMessage + 4));
                    break;

                case 4:
                    /* Wind Direction */
                    PValue = (DOUBLE) *((const PSHORT)(MyInMessage + 5));
                    break;

                case 5:
                    /* Barometer */
                    PValue = BarometerInches = (DOUBLE) *((const PSHORT)(MyInMessage + 7)) / 1000.0;
                    break;

                case 6:
                    /* Inside Humidity */
                    PValue = InsideHumidity = (DOUBLE) *((const PBYTE)(MyInMessage + 9));
                    break;

                case 7:
                    /* Outside Humidity */
                    PValue = OutsideHumidity = (DOUBLE) *((const PBYTE)(MyInMessage + 10));
                    break;

                case 8:
                    /* Total Rain */
                    PValue = (DOUBLE) *((const PSHORT)(MyInMessage + 11)) / 100.0;
                    break;
            }

            PValue = PValue * pAnalog->getMultiplier() + pAnalog->getDataOffset();

            tStr = getName() + " point " + getPointOffsetName( i ) + " = " + CtiNumStr(PValue);

            pData = CTIDBG_new CtiPointDataMsg(pAnalog->getPointID(), PValue, NormalQuality, AnalogPointType, tStr);
            if(pData != NULL)
            {
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
    }

    resetScanFlag(ScanRateGeneral);

    /* Now Calculate up the derived values */
    for (i = 100; i <= 111; i++)
    {
        /* Check if this point exists */
        CtiPointAnalogSPtr pAnalog = boost::static_pointer_cast<CtiPointAnalog>(getDevicePointOffsetTypeEqual(i, AnalogPointType));

        if(pAnalog)
        {
            /* Point numbers are as follows
                100 =   Inside Temp. C
                101 =   Outside Temp. C
                102 =   WindSpeed Meters/Second
                103 =   WindSpeed Kilometers/Hour
                104 =   WindSpeed Knots
                105 =   Barometer, Millibars
                106 =   WindChill, F
                107 =   WindChill, C
                108 =   DewPoint, C
                109 =   DewPoint, F
                110 =   Heat Index, F
                111 =   Heat Index, C
            */

            /* Calculate derived values, some are saved for later calculations*/
            switch (i)
            {
                case 100:
                    /* Inside Temperature, C */
                    PValue = (InsideTempF - 32.0) * 5.0 / 9.0;
                    break;

                case 101:
                    /* Outside Temperature, C */
                    PValue = OutsideTempC = (OutsideTempF - 32.0) * 5.0 / 9.0;
                    break;

                case 102:
                    /* Wind Speed, Meters/Second */
                    PValue = WindSpeedMPH * .44704;
                    break;

                case 103:
                    /* Wind Speed, Kilometers/Hour */
                    PValue = WindSpeedMPH * 1.60934;
                    break;

                case 104:
                    /* Wind Speed, Knots */
                    PValue = WindSpeedMPH * 1.15193;
                    break;

                case 105:
                    /* Barometer, Millibars */
                    PValue = BarometerMillibars = BarometerInches * 33.8653;
                    break;

                case 106:
                    /* WindChill, F */
                    PValue = WindChillF = 0.0817 * (3.71 * sqrt (WindSpeedMPH) + 5.81 - 0.25 * WindSpeedMPH) * (OutsideTempF - 91.4) + 91.4;
                    if (WindChillF > OutsideTempF) {
                        PValue = WindChillF = OutsideTempF;
                    }
                    break;

                case 107:
                    /* Windchill, C */
                    PValue = (WindChillF - 32.0) * 5.0 / 9.0;
                    break;

                case 108:
                    /* Dew Point, C */
                    VP = 6.11 * pow (10.0,  (7.5 * OutsideTempC) / (237.7 + OutsideTempC));
                    DPVP = VP * (OutsideHumidity / 100.0);
                    Logs = log10 (DPVP) - log10 (6.11);
                    PValue = DewPointC = (237.7 * Logs) / (7.5 - Logs);
                    break;

                case 109:
                    /* Dew Point, F */
                    PValue = (DewPointC * 9.0 / 5.0) + 32.0;
                    break;

                case 110:
                    if (OutsideTempF < 75.0) {
                        PValue = HeatIndexF = OutsideTempF;
                    }
                    else {
                        /* Heat Index, F */
                        T2 = OutsideTempF * OutsideTempF;
                        T3 = T2 * OutsideTempF;
                        RH2 = OutsideHumidity * OutsideHumidity;
                        RH3 = RH2 * OutsideHumidity;

                        PValue = HeatIndexF = 16.923 + 0.185212 * OutsideTempF + 5.37941 * OutsideHumidity - 0.100254 * OutsideTempF * OutsideHumidity
                            + (0.941695e-2) * T2 + (0.728898e-2) * RH2 + (0.345372e-3) * T2 * OutsideHumidity
                            - (0.814971e-3) * OutsideTempF * RH2 + (0.102102e-4) * T2 * RH2 - (0.345372e-4) * T3
                            + (0.291583e-4) * RH3 + (0.142721e-5) * T3 * OutsideHumidity + (0.197483e-6) * OutsideTempF * RH3
                            - (0.218429e-7) * T3 * RH2 + (0.843296e-9) * T2 * RH3 - (0.481975e-10) * T3 * RH3;

                        if (PValue < OutsideTempF) {
                            PValue = HeatIndexF = OutsideTempF;
                        }
                    }

                    break;

                case 111:
                    /* Heat Index, C */
                    PValue = (HeatIndexF - 32.0) * 5.0 / 9.0;
                    break;

            }

            PValue = PValue * pAnalog->getMultiplier() + pAnalog->getDataOffset();

            tStr = getName() + " point " + getPointOffsetName( i ) + " = " + CtiNumStr(PValue);

            pData = CTIDBG_new CtiPointDataMsg(pAnalog->getPointID(), PValue, NormalQuality, AnalogPointType, tStr);
            if(pData != NULL)
            {
                ReturnMsg->PointData().push_back(pData);
                pData = NULL;  // We just put it on the list...
            }
        }
    }

    if(ReturnMsg != NULL)
    {
        if(!(ReturnMsg->ResultString().empty()) || ReturnMsg->getData().size() > 0)
        {
            retList.push_back( ReturnMsg );
        }
        else
        {
            delete ReturnMsg;
        }
    }

    return ClientErrors::None;
}



string CtiDeviceDavis::getPointOffsetName(int offset)
{
    string offsetname("Offset Unknown");
    /* Point numbers are as follows
        1   =   "Inside Temp, F"
        2   =   "Outside Temp, F"
        3   =   "Wind Speed, MPH"
        4   =   "Wind Direction, Degrees"
        5   =   "Barometric Presure, Inches"
        6   =   "InSide Humidity, Percent"
        7   =   "OutSide Humidity, Percent"
        8   =   "Total Rainfall, Inches"
        100 =   "Inside Temp. C"
        101 =   "Outside Temp. C"
        102 =   "WindSpeed Meters/Second"
        103 =   "WindSpeed Kilometers/Hour"
        104 =   "WindSpeed Knots"
        105 =   "Barometer, Millibars"
        106 =   "WindChill, F"
        107 =   "WindChill, C"
        108 =   "DewPoint, C"
        109 =   "DewPoint, F"
        110 =   "Heat Index, F"
        111 =   "Heat Index, C"
    */

    switch(offset)
    {
    case 1:
        {
            offsetname = string("Inside Temp, F");
            break;
        }
    case 2:
        {
            offsetname = string("Outside Temp, F");
            break;
        }
    case 3:
        {
            offsetname = string("Wind Speed, MPH");
            break;
        }
    case 4:
        {
            offsetname = string("Wind Direction, Degrees");
            break;
        }
    case 5:
        {
            offsetname = string("Barometric Presure, Inches");
            break;
        }
    case 6:
        {
            offsetname = string("InSide Humidity, Percent");
            break;
        }
    case 7:
        {
            offsetname = string("OutSide Humidity, Percent");
            break;
        }
    case 8:
        {
            offsetname = string("Total Rainfall, Inches");
            break;
        }
    case 100:
        {
            offsetname = string("Inside Temp. C");
            break;
        }
    case 101:
        {
            offsetname = string("Outside Temp. C");
            break;
        }
    case 102:
        {
            offsetname = string("WindSpeed Meters/Second");
            break;
        }
    case 103:
        {
            offsetname = string("WindSpeed Kilometers/Hour");
            break;
        }
    case 104:
        {
            offsetname = string("WindSpeed Knots");
            break;
        }
    case 105:
        {
            offsetname = string("Barometer, Millibars");
            break;
        }
    case 106:
        {
            offsetname = string("WindChill, F");
            break;
        }
    case 107:
        {
            offsetname = string("WindChill, C");
            break;
        }
    case 108:
        {
            offsetname = string("DewPoint, C");
            break;
        }
    case 109:
        {
            offsetname = string("DewPoint, F");
            break;
        }
    case 110:
        {
            offsetname = string("Heat Index, F");
            break;
        }
    case 111:
        {
            offsetname = string("Heat Index, C");
            break;
        }
    }

    return offsetname;
}

