/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2004/04/14 16:39:00 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include <map>
using namespace std;

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"
#include "master.h"
#include "dllyukon.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_cmd.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_lmcontrolhistory.h"
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
            CtiPointBase   *point   = NULL;
            CtiPointStatus *control = NULL;
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

            if( control != NULL )
            {
                if( control->getPointStatus().getControlType() > NoneControlType &&
                    control->getPointStatus().getControlType() < InvalidControlType )
                {
                    //  NOTE - the control duration is completely arbitrary here.  Fix sometime if necessary
                    //           (i.e. customer doing sheds/restores that need to be accurately LMHist'd)
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), control->getPointID(), 0, RWTime(), 86400, 100);

                    //  if the control is latched
                    if( control->getPointStatus().getControlType() == LatchControlType ||
                        control->getPointStatus().getControlType() == SBOLatchControlType )
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )      //  CMD_FLAG_CTL_OPEN
                        {
                            controltype = CtiDNPBinaryOutputControl::LatchOff;

                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            controltype = CtiDNPBinaryOutputControl::LatchOn;

                            hist->setRawState(STATEONE);
                        }

                        offset      = control->getPointStatus().getControlOffset();
                        trip_close  = CtiDNPBinaryOutputControl::NUL;
                        on_time     = 0;
                        off_time    = 0;
                    }
                    else  //  assume pulsed
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )      //  CMD_FLAG_CTL_OPEN
                        {
                            trip_close  = CtiDNPBinaryOutputControl::Trip;
                            on_time     = control->getPointStatus().getCloseTime1();

                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            trip_close  = CtiDNPBinaryOutputControl::Close;
                            on_time     = control->getPointStatus().getCloseTime2();

                            hist->setRawState(STATEONE);
                        }

                        controltype = CtiDNPBinaryOutputControl::PulseOn;
                        offset      = control->getPointStatus().getControlOffset();
                        off_time    = 0;
                    }


                    hist->setMessagePriority(MAXPRIORITY - 1);
                    vgList.insert(hist);

                    if(control->isPseudoPoint() )
                    {
                        if( (control->getPointStatus().getControlType() == SBOPulseControlType ||
                             control->getPointStatus().getControlType() == SBOLatchControlType) && !parse.isKeyValid("sbo_operate") )
                        {
                            //  we have to wait until we're sending the operate to send the pseudo data
                        }
                        else
                        {
                            // There is no physical point to observe and respect.  We lie to the control point.
                            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(control->getID(),
                                                                                (DOUBLE)hist->getRawState(),
                                                                                NormalQuality,
                                                                                StatusPointType,
                                                                                RWCString("This point has been controlled"));
                            pData->setUser(pReq->getUser());
                            vgList.insert(pData);
                        }

                    }
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                OutMessage->ExpirationTime = RWTime().seconds() + control->getControlExpirationTime();
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
                if( parse.isKeyValid("sbo_selectonly") )
                {
                    _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut_SBO_SelectOnly, &controlout, 1);
                }
                else if( parse.isKeyValid("sbo_operate") )
                {
                    _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut_SBO_Operate, &controlout, 1);
                }
                else if( control != NULL &&
                         (control->getPointStatus().getControlType() == SBOPulseControlType ||
                          control->getPointStatus().getControlType() == SBOLatchControlType) )
                {
                    //  This command will send a DNP_SetDigitalOut_SBO_Operate to finish the command
                    //    when it gets to ResultDecode
                    _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut_SBO_Select, &controlout, 1);
                }
                else
                {
                    _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut_Direct, &controlout, 1);
                }

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
                    _dnp.setCommand(CtiProtocolDNP::DNP_Class1230Read);
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

        case PutConfigRequest:
        {
            if(parse.isKeyValid("timesync"))
            {
                _dnp.setCommand(CtiProtocolDNP::DNP_WriteTime);

                nRet = NoError;
            }

            break;
        }

        case GetConfigRequest:
        {
            if(parse.isKeyValid("time"))
            {
                _dnp.setCommand(CtiProtocolDNP::DNP_ReadTime);

                nRet = NoError;
            }

            break;
        }

        case GetValueRequest:
        case GetStatusRequest:
        {

        }
        case PutStatusRequest:
        case LoopbackRequest:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    OutMessage->Port     = getPortID();
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

    //  Okay, this is a HACK and needs to be resolved on Porter-side someday soon.
    //  If the command we just executed is a select, tag the Execute on the end right away
    if( _dnp.getCommand() == CtiProtocolDNP::DNP_SetDigitalOut_SBO_Select )
    {
        RWCString newRequestStr(pReq->CommandString());

        newRequestStr += " sbo_operate";

        //  just to make sure the "sbo_operate" token isn't cropped off
        CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(*pReq);

        //newReq->setMessagePriority(MAXPRIORITY - 1);
        newReq->setCommandString(newRequestStr);

        CtiCommandParser parse(newReq->CommandString());

        CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

        delete newReq;
    }

    return nRet;
}


