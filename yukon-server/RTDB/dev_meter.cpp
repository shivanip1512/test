/*-----------------------------------------------------------------------------*
*
* File:   dev_meter
*
* Class:  CtiDeviceMeter
* Date:   10/8/2001
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_xcu.cpp-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <fstream>
#include "dev_meter.h"
using namespace std;

CtiDeviceMeter::CtiDeviceMeter( )
{
   _dstFlagValid = FALSE;
   _dstFlag = FALSE;
}

inline bool CtiDeviceMeter::isMeter() const   { return true; }

int CtiDeviceMeter::readDSTFile( string &id )
{
    int tmpDSTFlag;
    string tmpID;
    ifstream dstFile( "\\yukon\\server\\config\\meterDSTfix.txt" );

    tmpDSTFlag = FALSE;

    _dstFlagValid = TRUE;

    if( dstFile.is_open() )
    {
        while( !dstFile.eof( ) )
        {
            //tmpID.readLine( dstFile );
            getline(dstFile,tmpID,'\n');
            if( tmpID == id )
            {
                tmpDSTFlag = TRUE;
            }
        }

        dstFile.close();
    }

    return tmpDSTFlag;
}

string CtiDeviceMeter::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate, IED.password, IED.slaveaddress, DMG.meternumber "
                                   "FROM Device DV, DeviceMeterGroup DMG, DeviceIED IED, DeviceDirectCommSettings CS, "
                                     "YukonPAObject YP LEFT OUTER JOIN DeviceDialupSettings DUS ON "
                                     "YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = DMG.deviceid AND YP.paobjectid = IED.deviceid "
                                     "AND YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void CtiDeviceMeter::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    MeterGroup.DecodeDatabaseReader(rdr);
}


bool CtiDeviceMeter::shouldRetrieveLoadProfile(ULONG &aLPTime, int aIntervalLength)
{
    ULONG nowTime     = CtiTime().seconds();
    ULONG deltaTime   = (nowTime - aLPTime);
    bool retVal;

    /**********************
    * if our last lp time is greater than our current time
    * the clock in the device must be ahead of our computer
    ***********************
    */
    if(aLPTime > nowTime)
    {
        retVal = false;
    }
    else if(deltaTime > (aIntervalLength * 60 +1))
    {
        retVal = true;

        // make sure we aren't grabbing more than 31 days
        if(deltaTime > (31 * 86400))
        {
            aLPTime = nowTime - (31 * 86400);
        }
    }
    else
    {
        retVal = false;
    }
    return retVal;
}


YukonError_t CtiDeviceMeter::ExecuteRequest(CtiRequestMsg     *pReq,
                                            CtiCommandParser  &parse,
                                            OUTMESS          *&OutMessage,
                                            CtiMessageList    &vgList,
                                            CtiMessageList    &retList,
                                            OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    bool found = false;


    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            nRet = executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }

        default:
        {
            nRet = ClientErrors::NoMethod;
            break;
        }
    }

    if( nRet )
    {
        CTILOG_ERROR(dout, "Couldn't come up with an operation for device "<< getName() <<
                endl <<"Command: "<< pReq->CommandString()
                );

        resultString = "NoMethod or invalid command.";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                        string(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.RetryMacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.GrpMsgID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        CtiMultiMsg_vec()));
    }

    return nRet;
}


