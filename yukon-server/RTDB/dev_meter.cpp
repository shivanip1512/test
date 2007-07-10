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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2007/07/10 16:59:07 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_meter.h"
using namespace std;

CtiDeviceMeter::CtiDeviceMeter( )
{
   _dstFlagValid = FALSE;
   _dstFlag = FALSE;
}


CtiDeviceMeter::CtiDeviceMeter(const CtiDeviceMeter& aRef)
{
    *this = aRef;
}


CtiDeviceMeter &CtiDeviceMeter::operator=(const CtiDeviceMeter& aRef)
{
    Inherited::operator=(aRef);

    if(this != &aRef)
    {
       MeterGroup = aRef.getMeterGroup();
//         _dstFlag = aRef._dstFlag;
//         _dstFlagValid = aRef._dstFlagValid;
    }
    return *this;
}


inline bool CtiDeviceMeter::isMeter() const   { return true; }

CtiTableDeviceMeterGroup  CtiDeviceMeter::getMeterGroup() const     {  return MeterGroup;  }
CtiTableDeviceMeterGroup& CtiDeviceMeter::getMeterGroup()           {  return MeterGroup;  }

CtiDeviceMeter& CtiDeviceMeter::setMeterGroup( const CtiTableDeviceMeterGroup &aMeterGroup )
{
    MeterGroup = aMeterGroup;
    return *this;
}


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


void CtiDeviceMeter::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceMeterGroup::getSQL(db, keyTable, selector);
}


void CtiDeviceMeter::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


INT CtiDeviceMeter::ExecuteRequest(CtiRequestMsg              *pReq,
                                   CtiCommandParser           &parse,
                                   OUTMESS                   *&OutMessage,
                                   list< CtiMessage* >  &vgList,
                                   list< CtiMessage* >  &retList,
                                   list< OUTMESS* >     &outList)
{
    INT   nRet = NoError;
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
            nRet = NoMethod;
            break;
        }
    }

    if(nRet != NORMAL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << CtiTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                        string(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.MacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.TrxID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        CtiMultiMsg_vec()));
    }

    return nRet;
}


