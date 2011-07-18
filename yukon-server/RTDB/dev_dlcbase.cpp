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

#include <boost/optional.hpp>

using std::string;
using std::endl;
using std::list;

using Cti::Protocols::EmetconProtocol;

using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

unsigned int DlcBaseDevice::_lpRetryMultiplier = 0;
unsigned int DlcBaseDevice::_lpRetryMinimum    = 0;
unsigned int DlcBaseDevice::_lpRetryMaximum    = 0;

DlcBaseDevice::DlcBaseDevice() :
    _activeIndex(EmetconProtocol::DLCCmd_LAST)
{}

DlcBaseDevice::DlcBaseDevice(const DlcBaseDevice& aRef)
{
    *this = aRef;
}

DlcBaseDevice::~DlcBaseDevice() {}

DlcBaseDevice& DlcBaseDevice::operator=(const DlcBaseDevice& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        CtiLockGuard<CtiMutex> guard(_classMutex);

        DeviceRoutes = aRef.DeviceRoutes;
    }
    return *this;
}

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

    Inherited::DecodeDatabaseReader(rdr);       //  get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    CtiLockGuard<CtiMutex> guard(_classMutex);
    CarrierSettings.DecodeDatabaseReader(rdr);

    if( oldAddress && *oldAddress != CarrierSettings.getAddress() )
    {
        purgeDynamicPaoInfo();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Device address for \"" << getName() << "\" (paoid " << getID() << ") has been updated"
                              << " from \"" << *oldAddress << "\""
                              << " to \"" << CarrierSettings.getAddress() << "\"."
                              << " Purging dynamicPaoInfo from memory and database." << endl;
        }
    }

    DeviceRoutes.DecodeDatabaseReader(rdr);
}

LONG DlcBaseDevice::getAddress() const   {   return CarrierSettings.getAddress();    }
LONG DlcBaseDevice::getRouteID() const   {   return DeviceRoutes.getRouteID();       }   //  From CtiTableDeviceRoute


INT DlcBaseDevice::ExecuteRequest( CtiRequestMsg        *pReq,
                                   CtiCommandParser     &parse,
                                   OUTMESS             *&OutMessage,
                                   list< CtiMessage* >  &vgList,
                                   list< CtiMessage* >  &retList,
                                   list< OUTMESS* >     &outList )
{
    int nRet = NoError;
    bool broadcast = false;
    list< OUTMESS* > tmpOutList;

    if( OutMessage )
    {
        EstablishOutMessagePriority( OutMessage, MAXPRIORITY - 4 );
    }

    try
    {
        switch( parse.getCommand( ) )
        {
            case LoopbackRequest:
            {
                nRet = executeLoopback( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                break;
            }
            case ScanRequest:
            {
                nRet = executeScan( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                break;
            }
            case GetValueRequest:
            {
                nRet = executeGetValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                break;
            }
            case PutValueRequest:
            {
                nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                broadcast = true;
                break;
            }
            case ControlRequest:
            {
                nRet = executeControl( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                broadcast = true;
                break;
            }
            case GetStatusRequest:
            {
                nRet = executeGetStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                break;
            }
            case PutStatusRequest:
            {
                nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                broadcast = true;
                break;
            }
            case GetConfigRequest:
            {
                nRet = executeGetConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                break;
            }
            case PutConfigRequest:
            {
                nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
                broadcast = true;
                break;
            }
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
                }
                nRet = NoMethod;

                break;
            }
        }
    }
    catch( BaseCommand::CommandException &e )
    {
        returnErrorMessage(e.error_code, OutMessage, retList, e.error_description);

        nRet = ExecutionComplete;
    }

    if( nRet != NORMAL )
    {
        string resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << CtiTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command.";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }
    else
    {
        long msgId = pReq->UserMessageId();
        long connHandle = (long)pReq->getConnectionHandle();

        if(OutMessage != NULL)
        {
            tmpOutList.push_back( OutMessage );
            OutMessage = NULL;
        }

        executeOnDLCRoute(pReq, parse, tmpOutList, vgList, retList, outList, broadcast);

        if (getGroupMessageCount(msgId, connHandle))
        {
            for (list< CtiMessage* >::iterator itr = retList.begin(); itr != retList.end(); itr++)
            {
                ((CtiReturnMsg*)*itr)->setExpectMore(true);
            }
        }
    }

    return nRet;
}


void DlcBaseDevice::returnErrorMessage( int retval, OUTMESS *&om, list< CtiMessage* > &retList, const string &error ) const
{
    retList.push_back(
        new CtiReturnMsg(
                getID(),
                om->Request,
                getName() + " / " + error,
                retval));

    delete om;
    om = NULL;
}


INT DlcBaseDevice::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList)
try
{
    int status = decodeCommand(*InMessage, TimeNow, vgList, retList, outList);

    if( status == NoResultDecodeMethod )
    {
        status = Inherited::ResultDecode(InMessage, TimeNow, vgList, retList, outList);
    }

    return status;
}
catch( BaseCommand::CommandException &e )
{
    retList.push_back(
        new CtiReturnMsg(
            getID(),
            InMessage->Return,
            getName() + " / " + e.error_description,
            e.error_code));

    return ExecutionComplete;
}


INT DlcBaseDevice::SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList)
try
{
    int status = decodeCommand(InMessage, TimeNow, vgList, retList, outList);

    if( status == NoResultDecodeMethod )
    {
        status = Inherited::SubmitRetry(InMessage, TimeNow, vgList, retList, outList);
    }

    return status;
}
catch( BaseCommand::CommandException &e )
{
    retList.push_back(
        new CtiReturnMsg(
            getID(),
            InMessage.Return,
            getName() + " / " + e.error_description,
            e.error_code));

    return ExecutionComplete;
}


