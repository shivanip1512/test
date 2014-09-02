#include "precompiled.h"

#include "dev_ccu721.h"
#include "dev_mct4xx.h"
#include "prot_emetcon.h"
#include "numstr.h"
#include "porter.h"
#include "mgr_route.h"

#include <boost/bind.hpp>

using namespace std;

using Cti::Protocols::KlondikeProtocol;
using namespace Cti::Protocols;

namespace Cti {
namespace Devices {

Ccu721Device::Ccu721Device() :
    _routes_loaded(false),
    _current_om(0)
{
}


Ccu721Device::~Ccu721Device()
{
    _current_om = 0;  //  owned by Porter, not us
}


Protocol::Interface *Ccu721Device::getProtocol()
{
    return (Protocol::Interface *)&_klondike;
}


INT Ccu721Device::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    INT nRet = NoMethod;

    switch( parse.getCommand() )
    {
        case ScanRequest:
        case LoopbackRequest:
        {
            OutMessage->Sequence = KlondikeProtocol::Command_Loopback;

            nRet = NoError;

            break;
        }
        case GetConfigRequest:
        {
            if( parse.isKeyValid("time") )
            {
                OutMessage->Sequence = KlondikeProtocol::Command_TimeRead;

                nRet = NoError;
            }
        }
        case PutConfigRequest:
        {
            if( parse.isKeyValid("raw") )
            {
                OutMessage->Sequence = KlondikeProtocol::Command_Raw;
                OutMessage->OutLength = parse.getsValue("raw").length();
                memcpy(OutMessage->Buffer.OutMessage, parse.getsValue("raw").data(), OutMessage->OutLength);

                nRet = NoError;
            }
            else if( parse.isKeyValid("timesync") )
            {
                OutMessage->Sequence = KlondikeProtocol::Command_TimeSync;

                nRet = NoError;
            }

            break;
        }
    }

    if( !nRet )
    {
        OutMessage->Port      = getPortID();
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Remote    = getAddress();
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Retry     = 0;  //  the retries will be handled internally by the protocol if they are necessary;

        EstablishOutMessagePriority( OutMessage, pReq->getMessagePriority() );

        outList.push_back(OutMessage);

        OutMessage = NULL;
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


INT Ccu721Device::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority )
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString(newParse.getCommandStr());

    status = ExecuteRequest(pReq,newParse,OutMessage,vgList,retList,outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT Ccu721Device::ResultDecode( const INMESS &InMessage, const CtiTime Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    if( ! InMessage.ErrorCode )
    {
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(InMessage.Return.CommandStr),
                                                  string((const char *)(InMessage.Buffer.InMessage + InMessage_StringOffset)),
                                                  InMessage.ErrorCode,
                                                  InMessage.Return.RouteID,
                                                  InMessage.Return.RetryMacroOffset,
                                                  InMessage.Return.Attempt,
                                                  InMessage.Return.GrpMsgID,
                                                  InMessage.Return.UserID));

        resetScanFlag();
    }

    return 0;
}


bool Ccu721Device::needsReset() const
{
    return !_routes_loaded;
}

string Ccu721Device::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate, AD.masteraddress, AD.slaveaddress, AD.postcommwait "
                                   "FROM Device DV, DeviceAddress AD, DeviceDirectCommSettings CS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.type = 'CCU-721' AND YP.paobjectid = AD.deviceid AND YP.paobjectid = "
                                     "DV.deviceid AND YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void Ccu721Device::DecodeDatabaseReader(RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _klondike.setAddresses(_address.getSlaveAddress(), _address.getMasterAddress());
    _klondike.setName(getName().data());
}


LONG Ccu721Device::getAddress() const
{
    return _address.getSlaveAddress();
}


bool Ccu721Device::hasQueuedWork()  const {  return ! _queued_outmessages.empty();  }
bool Ccu721Device::hasWaitingWork() const {  return _klondike.hasWaitingWork();   }
bool Ccu721Device::hasRemoteWork()  const {  return _klondike.hasRemoteWork();    }

unsigned Ccu721Device::queuedWorkCount() const {  return _queued_outmessages.size();  }


