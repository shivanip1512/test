#include "precompiled.h"

#include "dev_dlcbase.h"
#include "dev_mct.h"  //  for ARM commands
#include "cparms.h"
#include "devicetypes.h"
#include "msg_cmd.h"
#include "pt_base.h"
#include "dsm2.h"
#include "utility.h"
#include "porter.h"
#include "numstr.h"

#include "std_helper.h"

#include <boost/optional.hpp>
#include <boost/assign/list_of.hpp>

using std::string;
using std::endl;
using std::list;

using namespace Cti::Protocols;

using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

unsigned int DlcBaseDevice::_lpRetryMultiplier = 0;
unsigned int DlcBaseDevice::_lpRetryMinimum    = 0;
unsigned int DlcBaseDevice::_lpRetryMaximum    = 0;

DlcBaseDevice::DlcBaseDevice() :
    _activeIndex(EmetconProtocol::DLCCmd_LAST)
{}

string DlcBaseDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, DCS.address, "
                                     "RTS.routeid "
                                   "FROM Device DV, DeviceCarrierSettings DCS, YukonPAObject YP LEFT OUTER JOIN "
                                     "DeviceRoutes RTS ON YP.paobjectid = RTS.deviceid "
                                   "WHERE YP.paobjectid = DCS.deviceid AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void DlcBaseDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    boost::optional<int> oldAddress;

    if( CarrierSettings.isInitialized() )
    {
        oldAddress = CarrierSettings.getAddress();
    }

    Parent::DecodeDatabaseReader(rdr);       //  get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    CtiLockGuard<CtiMutex> guard(_classMutex);
    CarrierSettings.DecodeDatabaseReader(rdr);

    if( oldAddress && *oldAddress != CarrierSettings.getAddress() )
    {
        purgeDynamicPaoInfo();

        CTILOG_INFO(dout, "Device address for \""<< getName() <<"\" (paoid "<< getID() <<") has been updated"
                " from \"" << *oldAddress << "\" to \""<< CarrierSettings.getAddress() <<"\"."
                " Purging dynamicPaoInfo from memory and database."
                );
    }

    DeviceRoutes.DecodeDatabaseReader(rdr);
}

LONG DlcBaseDevice::getAddress() const   {   return CarrierSettings.getAddress();    }
LONG DlcBaseDevice::getRouteID() const   {   return DeviceRoutes.getRouteID();       }   //  From CtiTableDeviceRoute


const std::set<CtiClientRequest_t> BroadcastRequestTypes = boost::assign::list_of
    (PutValueRequest)
    (ControlRequest)
    (PutStatusRequest)
    (PutConfigRequest);

const std::map<CtiClientRequest_t, DlcBaseDevice::ExecuteMethod> DlcBaseDevice::_executeMethods = DlcBaseDevice::buildExecuteMethodMap();

const std::map<CtiClientRequest_t, DlcBaseDevice::ExecuteMethod> DlcBaseDevice::buildExecuteMethodMap()
{
    return boost::assign::map_list_of
            (LoopbackRequest,  &Self::executeLoopback)
            (ScanRequest,      &Self::executeScan)
            (GetValueRequest,  &Self::executeGetValue)
            (PutValueRequest,  &Self::executePutValue)
            (ControlRequest,   &Self::executeControl)
            (GetStatusRequest, &Self::executeGetStatus)
            (PutStatusRequest, &Self::executePutStatus)
            (GetConfigRequest, &Self::executeGetConfig)
            (PutConfigRequest, &Self::executePutConfig);
}

