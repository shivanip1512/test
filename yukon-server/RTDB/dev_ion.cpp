/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.cpp
 *
 * Class:  CtiDeviceION
 * Date:   07/02/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"

#include "dev_ion.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "logger.h"
#include "guard.h"

#include "utility.h"

#include "dllyukon.h"
#include "cparms.h"
#include "numstr.h"

CtiDeviceION::CtiDeviceION()
{
//    resetIONScansPending();
}

CtiDeviceION::CtiDeviceION(const CtiDeviceION &aRef)
{
   *this = aRef;
}

CtiDeviceION::~CtiDeviceION() {}

CtiDeviceION &CtiDeviceION::operator=(const CtiDeviceION &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


RWCString CtiDeviceION::getMeterGroupName() const
{
    return _collectionGroup;
}


RWCString CtiDeviceION::getAlternateMeterGroupName() const
{
    return _testCollectionGroup;
}


void CtiDeviceION::setMeterGroupData( const RWCString &collectionGroup, const RWCString &testCollectionGroup, const RWCString &meterNumber, const RWCString &billingGroup)
{
    _collectionGroup     = collectionGroup;
    _testCollectionGroup = testCollectionGroup;
    _meterNumber         = meterNumber;
    _billingGroup        = billingGroup;
}


CtiProtocolBase *CtiDeviceION::getProtocol( void ) const
{
    return (CtiProtocolBase *)&_ion;
}


/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceION::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


INT CtiDeviceION::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    bool found = false;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        {
            switch(parse.getiValue("scantype"))
            {
                case ScanRateStatus:
                case ScanRateGeneral:
                {
                    if( pReq->CommandString().contains("post_control", RWCString::ignoreCase) )
                    {
                        //  post-control scan
                        _ion.setCommand(CtiProtocolION::Command_ExceptionScanPostControl);
                    }
                    else
                    {
                        //  normal scan
                        _ion.setCommand(CtiProtocolION::Command_ExceptionScan);
                    }

                    found = true;

                    break;
                }

                case ScanRateAccum:
                {
                    //  same as getstatus eventlog
                    initEventLogPosition();

                    _ion.setCommand(CtiProtocolION::Command_EventLogRead);

                    found = true;

                    break;
                }

                case ScanRateIntegrity:
                default:
                {
                    _ion.setCommand(CtiProtocolION::Command_IntegrityScan);

                    found = true;

                    break;
                }
            }

            break;
        }

        case GetStatusRequest:
        {
            if( parse.isKeyValid("eventlog") )
            {
                initEventLogPosition();

                _ion.setCommand(CtiProtocolION::Command_EventLogRead);

                found = true;
            }

            break;
        }

        case ControlRequest:
        {
            bool has_offset = false;
            unsigned int offset;
            CtiPointBase *point;

            if( parse.getiValue("point") > 0 )
            {
                if( (point = getDevicePointEqual(parse.getiValue("point"))) != NULL && point->isStatus() )
                {
                    if( ((CtiPointStatus *)point)->getPointStatus().getControlType() == NormalControlType )
                    {
                        offset = ((CtiPointStatus *)point)->getPointStatus().getControlOffset();
                        has_offset = true;
                    }
                }
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");
                has_offset = true;
            }

            if( parse.getFlags() & CMD_FLAG_CTL_CLOSE && has_offset )
            {
                if( gConfigParms.getValueAsString("DUKE_ISSG").compareTo("true", RWCString::ignoreCase) == 0 )
                {
                    if( offset == 20 || offset == 21 )
                    {
                        pReq->setCommandString(pReq->CommandString() + " duke_issg_start");

                        //  call propageteRequest to put the command string into
                        //    pReq again.
                        //  is this a bit hairy?  i think it's okay, but...
                        propagateRequest(OutMessage, pReq);
                    }
                    else if( offset == 22 )
                    {
                        pReq->setCommandString(pReq->CommandString() + " duke_issg_stop");

                        propagateRequest(OutMessage, pReq);
                    }
                }

                _ion.setCommand(CtiProtocolION::Command_ExternalPulseTrigger, offset);

                _postControlScanCount = 0;

                found = true;
            }

            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command. Command = " << parse.getCommand() << endl;
            }
            nRet = NoMethod;

            break;
        }
    }


    if( found )
    {
        OutMessage->Port     = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        OutMessage->Retry    = IONRetries;
        OutMessage->Sequence = _ion.getCommand();
        _ion.sendCommRequest( OutMessage, outList );
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;

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



void CtiDeviceION::initEventLogPosition( void )
{
    if( _ion.getEventLogLastPosition() == 0 )
    {
        CtiPointAnalog *tmpPoint;
        unsigned long   lastRecordPosition;

        tmpPoint = (CtiPointAnalog *)getDevicePointOffsetTypeEqual(2600, AnalogPointType);

        if( tmpPoint != NULL )
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWCString sql       = RWCString("select value from rawpointhistory ") +
                                  "where pointid=" + CtiNumStr(tmpPoint->getPointID()) + " " +
                                        "and timestamp = (select max(timestamp) from rawpointhistory where pointid=" + CtiNumStr(tmpPoint->getPointID()) + ")";
            RWDBResult results  = conn.executeSql( sql );
            RWDBTable  resTable = results.table();

            if(!results.isValid())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ERROR **** RWDB Error #" << results.status().errorCode() << " in query:" << endl;
                dout << sql << endl << endl;
            }

            RWDBReader rdr = resTable.reader();

            if(rdr() && rdr.isValid())
            {
                rdr >> lastRecordPosition;
                _ion.setEventLogLastPosition(lastRecordPosition);
            }
            else
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint: Invalid Reader/No RawPointHistory for EventLog Point - reading ALL events **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
}


/*
void CtiDeviceION::resetIONScansPending( void )
{
    _scanGeneralPending     = false;
    _scanIntegrityPending   = false;
    _scanAccumulatorPending = false;
}


void CtiDeviceION::setIONScanPending(int scantype, bool pending)
{
    switch(scantype)
    {
        case ScanRateGeneral:   _scanGeneralPending     = pending;  break;
        case ScanRateIntegrity: _scanIntegrityPending   = pending;  break;
        case ScanRateAccum:     _scanAccumulatorPending = pending;  break;

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}


bool CtiDeviceION::clearedForScan(int scantype)
{
    bool status = false;

    switch(scantype)
    {
        case ScanRateGeneral:
        {
            status = !_scanGeneralPending;
            break;
        }
        case ScanRateIntegrity:
        {
            status = !_scanIntegrityPending;
            break;
        }
        case ScanRateAccum:
        {
            status = !_scanAccumulatorPending;  //  MSKF 2003-01-31 true; // CGP 032101  (!isScanFreezePending()  && !isScanResetting());
            break;
        }
        case ScanRateLoadProfile:
        {
           status = true;
           break;
        }
    }

    status = validatePendingStatus(status, scantype);

    return status;
}


void CtiDeviceION::resetForScan(int scantype)
{
    // OK, it is five minutes past the time I expected to have scanned this bad boy..
    switch(scantype)
    {
        case ScanRateGeneral:
        case ScanRateIntegrity:
        case ScanRateAccum:
        {
            setIONScanPending(scantype, false);

            if(isScanFreezePending())
            {
                resetScanFreezePending();
                setScanFreezeFailed();
            }

            if(isScanPending())
            {
                resetScanPending();
            }

            if(isScanResetting())
            {
                resetScanResetting();
                setScanResetFailed();
            }
            break;
        }
    }
}
*/


INT CtiDeviceION::GeneralScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

//    setIONScanPending(ScanRateGeneral, true);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}



INT CtiDeviceION::IntegrityScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan integrity");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

//    setIONScanPending(ScanRateIntegrity, true);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT CtiDeviceION::AccumulatorScan( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan accumulator");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Accumulator (EventLog) Scan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan accumulator");

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

//    setIONScanPending(ScanRateAccum, true);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


int CtiDeviceION::ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> pointData;
    RWTPtrSlist<CtiSignalMsg>    eventData;

    resetScanPending();

    if( !ErrReturn && !_ion.recvCommResult(InMessage, outList) )
    {
        if( _ion.hasInboundData() )
        {
            _ion.getInboundData(pointData, eventData);
        }

        processInboundData(InMessage, TimeNow, vgList, retList, outList, pointData, eventData);

        pointData.clear();
        eventData.clear();

        switch( _ion.getCommand() )
        {
            case CtiProtocolION::Command_ExternalPulseTrigger:
            {
                if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                 "scan general post_control",
                                                                 InMessage->Return.UserID,
                                                                 InMessage->Return.TrxID,
                                                                 InMessage->Return.RouteID,
                                                                 InMessage->Return.MacroOffset,
                                                                 InMessage->Return.Attempt);

                RWCString commandStr(InMessage->Return.CommandStr);

                if( commandStr.contains("duke_issg_start", RWCString::ignoreCase) )
                {
                    newReq->setCommandString(newReq->CommandString() + " duke_issg_start");
                }
                else if( commandStr.contains("duke_issg_stop", RWCString::ignoreCase) )
                {
                    newReq->setCommandString(newReq->CommandString() + " duke_issg_stop");
                }

                newReq->setMessagePriority(15);

                newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                CtiCommandParser parse(newReq->CommandString());

                CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                delete newReq;

                break;
            }

            case CtiProtocolION::Command_ExceptionScanPostControl:
            {
                if( _postControlScanCount < IONPostControlScanMax )
                {
                    RWCString commandStr(InMessage->Return.CommandStr);

                    if( commandStr.contains("duke_issg_start", RWCString::ignoreCase) )
                    {
                        if( _ion.hasPointUpdate(StatusPointType, 1) && _ion.getPointUpdateValue(StatusPointType, 1) == 0 &&
                            _ion.hasPointUpdate(StatusPointType, 2) && _ion.getPointUpdateValue(StatusPointType, 2) == 0 )
                        {
                            CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                             "scan general post_control duke_issg_start",
                                                                             InMessage->Return.UserID,
                                                                             InMessage->Return.TrxID,
                                                                             InMessage->Return.RouteID,
                                                                             InMessage->Return.MacroOffset,
                                                                             InMessage->Return.Attempt);

                            newReq->setMessagePriority(15);

                            newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                            CtiCommandParser parse(newReq->CommandString());

                            CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                            ++_postControlScanCount;

                            delete newReq;
                        }
                    }
                    else if( commandStr.contains("duke_issg_stop", RWCString::ignoreCase) )
                    {
                        if( _ion.hasPointUpdate(StatusPointType, 1) && _ion.getPointUpdateValue(StatusPointType, 1) != 0 ||
                            _ion.hasPointUpdate(StatusPointType, 2) && _ion.getPointUpdateValue(StatusPointType, 2) != 0 )
                        {
                            CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                             "scan general post_control duke_issg_stop",
                                                                             InMessage->Return.UserID,
                                                                             InMessage->Return.TrxID,
                                                                             InMessage->Return.RouteID,
                                                                             InMessage->Return.MacroOffset,
                                                                             InMessage->Return.Attempt);

                            newReq->setMessagePriority(15);

                            newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                            CtiCommandParser parse(newReq->CommandString());

                            CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                            ++_postControlScanCount;

                            delete newReq;
                        }
                    }
                }

                break;
            }

            case CtiProtocolION::Command_EventLogRead:
            {
                if( !_ion.areEventLogsComplete() )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - submitting request for additional event logs **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                     "getstatus eventlogs",
                                                                     InMessage->Return.UserID,
                                                                     InMessage->Return.TrxID,
                                                                     InMessage->Return.RouteID,
                                                                     InMessage->Return.MacroOffset,
                                                                     InMessage->Return.Attempt);

                    newReq->setMessagePriority(15);

                    newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                    CtiCommandParser parse(newReq->CommandString());

                    CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                    delete newReq;
                }
    /*            else
                {
                    setIONScanPending(ScanRateAccum, false);
                }*/

                break;
            }

    /*        case CtiProtocolION::Command_ExceptionScan:
            {
                setIONScanPending(ScanRateGeneral, false);

                break;
            }

            case CtiProtocolION::Command_IntegrityScan:
            {
                setIONScanPending(ScanRateIntegrity, false);

                break;
            }*/
        }

        _ion.clearInboundData();
    }
    else
    {
        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       getName() + " / operation failed",
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.append(retMsg);
    }

    return ErrReturn;
}