string Ccu721Device::queueReport() const
{
    ostringstream report;

    report.fill(' ');

    std::vector<KlondikeProtocol::request_status> waiting, pending, queued, completed;

    _klondike.getRequestStatus(waiting, pending, queued, completed);

    report << "Waiting requests (INUSE) : " << setw(5) << waiting.size() << " : in Yukon's queue" << endl;

    report << setw(8) << "MCT ID" << "|"
           << setw(3) << "Pri"    << "|"
                      << "Command" << endl;

    for each(KlondikeProtocol::request_status request in waiting)
    {
        request_handles::const_iterator itr = _queued_outmessages.find(static_cast<OUTMESS *>(request.requester));

        if( itr != _queued_outmessages.end() )
        {
            OUTMESS *om = *itr;

            report << setw(8) << om->TargetID << "|"
                   << setw(3) << om->Priority << "|"
                              << om->Request.CommandStr << endl;
        }
        else if( request.requester == this )
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            report << setw(8) << "(none)"         << "|"
                   << setw(3) << request.priority << "|"
                              << "(MCT-400 broadcast timesync)" << endl;
        }
        else if( request.requester )
        {
            report << setw(8) << request.requester << "|"
                   << setw(3) << request.priority       << "|"
                              << "(unknown request handle)" << endl;
        }
    }

    report << "Pending requests (INUSE) : " << setw(5) << pending.size() << " : waiting for ACK from CCU" << endl;

    report << setw(8) << "Queue ID" << "|"
           << setw(8) << "MCT ID"   << "|"
           << setw(3) << "Pri"      << "|"
                      << "Command" << endl;

    for each(KlondikeProtocol::request_status request in pending)
    {
        request_handles::const_iterator itr = _queued_outmessages.find(static_cast<OUTMESS *>(request.requester));

        if( itr != _queued_outmessages.end() )
        {
            OUTMESS *om = *itr;

            report << setw(8) << request.queue_id << "|"
                   << setw(8) << om->TargetID << "|"
                   << setw(3) << om->Priority << "|"
                              << om->Request.CommandStr << endl;
        }
        else if( request.requester == this )
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            report << setw(8) << request.queue_id << "|"
                   << setw(8) << "(none)"         << "|"
                   << setw(3) << request.priority << "|"
                              << "(MCT-400 broadcast timesync)" << endl;
        }
        else if( request.requester )
        {
            report << setw(8) << request.queue_id << "|"
                   << setw(8) << request.requester << "|"
                   << setw(3) << request.priority       << "|"
                              << "(unknown request handle)" << endl;
        }
    }

    report << "Remote requests (INCCU)  : " << setw(5) << queued.size() << " : in the CCU's queue" << endl;

    report << setw(8) << "Queue ID" << "|"
           << setw(8) << "MCT ID"   << "|"
           << setw(3) << "Pri"      << "|"
                      << "Command" << endl;

    for each(KlondikeProtocol::request_status request in queued)
    {
        request_handles::const_iterator itr = _queued_outmessages.find(static_cast<OUTMESS *>(request.requester));

        if( itr != _queued_outmessages.end() )
        {
            OUTMESS *om = *itr;

            report << setw(8) << request.queue_id << "|"
                   << setw(8) << om->TargetID << "|"
                   << setw(3) << om->Priority << "|"
                              << om->Request.CommandStr << endl;
        }
        else if( request.requester == this )
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            report << setw(8) << request.queue_id << "|"
                   << setw(8) << "(none)"         << "|"
                   << setw(3) << request.priority << "|"
                              << "(MCT-400 broadcast timesync)" << endl;
        }
        else if( request.requester )
        {
            report << setw(8) << request.queue_id << "|"
                   << setw(8) << request.requester << "|"
                   << setw(3) << request.priority       << "|"
                              << "(unknown request handle)" << endl;
        }
    }

    report << "Completed requests       : " << setw(5) << completed.size() << " : in the CCU's queue" << endl;

    report << setw(8) << "MCT ID"   << "|"
                      << "Command" << endl;

    for each(KlondikeProtocol::request_status request in completed)
    {
        request_handles::const_iterator itr = _queued_outmessages.find(static_cast<OUTMESS *>(request.requester));

        if( itr != _queued_outmessages.end() )
        {
            OUTMESS *om = *itr;

            report << setw(8) << om->TargetID << "|"
                              << om->Request.CommandStr << endl;
        }
        else if( request.requester == this )
        {
            //  so far, we only send MCT timesyncs to the CCU without an OutMessage
            report << setw(8) << "(none)" << "|"
                              << "(MCT-400 broadcast timesync)" << endl;
        }
        else if( request.requester )
        {
            report << setw(8) << request.requester << "|"
                              << "(unknown request handle)" << endl;
        }
    }


    return report.str();
}


