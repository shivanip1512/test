/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.17.2.1 $
* DATE         :  $Date: 2008/11/19 15:21:28 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_ccu721.h"
#include "dev_mct4xx.h"
#include "prot_emetcon.h"
#include "numstr.h"
#include "porter.h"
#include "portdecl.h"
#include "mgr_route.h"


using namespace std;

using Cti::Protocol::Klondike;
using Cti::Protocols::EmetconProtocol;

namespace Cti {
namespace Devices {

Ccu721Device::Ccu721Device() :
    _initialized(false),
    _queueInterface(_klondike),
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
            OutMessage->Sequence = Klondike::Command_Loopback;

            nRet = NoError;

            break;
        }
        case GetConfigRequest:
        {
            if( parse.isKeyValid("time") )
            {
                OutMessage->Sequence = Klondike::Command_TimeRead;

                nRet = NoError;
            }
        }
        case PutConfigRequest:
        {
            if( parse.isKeyValid("raw") )
            {
                OutMessage->Sequence = Klondike::Command_Raw;
                OutMessage->OutLength = parse.getsValue("raw").length();
                memcpy(OutMessage->Buffer.OutMessage, parse.getsValue("raw").data(), OutMessage->OutLength);

                nRet = NoError;
            }
            else if( parse.isKeyValid("timesync") )
            {
                OutMessage->Sequence = Klondike::Command_TimeSync;

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


INT Ccu721Device::ResultDecode( INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if( !ErrReturn )
    {
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(InMessage->Return.CommandStr),
                                                  string((char *)(InMessage->Buffer.InMessage + InMessage_StringOffset)),
                                                  InMessage->EventCode & 0x7fff,
                                                  InMessage->Return.RouteID,
                                                  InMessage->Return.MacroOffset,
                                                  InMessage->Return.Attempt,
                                                  InMessage->Return.GrpMsgID,
                                                  InMessage->Return.UserID));

        resetScanFlag();
    }

    return 0;
}


bool Ccu721Device::needsReset() const
{
    return !_initialized;
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


bool Ccu721Device::hasQueuedWork()  const {  return _klondike.hasQueuedWork();    }
bool Ccu721Device::hasWaitingWork() const {  return _klondike.hasWaitingWork();   }
bool Ccu721Device::hasRemoteWork()  const {  return _klondike.hasRemoteWork();    }

INT Ccu721Device::queuedWorkCount() const {  return _klondike.queuedWorkCount();  }

string Ccu721Device::queueReport() const  {  return _klondike.queueReport();      }

DeviceQueueInterface *Ccu721Device::getDeviceQueueHandler()
{
    return &_queueInterface;
}


INT Ccu721Device::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;

    // If they are the same, it is a message to the CCU and should not be queued
    // Instead of checking this, should all messages to the CCU be marked DTRAN?
    if( OutMessage->TargetID != OutMessage->DeviceID &&
        !(OutMessage->EventCode & DTRAN) )
    {
        vector<unsigned char> queued_message;

        writeDLCMessage(queued_message, OutMessage);

        //  the OM is now owned by _klondike
        _klondike.addQueuedWork(OutMessage,
                                queued_message,
                                OutMessage->Priority,
                                Klondike::DLCParms_None | OutMessage->Buffer.BSt.DlcRoute.Bus + 1,
                                OutMessage->Buffer.BSt.DlcRoute.Stages);

        OutMessage = 0;  //  we control the horizontal and the vertical

        *dqcnt = _klondike.queuedWorkCount();

        retval = QUEUED_TO_DEVICE;
    }

    return retval;
}


//  called by KickerThread() via applyKick() and by LoadRemoteRoutes()
bool Ccu721Device::buildCommand(CtiOutMessage *&OutMessage, Commands command)
{
    bool command_built = false;
    CtiTime now;

    if( OutMessage )
    {
        switch( command )
        {
            case Command_LoadRoutes:
            {
                _initialized = true;

                OutMessage->Sequence = Klondike::Command_LoadRoutes;

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
                    OutMessage->Sequence = Klondike::Command_ReadQueue;

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
                    OutMessage->Sequence = Klondike::Command_LoadQueue;

                    command_built = true;
                }

                break;
            }
            case Command_Timesync:
            {
                OutMessage->Priority = MAXPRIORITY;
                OutMessage->Sequence = Klondike::Command_TimeSync;

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
    }

    return command_built;
}


int Ccu721Device::recvCommRequest(OUTMESS *OutMessage)
{
    if( !OutMessage )
    {
        return MEMORY;
    }

    _current_om = OutMessage;

    if( _current_om->EventCode & DTRAN &&
        _current_om->EventCode & BWORD )
    {
        byte_buffer_t dtran_message;

        writeDLCMessage(dtran_message, _current_om);

        return _klondike.setCommand(Klondike::Command_DirectTransmission,
                                    dtran_message,
                                    (_current_om->InLength)?(EmetconProtocol::determineDWordCount(_current_om->InLength) * DWORDLEN):(0),
                                    _current_om->Priority,
                                    _current_om->Buffer.BSt.DlcRoute.Stages,
                                    Klondike::DLCParms_None | _current_om->Buffer.BSt.DlcRoute.Bus + 1);
    }
    else if( _current_om->Sequence == Klondike::Command_Raw )
    {
        byte_buffer_t raw_message(OutMessage->Buffer.OutMessage, OutMessage->Buffer.OutMessage + OutMessage->OutLength);

        return _klondike.setCommand(Klondike::Command_Raw,
                                    raw_message);
    }
    else
    {
        return _klondike.setCommand(_current_om->Sequence);
    }
}


int Ccu721Device::sendCommResult(INMESS *InMessage)
{
    int status = translateKlondikeError(_klondike.errorCode());

    //  if the CCU owns the InMessage - we don't end up owning the DTRAN InMessage, the MCT does...
    //    so there's no use in putting a string in there, since we won't decode it anyway
    if( _klondike.getCommand() != Klondike::Command_DirectTransmission )
    {
        string results = _klondike.describeCurrentStatus();

        strncpy(reinterpret_cast<char *>(InMessage->Buffer.InMessage + InMessage_StringOffset),
                results.data(),
                4096 - InMessage_StringOffset);
    }


    if( _klondike.errorCondition() )
    {
        switch( _klondike.getCommand() )
        {
            case Klondike::Command_DirectTransmission:
            {
                InMessage->Buffer.DSt.Time     = InMessage->Time;
                InMessage->Buffer.DSt.DSTFlag  = InMessage->MilliTime & DSTACTIVE;

                break;
            }
        }
    }
    else
    {
        switch( _klondike.getCommand() )
        {
            case Klondike::Command_TimeSync:
            {
                byte_buffer_t timesync;
                writeDLCTimesync(timesync);

                _klondike.addQueuedWork(0,
                                        timesync,
                                        MAXPRIORITY,
                                        Klondike::DLCParms_BroadcastFlag,
                                        0);

                break;
            }
            case Klondike::Command_DirectTransmission:
            {
                byte_buffer_t dtran_result = _klondike.getDTranResult();

                OutEchoToIN(_current_om, InMessage);

                InMessage->Port   = _current_om->Port;
                InMessage->Remote = _current_om->Remote;

                InMessage->Time   = CtiTime::now().seconds();

                InMessage->InLength  = dtran_result.size();

                copy(dtran_result.begin(), dtran_result.end(), InMessage->Buffer.InMessage);

                //  unlike in Command_LoadQueue/Command_ReadQueue, the InMessage->EventCode is set by
                //    the CommResult/status later on in CtiDeviceSingle::ProcessResult
                if( !status )
                {
                    status = processInbound(_current_om, InMessage);
                }

                break;
            }
            case Klondike::Command_LoadQueue:
            case Klondike::Command_ReadQueue:
            {
                std::queue<Klondike::queue_result_t> queued_results;

                _klondike.getQueuedResults(queued_results);

                while( !queued_results.empty() )
                {
                    Klondike::queue_result_t result = queued_results.front();
                    queued_results.pop();

                    const OUTMESS *om = result.om;

                    if( om )
                    {
                        INMESS *im = CTIDBG_new INMESS;

                        OutEchoToIN(om, im);

                        im->Port      = om->Port;
                        im->Remote    = om->Remote;

                        im->EventCode = translateKlondikeError(result.error);
                        im->Time      = result.timestamp;
                        im->InLength  = result.message.size();

                        copy(result.message.begin(), result.message.end(), im->Buffer.InMessage);

                        if( !im->EventCode )
                        {
                            im->EventCode = processInbound(om, im);
                        }

                        int socket_error;
                        unsigned long bytes_written;

                        OUTMESS statistics_report;

                        statistics_report.Port          = im->Port;
                        statistics_report.DeviceID      = im->DeviceID;
                        statistics_report.TargetID      = im->TargetID;
                        statistics_report.EventCode     = im->EventCode & 0x3fff;
                        statistics_report.MessageFlags  = im->MessageFlags;

                        _statistics.push_back(statistics_report);

                        if( (socket_error = im->ReturnNexus->CTINexusWrite(im, sizeof(INMESS), &bytes_written, 60L)) != NORMAL )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Error writing to nexus. Ccu721Device::sendCommResult() on device \"" << getName() << "\".  "
                                     << "Wrote " << bytes_written << "/" << sizeof(INMESS) << " bytes" << endl;
                            }

                            if( CTINEXUS::CTINexusIsFatalSocketError(socket_error) )
                            {
                                status = socket_error;
                            }
                        }

                        delete om;
                    }
                }

                break;
            }
        }
    }

