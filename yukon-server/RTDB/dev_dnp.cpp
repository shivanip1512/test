
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2003/03/05 23:54:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "master.h"
#include "dllyukon.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_dnp.h"
#include "device.h"
#include "yukon.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"


CtiDeviceDNP::CtiDeviceDNP() {}

CtiDeviceDNP::CtiDeviceDNP(const CtiDeviceDNP &aRef)
{
   *this = aRef;
}

CtiDeviceDNP::~CtiDeviceDNP() {}

CtiDeviceDNP &CtiDeviceDNP::operator=(const CtiDeviceDNP &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


CtiProtocolBase *CtiDeviceDNP::getProtocol() const
{
    return (CtiProtocolBase *)&_dnp;
}


INT CtiDeviceDNP::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
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


INT CtiDeviceDNP::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
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


INT CtiDeviceDNP::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    switch( parse.getCommand() )
    {
        case ControlRequest:
        {
            int offset, on_time, off_time;
            CtiPointBase   *point;
            CtiPointStatus *control;
            CtiDNPBinaryOutputControl::ControlCode controltype;
            CtiDNPBinaryOutputControl::TripClose   trip_close;

            if( parse.getiValue("point") > 0 )
            {
                if( (point = getDevicePointEqual(parse.getiValue("point"))) != NULL )
                {
                    if( point->isStatus() )
                    {
                        control = (CtiPointStatus *)point;
                    }
                }
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                offset = parse.getiValue("offset");

                control = (CtiPointStatus*)getDeviceControlPointOffsetEqual(offset);
            }

            if( control != NULL && (control->getPointStatus().getControlType() == NormalControlType) )
            {
                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = CtiDNPBinaryOutputControl::PulseOn;

                    offset      = control->getPointStatus().getControlOffset();

                    trip_close  = CtiDNPBinaryOutputControl::Trip;
                    on_time     = control->getPointStatus().getCloseTime1();
                    off_time    = 0;
                }
                else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                {
                    controltype = CtiDNPBinaryOutputControl::PulseOn;

                    offset      = control->getPointStatus().getControlOffset();

                    trip_close  = CtiDNPBinaryOutputControl::Close;
                    on_time     = control->getPointStatus().getCloseTime2();
                    off_time    = 0;
                }
            }
            else
            {
                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = CtiDNPBinaryOutputControl::PulseOff;
                }
                else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                {
                    controltype = CtiDNPBinaryOutputControl::PulseOn;
                }

                on_time  = 0;
                off_time = 0;
            }

            CtiProtocolDNP::dnp_output_point controlout;

            controlout.type   = CtiProtocolDNP::DigitalOutput;
            controlout.offset = offset;

            controlout.dout.control    = controltype;
            controlout.dout.trip_close = trip_close;
            controlout.dout.on_time    = on_time;
            controlout.dout.off_time   = off_time;
            controlout.dout.count      = 1;
            controlout.dout.queue      = false;
            controlout.dout.clear      = false;

            if( (control != NULL) && control->getPointStatus().getControlInhibit() )
            {
                nRet = NoMethod;
            }
            else
            {
                _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut, &controlout, 1);

                nRet = NoError;
            }

            break;
        }

        case ScanRequest:
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateStatus:
                {
                    nRet = NoMethod;
                    break;
                }

                case ScanRateGeneral:
                {
                    _dnp.setCommand(CtiProtocolDNP::DNP_Class123Read);
                    nRet = NoError;
                    break;
                }

                case ScanRateAccum:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "Accumulator scanrates not defined for DNP devices - check the DeviceScanRate table" << endl;
                }
                case ScanRateIntegrity:
                {
                    _dnp.setCommand(CtiProtocolDNP::DNP_Class0123Read);
                    nRet = NoError;
                    break;
                }
            }


            break;
        }

        case PutValueRequest:
        {
            int offset;

            if( parse.getFlags() & CMD_FLAG_PV_ANALOG )
            {
                CtiProtocolDNP::dnp_output_point controlout;

                controlout.type = CtiProtocolDNP::AnalogOutput;

                controlout.aout.value = parse.getiValue("analogvalue");
                controlout.offset     = parse.getiValue("analogoffset");

                _dnp.setCommand(CtiProtocolDNP::DNP_SetAnalogOut, &controlout, 1);

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
    }

    OutMessage->Port = getPortID();
    OutMessage->DeviceID = getID();
    OutMessage->TargetID = getID();

    if( nRet == NoError )
    {
        _dnp.sendCommRequest(OutMessage, outList);
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT CtiDeviceDNP::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    _dnp.recvCommResult(InMessage, outList);

    resetScanPending();

    if( _dnp.hasInboundPoints() )
    {
        _dnp.getInboundPoints(dnpPoints);

        processInboundPoints(InMessage, TimeNow, vgList, retList, outList, dnpPoints);
    }

    return ErrReturn;
}


void CtiDeviceDNP::processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points )
{
    CtiReturnMsg    *retMsg,
                    *vgMsg;
    CtiPointDataMsg *tmpMsg;
    CtiPointBase    *point;
    CtiPointNumeric *pNumeric;
    RWCString        resultString;

    retMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);
    vgMsg  = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

    retMsg->setUserMessageId(InMessage->Return.UserID);
    vgMsg->setUserMessageId (InMessage->Return.UserID);

    double tmpValue;

    while( !points.isEmpty() )
    {
        tmpMsg = points.removeFirst();

        //  !!! tmpMsg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( (point = getDevicePointOffsetTypeEqual(tmpMsg->getId(), tmpMsg->getType())) != NULL )
        {
            tmpMsg->setId(point->getID());

            if( point->isNumeric() )
            {
                pNumeric = (CtiPointNumeric *)point;

                tmpValue = pNumeric->computeValueForUOM(tmpMsg->getValue());

                tmpMsg->setValue(tmpValue);

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

    retList.append(retMsg);
    vgList.append(vgMsg);
}


INT CtiDeviceDNP::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
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



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceDNP::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


void CtiDeviceDNP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceDNP::getSQL(db, keyTable, selector);
}

void CtiDeviceDNP::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _dnpAddress.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _dnp.setSlaveAddress(_dnpAddress.getSlaveAddress());
   _dnp.setMasterAddress(_dnpAddress.getMasterAddress());

   if( getType() == TYPE_DARTRTU )
   {
       _dnp.setOptions(CtiProtocolDNP::DatalinkConfirm);
   }
}