unsigned long Ccu721Device::getRequestCount(ULONG requestID) const
{
    return std::count_if(_queued_outmessages.begin(),
                         _queued_outmessages.end(),
                         boost::bind(findRequestIDMatch, reinterpret_cast<void *>(requestID), _1));
}


void Ccu721Device::retrieveQueueEntries(bool (*myFindFunc)(void*, void*), void *findParameter, std::list<void *> &entries)
{
    request_handles::iterator itr = _queued_outmessages.begin();

    while( itr != _queued_outmessages.end() )
    {
        if( *itr && myFindFunc(findParameter, *itr) )
        {
            entries.push_back(*itr);

            _klondike.removeQueuedWork(*itr);

            itr = _queued_outmessages.erase(itr);
        }
        else
        {
            ++itr;
        }
    }
}


DeviceQueueInterface *Ccu721Device::getDeviceQueueHandler()
{
    return this;
}


YukonError_t Ccu721Device::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    YukonError_t retval = NORMAL;

    // If they are the same, it is a message to the CCU and should not be queued
    // Instead of checking this, should all messages to the CCU be marked DTRAN?
    if( OutMessage->TargetID != OutMessage->DeviceID &&
        !(OutMessage->EventCode & DTRAN) )
    {
        if( !_queued_outmessages.insert(OutMessage).second )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - " << __FUNCTION__ << " - unable to insert duplicate OM pointer (" << OutMessage << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            return MEMORY;
        }

        vector<unsigned char> queued_message;

        writeDLCMessage(queued_message, OutMessage);

        _klondike.addQueuedWork(static_cast<void *>(OutMessage),  //  just a handle to the OM
                                queued_message,
                                OutMessage->Priority,
                                KlondikeProtocol::DLCParms_None | OutMessage->Buffer.BSt.DlcRoute.Bus + 1,
                                OutMessage->Buffer.BSt.DlcRoute.Stages);

        OutMessage = 0;  //  the OutMessage is ours now

        *dqcnt = _queued_outmessages.size();

        retval = QUEUED_TO_DEVICE;
    }

    return retval;
}


//  called by KickerThread() via applyKick() and by LoadRemoteRoutes()
bool Ccu721Device::buildCommand(CtiOutMessage *&OutMessage, Commands command)
{
    if( ! OutMessage )
    {
        return false;
    }

    bool command_built = false;
    CtiTime now;

    switch( command )
    {
        case Command_LoadRoutes:
        {
            _routes_loaded = true;

            OutMessage->Sequence = KlondikeProtocol::Command_LoadRoutes;

            CtiRouteManager::spiterator itr;

            CtiRouteManager::coll_type::reader_lock_guard_t guard(_routeMgr->getLock());

            if( !_routeMgr->empty() )
            {
                _klondike.clearRoutes();
            }

            for( itr = _routeMgr->begin(); itr != _routeMgr->end(); CtiRouteManager::nextPos(itr) )
            {
                CtiRouteSPtr &ccu_route = itr->second;

                //  routes for which this device is the transmitter
                if( ccu_route && ccu_route->getCommRoute().getTrxDeviceID() == getID() )
                {
                    //  if( ccu_route->getType() == RouteTypeCCU )  ?

                    _klondike.addRoute(ccu_route->getBus(),
                                       ccu_route->getCCUFixBits(),
                                       ccu_route->getCCUVarBits(),
                                       ccu_route->getStages());
                }
            }

            command_built = true;

            break;
        }
        case Command_ReadQueue:
        {
            if( _klondike.hasRemoteWork() && !_klondike.isReadingDeviceQueue() )
            {
                _klondike.setReadingDeviceQueue(true);

                //  read at high priority to ensure we get out ahead of any load queue commands
                OutMessage->Priority = MAXPRIORITY - 1;  //  _klondike.getRemoteWorkPriority();
                OutMessage->Sequence = KlondikeProtocol::Command_ReadQueue;

                command_built = true;
            }

            break;
        }
        case Command_LoadQueue:
        {
            if( _klondike.hasWaitingWork() && !_klondike.isLoadingDeviceQueue() )
            {
                _klondike.setLoadingDeviceQueue(true);

                OutMessage->Priority = _klondike.getWaitingWorkPriority();
                OutMessage->Sequence = KlondikeProtocol::Command_LoadQueue;

                command_built = true;
            }

            break;
        }
        case Command_Timesync:
        {
            OutMessage->Priority = MAXPRIORITY;
            OutMessage->Sequence = KlondikeProtocol::Command_TimeSync;

            command_built = true;

            break;
        }
    }

    if( command_built )
    {
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        OutMessage->Port     = getPortID();
    }

    return command_built;
}