YukonError_t DlcBaseDevice::ExecuteRequest( CtiRequestMsg     *pReq,
                                            CtiCommandParser  &parse,
                                            OUTMESS          *&OutMessage,
                                            CtiMessageList    &vgList,
                                            CtiMessageList    &retList,
                                            OutMessageList    &outList )
{
    YukonError_t nRet = ClientErrors::None;
    bool broadcast = false;
    OutMessageList tmpOutList;

    if( OutMessage )
    {
        EstablishOutMessagePriority( OutMessage, MAXPRIORITY - 4 );
    }

    try
    {
        if( const boost::optional<ExecuteMethod> method = mapFind(_executeMethods, parse.getCommand()) )
        {
            nRet = (this->**method)(pReq, parse, OutMessage, vgList, retList, tmpOutList);

            broadcast = BroadcastRequestTypes.count(parse.getCommand());
        }
        else
        {
            CTILOG_ERROR(dout, "Unsupported command on EMETCON route. Command = "<< parse.getCommand());

            nRet = ClientErrors::NoMethod;
        }
    }
    catch( DlcCommand::CommandException &e )
    {
        returnErrorMessage(e.error_code, OutMessage, retList, e.error_description);

        nRet = ExecutionComplete;
    }

    if( nRet )
    {
        CTILOG_ERROR(dout, "Couldn't come up with an operation for device "<< getName() <<". Command: " << pReq->CommandString());

        retList.push_back(
                new CtiReturnMsg(
                        getID( ),
                        OutMessage->Request,
                        "NoMethod or invalid command.",
                        nRet));
    }
    else
    {
        if( OutMessage )
        {
            tmpOutList.push_back( OutMessage );
            OutMessage = NULL;
        }

        executeOnDLCRoute(pReq, parse, tmpOutList, vgList, retList, outList, broadcast);
    }

    return nRet;
}


YukonError_t DlcBaseDevice::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    try
    {
        findAndDecodeCommand(InMessage, TimeNow, vgList, retList, outList);
    }
    catch( DlcCommand::CommandException &e )
    {
        retList.push_back(
            new CtiReturnMsg(
                getID(),
                InMessage.Return,
                getName() + " / " + e.error_description,
                e.error_code));
    }

    return ExecutionComplete;
}


YukonError_t DlcBaseDevice::SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    try
    {
        findAndDecodeCommand(InMessage, TimeNow, vgList, retList, outList);
    }
    catch( DlcCommand::CommandException &e )
    {
        retList.push_back(
            new CtiReturnMsg(
                getID(),
                InMessage.Return,
                getName() + " / " + e.error_description,
                e.error_code));
    }

    return ClientErrors::None;
}


void DlcBaseDevice::retMsgHandler( string commandStr, YukonError_t status, CtiReturnMsg *retMsg, CtiMessageList &vgList, CtiMessageList &retList, bool expectMore ) const
{
    CtiReturnMsg    *tmpVGRetMsg = NULL;
    CtiPointDataMsg *tmpMsg;
    bool archive = false;

    archive = (commandStr.find(" update") != string::npos);

    if( retMsg )
    {
        //  is there anything to send?
        if( !retMsg->ResultString().empty() || !retMsg->PointData().empty() )
        {
            //  if it's an update command, PIL will copy the data to Dispatch (vgList) for us, but we still
            //    need to mark the points "must archive."
            //    This should be unified.  It's too confusing right now, what with the retList and vgList.
            //  Also, if we're Scanner, don't copy the data - it'll all go to Dispatch anyway.
            if( !useScanFlags() )
            {
                if( !archive )
                {
                    //  PIL won't be copying the LP data for us, so we need to
                    //    make a return msg for possible use
                    tmpVGRetMsg = (CtiReturnMsg *)retMsg->replicateMessage();

                    //  make sure it's empty so we only append the messages we intend to
                    delete_container( tmpVGRetMsg->PointData() );
                    tmpVGRetMsg->PointData().clear();
                }

                const CtiMultiMsg_vec &subMsgs = retMsg->getData();

                CtiMultiMsg_vec::const_iterator itr;
                //  Check for any "Must Archive" points and send them to Dispatch
                for( itr = subMsgs.begin(); itr != subMsgs.end(); itr++ )
                {
                    if( (*itr)->isA() == MSG_POINTDATA )
                    {
                        tmpMsg = (CtiPointDataMsg *)(*itr);

                        if( archive )
                        {
                            //  PIL will be copying the data - so all we need to do is mark it "must archive"
                            //    if it's not already a load profile point
                            if( !(tmpMsg->getTags() & TAG_POINT_LOAD_PROFILE_DATA) )
                            {
                                tmpMsg->setTags(TAG_POINT_MUST_ARCHIVE);
                            }
                        }
                        else if( tmpMsg->getTags() & (TAG_POINT_MUST_ARCHIVE | TAG_POINT_LOAD_PROFILE_DATA) )
                        {
                            //  otherwise, we need to copy the "must archive" data ourselves
                            tmpVGRetMsg->PointData().push_back(tmpMsg->replicateMessage());
                        }
                    }
                }

                if( tmpVGRetMsg )
                {
                    if( !tmpVGRetMsg->PointData().empty() )
                    {
                        vgList.push_back(tmpVGRetMsg);
                    }
                    else
                    {
                        delete tmpVGRetMsg;
                    }
                }
            }

            retMsg->setStatus(status);

            if( expectMore )
            {
                retMsg->setExpectMore(true);
            }

            retList.push_back(retMsg);
        }
        else
        {
            delete retMsg;
        }
    }
}

