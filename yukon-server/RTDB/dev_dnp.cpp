#include "precompiled.h"

#include "dev_dnp.h"

#include "dnp_object_analogoutput.h"
#include "logger.h"
#include "porter.h"

#include "mgr_point.h"
#include "pt_status.h"
#include "pt_accum.h"
#include "pt_analog.h"

#include "dllyukon.h"

#include "exceptions.h"
#include "config_data_dnp.h"
#include "config_helpers.h"

#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"

#include <boost/optional.hpp>

using namespace std;
using namespace Cti::Config;

namespace Cti {
namespace Devices {

DnpDevice::DnpDevice()
{
    resetDNPScansPending();
}

LONG DnpDevice::getAddress() const
{
    return _dnp_address.getSlaveAddress();
}


LONG DnpDevice::getMasterAddress() const
{
    return _dnp_address.getMasterAddress();
}


void DnpDevice::resetDNPScansPending( void )
{
    setScanFlag(ScanRateGeneral, false);
    setScanFlag(ScanRateIntegrity, false);
    setScanFlag(ScanRateAccum, false);
}

bool DnpDevice::clearedForScan(const CtiScanRate_t scantype)
{
    if( ! useScanFlags() )
    {
        return true;
    }

    bool status = false;

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

    return validateClearedForScan(status, scantype);
}


YukonError_t DnpDevice::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    CtiCommandParser newParse("scan general");

    setScanFlag(ScanRateGeneral, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
    }

    pReq->setCommandString("scan general");
    pReq->setMessagePriority(ScanPriority);

    YukonError_t status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


YukonError_t DnpDevice::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan integrity");