INT DlcBaseDevice::retMsgHandler( string commandStr, int status, CtiReturnMsg *retMsg, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, bool expectMore ) const
{
    CtiReturnMsg    *tmpVGRetMsg = NULL;
    CtiPointDataMsg *tmpMsg;
    int retVal = 0;
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

            retVal = TRUE;
        }
        else
        {
            delete retMsg;
        }
    }

    return retVal;
}



INT DlcBaseDevice::decodeCheckErrorReturn(INMESS *InMessage, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    CtiReturnMsg    *retMsg;

    CtiCommandMsg   *pMsg;


    if(!ErrReturn)
    {
        //  verify we heard back from the correct device (only if we heard it)
        //
        //    Note:  The returned address from the device is only the lower 13 bits,
        //           which means we would not have to mask of the Dst address, but for some
        //           reason when the reading is queued into the CCU711 we get the whole address
        //           returned.  So by comparing only 13 bit for both it will not break in
        //           either case.

        if( InMessage->Buffer.DSt.Length && //  make sure it's not just an ACK
            getAddress() != MctDevice::TestAddress1 &&  //  also, make sure we're not sending to an FCT-jumpered MCT,
            getAddress() != MctDevice::TestAddress2 &&  //    since it'll return its native address and not the test address
            (getAddress() & 0x1fff) != (InMessage->Buffer.DSt.Address & 0x1fff) )
        {
            //  Address did not match, so it's a comm error
            ErrReturn = WRONGADDRESS;
            InMessage->EventCode = WRONGADDRESS;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - Wrong DLC Address: \"" << getName() << "\" ";
                dout << "(" << getAddress() << ") != (" << InMessage->Buffer.DSt.Address << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    //  ACH: update performace stats for device and route... ?

    //  check for communication failure
    if(ErrReturn)
    {
        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage->Return.CommandStr),
                                         string(),
                                         ErrReturn,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.GrpMsgID,
                                         InMessage->Return.UserID);

        if( retMsg != NULL )
        {
            //  send a Device Failed/Point Failed message to dispatch, if applicable
            pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

            if( pMsg != NULL )
            {
                switch( InMessage->Sequence )
                {
                    case EmetconProtocol::Scan_General:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(CtiCommandMsg::OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateGeneral);  //  defined in yukon.h
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case EmetconProtocol::Scan_Accum:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(CtiCommandMsg::OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateAccum);
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case EmetconProtocol::Scan_Integrity:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(CtiCommandMsg::OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateIntegrity);
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    default:
                    {
                        delete pMsg;
                        pMsg = NULL;

                        break;
                    }
                }

                if( pMsg != NULL )
                {
                    retMsg->insert(pMsg);
                }
            }

            char error_str[80];

            GetErrorString(ErrReturn, error_str);

            string resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

            retMsg->setResultString(resultString);

            retList.push_back(retMsg);
        }

        //  Find the next route and resubmit request to porter
        //    ACH:  if no more routes exist, plug the value points... ?

        if( InMessage->Return.MacroOffset > 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " MacroOffset specified, generating a request for the next macro. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            OUTMESS *NewOutMessage = CTIDBG_new OUTMESS;

            if(NewOutMessage)
            {
                InEchoToOut(*InMessage, NewOutMessage);
                NewOutMessage->Port = InMessage->Port;
                NewOutMessage->DeviceID = InMessage->DeviceID;
                NewOutMessage->TargetID = InMessage->TargetID;
                NewOutMessage->Request.BuildIt = TRUE;

                outList.push_back( NewOutMessage );
            }
        }
    }
    else
    {
        //  ACH:  Log the communication success on this route... ?
    }

    return ErrReturn;
}