bool DlcBaseDevice::dlcAddressMismatch(const DSTRUCT dst, const CtiDeviceBase & temDevice)
{
    if( dst.Length && //  make sure it's not just an ACK
        temDevice.getAddress() != MctDevice::TestAddress1 &&  //  also, make sure we're not sending to an FCT-jumpered MCT,
        temDevice.getAddress() != MctDevice::TestAddress2 &&  //    since it'll return its native address and not the test address
        (temDevice.getAddress() & 0x1fff) != (dst.Address & 0x1fff) )
    {
        //  Seems this should percolate to the device level, although setting status here kills the decode
        CTILOG_ERROR(dout, "Wrong DLC Address: \""<< temDevice.getName() <<"\" ("<< temDevice.getAddress() <<") != ("<< dst.Address <<")");

        return true;
    }
    return false;
}


void DlcBaseDevice::handleCommandResult(const Commands::DlcCommand &command)
{
    command.invokeResultHandler(*this);
}


void DlcBaseDevice::findAndDecodeCommand(const INMESS &InMessage, CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    //  We need to protect _activeCommands/trackCommand()
    CtiLockGuard<CtiMutex> lock(getMux());

    active_command_map::iterator command_itr = _activeCommands.find(InMessage.Sequence);

    if( command_itr == _activeCommands.end() )
    {
        return;  //  in order to silently absorb macro route returns that have already been acted upon
    }

    DlcCommand &command = *(command_itr->second);

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return);

    try
    {
        DlcCommand::request_ptr ptr;
        string description;

        if( InMessage.ErrorCode )
        {
            ptr = command.error(TimeNow, InMessage.ErrorCode, description);
        }
        else
        {
            const DSTRUCT &dst = InMessage.Buffer.DSt;

            boost::optional<DlcCommand::Bytes> payload;

            if( InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read ||
                InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Read )
            {
                payload =
                    DlcCommand::Bytes(
                        dst.Message,
                        dst.Message + std::min<unsigned short>(dst.Length, DSTRUCT::MessageLength_Max));
            }

            std::vector<DlcCommand::point_data> points;

            unsigned function = InMessage.Return.ProtocolInfo.Emetcon.Function;

            if( InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read ||
                InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Write )
            {
                function += 0x100;
            }

            ptr = command.decodeCommand(TimeNow, function, payload, description, points);

            handleCommandResult(command);

            for each( const DlcCommand::point_data &pdata in points )
            {
                point_info pi;

                pi.description  = pdata.description;
                pi.quality      = pdata.quality;
                pi.value        = pdata.value;

                insertPointDataReport(pdata.type, pdata.offset, ReturnMsg, pi, pdata.name, pdata.time);
            }
        }

        if( ! description.empty() )
        {
            ReturnMsg->setResultString(getName() + " / " + description);
        }

        retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg, vgList, retList);

        if( ptr.get() )
        {
            OUTMESS *OutMessage = new OUTMESS;

            InEchoToOut(InMessage, *OutMessage);

            //  If there were no errors, start the command on the first macro route
            if( ! InMessage.ErrorCode )
            {
                OutMessage->Request.RetryMacroOffset = selectInitialMacroRouteOffset(OutMessage->Request.RouteID);
            }

            fillOutMessage(*OutMessage, *ptr);

            CtiRequestMsg newReq(getID(),
                                 InMessage.Return.CommandStr,
                                 InMessage.Return.UserID,
                                 InMessage.Return.GrpMsgID,
                                 InMessage.Return.RouteID,
                                 selectInitialMacroRouteOffset(InMessage.Return.RouteID),
                                 InMessage.Return.Attempt,
                                 0,
                                 InMessage.Priority);

            newReq.setConnectionHandle((void *)InMessage.Return.Connection);

            executeOnDLCRoute(&newReq,
                              CtiCommandParser(newReq.CommandString()),
                              OutMessageList(1, OutMessage),
                              vgList, retList, outList, false);
        }
        else
        {
            _activeCommands.erase(command_itr);

            decrementGroupMessageCount(InMessage.Return.UserID, (long)InMessage.Return.Connection);
        }
    }
    catch( DlcCommand::CommandException &e )
    {
        ReturnMsg->setStatus(e.error_code);
        ReturnMsg->setResultString(getName() + " / " + e.error_description);

        retList.push_back(ReturnMsg);

        //  broken!
        _activeCommands.erase(command_itr);
    }
}