    setScanFlag(ScanRateIntegrity, true);

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "IntegrityScan for \""<< getName() <<"\"");
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


YukonError_t DnpDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
try
{
    YukonError_t nRet = ClientErrors::NoMethod;

    using Protocols::DnpProtocol;
    using namespace Protocols::DNP;

    DnpProtocol::Command command = DnpProtocol::Command_Invalid;
    DnpProtocol::output_point controlout;
    pseudo_info p_i = {false, -1, -1};

    switch( parse.getCommand() )
    {
        case LoopbackRequest:
        {
            command = DnpProtocol::Command_Loopback;

            break;
        }

        case GetStatusRequest:
        {
            if( parse.getFlags() & CMD_FLAG_GS_INTERNAL )
            {
                command = DnpProtocol::Command_ReadInternalIndications;
            }

            break;
        }

        case ControlRequest:
        {
            CtiPointStatusSPtr pStatus;

            BinaryOutputControl::ControlCode controltype = BinaryOutputControl::Noop;
            BinaryOutputControl::TripClose   trip_close  = BinaryOutputControl::NUL;
            int offset   = 0,
                on_time  = 0,
                off_time = 0;

            const int pointid = parse.getiValue("point");

            if( pointid > 0 )
            {
                //  select by raw pointid
                CtiPointSPtr point = getDevicePointByID(pointid);

                if ( ! point )
                {
                    throw YukonErrorException(ClientErrors::PointLookupFailed, "The specified point is not on the device");
                }

                if( ! point->isStatus() )
                {
                    throw YukonErrorException(ClientErrors::PointLookupFailed, "The specified point is not Status type");
                }

                pStatus = boost::static_pointer_cast<CtiPointStatus>(point);
            }
            else if( parse.getFlags() & CMD_FLAG_OFFSET )
            {
                //  select by a control point on the device
                offset  = parse.getiValue("offset");

                pStatus = boost::static_pointer_cast<CtiPointStatus>(getDeviceControlPointOffsetEqual(offset));
            }

            if( pStatus )
            {
                //  we got a point - check for a valid control type
                if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                {
                    //  NOTE - the control duration is completely arbitrary here.  Fix sometime if necessary
                    //           (i.e. customer doing sheds/restores that need to be accurately LMHist'd)
                    //  ugh - does this need to be sent from Porter as well?  do we send it if the control fails?
                    CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg(getID(), pStatus->getPointID(), 0, CtiTime(), 86400, 100);

                    //  if the control is latched
                    if( controlParameters->getControlType() == ControlType_Latch ||
                        controlParameters->getControlType() == ControlType_SBOLatch )
                    {
                        if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateZeroControl()) )
                        {
                            hist->setRawState(STATEZERO);
                        }
                        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateOneControl()) )
                        {
                            hist->setRawState(STATEONE);
                        }

                        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                        {
                            controltype = BinaryOutputControl::LatchOff;
                        }
                        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                        {
                            controltype = BinaryOutputControl::LatchOn;
                        }

                        offset = controlParameters->getControlOffset();
                    }
                    else  //  assume pulsed
                    {
                        if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateZeroControl()) )      //  CMD_FLAG_CTL_OPEN
                        {
                            on_time = controlParameters->getCloseTime1();

                            hist->setRawState(STATEZERO);
                        }
                        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateOneControl()) )  //  CMD_FLAG_CTL_CLOSE
                        {
                            on_time = controlParameters->getCloseTime2();

                            hist->setRawState(STATEONE);
                        }

                        if( findStringIgnoreCase(parse.getCommandStr().c_str(), " direct") )
                        {
                            trip_close = BinaryOutputControl::NUL;
                        }
                        else
                        {
                            if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                            {
                                trip_close = BinaryOutputControl::Trip;
                            }
                            else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                            {
                                trip_close = BinaryOutputControl::Close;
                            }
                        }

                        controltype = BinaryOutputControl::PulseOn;
                        offset      = controlParameters->getControlOffset();
                    }

                    //  set up the pseudo data to be sent from Porter-side
                    p_i.is_pseudo = pStatus->isPseudoPoint();
                    p_i.pointid   = pStatus->getPointID();
                    p_i.state     = hist->getRawState();

                    hist->setMessagePriority(MAXPRIORITY - 1);
                    hist->setUser(pReq->getUser());
                    vgList.push_back(hist);
                }
                else
                {
                    throw YukonErrorException(ClientErrors::NoPointControlConfiguration, "The specified point has no control information");
                }
            }
            else if( offset )
            {
                //  no point - send it raw if we have an offset

                if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
                {
                    controltype = BinaryOutputControl::PulseOff;
                }
                else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
                {
                    controltype = BinaryOutputControl::PulseOn;
                }

                p_i.is_pseudo = false;
            }

            if( offset <= 0 )
            {
                CTILOG_WARN(dout, "no point specified for control for device \""<< getName() <<"\"");
            }
            else
            {
                controlout.type            = DnpProtocol::DigitalOutputPointType;
                controlout.control_offset  = offset - 1;  //  convert to DNP's 0-based indexing

                controlout.dout.control    = controltype;
                controlout.dout.trip_close = trip_close;
                controlout.dout.on_time    = on_time;
                controlout.dout.off_time   = off_time;
                controlout.dout.count      = 1;
                controlout.dout.queue      = false;
                controlout.dout.clear      = false;

                if( pStatus )
                {
                    int controlTimeout = DefaultControlExpirationTime;

                    if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                    {
                        const int dbCommandTimeout = controlParameters->getCommandTimeout();

                        if( dbCommandTimeout > 0 )
                        {
                            controlTimeout = dbCommandTimeout;
                        }
                    }

                    OutMessage->ExpirationTime = CtiTime().seconds() + controlTimeout;
                }

                if( pStatus &&
                    pStatus->getControlParameters() &&
                    pStatus->getControlParameters()->isControlInhibited() )
                {
                    CTILOG_WARN(dout, "control inhibited for device \""<< getName() <<"\" point \""<< pStatus->getName() <<"\"");
                }
                else
                {
                    if( parse.isKeyValid("sbo_selectonly") )
                    {
                        //  for diagnostics - used for verifying SBO timeouts
                        command = DnpProtocol::Command_SetDigitalOut_SBO_SelectOnly;
                    }
                    else if( parse.isKeyValid("sbo_operate") )
                    {
                        //  the other half of SBO_SelectOnly
                        command = DnpProtocol::Command_SetDigitalOut_SBO_Operate;
                    }
                    else if( pStatus &&
                             pStatus->getControlParameters() &&
                             (pStatus->getControlParameters()->getControlType() == ControlType_SBOPulse ||
                              pStatus->getControlParameters()->getControlType() == ControlType_SBOLatch) )
                    {
                        //  if successful, this will transition to SBO_Operate in DNPInterface on Porter-side
                        command = DnpProtocol::Command_SetDigitalOut_SBO_Select;
                    }
                    else
                    {
                        //  boring old direct control
                        command = DnpProtocol::Command_SetDigitalOut_Direct;
                    }
                }
            }

            break;
        }

        case ScanRequest:
        {
            Cti::Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
            if( ! deviceConfig )
            {
                CTILOG_ERROR(dout, "DNP configuration missing for DNP device \""<< getName() <<"\"");

                throw YukonErrorException(ClientErrors::MissingConfig, "DNP configuration missing for DNP device");
            }

            const bool omitTimeRequest = ciStringEqual(deviceConfig->getValueFromKey(DNPStrings::omitTimeRequest), "true");

            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                {
                    if( omitTimeRequest )
                    {
                        command = DnpProtocol::Command_Class123Read;
                    }
                    else
                    {
                        command = DnpProtocol::Command_Class123Read_WithTime;
                    }

                    break;
                }

                case ScanRateAccum:
                {
                    CTILOG_ERROR(dout, "Accumulator scanrates not defined for DNP device \""<< getName() <<"\"");

                    break;
                }

                case ScanRateIntegrity:
                {
                    if( omitTimeRequest )
                    {
                        command = DnpProtocol::Command_Class1230Read;
                    }
                    else
                    {
                        command = DnpProtocol::Command_Class1230Read_WithTime;
                    }

                    break;
                }
            }

            break;
        }

        case PutValueRequest:
        {
            static auto getAnalogControl = [](const CtiPointSPtr& point) 
            {
                if( point->getType() != AnalogPointType )
                {
                    throw YukonErrorException(ClientErrors::PointLookupFailed, "The specified point is not Analog type");
                }

                auto pAnalog = boost::static_pointer_cast<const CtiPointAnalog>(point);

                const auto control = pAnalog->getControl();

                if( control && control->isControlInhibited() )
                {
                    throw YukonErrorException(ClientErrors::ControlInhibitedOnPoint, "Control is inhibited for the specified analog point" + FormattedList::of(
                        "Point ID",   point->getPointID(),
                        "Point name", point->getName()));
                }

                return control;
            };

            if( parse.isKeyValid("analog") )
            {
                long control_offset = 0;

                if( parse.isKeyValid("analogoffset") )
                {
                    const auto analogoffset = parse.getiValue("analogoffset");

                    if( const auto point = getDeviceAnalogOutputPoint(analogoffset) )
                    {
                        getAnalogControl(point);
                    }

                    control_offset = analogoffset;
                }
                else if( parse.isKeyValid("point") )
                {
                    if( const auto point = getDevicePointByID(parse.getiValue("point")) )
                    {
                        if( const auto control = getAnalogControl(point) )
                        {
                            control_offset = control->getControlOffset();
                        }
                        else if( point->getPointOffset() > AnalogOutputStatus::AnalogOutputOffset )
                        {
                            control_offset = point->getPointOffset() % AnalogOutputStatus::AnalogOutputOffset;
                        }
                        else
                        {
                            throw YukonErrorException(ClientErrors::NoPointControlConfiguration, "Analog point has no control offset");
                        }
                    }
                    else 
                    {
                        throw YukonErrorException(ClientErrors::PointLookupFailed, "The specified point is not on the device");
                    }
                }
                else
                {
                    break;
                }

                if( control_offset <= 0 )
                {
                    throw YukonErrorException(ClientErrors::BadParameter, "Invalid analog output offset (" + std::to_string(control_offset) + ")");
                }

                controlout.control_offset = control_offset - 1;  //  convert to DNP's 0-based indexing

                if (parse.isKeyValid("analogfloatvalue"))
                {
                    controlout.type = DnpProtocol::AnalogOutputFloatPointType;
                }
                else
                {
                    controlout.type = DnpProtocol::AnalogOutputPointType;
                }

                controlout.aout.value     = parse.getdValue("analogvalue");

                command = DnpProtocol::Command_SetAnalogOut;
            }

            break;
        }

        case PutConfigRequest:
        {
            if(parse.isKeyValid("timesync"))
            {
                command = DnpProtocol::Command_WriteTime;
            }
            else if( parse.isKeyValid("unsolicited") )
            {
                if( parse.getiValue("unsolicited") )
                {
                    command = DnpProtocol::Command_UnsolicitedEnable;
                }
                else
                {
                    command = DnpProtocol::Command_UnsolicitedDisable;
                }
            }

            break;
        }

        case GetConfigRequest:
        {
            if(parse.isKeyValid("time"))
            {
                command = DnpProtocol::Command_ReadTime;
            }

            break;
        }

        case GetValueRequest:
        case PutStatusRequest:
        default:
        {
            CTILOG_ERROR(dout, "command type \"" << parse.getCommand() << "\" not found");
        }
    }

    if( command == DnpProtocol::Command_Invalid )
    {
        string resultString;

        CTILOG_ERROR(dout, "Couldn't come up with an operation for device "<< getName() <<". Command: " << pReq->CommandString());

        resultString = "Invalid command for device \"" + getName() + "\"";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.RetryMacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );

        delete OutMessage;
        OutMessage = NULL;
    }
    else
    {
        //  only used for the call to sendCommRequest(), unreliable after - DO NOT USE FOR DECODES
        _pil_info.protocol_command   = command;
        _pil_info.protocol_parameter = controlout;
        _pil_info.pseudo_info        = p_i;
        _pil_info.user               = pReq->getUser();

        OutMessage->Port     = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        EstablishOutMessagePriority(OutMessage, pReq->getMessagePriority());

        sendCommRequest(OutMessage, outList);

        nRet = ClientErrors::None;
    }

    return nRet;
}
catch( YukonErrorException & ex )
{
    return insertReturnMsg(ex.error_code, OutMessage, retList, ex.error_description);
}


