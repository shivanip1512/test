/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.29 $
* DATE         :  $Date: 2005/03/10 21:00:32 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



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
#include "logger.h"
#include "numstr.h"
#include "cparms.h"


namespace Cti       {
namespace Device    {

DNP::DNP()
{
    resetDNPScansPending();
}

DNP::DNP(const DNP &aRef)
{
   *this = aRef;
}

DNP::~DNP() {}

DNP &DNP::operator=(const DNP &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}


void DNP::resetDNPScansPending( void )
{
    _scanGeneralPending     = false;
    _scanIntegrityPending   = false;
    _scanAccumulatorPending = false;
}


void DNP::setDNPScanPending(int scantype, bool pending)
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


bool DNP::clearedForScan(int scantype)
{
    bool status = false;

    if( useScanFlags() )
    {
        switch(scantype)
        {
            case ScanRateGeneral:
            {
                status = !(_scanGeneralPending || _scanIntegrityPending || _scanAccumulatorPending);
                break;
            }
            case ScanRateIntegrity:
            {
                status = !(_scanIntegrityPending || _scanAccumulatorPending);
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
    }
    else
    {
        status = true;
    }

    return status;
}


void DNP::resetForScan(int scantype)
{
    // OK, it is five minutes past the time I expected to have scanned this bad boy..
    switch(scantype)
    {
        case ScanRateGeneral:
        case ScanRateIntegrity:
        case ScanRateAccum:
        {
            setDNPScanPending(scantype, false);

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



INT DNP::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    setDNPScanPending(ScanRateGeneral, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");
    pReq->setMessagePriority(ScanPriority);

    status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT DNP::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    setDNPScanPending(ScanRateIntegrity, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan integrity");
    pReq->setMessagePriority(ScanPriority);

    status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT DNP::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    Protocol::DNPInterface::Command command = Protocol::DNPInterface::Command_Invalid;
    Protocol::DNPInterface::output_point controlout;
    pseudo_info p_i = {false, -1, -1};

    switch( parse.getCommand() )
    {
        case ControlRequest:
        {
            CtiPointBase   *point   = NULL;
            CtiPointStatus *control = NULL;

            Protocol::DNP::BinaryOutputControl::ControlCode controltype;
            Protocol::DNP::BinaryOutputControl::TripClose   trip_close;
            int offset, on_time, off_time;
            bool valid_control = true;

            if( parse.getiValue("point") > 0 )
            {
                //  select by raw pointid
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
                //  select by a control point on the device
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
                    //  ugh - does this need to be sent from Porter as well?  do we send it if the control fails?
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), control->getPointID(), 0, RWTime(), 86400, 100);

                    //  if the control is latched
                    if( control->getPointStatus().getControlType() == LatchControlType ||
                        control->getPointStatus().getControlType() == SBOLatchControlType )
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )
                        {
                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )
                        {
                            hist->setRawState(STATEONE);
                        }

                        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                        {
                            controltype = Protocol::DNP::BinaryOutputControl::LatchOff;
                        }
                        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                        {
                            controltype = Protocol::DNP::BinaryOutputControl::LatchOn;
                        }

                        offset      = control->getPointStatus().getControlOffset();
                        trip_close  = Protocol::DNP::BinaryOutputControl::NUL;
                        on_time     = 0;
                        off_time    = 0;
                    }
                    else  //  assume pulsed
                    {
                        if( parse.getCommandStr().contains(control->getPointStatus().getStateZeroControl(), RWCString::ignoreCase) )      //  CMD_FLAG_CTL_OPEN
                        {
                            on_time     = control->getPointStatus().getCloseTime1();

                            hist->setRawState(STATEZERO);
                        }
                        else if( parse.getCommandStr().contains(control->getPointStatus().getStateOneControl(), RWCString::ignoreCase) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            on_time     = control->getPointStatus().getCloseTime2();

                            hist->setRawState(STATEONE);
                        }

                        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                        {
                            trip_close  = Protocol::DNP::BinaryOutputControl::Trip;
                        }
                        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                        {
                            trip_close  = Protocol::DNP::BinaryOutputControl::Close;
                        }

                        controltype = Protocol::DNP::BinaryOutputControl::PulseOn;
                        offset      = control->getPointStatus().getControlOffset();
                        off_time    = 0;
                    }

                    //  set up the pseudo data to be sent from Porter-side
                    p_i.is_pseudo = control->isPseudoPoint();
                    p_i.pointid   = control->getPointID();
                    p_i.state     = hist->getRawState();

                    hist->setMessagePriority(MAXPRIORITY - 1);
                    hist->setUser(pReq->getUser());
                    vgList.insert(hist);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - invalid control type \"" << control->getPointStatus().getControlType() << "\" specified in DNP::ExecuteRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    //  i don't like this, but i don't see a better way yet
                    valid_control = false;
                }
            }
            else
            {
                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = Protocol::DNP::BinaryOutputControl::PulseOff;
                }
                else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                {
                    controltype = Protocol::DNP::BinaryOutputControl::PulseOn;
                }

                on_time  = 0;
                off_time = 0;

                p_i.is_pseudo = false;
            }


            controlout.type            = Protocol::DNPInterface::DigitalOutput;
            controlout.control_offset  = offset;

            controlout.dout.control    = controltype;
            controlout.dout.trip_close = trip_close;
            controlout.dout.on_time    = on_time;
            controlout.dout.off_time   = off_time;
            controlout.dout.count      = 1;
            controlout.dout.queue      = false;
            controlout.dout.clear      = false;

            if( control )
            {
                OutMessage->ExpirationTime = RWTime().seconds() + control->getControlExpirationTime();
            }

            if( control && control->getPointStatus().getControlInhibit() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint - control inhibited for device \"" << getName() << "\" point \"" << control->getName() << "\" in DNP::ExecuteRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else if( valid_control )
            {
                if( parse.isKeyValid("sbo_selectonly") )
                {
                    //  for diagnostics - used for verifying SBO timeouts
                    command = Protocol::DNPInterface::Command_SetDigitalOut_SBO_SelectOnly;
                }
                else if( parse.isKeyValid("sbo_operate") )
                {
                    //  the other half of SBO_SelectOnly
                    command = Protocol::DNPInterface::Command_SetDigitalOut_SBO_Operate;
                }
                else if( (control && control->getPointStatus().getControlType() == SBOPulseControlType) ||
                         (control && control->getPointStatus().getControlType() == SBOLatchControlType) )
                {
                    //  if successful, this will transition to SBO_Operate in DNPInterface on Porter-side
                    command = Protocol::DNPInterface::Command_SetDigitalOut_SBO_Select;
                }
                else
                {
                    //  boring old direct control
                    command = Protocol::DNPInterface::Command_SetDigitalOut_Direct;
                }
            }

            break;
        }

        case ScanRequest:
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                {
                    command = Protocol::DNPInterface::Command_Class123Read;

                    break;
                }

                case ScanRateAccum:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - Accumulator scanrates not defined for DNP device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }

                case ScanRateIntegrity:
                {
                    command = Protocol::DNPInterface::Command_Class1230Read;

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
                controlout.type = Protocol::DNPInterface::AnalogOutput;
                controlout.control_offset = parse.getiValue("analogoffset");

                controlout.aout.value     = parse.getiValue("analogvalue");

                command = Protocol::DNPInterface::Command_SetAnalogOut, controlout;
            }

            break;
        }

        case PutConfigRequest:
        {
            if(parse.isKeyValid("timesync"))
            {
                command = Protocol::DNPInterface::Command_WriteTime;
            }

            break;
        }

        case GetConfigRequest:
        {
            if(parse.isKeyValid("time"))
            {
                command = Protocol::DNPInterface::Command_ReadTime;
            }

            break;
        }

        case GetValueRequest:
        case GetStatusRequest:
        case PutStatusRequest:
        case LoopbackRequest:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - command type \"" << parse.getCommand() << "\" not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if( command != Protocol::DNPInterface::Command_Invalid )
    {
        _pil_info.protocol_command   = command;
        _pil_info.protocol_parameter = controlout;
        _pil_info.pseudo_info        = p_i;
        _pil_info.user               = pReq->getUser();

        OutMessage->Port     = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        EstablishOutMessagePriority(OutMessage, pReq->getMessagePriority());

        sendCommRequest(OutMessage, outList);

        nRet = NoError;
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


Protocol::Interface *DNP::getProtocol()
{
    return &_dnp;
}


int DNP::generate(CtiXfer &xfer)
{
    return _dnp.generate(xfer);
}


int DNP::decode(CtiXfer &xfer, int status)
{
    int retval = NoError;

    retval = _dnp.decode(xfer, status);

    if( _dnp.isTransactionComplete() )
    {
        const Protocol::DNPInterface::output_point &op = _porter_info.protocol_parameter;
    }

    return retval;
}


int DNP::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    //  write the outmess_header
    outmess_header *om_buf = reinterpret_cast<outmess_header *>(OutMessage->Buffer.OutMessage);
    om_buf->command     = _pil_info.protocol_command;
    om_buf->parameter   = _pil_info.protocol_parameter;
    om_buf->pseudo_info = _pil_info.pseudo_info;

    char *buf = reinterpret_cast<char *>(OutMessage->Buffer.OutMessage) + sizeof(outmess_header);
    strncpy(buf, _pil_info.user.data(), 127);
    buf[127] = 0;  //  max of 128, just because i feel like it

    if( OutMessage )
    {
        //  assign all of the standard OM bits
        OutMessage->OutLength    = sizeof(om_buf) + strlen(buf) + 1;  //  plus null
        OutMessage->Source       = _dnp_address.getMasterAddress();
        OutMessage->Destination  = _dnp_address.getSlaveAddress();
        OutMessage->EventCode    = RESULT;
        OutMessage->MessageFlags = _dnp.commandRequiresRequeueOnFail() ? MSGFLG_REQUEUE_CMD_ONCE_ON_FAIL : 0;
        OutMessage->Retry        = _dnp.commandRetries();

        //  give it a higher priority if it's a time sync or a control command
        switch( _pil_info.protocol_command )
        {
            case Protocol::DNPInterface::Command_WriteTime:
                OutMessage->Priority = MAXPRIORITY - 1;
                break;

            case Protocol::DNPInterface::Command_SetDigitalOut_Direct:
            case Protocol::DNPInterface::Command_SetDigitalOut_SBO_Operate:
            case Protocol::DNPInterface::Command_SetDigitalOut_SBO_Select:
            case Protocol::DNPInterface::Command_SetDigitalOut_SBO_SelectOnly:
                OutMessage->Priority = MAXPRIORITY - 2;
                break;
        }

        outList.append(OutMessage);
        OutMessage = 0;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid OutMessage in DNPInterface::sendCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


int DNP::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    if( OutMessage )
    {
        outmess_header *om_buf = reinterpret_cast<outmess_header *>(OutMessage->Buffer.OutMessage);

        //  we keep a copy for the decode later on
        _porter_info.protocol_command   = om_buf->command;
        _porter_info.protocol_parameter = om_buf->parameter;
        _porter_info.pseudo_info        = om_buf->pseudo_info;

        char *buf = reinterpret_cast<char *>(OutMessage->Buffer.OutMessage) + sizeof(outmess_header);
        buf[127] = 0;           //  make sure it's null-terminated somewhere before we do this:
        _porter_info.user = buf;   //  ooo, how daring

        _dnp.setCommand(_porter_info.protocol_command, _porter_info.protocol_parameter);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid OutMessage in DNPInterface::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


int DNP::sendCommResult(INMESS *InMessage)
{
    int retval = NoError;

    char *buf;
    inmess_header *ih;
    int offset;
    string result_string;
    queue<string *> strings;

    buf = reinterpret_cast<char *>(InMessage->Buffer.InMessage);
    offset = 0;

    //  we're just going to send a single string across

    _dnp.getInboundStrings(strings);

    while( !strings.empty() )
    {
        result_string += getName().data();
        result_string += " / ";
        result_string += *(strings.front());
        result_string += "\n";

        delete strings.front();
        strings.pop();
    }

    while( !_results.empty() )
    {
        result_string += *(_results.front());
        result_string += "\n";

        delete _results.front();
        _results.pop();
    }

    strncpy(buf, result_string.c_str(), sizeof(InMessage->Buffer.InMessage) - 1);
    InMessage->Buffer.InMessage[sizeof(InMessage->Buffer.InMessage) - 1] = 0;

    InMessage->InLength = result_string.size() + 1;

    return retval;
}


void DNP::sendDispatchResults(CtiConnection &vg_connection)
{
    CtiReturnMsg                *vgMsg;
    CtiPointDataMsg             *tmpMsg;
    CtiPointBase                *point;
    CtiPointNumeric             *pNumeric;
    RWCString                    resultString;
    RWTime                       Now;
    queue<CtiPointDataMsg *>     points;

    vgMsg  = CTIDBG_new CtiReturnMsg(getID());  //  , InMessage->Return.CommandStr

    double tmpValue;

    _dnp.getInboundPoints(points);

    while( !points.empty() )
    {
        tmpMsg = points.front();
        points.pop();

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

                            vgMsg->PointData().append(demandMsg->replicateMessage());

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

                resultString  = getName() + " / " + point->getName() + ": " + CtiNumStr(tmpMsg->getValue(), ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
                resultString += " @ " + tmpMsg->getTime().asString();
            }
            else if( point->isStatus() )
            {
                resultString  = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), tmpMsg->getValue());
                resultString += " @ " + tmpMsg->getTime().asString();
            }
            else
            {
                resultString = "";
            }

            _results.push(CTIDBG_new string(resultString.data()));
            tmpMsg->setString(resultString);

            vgMsg->PointData().append(tmpMsg->replicateMessage());
        }
        else
        {
            delete tmpMsg;
        }
    }

    //  now send the pseudos related to the control point
    //    note:  points are the domain of the device, not the protocol,
    //           so i have to handle pseudo points here, in the device code
    switch( _porter_info.protocol_command )
    {
        case Protocol::DNPInterface::Command_SetDigitalOut_Direct:
        case Protocol::DNPInterface::Command_SetDigitalOut_SBO_Operate:
        {
            if( _porter_info.pseudo_info.is_pseudo /*&& !_dnp.errorCondition()*/ )  //  if the control was successful
            {
                CtiPointDataMsg *msg = CTIDBG_new CtiPointDataMsg(_porter_info.pseudo_info.pointid,
                                                                  _porter_info.pseudo_info.state,
                                                                  NormalQuality,
                                                                  StatusPointType,
                                                                  "This point has been controlled");
                msg->setUser(_porter_info.user.data());
                vgMsg->PointData().append(msg);
            }

            break;
        }
    }

    vg_connection.WriteConnQue(vgMsg);
}


INT DNP::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        string result_string;

        InMessage->Buffer.InMessage[InMessage->InLength - 1] = 0;

        result_string.assign(reinterpret_cast<char *>(InMessage->Buffer.InMessage), InMessage->InLength);

        switch( _pil_info.protocol_command )
        {
            case Protocol::DNPInterface::Command_Class0Read:
            case Protocol::DNPInterface::Command_Class1230Read:
            {
                setDNPScanPending(ScanRateIntegrity, false);
                break;
            }
            case Protocol::DNPInterface::Command_Class123Read:
            case Protocol::DNPInterface::Command_Class1Read:
            case Protocol::DNPInterface::Command_Class2Read:
            case Protocol::DNPInterface::Command_Class3Read:
            {
                setDNPScanPending(ScanRateGeneral, false);
                break;
            }
        }

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         RWCString(InMessage->Return.CommandStr),
                                         result_string.data(),
                                         InMessage->EventCode & 0x7fff,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.TrxID,
                                         InMessage->Return.UserID);

        retList.append(retMsg);
    }
    else
    {
        char error_str[80];
        RWCString resultString;

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + RWCString(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         RWCString(InMessage->Return.CommandStr),
                                         resultString,
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


INT DNP::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
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
RWCString DNP::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


void DNP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceAddress::getSQL(db, keyTable, selector);
}

void DNP::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _dnp_address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _dnp.setAddresses(_dnp_address.getSlaveAddress(), _dnp_address.getMasterAddress());

   if( getType() == TYPE_DARTRTU )
   {
       _dnp.setOptions(Protocol::DNPInterface::Options_DatalinkConfirm);
   }
}

}
}