int DlcBaseDevice::decodeCommand(const INMESS &InMessage, CtiTime TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    //  We need to protect _activeCommands/trackCommand()
    CtiLockGuard<CtiMutex> lock(getMux());

    active_command_map::iterator command_itr = _activeCommands.find(InMessage.Sequence);

    if( command_itr == _activeCommands.end() )
    {
        return NoError;  //  in order to silently absorb macro route returns that have already been acted upon
    }

    if( ! command_itr->second )
    {
        _activeCommands.erase(command_itr);

        return NoResultDecodeMethod;  //  however, we will squawk about a null command pointer
    }

    DlcCommandSPtr  command_ptr =  command_itr->second;
    DlcCommand     &command     = *command_ptr;

    CtiReturnMsg *ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return);

    try
    {
        DlcCommand::request_ptr ptr;
        string description;

        if( InMessage.EventCode )
        {
            ptr = command.error(TimeNow, InMessage.EventCode, description);
        }
        else
        {
            const DSTRUCT &dst = InMessage.Buffer.DSt;

            const DlcCommand::payload_t
                payload(
                    dst.Message,
                    dst.Message + std::min<unsigned short>(dst.Length, DSTRUCT::MessageLength_Max));

            std::vector<DlcCommand::point_data> points;

            unsigned function = InMessage.Return.ProtocolInfo.Emetcon.Function;

            if( InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Read ||
                InMessage.Return.ProtocolInfo.Emetcon.IO == EmetconProtocol::IO_Function_Write )
            {
                function += 0x100;
            }

            ptr = command.decode(TimeNow, function, payload, description, points);

            for each( const DlcCommand::point_data &pdata in points )
            {
                point_info pi;

                pi.description  = pdata.description;
                pi.quality      = pdata.quality;
                pi.value        = pdata.value;
                pi.freeze_bit   = false;

                insertPointDataReport(pdata.type, pdata.offset, ReturnMsg, pi, pdata.name, pdata.time);
            }
        }

        ReturnMsg->setResultString(description);

        if( ptr.get() )
        {
            ReturnMsg->setExpectMore(true);
        }

        retMsgHandler(InMessage.Return.CommandStr, NoError, ReturnMsg, vgList, retList);

        if( ptr.get() )
        {
            OUTMESS *OutMessage = new OUTMESS;

            InEchoToOut(InMessage, OutMessage);

            //  If there were no errors, start the command on the first macro route
            if( ! InMessage.EventCode )
            {
                OutMessage->Request.MacroOffset = selectInitialMacroRouteOffset(OutMessage->Request.RouteID);
            }

            fillOutMessage(*OutMessage, *ptr);

            //  ExecuteRequest already has the CtiDeviceBase::_classMutex at this point, so it's safe to call trackCommand()
            OutMessage->Sequence = trackCommand(command_ptr);

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
                              list<OUTMESS *>(1, OutMessage),
                              vgList, retList, outList, false);
        }

        _activeCommands.erase(command_itr);

        return NORMAL;
    }
    catch( BaseCommand::CommandException &e )
    {
        ReturnMsg->setStatus(e.error_code);
        ReturnMsg->setResultString(getName() + " / " + e.error_description);

        retList.push_back(ReturnMsg);

        //  broken!
        _activeCommands.erase(command_itr);

        return ExecutionComplete;
    }
}


long DlcBaseDevice::trackCommand(const DlcCommandSPtr &command)
{
    if( _activeIndex < EmetconProtocol::DLCCmd_LAST )
    {
        _activeIndex = EmetconProtocol::DLCCmd_LAST;
    }

    while( ! _activeCommands.insert(std::make_pair(_activeIndex, command)).second )
    {
        _activeIndex++;
    }

    return _activeIndex++;
}


void DlcBaseDevice::fillOutMessage(OUTMESS &OutMessage, DlcCommand::request_t &request)
{
    OutMessage.DeviceID  = getID();
    OutMessage.TargetID  = getID();
    OutMessage.Port      = getPortID();
    OutMessage.Remote    = getAddress();
    OutMessage.TimeOut   = 2;
    OutMessage.Retry     = 2;

    OutMessage.Request.RouteID     = getRouteID();

    if( ! OutMessage.Request.MacroOffset )
    {
        OutMessage.Request.MacroOffset = selectInitialMacroRouteOffset(OutMessage.Request.RouteID);
    }

    OutMessage.Buffer.BSt.Function = request.function;
    OutMessage.Buffer.BSt.IO       = request.io();
    OutMessage.Buffer.BSt.Length   = request.length();

    DlcCommand::payload_t payload = request.payload();

    std::copy(payload.begin(),
              payload.begin() + std::min<unsigned>(payload.size(), BSTRUCT::MessageLength_Max),
              OutMessage.Buffer.BSt.Message);
}