Protocols::Interface *DnpDevice::getProtocol()
{
    return &_dnp;
}


YukonError_t DnpDevice::sendCommRequest( OUTMESS *&OutMessage, OutMessageList &outList )
{
    YukonError_t retVal = ClientErrors::None;

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
        OutMessage->Retry         = 0;  //  _dnp.commandRetries();

        //  give it a higher priority if it's a time sync or a control command
        switch( _pil_info.protocol_command )
        {
            case Protocols::DnpProtocol::Command_WriteTime:
                OutMessage->Priority = MAXPRIORITY - 1;
                break;

            case Protocols::DnpProtocol::Command_SetDigitalOut_Direct:
            case Protocols::DnpProtocol::Command_SetDigitalOut_SBO_Operate:
            case Protocols::DnpProtocol::Command_SetDigitalOut_SBO_Select:
            case Protocols::DnpProtocol::Command_SetDigitalOut_SBO_SelectOnly:
                OutMessage->Priority = MAXPRIORITY - 2;
                break;
        }

        outList.push_back(OutMessage);
        OutMessage = 0;
    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");
        retVal = ClientErrors::Memory;
    }

    return retVal;
}


YukonError_t DnpDevice::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retVal = ClientErrors::None;

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

        try
        {
            loadConfigData();
            _dnp.setCommand(_porter_info.protocol_command, _porter_info.protocol_parameter);
        }
        catch( const std::exception& e )
        {
            CTILOG_EXCEPTION_ERROR(dout, e, "Device "<< getName() <<" had a configuration failure, unable to process comm request");

            retVal = ClientErrors::NoConfigData;
        }
    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");
        retVal = ClientErrors::Memory;
    }

    return retVal;
}