YukonError_t Ccu721Device::recvCommRequest(OUTMESS *OutMessage)
{
    if( !OutMessage )
    {
        return MEMORY;
    }

    _current_om = OutMessage;

    if( _current_om->EventCode & DTRAN
        && (_current_om->EventCode & BWORD ||
            _current_om->EventCode & AWORD) )
    {
        byte_buffer_t dtran_message;

        writeDLCMessage(dtran_message, _current_om);

        return _klondike.setCommand(KlondikeProtocol::Command_DirectTransmission,
                                    dtran_message,
                                    //  This only works because A words don't have an InLength set.
                                    (_current_om->InLength)?(EmetconProtocol::determineDWordCount(_current_om->InLength) * DWORDLEN):(0),
                                    _current_om->Priority,
                                    _current_om->Buffer.BSt.DlcRoute.Stages,
                                    KlondikeProtocol::DLCParms_None | _current_om->Buffer.BSt.DlcRoute.Bus + 1);
    }
    else if( _current_om->Sequence == KlondikeProtocol::Command_Raw )
    {
        byte_buffer_t raw_message(OutMessage->Buffer.OutMessage, OutMessage->Buffer.OutMessage + OutMessage->OutLength);

        return _klondike.setCommand(KlondikeProtocol::Command_Raw,
                                    raw_message);
    }
    else
    {
        return _klondike.setCommand(_current_om->Sequence);
    }
}


YukonError_t Ccu721Device::sendCommResult(INMESS &InMessage)
{
    YukonError_t status = translateKlondikeError(_klondike.errorCode());

    //  if the CCU owns the InMessage - we don't end up owning the DTRAN InMessage, the MCT does...
    //    so there's no use in putting a string in there, since we won't decode it anyway
    if( _klondike.getCommand() != KlondikeProtocol::Command_DirectTransmission )
    {
        string results = _klondike.describeCurrentStatus();

        strncpy(reinterpret_cast<char *>(InMessage.Buffer.InMessage + InMessage_StringOffset),
                results.data(),
                4096 - InMessage_StringOffset);
    }


    if( _klondike.errorCondition() )
    {
        switch( _klondike.getCommand() )
        {
            case KlondikeProtocol::Command_DirectTransmission:
            {
                InMessage.Buffer.DSt.Time     = InMessage.Time;
                InMessage.Buffer.DSt.DSTFlag  = InMessage.MilliTime & DSTACTIVE;

                break;
            }
        }
    }
    else
    {
        switch( _klondike.getCommand() )
        {
            case KlondikeProtocol::Command_TimeSync:
            {
                byte_buffer_t timesync;
                writeDLCTimesync(timesync);

                _klondike.addQueuedWork(this,  //  so we can filter out the timesync requests
                                        timesync,
                                        MAXPRIORITY,
                                        KlondikeProtocol::DLCParms_BroadcastFlag,
                                        0);

                break;
            }
            case KlondikeProtocol::Command_DirectTransmission:
            {
                byte_buffer_t dtran_result = _klondike.getDTranResult();

                OutEchoToIN(_current_om, InMessage);

                InMessage.Port   = _current_om->Port;
                InMessage.Remote = _current_om->Remote;

                InMessage.Time   = CtiTime::now().seconds();

                InMessage.InLength  = dtran_result.size();

                copy(dtran_result.begin(), dtran_result.end(), InMessage.Buffer.InMessage);

                //  unlike in Command_LoadQueue/Command_ReadQueue, the InMessage.EventCode is set by
                //    the CommResult/status later on in CtiDeviceSingle::ProcessResult
                if( !status )
                {
                    status = processInbound(_current_om, InMessage);
                }

                break;
            }
            case KlondikeProtocol::Command_LoadQueue:
            case KlondikeProtocol::Command_ReadQueue:
            {
                for each( KlondikeProtocol::queue_result_t result in _klondike.getQueuedResults() )
                {
                    OUTMESS *om = static_cast<OUTMESS *>(result.requester);

                    //  Was it in our list of OMs?
                    if( _queued_outmessages.erase(om) > 0 )
                    {
                        INMESS *im = new INMESS;

                        OutEchoToIN(om, *im);

                        im->Port      = om->Port;
                        im->Remote    = om->Remote;

                        im->ErrorCode = translateKlondikeError(result.error);
                        im->Time      = result.timestamp;
                        im->InLength  = result.message.size();

                        copy(result.message.begin(), result.message.end(), im->Buffer.InMessage);

                        if( ! im->ErrorCode )
                        {
                            im->ErrorCode = processInbound(om, *im);
                        }

                        _results.push_back(make_pair(om, im));
                    }
                }

                break;
            }
        }
    }

    return status;
}