INT CtiDeviceDNP::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn && !_dnp.recvCommResult(InMessage, outList) )
    {
        if( _dnp.hasInboundPoints() )
        {
            _dnp.getInboundPoints(dnpPoints);

            processInboundPoints(InMessage, TimeNow, vgList, retList, outList, dnpPoints);
        }

        switch( _dnp.getCommand() )
        {
            case CtiProtocolDNP::DNP_Class0Read:
            case CtiProtocolDNP::DNP_Class123Read:
            case CtiProtocolDNP::DNP_Class1230Read:
            case CtiProtocolDNP::DNP_Class1Read:
            case CtiProtocolDNP::DNP_Class2Read:
            case CtiProtocolDNP::DNP_Class3Read:
            {
                resetScanPending();
                break;
            }
            case CtiProtocolDNP::DNP_SetDigitalOut_SBO_SelectOnly:
            case CtiProtocolDNP::DNP_SetDigitalOut_SBO_Select:
            {
                retMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

                retMsg->setUserMessageId(InMessage->Return.UserID);

                if( _dnp.hasControlResult() )
                {
                    resultString = getName() + " / SBO select: " + _dnp.getControlResultString();

                    retMsg->setResultString(resultString);
                    /*
                    if( _dnp.getCommand() == CtiProtocolDNP::DNP_SetDigitalOut_SBO_Select &&
                        _dnp.getControlResultSuccess() )
                    {
                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - sending SBO operate **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        if( InMessage->Return.Attempt >= 0 )
                        {
                            RWCString newRequest(InMessage->Return.CommandStr);

                            newRequest += " sbo_operate";

                            //  just to make sure the "sbo_operate" token isn't cropped off
                            CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                             newRequest,
                                                                             InMessage->Return.UserID,
                                                                             InMessage->Return.TrxID,
                                                                             InMessage->Return.RouteID,
                                                                             InMessage->Return.MacroOffset,
                                                                             -1);

                            newReq->setMessagePriority(MAXPRIORITY - 1);

                            newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                            CtiCommandParser parse(newReq->CommandString());

                            CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                            delete newReq;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - averted SBO select loop for device " << getName() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - SBO select failed for device " << getName() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                    }*/
                }
                else
                {
                    retMsg->setResultString("Incorrect control response from DNP device");
                }

                retList.append(retMsg);

                break;
            }
            case CtiProtocolDNP::DNP_SetDigitalOut_Direct:
            case CtiProtocolDNP::DNP_SetDigitalOut_SBO_Operate:
            {
                //  check the return from the control

                retMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

                retMsg->setUserMessageId(InMessage->Return.UserID);

                if( _dnp.hasControlResult() )
                {
                    resultString = getName() + " / control result: " + _dnp.getControlResultString();

                    retMsg->setResultString(resultString);

                    if( !_dnp.getControlResultSuccess() )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - control failed for device " << getName() << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                else
                {
                    retMsg->setResultString("Incorrect control response from DNP device");
                }

                retList.append(retMsg);

                //  scan the statuses to verify control, but only if we were successful
                if( _dnp.hasControlResult() && _dnp.getControlResultSuccess() )
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    CtiRequestMsg *newReq = CTIDBG_new CtiRequestMsg(getID(),
                                                                     "scan general",
                                                                     InMessage->Return.UserID,
                                                                     InMessage->Return.TrxID,
                                                                     InMessage->Return.RouteID,
                                                                     InMessage->Return.MacroOffset,
                                                                     InMessage->Return.Attempt);

                    newReq->setMessagePriority(MAXPRIORITY - 1);

                    newReq->setConnectionHandle((void *)InMessage->Return.Connection);

                    CtiCommandParser parse(newReq->CommandString());

                    CtiDeviceBase::ExecuteRequest(newReq, parse, vgList, retList, outList);

                    delete newReq;
                }

                break;
            }
            case CtiProtocolDNP::DNP_ReadTime:
            {
                retMsg = new CtiReturnMsg(getID(), InMessage->Return.CommandStr);

                retMsg->setUserMessageId(InMessage->Return.UserID);

                if( _dnp.hasTimeResult() )
                {
                    RWTime deviceTime(_dnp.getTimeResult() + rwEpoch);

                    resultString = getName() + " / current time: " + deviceTime.asString();

                    retMsg->setResultString(resultString);
                }
                else
                {
                    retMsg->setResultString("No time response from DNP device");
                }

                retList.append(retMsg);

                break;
            }

            default:
            {
                break;
            }
        }
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


void CtiDeviceDNP::processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points )
{
    CtiReturnMsg    *retMsg,
                    *vgMsg;
    CtiPointDataMsg *tmpMsg;
    CtiPointBase    *point;
    CtiPointNumeric *pNumeric;
    RWCString        resultString;
    RWTime           Now;

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
            //  if it's a pulse accumulator, we must attempt to calculate its demand accumulator
            if( point->getType() == PulseAccumulatorPointType )
            {
                CtiPointAccumulator *demandPoint;

                //  is there an accompanying demand accumulator for this pulse accumulator?
                if( (demandPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(point->getPointOffset(), DemandAccumulatorPointType)) != NULL )
                {
                    dnp_accumulator_pointdata_map::iterator itr;
                    dnp_accumulator_pointdata previous, current;

                    //  get the raw pulses from the pulse accumulator
                    current.point_value = tmpMsg->getValue();
                    current.point_time  = Now.seconds();

                    itr = _lastIntervalAccumulatorData.find(demandPoint->getPointOffset());

                    if( itr != _lastIntervalAccumulatorData.end() )
                    {
                        float demandValue;
                        previous = itr->second;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "current.point_value  = " << current.point_value << endl;
                            dout << "current.point_time   = " << current.point_time << endl;
                            dout << "previous.point_value = " << previous.point_value << endl;
                            dout << "previous.point_time  = " << previous.point_time << endl;
                        }

                        if( previous.point_time + 60 <= current.point_time )
                        {
                            if( previous.point_value <= current.point_value )
                            {
                                demandValue = current.point_value - previous.point_value;
                            }
                            if( previous.point_value > current.point_value )
                            {
                                //  rollover has occurred - figure out how big the accumulator was
                                if( previous.point_value > 0x0000ffff )
                                {
                                    //  it was a 32-bit accumulator
                                    demandValue = (numeric_limits<unsigned long>::max() - previous.point_value) + current.point_value;
                                }
                                else
                                {
                                    //  it was a 16-bit accumulator
                                    demandValue = (numeric_limits<unsigned short>::max() - previous.point_value) + current.point_value;
                                }
                            }

                            demandValue *= 3600.0;
                            demandValue /= (current.point_time - previous.point_time);

                            demandValue = demandPoint->computeValueForUOM(demandValue);

                            resultString = getName() + " / " + demandPoint->getName() + ": " + CtiNumStr(demandValue, ((CtiPointNumeric *)demandPoint)->getPointUnits().getDecimalPlaces());

                            CtiPointDataMsg *demandMsg = new CtiPointDataMsg(demandPoint->getID(), demandValue, NormalQuality, DemandAccumulatorPointType, resultString);

                            if( !useScanFlags() )  //  if we're not Scanner, send it to VG as well (scanner will do this on his own)
                            {
                                //  maybe (parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE)) someday
                                vgMsg->PointData().append(demandMsg->replicateMessage());
                            }
                            retMsg->PointData().append(demandMsg);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - updating demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "current.point_value  = " << current.point_value << endl;
                                dout << "current.point_time   = " << current.point_time << endl;
                            }

                            itr->second = current;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - demand not calculated; interval < 60 sec **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint - inserting demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "current.point_value  = " << current.point_value << endl;
                            dout << "current.point_time   = " << current.point_time << endl;
                        }

                        _lastIntervalAccumulatorData.insert(dnp_accumulator_pointdata_map::value_type(point->getPointOffset(), current));
                    }
                }
            }

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

            if( !useScanFlags() )  //  if we're not Scanner, send it to VG as well (scanner will do this on his own)
            {
                //  maybe (parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE)) someday
                vgMsg->PointData().append(tmpMsg->replicateMessage());
            }
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
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if(pMsg != NULL)
        {
            pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            if(InMessage->EventCode != 0)
            {
                pMsg->insert(InMessage->EventCode);
            }
            else
            {
                pMsg->insert(GeneralScanAborted);
            }

            retList.insert( pMsg );
        }
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
   CtiTableDeviceAddress::getSQL(db, keyTable, selector);
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

   _dnp.setAddresses(_dnpAddress.getSlaveAddress(), _dnpAddress.getMasterAddress());

   if( getType() == TYPE_DARTRTU )
   {
       _dnp.setOptions(CtiProtocolDNP::DatalinkConfirm);
   }
}