    return status;
}


int Ccu721Device::translateKlondikeError(Klondike::Errors error)
{
    switch( error )
    {
        case Klondike::Error_None:                      return NoError;

        case Klondike::Error_BusDisabled:               return BADBUSS;
        case Klondike::Error_InvalidBus:                return BADBUSS;
        case Klondike::Error_InvalidDLCType:            return NOTNORMAL;
        case Klondike::Error_InvalidMessageLength:      return BADLENGTH;
        case Klondike::Error_InvalidSequence:           return BADSEQUENCE;
        case Klondike::Error_NoRoutes:                  return RTNF;
        case Klondike::Error_QueueEntryLost:            return QUEUEFLUSHED;

        default:
        case Klondike::Error_Unknown:                   return NOTNORMAL;
    }
}


void Ccu721Device::getTargetDeviceStatistics(std::vector< OUTMESS > &om_statistics)
{
    om_statistics.insert(om_statistics.end(),
                         _statistics.begin(),
                         _statistics.end());

    _statistics.clear();
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


int Ccu721Device::processInbound(const OUTMESS *om, INMESS *im)
{
    if( (om->EventCode & BWORD) &&
        (om->Buffer.BSt.IO & Protocols::EmetconProtocol::IO_Read ) )
    {
        DSTRUCT tmp_d_struct;

        //  inbound command - decode the D words
        int dword_status = decodeDWords(im->Buffer.InMessage, im->InLength, om->Remote, &tmp_d_struct);

        if( !dword_status )
        {
            im->Buffer.DSt = tmp_d_struct;

            im->InLength = im->Buffer.DSt.Length = (im->InLength / DWORDLEN) * 5 - 2;  //  calculate the number of bytes we get back
        }
        else
        {
            im->InLength = 0;
        }

        im->Buffer.DSt.Time    = im->Time;
        im->Buffer.DSt.DSTFlag = im->MilliTime & DSTACTIVE;

        return dword_status;
    }

    im->InLength = 0;

    return NoError;
}


int Ccu721Device::decodeDWords(const unsigned char *input, const unsigned input_length, const unsigned Remote, DSTRUCT *DSt) const
{
    int status = NoError;
    unsigned short unused;

    if( input_length % DWORDLEN )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Devices::CCU721::decodeDWords() : input_length % DWORDLEN > 0 : " << input_length << " : \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    byte_buffer_t input_buffer(input, input + input_length);

    for( int i = 1; (i * DWORDLEN) <= input_length && !status; i++ )
    {
        unsigned char *word_buf = &input_buffer.front();

        word_buf += DWORDLEN * (i - 1);

        switch( i )
        {
            //  old code is stuck in its non-const ways
            case 1:  status = D1_Word (word_buf, &DSt->Message[0], &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm);  break;
            case 2:  status = D23_Word(word_buf, &DSt->Message[3], &DSt->TSync, &unused);  break;
            case 3:  status = D23_Word(word_buf, &DSt->Message[8], &unused, &unused);  break;
        }

        if( status == EWORDRCV )
        {
            status = decodeEWord(word_buf, EWORDLEN);
        }
    }

    return status;
}


int Ccu721Device::decodeEWord(const unsigned char *input, const unsigned input_length)
{
    ESTRUCT ESt;

    byte_buffer_t input_buffer(input, input + input_length);

    int error = E_Word(&input_buffer.front(), &ESt);

    if( error )
    {
        return error;
    }

    //  Was this the CCU?
    if( !ESt.echo_address )
    {
        if( ESt.diagnostics.incoming_bch_error )        return BADBCH;

        //  these all indicate no response
        if( ESt.diagnostics.incoming_no_response )      return NACKPAD1;
        if( ESt.diagnostics.listen_ahead_bch_error )    return NACKPAD1;
        if( ESt.diagnostics.listen_ahead_no_response )  return NACKPAD1;
        if( ESt.diagnostics.repeater_code_mismatch )    return NACKPAD1;

        //  this means the signal dropped out
        if( ESt.diagnostics.weak_signal )               return NACK1;
    }

    //  just pass along the E word like we used to
    return EWORDRCV;
}


}
}