long DlcBaseDevice::trackCommand(DlcCommandAutoPtr command)
{
    if( _activeIndex < EmetconProtocol::DLCCmd_LAST )
    {
        _activeIndex = EmetconProtocol::DLCCmd_LAST;
    }

    while( _activeCommands.count(_activeIndex) )
    {
        _activeIndex++;
    }

    _activeCommands.insert(_activeIndex, command);

    return _activeIndex++;
}


void DlcBaseDevice::fillOutMessage(OUTMESS &OutMessage, DlcCommand::request_t &request)
{
    populateDlcOutMessage(OutMessage);

    OutMessage.Request.RouteID     = getRouteID();

    if( ! OutMessage.Request.RetryMacroOffset )
    {
        OutMessage.Request.RetryMacroOffset = selectInitialMacroRouteOffset(OutMessage.Request.RouteID);
    }

    OutMessage.Buffer.BSt.Function = request.function();
    OutMessage.Buffer.BSt.IO       = request.io();
    OutMessage.Buffer.BSt.Length   = request.length();

    DlcCommand::Bytes payload = request.payload();

    std::copy(payload.begin(),
              payload.begin() + std::min<unsigned>(payload.size(), BSTRUCT::MessageLength_Max),
              OutMessage.Buffer.BSt.Message);
}


void DlcBaseDevice::populateDlcOutMessage(OUTMESS &OutMessage)
{
    populateOutMessage(OutMessage);

    OutMessage.TargetID = getID();
    OutMessage.Retry = 2;
}


bool DlcBaseDevice::tryExecuteCommand(OUTMESS &OutMessage, DlcCommandAutoPtr command)
{
    DlcCommand::request_ptr request = command->executeCommand(CtiTime());

    if( request.get() )
    {
        fillOutMessage(OutMessage, *request);

        //  ExecuteRequest already has the CtiDeviceBase::_classMutex at this point, so it's safe to call trackCommand()
        OutMessage.Sequence = trackCommand(command);
    }

    return request.get();
}