bool DlcBaseDevice::tryExecuteCommand(OUTMESS &OutMessage, DlcCommandSPtr command)
{
    DlcCommand::request_ptr request = command->execute(CtiTime());

    if( request.get() )
    {
        fillOutMessage(OutMessage, *request);

        //  ExecuteRequest already has the CtiDeviceBase::_classMutex at this point, so it's safe to call trackCommand()
        OutMessage.Sequence = trackCommand(command);
    }

    return request.get();
}


int DlcBaseDevice::executeOnDLCRoute( CtiRequestMsg              *pReq,
                                         CtiCommandParser           &parse,
                                         list< OUTMESS* >     &tmpOutList,
                                         list< CtiMessage* >  &vgList,
                                         list< CtiMessage* >  &retList,
                                         list< OUTMESS* >     &outList,
                                         bool                  broadcastWritesOnMacroSubroutes )
{
    int nRet = NoError;

    CtiRouteSPtr Route;

    string resultString;
    long      routeID;

    CtiReturnMsg* pRet = 0;

    while( !tmpOutList.empty() )
    {
        OUTMESS *pOut = tmpOutList.front(); tmpOutList.pop_front();

        if( pReq->RouteId() )
        {
            pOut->Request.RouteID = pReq->RouteId();
        }
        else
        {
            pOut->Request.RouteID = getRouteID();
        }

        EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

        //  if they said to broadcast it and it's a write, tag it for macro route broadcast
        if( broadcastWritesOnMacroSubroutes
            && (pOut->Buffer.BSt.IO == EmetconProtocol::IO_Function_Write ||
                pOut->Buffer.BSt.IO == EmetconProtocol::IO_Write) )
        {
            pOut->MessageFlags |= MessageFlag_BroadcastOnMacroSubroutes;
        }

        if( (Route = CtiDeviceBase::getRoute( pOut->Request.RouteID )) )
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint - multiple ARM flags set in command \"" << pOut->Request.CommandStr << "\" sent to device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        list<CtiMessage *> tmp_retlist;

                        if( beginExecuteRequestFromTemplate(arm_req, arm_parse, vgList, tmp_retlist, outList, pOut) )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - error sending ARM to device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            pRet = CTIDBG_new CtiReturnMsg(getID(), string(pOut->Request.CommandStr), Route->getName(), nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.GrpMsgID, pOut->Request.UserID, pOut->Request.SOE, CtiMultiMsg_vec());
            // Start the control request on its route(s)
            if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
            {
                resultString = getName() + ": ERROR " + CtiNumStr(nRet) + " (" + FormatError(nRet) + ") performing command on route " + Route->getName().data();
                pRet->setResultString(resultString);
                pRet->setStatus( nRet );
            }
            else
            {
                delete pRet;
                pRet = 0;
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
            nRet = BADROUTE;

            resultString = getName() + ": ERROR: Route or Route Transmitter not available for device ";

            pRet = CTIDBG_new CtiReturnMsg(getID(),
                                           string(pOut->Request.CommandStr),
                                           resultString,
                                           nRet,
                                           pOut->Request.RouteID,
                                           pOut->Request.MacroOffset,
                                           pOut->Request.Attempt,
                                           pOut->Request.GrpMsgID,
                                           pOut->Request.UserID,
                                           pOut->Request.SOE,
                                           CtiMultiMsg_vec());
        }

        if(pRet)
        {
            retList.push_back( pRet );
        }

        if( pOut )
        {
            delete pOut;
        }
    }

    return nRet;
}



bool DlcBaseDevice::processAdditionalRoutes( INMESS *InMessage ) const
{
    bool bret = false;

    if(InMessage->Return.MacroOffset != 0)
    {
        CtiRouteSPtr Route;

        if( (Route = CtiDeviceBase::getRoute( InMessage->Return.RouteID )) )    // This is "this's" route
        {
            bret = Route->processAdditionalRoutes(InMessage);
        }
        else
        {
            bret = true;        // Presume the existence of MacroOffset != 0 indicates a GO status!
        }
    }
    return bret;
}


inline ULONG DlcBaseDevice::selectInitialMacroRouteOffset(LONG routeid) const
{
    ULONG offset = 0;

    CtiRouteSPtr Route;

    if(routeid > 0 && (Route = CtiDeviceBase::getRoute( routeid )) )    // This is "this's" route
    {
        if(Route->getType() == RouteTypeMacro)
        {
            offset = 1;
        }
    }
    else
    {
        offset = 0;
    }

    return offset;
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

