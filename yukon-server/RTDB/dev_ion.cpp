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
#include "numstr.h"

CtiDeviceION::CtiDeviceION() {}

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

/*
inline bool      CtiDeviceION::isMeter() const                      {   return true;    }
inline RWCString CtiDeviceION::getMeterGroupName() const            {   return getMeterGroup().getCollectionGroup();    }
inline RWCString CtiDeviceION::getAlternateMeterGroupName() const   {   return getMeterGroup().getTestCollectionGroup();    }

CtiTableDeviceMeterGroup  CtiDeviceION::getMeterGroup() const       {  return MeterGroup;  }
CtiTableDeviceMeterGroup& CtiDeviceION::getMeterGroup()             {  return MeterGroup;  }

CtiDeviceION& CtiDeviceION::setMeterGroup( const CtiTableDeviceMeterGroup &aMeterGroup )
{
    MeterGroup = aMeterGroup;
    return *this;
}
*/

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
                    _ion.setCommand(CtiProtocolION::Command_ExceptionScan);

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
            unsigned int offset;

            if( parse.getFlags() & CMD_FLAG_OFFSET &&
                parse.getFlags() & CMD_FLAG_CTL_CLOSE )
            {
                offset = parse.getiValue("offset");

                if( offset > 0 )
                {
                    _ion.setCommand(CtiProtocolION::Command_ExternalPulseTrigger, offset);

                    found = true;
                }
            }

            break;
        }

        /*
        case ControlRequest:
        {
            int offset;
            CtiIONBinaryOutputControl::ControlCode controltype;

            if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");

                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = CtiIONBinaryOutputControl::PulseOff;
                }
                else
                {
                    controltype = CtiIONBinaryOutputControl::PulseOn;
                }

                CtiProtocolION::ion_output_point controlout;

                controlout.type   = CtiProtocolION::DigitalOutput;
                controlout.offset = offset;

                controlout.dout.control    = controltype;
                controlout.dout.trip_close = CtiIONBinaryOutputControl::NUL;
                controlout.dout.on_time    = 0;
                controlout.dout.off_time   = 0;
                controlout.dout.count      = 1;
                controlout.dout.queue      = false;
                controlout.dout.clear      = false;

                _ion.setCommand(CtiProtocolION::ION_SetDigitalOut, &controlout, 1);

                nRet = NoError;
            }

            break;
        }

        case PutValueRequest:
        {
            int offset;

            if( parse.getFlags() & CMD_FLAG_PV_ANALOG )
            {
                CtiProtocolION::ion_output_point controlout;

                controlout.type = CtiProtocolION::AnalogOutput;

                controlout.aout.value = parse.getiValue("analogvalue");
                controlout.offset     = parse.getiValue("analogoffset");

                _ion.setCommand(CtiProtocolION::ION_SetAnalogOut, &controlout, 1);

                nRet = NoError;
            }

            break;
        }

        case GetValueRequest:
        case GetStatusRequest:
        {

        }
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        case LoopbackRequest:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

    */

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


    if( nRet == NoError )
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
                    dout << "**** Checkpoint: Invalid Reader **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
}



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

    _ion.recvCommResult(InMessage, outList);

    resetScanPending();

    if( _ion.hasInboundData() )
    {
        _ion.getInboundData(pointData, eventData);

        processInboundData(InMessage, TimeNow, vgList, retList, outList, pointData, eventData);
    }

    switch( _ion.getCommand() )
    {
        case CtiProtocolION::Command_ExternalPulseTrigger:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            CtiRequestMsg newReq(getID(),
                                 "scan general",
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

        case CtiProtocolION::Command_EventLogRead:
        {
            if( !_ion.areEventLogsComplete() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                CtiRequestMsg newReq(getID(),
                                     "getstatus eventlogs",
                                     InMessage->Return.UserID,
                                     InMessage->Return.TrxID,
                                     InMessage->Return.RouteID,
                                     InMessage->Return.MacroOffset,
                                     InMessage->Return.Attempt);

                newReq.setConnectionHandle((void *)InMessage->Return.Connection);

                CtiCommandParser parse(newReq.CommandString());

                CtiDeviceBase::ExecuteRequest(&newReq, parse, vgList, retList, outList);
            }

            break;
        }
    }


    return ErrReturn;
}


void CtiDeviceION::processInboundData( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList,
                                       RWTPtrSlist<CtiPointDataMsg> &points, RWTPtrSlist<CtiSignalMsg> &events )
{
    CtiReturnMsg *retMsg, *vgMsg;

    retMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);
    vgMsg  = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    retMsg->setUserMessageId(InMessage->Return.UserID);
    vgMsg->setUserMessageId (InMessage->Return.UserID);

    while( !points.isEmpty() )
    {
        CtiPointDataMsg *tmpMsg;
        CtiPointBase    *point;
        double value;
        RWCString resultString;

        tmpMsg = points.removeFirst();

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

            //  ACH:  maybe check for "update" someday...  but for now, who cares
            vgMsg->PointData().append(tmpMsg->replicateMessage());
            retMsg->PointData().append(tmpMsg);
        }
        else
        {
            delete tmpMsg;
        }
    }

    while( !events.isEmpty() )
    {
        CtiSignalMsg *tmpSignal;
        CtiPointBase *point;

        tmpSignal = events.removeFirst();

        if( (point = getDevicePointOffsetTypeEqual(tmpSignal->getId(), AnalogPointType)) != NULL )
        {
            tmpSignal->setId(point->getID());
        }

        vgList.append(tmpSignal->replicateMessage());
        retList.append(tmpSignal);
    }

    retList.append(retMsg);
    vgList.append(vgMsg);
}


INT CtiDeviceION::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                              RWCString(InMessage->Return.CommandStr),
                                              RWCString(),
                                              InMessage->EventCode & 0x7fff,
                                              InMessage->Return.RouteID,
                                              InMessage->Return.MacroOffset,
                                              InMessage->Return.Attempt,
                                              InMessage->Return.TrxID,
                                              InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

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
        pPIL = NULL;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}


void CtiDeviceION::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    //RWDBSelector tmpSelector;

    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceDNP::getSQL(db, keyTable, selector);

    //  this will remain commented out until it is needed...
    //CtiTableDeviceMeterGroup::getSQL(db, keyTable, tmpSelector);

    //selector << tmpSelector;
    //selector.where( selector.where() && selector.where().leftOuterJoin(tmpSelector.where()) );
}


void CtiDeviceION::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _address.DecodeDatabaseReader(rdr);
   //MeterGroup.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _ion.setAddresses(_address.getMasterAddress(), _address.getSlaveAddress());
}