YukonError_t DlcBaseDevice::executeOnDLCRoute( CtiRequestMsg       *pReq,
                                               CtiCommandParser &parse,
                                               OutMessageList   &tmpOutList,
                                               CtiMessageList   &vgList,
                                               CtiMessageList   &retList,
                                               OutMessageList   &outList,
                                               bool              broadcastWritesOnMacroSubroutes )
{
    YukonError_t nRet = ClientErrors::None;

    string resultString;
    long      routeID;

    while( !tmpOutList.empty() )
    {
        OUTMESS *pOut = tmpOutList.front(); tmpOutList.pop_front();

        pOut->Request.RouteID =
                pReq->RouteId()
                    ? pReq->RouteId()
                    : getRouteID();

        EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

        //  if they said to broadcast it and it's a write, tag it for macro route broadcast
        if( broadcastWritesOnMacroSubroutes
            && (pOut->Buffer.BSt.IO == EmetconProtocol::IO_Function_Write ||
                pOut->Buffer.BSt.IO == EmetconProtocol::IO_Write) )
        {
            pOut->MessageFlags |= MessageFlag_BroadcastOnMacroSubroutes;
        }

        if( CtiRouteSPtr Route = getRoute(pOut->Request.RouteID) )
        {
            pOut->TargetID  = getID();

            //  all B word DLC commands return a "result" - even if it's just notification that a one-way command was submitted (such as a control, write, etc)
            if( parse.getiValue("type") != ProtocolExpresscomType )
            {
                pOut->EventCode = BWORD | WAIT | RESULT;
            }

            if( pOut->Sequence == EmetconProtocol::PutConfig_TSync )
            {
                pOut->EventCode |= TSYNC;
                pOut->EventCode |= DTRAN;  //  Send timesyncs nonqueued no matter what, even if "noqueue" wasn't specified for the command
            }

            static const string str_noqueue = "noqueue";
            if( parse.isKeyValid(str_noqueue) )
            {
                pOut->EventCode |= DTRAN;
                //  pOut->EventCode &= ~QUEUED;
            }

            pOut->Buffer.BSt.Address      = getAddress();            // The DLC address of the device

            //  store the request info for later use
            pOut->Request.ProtocolInfo.Emetcon.Function = pOut->Buffer.BSt.Function;
            pOut->Request.ProtocolInfo.Emetcon.IO       = pOut->Buffer.BSt.IO;

            /*
             * OK, these are the items we are about to set out to perform..  Any additional signals will
             * be added into the list upon completion of the Execute!
             */
            if(parse.getActionItems().size())
            {
                for(std::list< string >::const_iterator itr = parse.getActionItems().begin();
                     itr != parse.getActionItems().end();
                     ++itr )
                {
                    vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), getDescription(parse), *itr, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                }
            }

            //  Only one ARM flag should be used at once
            if( pOut->Buffer.BSt.IO & (Q_ARML | Q_ARMC | Q_ARMS) )
            {
                string arm_name;
                int arm;

                switch( pOut->Buffer.BSt.IO & (Q_ARML | Q_ARMC | Q_ARMS) )
                {
                    case Q_ARML:    arm = Q_ARML;   arm_name = "arml";  break;
                    case Q_ARMC:    arm = Q_ARMC;   arm_name = "armc";  break;
                    case Q_ARMS:    arm = Q_ARMS;   arm_name = "arms";  break;
                    default:
                    {
                        CTILOG_ERROR(dout, "multiple ARM flags set in command \""<< pOut->Request.CommandStr <<"\" sent to device \""<< getName() <<"\"");
                    }
                    //  if multiple ARM flags are used, none will be sent
                    arm = 0;
                }

                if( arm && !parse.isKeyValid(arm_name) )
                {
                    //  for safety, I'll just unset them all at once
                    pOut->Buffer.BSt.IO &= ~(Q_ARML | Q_ARMC | Q_ARMS);

                    CtiRequestMsg *arm_req = CTIDBG_new CtiRequestMsg(*pReq);

                    if( arm_req )
                    {
                        string arm_command = "putconfig emetcon " + arm_name;

                        arm_req->setCommandString(arm_command.c_str());

                        if( parse.isKeyValid("noqueue") )
                        {
                            arm_req->setCommandString(arm_req->CommandString() + " noqueue");
                        }

                        arm_req->setMessagePriority(pReq->getMessagePriority());

                        CtiCommandParser arm_parse(arm_req->CommandString());

                        //  we must trap any return messages to the client that this creates...
                        CtiMessageList tmp_retlist;

                        if( beginExecuteRequestFromTemplate(arm_req, arm_parse, vgList, tmp_retlist, outList, pOut) )
                        {
                            CTILOG_ERROR(dout, "Could not send ARM to device \""<< getName() <<"\"");
                        }

                        //  ... and erase them
                        delete_container(tmp_retlist);

                        delete arm_req;
                    }
                }
            }

            /*
             *  Form up the reply here since the ExecuteRequest funciton will consume the
             *  OutMessage.
             */

            // Start the control request on its route(s)
            if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
            {
                retList.push_back(
                        new CtiReturnMsg(
                                getID(),
                                pOut->Request,
                                getName() + ": ERROR " + CtiNumStr(nRet) + " (" + GetErrorString(nRet) + ") performing command on route " + Route->getName(),
                                nRet));
            }
        }
        else if( getRouteManager() == 0 )       // If there is no route manager, we need porter to do the route work!
        {
            // Tell the porter side to complete the assembly of the message.
            pOut->Request.BuildIt = TRUE;
            strncpy(pOut->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

            outList.push_back( pOut );       // May porter have mercy.
            pOut = 0;
        }
        else
        {
            retList.push_back(
                    new CtiReturnMsg(
                            getID(),
                            pOut->Request,
                            getName() + ": ERROR: Route or Route Transmitter not available for device ",
                            ClientErrors::BadRoute));
        }

        if( pOut )
        {
            delete pOut;
        }
    }

    const bool outMessagesGenerated = getGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());

    for( CtiMessageList::iterator itr = retList.begin(); itr != retList.end(); )
    {
        CtiReturnMsg *retMsg = static_cast<CtiReturnMsg *>(*itr);

        // Set expectMore on all CtiReturnMsgs but the last, unless there was a command sent, in which case set expectMore on all of them.
        if( ++itr != retList.end() || outMessagesGenerated )
        {
            retMsg->setExpectMore(true);
        }
    }

    return nRet;
}



