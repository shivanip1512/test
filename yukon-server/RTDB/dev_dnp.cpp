/*-----------------------------------------------------------------------------*
*
* File:   dev_dnp
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.43 $
* DATE         :  $Date: 2006/01/24 19:55:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <map>
#include <string>


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

using namespace std;
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



LONG DNP::getAddress() const
{
    return _dnp_address.getSlaveAddress();
}


void DNP::resetDNPScansPending( void )
{
    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateAccum, false);
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
                status = !(isScanFlagSet(ScanRateGeneral) || isScanFlagSet(ScanRateIntegrity) || isScanFlagSet(ScanRateAccum));
                break;
            }
            case ScanRateIntegrity:
            {
                status = !(isScanFlagSet(ScanRateIntegrity) || isScanFlagSet(ScanRateAccum));
                break;
            }
            case ScanRateAccum:
            {
                status = !isScanFlagSet(ScanRateAccum);
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


INT DNP::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    setScanFlag(ScanRateGeneral, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    setScanFlag(ScanRateIntegrity, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case LoopbackRequest:
        {
            command = Protocol::DNPInterface::Command_Loopback;

            break;
        }

        case ControlRequest:
        {
            CtiPointBase   *point   = NULL;
            CtiPointStatus *control = NULL;

            Protocol::DNP::BinaryOutputControl::ControlCode controltype;
            Protocol::DNP::BinaryOutputControl::TripClose   trip_close = Protocol::DNP::BinaryOutputControl::NUL;
            int offset   = 0,
                on_time  = 0,
                off_time = 0;
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
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), control->getPointID(), 0, CtiTime(), 86400, 100);

                    //  if the control is latched
                    if( control->getPointStatus().getControlType() == LatchControlType ||
                        control->getPointStatus().getControlType() == SBOLatchControlType )
                    {
                        if( findStringIgnoreCase(parse.getCommandStr().c_str(), control->getPointStatus().getStateZeroControl()) )
                        {
                            hist->setRawState(STATEZERO);
                        }
                        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), control->getPointStatus().getStateOneControl()) )
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
                    }
                    else  //  assume pulsed
                    {
                        if( findStringIgnoreCase(parse.getCommandStr().c_str(), control->getPointStatus().getStateZeroControl()) )      //  CMD_FLAG_CTL_OPEN
                        {
                            on_time     = control->getPointStatus().getCloseTime1();

                            hist->setRawState(STATEZERO);
                        }
                        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), control->getPointStatus().getStateOneControl()) )  //  CMD_FLAG_CTL_CLOSE
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
                        dout << CtiTime() << " **** Checkpoint - invalid control type \"" << control->getPointStatus().getControlType() << "\" specified in DNP::ExecuteRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                OutMessage->ExpirationTime = CtiTime().seconds() + control->getControlExpirationTime();
            }

            if( control && control->getPointStatus().getControlInhibit() )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - control inhibited for device \"" << getName() << "\" point \"" << control->getName() << "\" in DNP::ExecuteRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        dout << CtiTime() << " **** Checkpoint - Accumulator scanrates not defined for DNP device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

                command = Protocol::DNPInterface::Command_SetAnalogOut;
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
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - command type \"" << parse.getCommand() << "\" not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        if( getPortID() == gConfigParms.getValueAsInt("PORTER_DNPUDP_DB_PORTID", 0) )
        {
            OutMessage->MessageFlags |= MessageFlag_RouteToPorterDNPUDPThread;
        }

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
    ::strncpy(buf, _pil_info.user.c_str(), 127);
    buf[127] = 0;  //  max of 128, just because i feel like it

    if( OutMessage )
    {
        //  assign all of the standard OM bits
        OutMessage->OutLength     = sizeof(om_buf) + ::strlen(buf) + 1;  //  plus null
        OutMessage->Source        = _dnp_address.getMasterAddress();
        OutMessage->Destination   = _dnp_address.getSlaveAddress();
        OutMessage->EventCode     = RESULT;
        OutMessage->MessageFlags |= _dnp.commandRequiresRequeueOnFail() ? MessageFlag_RequeueCommandOnceOnFail : 0;
        OutMessage->Retry         = _dnp.commandRetries();

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
            dout << CtiTime() << " **** Checkpoint - invalid OutMessage in DNPInterface::sendCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        buf[127] = 0;             //  make sure it's null-terminated somewhere before we do this:
        _porter_info.user = buf;  //  ooo, how daring

        _dnp.setCommand(_porter_info.protocol_command, _porter_info.protocol_parameter);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid OutMessage in DNPInterface::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


void DNP::initUnsolicited()
{
    _dnp.setCommand(Protocol::DNPInterface::Command_Unsolicited);
}


int DNP::sendCommResult(INMESS *InMessage)
{
    int retval = NoError;

    char *buf;
    int offset;
    string result_string;
    Protocol::DNPInterface::stringlist_t strings;
    Protocol::DNPInterface::stringlist_t::iterator itr;

    buf = reinterpret_cast<char *>(InMessage->Buffer.InMessage);
    offset = 0;

    _dnp.getInboundStrings(strings);

    //  this needs to be smarter and send the device name and point data elements seperately...
    for( itr = strings.begin(); itr != strings.end(); itr++ )
    {
        result_string += getName().c_str();
        result_string += " / ";
        result_string += *(*itr);
        result_string += "\n";
    }

    while( !strings.empty() )
    {
        delete strings.back();

        strings.pop_back();
    }

    //  ... as does this
    for( itr = _string_results.begin(); itr != _string_results.end(); itr++ )
    {
        result_string += *(*itr);
        result_string += "\n";
    }

    while( !_string_results.empty() )
    {
        delete _string_results.back();

        _string_results.pop_back();
    }

    if( result_string.size() >= sizeof(InMessage->Buffer.InMessage) )
    {
        //  make sure we complain about it so we know the magnitude of the problem when people bring it up...
        //    one possible alternative is to send multple InMessages across with the string data - although,
        //    considering that the largest message I saw was on the order of 60k, sending 15 InMessages is not very appealing
        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Info - result_string.size = " << result_string.size() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        string cropped("\n---cropped---");

        //  erase the end chunk so we can append the "cropped" string in
        result_string.erase(sizeof(InMessage->Buffer.InMessage) - cropped.size() - 1, result_string.size());
        result_string += cropped;
    }

    InMessage->InLength = result_string.size() + 1;

    //  make sure we don't overrun the buffer, even though we just checked above
    strncpy(buf, result_string.c_str(), sizeof(InMessage->Buffer.InMessage) - 1);
    //  and mark the end with a null, again, just to be sure
    InMessage->Buffer.InMessage[sizeof(InMessage->Buffer.InMessage) - 1] = 0;

    return retval;
}


void DNP::sendDispatchResults(CtiConnection &vg_connection)
{
    CtiReturnMsg                *vgMsg;
    CtiPointDataMsg             *pt_msg;
    CtiPointBase                *point;
    CtiPointNumeric             *pNumeric;
    string                    resultString;
    CtiTime                       Now;

    Protocol::Interface::pointlist_t points;
    Protocol::Interface::pointlist_t::iterator itr;

    vgMsg  = CTIDBG_new CtiReturnMsg(getID());  //  , InMessage->Return.CommandStr

    double tmpValue;

    _dnp.getInboundPoints(points);

    //  do any device-dependent work on the points (CBC 6510, for example)
    processPoints(points);

    //  then toss them into the return msg
    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        pt_msg = *itr;

        if( pt_msg )
        {
            _string_results.push_back(CTIDBG_new string(pt_msg->getString()));

            vgMsg->PointData().append(pt_msg);
        }
    }

    points.erase(points.begin(), points.end());

    //  now send the pseudos related to the control point
    //    note:  points are the domain of the device, not the protocol,
    //           so i have to handle pseudo points here, in the device code
    switch( _porter_info.protocol_command )
    {
        case Protocol::DNPInterface::Command_SetDigitalOut_Direct:
        case Protocol::DNPInterface::Command_SetDigitalOut_SBO_Select:  //  presumably this will transition...  we need to verify this...
        case Protocol::DNPInterface::Command_SetDigitalOut_SBO_Operate:
        {
            if( _porter_info.pseudo_info.is_pseudo /*&& !_dnp.errorCondition()*/ )  //  ... for example, make sure the control was successful
            {
                CtiPointDataMsg *msg = CTIDBG_new CtiPointDataMsg(_porter_info.pseudo_info.pointid,
                                                                  _porter_info.pseudo_info.state,
                                                                  NormalQuality,
                                                                  StatusPointType,
                                                                  "This point has been controlled");
                msg->setUser(_porter_info.user.c_str());
                vgMsg->PointData().append(msg);
            }

            break;
        }
    }

    vg_connection.WriteConnQue(vgMsg);
}