void CtiDeviceION::processInboundData( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList,
                                       RWTPtrSlist<CtiPointDataMsg> &points, RWTPtrSlist<CtiSignalMsg> &events )
{
    CtiReturnMsg *retMsg, *vgMsg;
    RWTPtrSlist<CtiPointDataMsg>::iterator pt_iter;
    RWTPtrSlist<CtiSignalMsg>::iterator    ev_iter;

    CtiCommandParser parse(InMessage->Return.CommandStr);

    retMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);
    vgMsg  = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    retMsg->setUserMessageId(InMessage->Return.UserID);
    vgMsg->setUserMessageId (InMessage->Return.UserID);

    for( pt_iter = points.begin();  pt_iter != points.end(); ++pt_iter )
    {
        CtiPointDataMsg *tmpMsg;
        CtiPointBase    *point;
        double           value;
        RWCString        resultString;

        tmpMsg = *pt_iter;

        //  !!! tmpMsg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
        {
            tmpMsg->setId(point->getID());

            //  generate the point update string, if applicable
            if( point->isNumeric() )
            {
                value = ((CtiPointNumeric *)point)->computeValueForUOM(tmpMsg->getValue());
                tmpMsg->setValue(value);

                //tmpMsg->getString()
                resultString = getName() + " / " + point->getName() + ": " + CtiNumStr(tmpMsg->getValue(), ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
            }
            else if( point->isStatus() )
            {
                resultString = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), tmpMsg->getValue());
            }
            else
            {
                resultString = "";
            }

            tmpMsg->setString(resultString);

            if( !useScanFlags() )  //  if we're not Scanner, send it to VG as well (scanner will do this on his own)
            {
                //  maybe (parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE)) someday
                vgMsg->PointData().append(tmpMsg->replicateMessage());
            }

            retMsg->PointData().append(tmpMsg);
        }
    }

    for( ev_iter = events.begin(); ev_iter != events.end();  ++ev_iter )
    {
        CtiSignalMsg *tmpSignal;
        CtiPointBase *point;

        tmpSignal = *ev_iter;

        if( (point = getDevicePointOffsetTypeEqual(tmpSignal->getId(), AnalogPointType)) != NULL )
        {
            tmpSignal->setId(point->getID());

            //  only send to Dispatch
            vgList.append(tmpSignal->replicateMessage());
        }
    }

    //  not too kosher, but gets the job done
    if( parse.getCommandStr().contains("eventlog", RWCString::ignoreCase) )
    {
        if( !_ion.areEventLogsComplete() )
        {
            retMsg->setExpectMore(true);
        }
        else
        {
            retMsg->setResultString("Event log collection complete.");
        }
    }

    retList.append(retMsg);
    vgList.append(vgMsg);
}


INT CtiDeviceION::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL;
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                   RWCString(InMessage->Return.CommandStr),
                                   RWCString(),
                                   InMessage->EventCode & 0x7fff,
                                   InMessage->Return.RouteID,
                                   InMessage->Return.MacroOffset,
                                   InMessage->Return.Attempt,
                                   InMessage->Return.TrxID,
                                   InMessage->Return.UserID);

    if( pPIL != NULL )
    {
        //  insert "Sky is falling" messages into pPIL here

        // send the whole mess to dispatch
        if (pPIL->PointData().entries() > 0)
        {
            retList.insert( pPIL );
        }
        else
        {
            delete pPIL;
        }
    }

    return retCode;
}


void CtiDeviceION::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceDNP::getSQL(db, keyTable, selector);
}


void CtiDeviceION::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _ion.setAddresses(_address.getMasterAddress(), _address.getSlaveAddress());
}