void DnpDevice::loadConfigData()
{
    Cti::Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( !deviceConfig )
    {
        throw MissingConfigException();
    }

    static const std::map<std::string, Protocols::DNP::TimeOffset> TimeOffsets {
        { "UTC",            Protocols::DNP::TimeOffset::Utc },
        { "LOCAL",          Protocols::DNP::TimeOffset::Local },
        { "LOCAL_NO_DST",   Protocols::DNP::TimeOffset::LocalStandard }};

    const unsigned internalRetries = deviceConfig->getLongValueFromKey(DNPStrings::internalRetries);
    const auto timeOffset = getConfigDataEnum(deviceConfig, DNPStrings::timeOffset, TimeOffsets);
    const bool enableDnpTimesyncs = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::enableDnpTimesyncs));
    const bool omitTimeRequest = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::omitTimeRequest));
    const bool enableUnsolicitedClass1 = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::enableUnsolicitedClass1));
    const bool enableUnsolicitedClass2 = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::enableUnsolicitedClass2));
    const bool enableUnsolicitedClass3 = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::enableUnsolicitedClass3));
    const bool enableNonUpdatedOnFailedScan = isConfigurationValueTrue(deviceConfig->getValueFromKey(DNPStrings::enableNonUpdatedOnFailedScan));

    _dnp.setConfigData(internalRetries, timeOffset, enableDnpTimesyncs, omitTimeRequest, enableUnsolicitedClass1,
                       enableUnsolicitedClass2, enableUnsolicitedClass3, enableNonUpdatedOnFailedScan);
}