void DNP::processPoints( Protocol::Interface::pointlist_t &points )
{
    Protocol::Interface::pointlist_t::iterator itr;
    CtiPointDataMsg *msg;
    CtiPoint *point;
    string resultString;

    Protocol::Interface::pointlist_t demand_points;

    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        msg = *itr;

        //  !!! msg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( msg && (point = getDevicePointOffsetTypeEqual(msg->getId(), msg->getType())) )
        {
            //  if it's a pulse accumulator, we must attempt to calculate its demand accumulator
            if( point->getType() == PulseAccumulatorPointType )
            {
                CtiPointAccumulator *demandPoint;

                //  is there an accompanying demand accumulator for this pulse accumulator?
                if( (demandPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(point->getPointOffset(), DemandAccumulatorPointType)) != NULL )
                {
                    dnp_accumulator_pointdata_map::iterator pd_itr;
                    dnp_accumulator_pointdata previous, current;

                    //  get the raw pulses from the pulse accumulator
                    current.point_value = msg->getValue();
                    current.point_time  = CtiTime::now().seconds();

                    pd_itr = _lastIntervalAccumulatorData.find(demandPoint->getPointOffset());

                    if( pd_itr != _lastIntervalAccumulatorData.end() )
                    {
                        float demandValue;
                        previous = pd_itr->second;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

                            resultString = getName() + " / " + demandPoint->getName();
                            resultString += ": " + CtiNumStr(demandValue, ((CtiPointNumeric *)demandPoint)->getPointUnits().getDecimalPlaces());

                            CtiPointDataMsg *demandMsg = new CtiPointDataMsg(demandPoint->getID(), demandValue, NormalQuality, DemandAccumulatorPointType, resultString);

                            demand_points.push_back(demandMsg);

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - updating demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "current.point_value  = " << current.point_value << endl;
                                dout << "current.point_time   = " << current.point_time << endl;
                            }

                            pd_itr->second = current;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - demand not calculated; interval < 60 sec **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - inserting demand accumulator calculation data **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "current.point_value  = " << current.point_value << endl;
                            dout << "current.point_time   = " << current.point_time << endl;
                        }

                        _lastIntervalAccumulatorData.insert(dnp_accumulator_pointdata_map::value_type(point->getPointOffset(), current));
                    }
                }
            }

            //  NOTE:  we had to retrieve the actual pointid by offset+type (see above), so we assign the actual id now
            msg->setId(point->getID());

            if( point->isNumeric() )
            {
                CtiPointNumeric *pNumeric = (CtiPointNumeric *)point;

                msg->setValue(pNumeric->computeValueForUOM(msg->getValue()));

                resultString  = getName() + " / " + point->getName();
                resultString += ": " + CtiNumStr(msg->getValue(), ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
                resultString += " @ " + msg->getTime().asString();
            }
            else if( point->isStatus() )
            {
                resultString  = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), msg->getValue());
                resultString += " @ " + msg->getTime().asString();
            }
            else
            {
                resultString = "";
            }

            msg->setString(resultString);
        }
        else
        {
            delete *itr;

            *itr = 0;
        }
    }

    points.insert(points.end(), demand_points.begin(), demand_points.end());

    demand_points.erase(demand_points.begin(), demand_points.end());
}