bool DlcBaseDevice::processAdditionalRoutes( const INMESS &InMessage, int nRet ) const
{
    if( ! InMessage.Return.RetryMacroOffset )
    {
        return false;
    }

    if( nRet == ClientErrors::InvalidSSPEC ||
        nRet == ClientErrors::InvalidTimestamp )
    {
        //  we cannot recover from these errors, even if we attempt on additional subroutes
        return false;
    }

    if( CtiRouteSPtr Route = getRoute(InMessage.Return.RouteID) )
    {
        return Route->processAdditionalRoutes(InMessage);
    }

    // Presume the existence of MacroOffset != 0 indicates a GO status!
    return true;
}


inline MacroOffset DlcBaseDevice::selectInitialMacroRouteOffset(LONG routeid) const
{
    CtiRouteSPtr Route;

    if(routeid > 0 && (Route = CtiDeviceBase::getRoute( routeid )) )    // This is "this's" route
    {
        if(Route->getType() == RouteTypeMacro)
        {
            return MacroOffset(0); // Pick the first sub-route in the macro
        }
    }

    return MacroOffset::none;
}


unsigned int DlcBaseDevice::getLPRetryRate( unsigned int interval )
{
    unsigned int retVal;

    //  check if it's been initialized
    if( _lpRetryMultiplier == 0 )
    {
        _lpRetryMultiplier = gConfigParms.getValueAsInt("DLC_LP_RETRY_MULTIPLIER", DefaultLPRetryMultiplier);

        if( _lpRetryMultiplier < DefaultLPRetryMultiplier )
        {
            _lpRetryMultiplier = DefaultLPRetryMultiplier;
        }
    }

    //  check if it's been initialized
    if( _lpRetryMinimum == 0 )
    {
        _lpRetryMinimum = gConfigParms.getValueAsInt("DLC_LP_RETRY_MINIMUM", DefaultLPRetryMinimum);

        if( _lpRetryMinimum < DefaultLPRetryMinimum )
        {
            _lpRetryMinimum = DefaultLPRetryMinimum;
        }
    }

    //  check if it's been initialized
    if( _lpRetryMaximum == 0 )
    {
        _lpRetryMaximum = gConfigParms.getValueAsInt("DLC_LP_RETRY_MAXIMUM", DefaultLPRetryMaximum);

        if( _lpRetryMaximum > DefaultLPRetryMaximum )
        {
            _lpRetryMaximum = DefaultLPRetryMaximum;
        }
    }

    if( _lpRetryMinimum > _lpRetryMaximum )
    {
        unsigned int tmp;
        tmp             = _lpRetryMinimum;
        _lpRetryMinimum = _lpRetryMaximum;
        _lpRetryMaximum = tmp;
    }

    retVal = interval * _lpRetryMultiplier;

    if( retVal > _lpRetryMaximum )
    {
        retVal = _lpRetryMaximum;
    }
    else if( retVal < _lpRetryMinimum )
    {
        retVal = _lpRetryMinimum;
    }

    return retVal;
}

}
}