bool DnpDevice::isConfigurationValueTrue(const std::string &configKey) const
{
    return ciStringEqual(configKey, "true");
}


void DnpDevice::initUnsolicited()
{
    loadConfigData();
    _dnp.setCommand(Protocols::DnpProtocol::Command_UnsolicitedInbound);
}


YukonError_t DnpDevice::sendCommResult(INMESS &InMessage)
{
    char *buf;
    string result_string;

    buf = reinterpret_cast<char *>(InMessage.Buffer.InMessage);

    //  this needs to be smarter and send the device name and point data elements seperately...
    for( const auto &str : _dnp.getInboundStrings() )
    {
        result_string += getName();
        result_string += " / ";
        result_string += str;
        result_string += "\n";
    }

    InMessage.InLength = result_string.size() + 1;

    //  make sure we don't overrun the buffer, even though we just checked above
    strncpy(buf, result_string.c_str(), sizeof(InMessage.Buffer.InMessage) - 1);
    //  and mark the end with a null, again, just to be sure
    InMessage.Buffer.InMessage[sizeof(InMessage.Buffer.InMessage) - 1] = 0;

    return ClientErrors::None;
}


void DnpDevice::sendDispatchResults(CtiConnection &vg_connection)
{
    auto vgMsg = std::make_unique<CtiReturnMsg>(getID());  //  , InMessage.Return.CommandStr

    {
        Protocols::Interface::pointlist_t points;

        _dnp.getInboundPoints(points);

        //  do any device-dependent work on the points
        processPoints(points);

        //  then toss them into the return msg
        for( auto pt_msg : points )
        {
            if( pt_msg )
            {
                vgMsg->PointData().push_back(pt_msg);
            }
        }
    }

    //  now send the pseudos related to the control point
    //    note:  points are the domain of the device, not the protocol,
    //           so i have to handle pseudo points here, in the device code
    switch( _porter_info.protocol_command )
    {
        case Protocols::DnpProtocol::Command_SetDigitalOut_Direct:
        case Protocols::DnpProtocol::Command_SetDigitalOut_SBO_Select:  //  presumably this will transition...  we need to verify this...
        case Protocols::DnpProtocol::Command_SetDigitalOut_SBO_Operate:
        {
            if( _porter_info.pseudo_info.is_pseudo /*&& !_dnp.errorCondition()*/ )  //  ... for example, make sure the control was successful
            {
                CtiPointDataMsg *msg = CTIDBG_new CtiPointDataMsg(_porter_info.pseudo_info.pointid,
                                                                  _porter_info.pseudo_info.state,
                                                                  NormalQuality,
                                                                  StatusPointType,
                                                                  "This point has been controlled");
                msg->setUser(_porter_info.user.c_str());
                vgMsg->PointData().push_back(msg);
            }

            break;
        }
    }

    vg_connection.WriteConnQue(std::move(vgMsg), CALLSITE);
}


std::unique_ptr<CtiPointDataMsg> DnpDevice::calculateDemandAccumulator(const CtiPointAccumulatorSPtr& demandPoint, dnp_accumulator_pointdata current)
{
    if( auto previous = Cti::mapFindRef(_lastIntervalAccumulatorData, demandPoint->getPointOffset()) )
    {
        double demandValue;

        CTILOG_INFO(dout, "demand accumulator calculation data for device \"" << getName() << "\", pointid " << demandPoint->getPointID() <<
            endl << "current.point_value  = " << current.point_value <<
            endl << "current.point_time   = " << current.point_time <<
            endl << "previous.point_value = " << previous->point_value <<
            endl << "previous.point_time  = " << previous->point_time
        );

        if( previous->point_time + 60 <= current.point_time )
        {
            if( previous->point_value <= current.point_value )
            {
                demandValue = current.point_value - previous->point_value;
            }
            else // previous.point_value > current.point_value
            {
                //  rollover has occurred - figure out how big the accumulator was
                if( previous->point_value > 0x0000ffff )
                {
                    //  it was a 32-bit accumulator
                    demandValue = (numeric_limits<unsigned long>::max() - previous->point_value) + current.point_value;
                }
                else
                {
                    //  it was a 16-bit accumulator
                    demandValue = (numeric_limits<unsigned short>::max() - previous->point_value) + current.point_value;
                }
            }

            demandValue *= 3600.0;
            demandValue /= (current.point_time - previous->point_time);

            demandValue = demandPoint->computeValueForUOM(demandValue);

            auto resultString = getName() + " / " + demandPoint->getName();
            resultString += ": " + CtiNumStr(demandValue, (demandPoint)->getPointUnits().getDecimalPlaces());

            CTILOG_INFO(dout, "updating demand accumulator calculation data" <<
                endl << "current.point_value  = " << current.point_value <<
                endl << "current.point_time   = " << current.point_time
            );

            previous = current;

            auto msg = std::make_unique<CtiPointDataMsg>(demandPoint->getID(), demandValue, NormalQuality, DemandAccumulatorPointType, resultString);
            msg->setTime(current.point_time);

            return msg;
        }
        else
        {
            CTILOG_WARN(dout, "demand not calculated; interval < 60 sec");
        }
    }
    else
    {
        CTILOG_INFO(dout, "inserting demand accumulator calculation data" <<
            endl << "current.point_value  = " << current.point_value <<
            endl << "current.point_time   = " << current.point_time
        );

        _lastIntervalAccumulatorData.emplace(demandPoint->getPointOffset(), current);
    }

    return nullptr;
}