INT DNP::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        string result_string;

        //  safety first
        if( InMessage->InLength > sizeof(InMessage->Buffer.InMessage) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint InMessage->InLength > sizeof(InMessage->Buffer.InMessage) for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            InMessage->InLength = sizeof(InMessage->Buffer.InMessage);
        }
        InMessage->Buffer.InMessage[InMessage->InLength - 1] = 0;

        result_string.assign(reinterpret_cast<char *>(InMessage->Buffer.InMessage), InMessage->InLength);

        switch( _pil_info.protocol_command )
        {
            case Protocol::DNPInterface::Command_Class1230Read:
            {
                setScanFlag(ScanRateIntegrity, false);
                break;
            }
            case Protocol::DNPInterface::Command_Class123Read:
            case Protocol::DNPInterface::Command_Class1Read:
            case Protocol::DNPInterface::Command_Class2Read:
            case Protocol::DNPInterface::Command_Class3Read:
            {
                setScanFlag(ScanRateGeneral, false);
                break;
            }
        }

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage->Return.CommandStr),
                                         result_string.c_str(),
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
        string resultString;

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage->Return.CommandStr),
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


INT DNP::ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                              string(InMessage->Return.CommandStr),
                                              string(),
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
        dout << CtiTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    switch( _pil_info.protocol_command )
    {
        case Protocol::DNPInterface::Command_Class1230Read:
        {
            setScanFlag(ScanRateIntegrity, false);
            break;
        }
        case Protocol::DNPInterface::Command_Class123Read:
        case Protocol::DNPInterface::Command_Class1Read:
        case Protocol::DNPInterface::Command_Class2Read:
        case Protocol::DNPInterface::Command_Class3Read:
        {
            setScanFlag(ScanRateGeneral, false);
            break;
        }
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string DNP::getDescription(const CtiCommandParser &parse) const
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

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _dnp.setAddresses(_dnp_address.getSlaveAddress(), _dnp_address.getMasterAddress());
   _dnp.setName(string(getName().data()));

   if( getType() == TYPE_DARTRTU )
   {
       _dnp.setOptions(Protocol::DNPInterface::Options_DatalinkConfirm);
   }
}


INT CBC7020::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    //  if it's a control open/close request without an offset
    if( (parse.getCommand() == ControlRequest) && !(parse.getFlags() & CMD_FLAG_OFFSET) &&
        (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
    {
        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            pReq->setCommandString("control open offset 1");
        }
        else // if( parse.getFlags() & CMD_FLAG_CTL_CLOSE ) - implied because of the above if condition
        {
            pReq->setCommandString("control close offset 1");
        }

        CtiCommandParser new_parse(pReq->CommandString());

        //  NOTE the new parser I'm passing in - i've already touched the pReq string, so
        //    i need to seal the deal with a new parse
        nRet = Inherited::ExecuteRequest(pReq, new_parse, OutMessage, vgList, retList, outList);
    }
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


}
}
