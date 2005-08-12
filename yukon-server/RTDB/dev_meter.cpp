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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/08/12 14:08:07 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_meter.h"


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
inline RWCString CtiDeviceMeter::getMeterGroupName() const              { return getMeterGroup().getCollectionGroup();}
inline RWCString CtiDeviceMeter::getAlternateMeterGroupName() const     { return getMeterGroup().getTestCollectionGroup();}
inline RWCString CtiDeviceMeter::getBillingGroupName() const            { return getMeterGroup().getBillingGroup();}

CtiTableDeviceMeterGroup  CtiDeviceMeter::getMeterGroup() const     {  return MeterGroup;  }
CtiTableDeviceMeterGroup& CtiDeviceMeter::getMeterGroup()           {  return MeterGroup;  }

CtiDeviceMeter& CtiDeviceMeter::setMeterGroup( const CtiTableDeviceMeterGroup &aMeterGroup )
{
    MeterGroup = aMeterGroup;
    return *this;
}


int CtiDeviceMeter::readDSTFile( RWCString &id )
{
    int tmpDSTFlag;
    RWCString tmpID;
    ifstream dstFile( "\\yukon\\server\\config\\meterDSTfix.txt" );

    tmpDSTFlag = FALSE;

    _dstFlagValid = TRUE;

    if( dstFile.is_open() )
    {
        while( !dstFile.eof( ) )
        {
            tmpID.readLine( dstFile );

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
    ULONG nowTime     = RWTime().seconds();
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
                                   RWTPtrSlist< CtiMessage >  &vgList,
                                   RWTPtrSlist< CtiMessage >  &retList,
                                   RWTPtrSlist< OUTMESS >     &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

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
            dout << RWTime() << " Couldn't come up with an operation for device " << getName() << endl;
            dout << RWTime() << "   Command: " << pReq->CommandString() << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.insert(CTIDBG_new CtiReturnMsg(getID(),
                                        RWCString(OutMessage->Request.CommandStr),
                                        resultString,
                                        nRet,
                                        OutMessage->Request.RouteID,
                                        OutMessage->Request.MacroOffset,
                                        OutMessage->Request.Attempt,
                                        OutMessage->Request.TrxID,
                                        OutMessage->Request.UserID,
                                        OutMessage->Request.SOE,
                                        RWOrdered()));
    }

    return nRet;
}