auto DnpDevice::getDevicePointsByType() const -> PointsByType
{
    std::vector<CtiPointSPtr> devicePoints;

    getDevicePoints(devicePoints);

    PointsByType points;

    for( auto& pt : devicePoints )
    {
        points[pt->getType()].emplace(pt->getPointOffset(), pt);
    }

    return points;
}

void DnpDevice::processPoints( Protocols::Interface::pointlist_t &pointdata )
{
    if( ! _pointMgr )
    {
        CTILOG_ERROR(dout, "Point manager not set, cannot process points" << Cti::FormattedList::of(
            "Device ID", getID(),
            "Device name", getName()));

        delete_container(pointdata);  pointdata.clear();
        return;
    }

    auto devicePointsByType = getDevicePointsByType();

    Protocols::Interface::pointlist_t demand_pointdata;

    for( auto& msg : pointdata )
    {
        if( ! msg )
        {
            continue;
        }

        CtiPointSPtr point;

        if( auto& typePoints = mapFindRef(devicePointsByType, msg->getType()) )
        {
            //  !!! msg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
            if( auto& optPoint = mapFindRef(*typePoints, msg->getId()) )
            {
                point = *optPoint;
            }
        }

        if( ! point )
        {
            delete msg;
            msg = nullptr;
            continue;
        }

        //  assign the device's actual point id now
        msg->setId(point->getID());

        if( point->isNumeric() )
        {
            //  if it's a pulse accumulator, we must attempt to calculate its demand accumulator
            if( point->getType() == PulseAccumulatorPointType )
            {
                //  is there an accompanying demand accumulator for this pulse accumulator?
                if( auto& demandPoints = mapFindRef(devicePointsByType, DemandAccumulatorPointType) )
                {
                    if( auto& optDemandPoint = mapFindRef(*demandPoints, point->getPointOffset()) )
                    {
                        auto demandPoint = boost::static_pointer_cast<CtiPointAccumulator>(*optDemandPoint);

                        //  get the raw pulses from the pulse accumulator
                        dnp_accumulator_pointdata update {
                            msg->getValue(),
                            msg->getTime().seconds() };

                        if( auto demand_msg = calculateDemandAccumulator(demandPoint, update) )
                        {
                            demand_pointdata.push_back(demand_msg.release());
                        }
                    }
                }
            }

            CtiPointNumericSPtr pNumeric = boost::static_pointer_cast<CtiPointNumeric>(point);

            msg->setValue(pNumeric->computeValueForUOM(msg->getValue()));

            auto resultString  = getName() + " / " + point->getName() + ": " + CtiNumStr(msg->getValue(), (pNumeric)->getPointUnits().getDecimalPlaces());
            resultString += " @ " + msg->getTime().asString();
            msg->setString(resultString);
        }
        else if( point->isStatus() )
        {
            CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(point);
            
            auto resultString  = getName() + " / " + point->getName() + ": " + ResolveStateName(pStatus->getStateGroupID(), msg->getValue());
            resultString += " @ " + msg->getTime().asString();
            msg->setString(resultString);
        }
    }

    pointdata.insert(pointdata.end(), demand_pointdata.begin(), demand_pointdata.end());
}