YukonError_t Ccu721Device::translateKlondikeError(KlondikeProtocol::Errors error)
{
    switch( error )
    {
        case KlondikeProtocol::Error_None:                      return NoError;

        case KlondikeProtocol::Error_BusDisabled:               return BADBUSS;
        case KlondikeProtocol::Error_InvalidBus:                return BADBUSS;
        case KlondikeProtocol::Error_InvalidDLCType:            return NOTNORMAL;
        case KlondikeProtocol::Error_InvalidMessageLength:      return BADLENGTH;
        case KlondikeProtocol::Error_InvalidSequence:           return BADSEQUENCE;
        case KlondikeProtocol::Error_NoRoutes:                  return RTNF;
        case KlondikeProtocol::Error_QueueEntryLost:            return QUEUEFLUSHED;
        case KlondikeProtocol::Error_TransmitterOverheating:    return ErrorTransmitterOverheating;

        default:
        case KlondikeProtocol::Error_Unknown:                   return NOTNORMAL;
    }
}


void Ccu721Device::getQueuedResults(std::vector<queued_result_t> &results)
{
    results.insert(results.end(),
                   _results.begin(),
                   _results.end());

    _results.clear();
}


void Ccu721Device::writeDLCMessage( byte_buffer_t &buf, const OUTMESS *om )
{
    if( !om )   return;

    unsigned char length = 0, word_pos = 0;

    switch( om->EventCode & (AWORD | BWORD) )
    {
        case AWORD:  writeAWord(buf, om->Buffer.ASt);  break;
        case BWORD:  writeBWord(buf, om->Buffer.BSt);  break;
        default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - Protocol::Klondike::writeDLCMessage() : unhandled word type (" << (om->EventCode & (AWORD | BWORD)) << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void Ccu721Device::writeDLCTimesync( byte_buffer_t &buf )
{
    BSTRUCT timesync_bst;

    timesync_bst.Address           = Mct4xxDevice::UniversalAddress;
    timesync_bst.IO                = EmetconProtocol::IO_Function_Write;
    timesync_bst.Function          = Mct4xxDevice::FuncWrite_TSyncPos;
    timesync_bst.Length            = Mct4xxDevice::FuncWrite_TSyncLen;

    timesync_bst.DlcRoute.Amp      = 0;  //  filled in by the CCU
    timesync_bst.DlcRoute.Bus      = 0;  //
    timesync_bst.DlcRoute.RepFixed = 0;  //
    timesync_bst.DlcRoute.RepVar   = 0;  //
    timesync_bst.DlcRoute.Stages   = 0;  //

    timesync_bst.Message[0]        = gMCT400SeriesSPID;
    timesync_bst.Message[1]        = 0;  //  filled in by the CCU
    timesync_bst.Message[2]        = 0;  //
    timesync_bst.Message[3]        = 0;  //
    timesync_bst.Message[4]        = 0;  //
    timesync_bst.Message[5]        = CtiTime::now().isDST();

    writeBWord(buf, timesync_bst);
}


void Ccu721Device::writeAWord( byte_buffer_t &buf, const ASTRUCT &ASt )
{
    buf.insert(buf.end(), AWORDLEN, 0);

    A_Word( &*(buf.end() - AWORDLEN), ASt);
}

void Ccu721Device::writeBWord( byte_buffer_t &buf, const BSTRUCT &BSt )
{
    int words;

    if( BSt.IO == EmetconProtocol::IO_Write ||
        BSt.IO == EmetconProtocol::IO_Function_Write )
    {
        words = (BSt.Length + 4) / 5;
    }
    else
    {
        words = EmetconProtocol::determineDWordCount(BSt.Length);
    }

    //  we insert relative to the end so that we can append to any buffer given to us
    buf.insert(buf.end(), BWORDLEN, 0);
    B_Word( &* (buf.end() - BWORDLEN), BSt, words);

    if( words && (BSt.IO == EmetconProtocol::IO_Write ||
                  BSt.IO == EmetconProtocol::IO_Function_Write) )
    {
        buf.insert(buf.end(), CWORDLEN * words, 0);

        //  I really don't know why C_Words takes a const pointer to unsigned char instead of a pointer to const unsigned char...
        BSTRUCT tmpBSt = BSt;
        C_Words( &*(buf.end() - CWORDLEN * words), tmpBSt.Message, tmpBSt.Length, 0);
    }
}


YukonError_t Ccu721Device::processInbound(const OUTMESS *om, INMESS &im)
{
    if( (om->EventCode & BWORD) &&
        (om->Buffer.BSt.IO & Protocols::EmetconProtocol::IO_Read ) )
    {
        DSTRUCT tmp_d_struct;
        ESTRUCT tmp_e_struct;

        //  inbound command - decode the D words
        const YukonError_t dword_status = decodeDWords(im.Buffer.InMessage, im.InLength, om->Remote, &tmp_d_struct, &tmp_e_struct);

        switch( dword_status )
        {
            case NORMAL:
            {
                im.Buffer.DSt = tmp_d_struct;
                im.Buffer.DSt.Time    = im.Time;
                im.Buffer.DSt.DSTFlag = im.MilliTime & DSTACTIVE;

                im.InLength = im.Buffer.DSt.Length = (im.InLength / DWORDLEN) * 5 - 2;  //  calculate the number of bytes we get back

                break;
            }
            case EWORDRCV:
            {
                im.Buffer.RepeaterError.ESt = tmp_e_struct;
                im.Buffer.RepeaterError.Details = 0;  //  no details yet, may be filled in by portfield.cpp/CommunicateDevice()
                //  fall through
            }
            default:
            {
                im.InLength = 0;

                break;
            }
        }

        return dword_status;
    }

    im.InLength = 0;

    return NoError;
}


YukonError_t Ccu721Device::decodeDWords(const unsigned char *input, const unsigned input_length, const unsigned Remote, DSTRUCT *DSt, ESTRUCT *ESt) const
{
    if( input_length % DWORDLEN )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Devices::CCU721::decodeDWords() : input_length % DWORDLEN > 0 : " << input_length << " : \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    const unsigned char *input_itr = input;
    const unsigned char *input_end = input + input_length;

    for( int i = 1; i <= 3 && (input_itr + DWORDLEN) <= input_end; i++, input_itr += DWORDLEN )
    {
        unsigned short unused;

        YukonError_t status = NoError;

        switch( i )
        {
            //  old code is stuck in its non-const ways
            case 1:  status = D1_Word (input_itr, &DSt->Message[0], &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm);  break;
            case 2:  status = D23_Word(input_itr, &DSt->Message[3], &DSt->TSync, &unused);  break;
            case 3:  status = D23_Word(input_itr, &DSt->Message[8], &unused, &unused);  break;
        }

        if( status == EWORDRCV )
        {
            return decodeEWord(input_itr, input_end - input_itr, ESt);
        }
        else if( status )
        {
            return status;
        }
    }

    return NoError;
}


YukonError_t Ccu721Device::decodeEWord(const unsigned char *input, const unsigned input_length, ESTRUCT *ESt)
{
    if( input_length < EWORDLEN )
    {
        return MEMORY;
    }

    YukonError_t error = E_Word(input, ESt);

    if( error != EWORDRCV )
    {
        return error;
    }

    //  Was this the CCU?
    if( ESt->echo_address == 0 )
    {
        if( ESt->diagnostics.incoming_bch_error )        return BADBCH;

        //  this means the signal dropped out
        if( ESt->diagnostics.weak_signal )               return NACK1;

        //  these all indicate no response
        //if( ESt->diagnostics.incoming_no_response )
        //if( ESt->diagnostics.listen_ahead_bch_error )
        //if( ESt->diagnostics.listen_ahead_no_response )
        //if( ESt->diagnostics.repeater_code_mismatch )

        return NACKPAD1;
    }

    return EWORDRCV;
}


}
}