YukonError_t DnpDevice::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    CtiReturnMsg *retMsg;

    if( ! InMessage.ErrorCode )
    {
        string result_string;

        unsigned long length = InMessage.InLength;

        //  safety first
        if( InMessage.InLength > sizeof(InMessage.Buffer.InMessage) )
        {
            CTILOG_WARN(dout, "InMessage.InLength > sizeof(InMessage.Buffer.InMessage) for device \""<< getName() <<"\" ("<< InMessage.InLength <<" > "<< sizeof(InMessage.Buffer.InMessage) <<"), length will be limited to "<< sizeof(InMessage.Buffer.InMessage));

            length = sizeof(InMessage.Buffer.InMessage);
        }

        result_string.assign(InMessage.Buffer.InMessage,
                             InMessage.Buffer.InMessage + length);

        if( strstr(InMessage.Return.CommandStr, "scan integrity") )
        {
            //  case Protocol::DNPInterface::Command_Class1230Read:
            setScanFlag(ScanRateIntegrity, false);
        }
        if( strstr(InMessage.Return.CommandStr, "scan general") )
        {
            //  case Protocol::DNPInterface::Command_Class123Read:
            setScanFlag(ScanRateGeneral, false);
        }

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage.Return.CommandStr),
                                         result_string.c_str(),
                                         InMessage.ErrorCode,
                                         InMessage.Return.RouteID,
                                         InMessage.Return.RetryMacroOffset,
                                         InMessage.Return.Attempt,
                                         InMessage.Return.GrpMsgID,
                                         InMessage.Return.UserID);

        retList.push_back(retMsg);
    }
    else
    {
        const string error_str = CtiError::GetErrorString(InMessage.ErrorCode);

        string resultString;

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(InMessage.ErrorCode).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage.Return.CommandStr),
                                         resultString,
                                         InMessage.ErrorCode,
                                         InMessage.Return.RouteID,
                                         InMessage.Return.RetryMacroOffset,
                                         InMessage.Return.Attempt,
                                         InMessage.Return.GrpMsgID,
                                         InMessage.Return.UserID);

        retList.push_back(retMsg);
    }

    return  InMessage.ErrorCode;
}


YukonError_t DnpDevice::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    YukonError_t retCode = ClientErrors::None;

    CtiCommandParser  parse(InMessage.Return.CommandStr);

    CtiPointDataMsg  *commFailed;
    CtiPointSPtr     commPoint;

    Cti::Config::DeviceConfigSPtr deviceConfig = getDeviceConfig();
    if (!deviceConfig)
    {
        CTILOG_ERROR(dout, "DNP configuration missing for DNP device \"" << getName() << "\"");

        retCode = ClientErrors::MissingConfig;
        return retCode;
    }

    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    if( strstr(InMessage.Return.CommandStr, "scan integrity") )
    {
        //  case Protocol::DNPInterface::Command_Class1230Read:
        setScanFlag(ScanRateIntegrity, false);
    }
    if( strstr(InMessage.Return.CommandStr, "scan general") )
    {
        //  case Protocol::DNPInterface::Command_Class123Read:
        setScanFlag(ScanRateGeneral, false);
    }

    if(ciStringEqual(deviceConfig->getValueFromKey(DNPStrings::enableNonUpdatedOnFailedScan), "true"))
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert(-1);             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(CtiCommandMsg::OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            pMsg->insert(
                InMessage.ErrorCode
                ? InMessage.ErrorCode
                : ClientErrors::GeneralScanAborted);

            retList.push_back(pMsg);
        }
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string DnpDevice::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}

string DnpDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, DUS.phonenumber, "
                                     "DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, DUS.baudrate, "
                                     "AD.masteraddress, AD.slaveaddress, AD.postcommwait "
                                   "FROM Device DV, DeviceAddress AD, DeviceDirectCommSettings CS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = AD.deviceid AND YP.paobjectid = DV.deviceid AND "
                                     "YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void DnpDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _dnp_address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CTILOG_DEBUG(dout, "Decoding DB reader");
   }

   _dnp.setAddresses(_dnp_address.getSlaveAddress(), _dnp_address.getMasterAddress());
   _dnp.setName(getName());

   if( getType() == TYPE_DARTRTU )
   {
       _dnp.setDatalinkConfirm();
   }
}

}
}
