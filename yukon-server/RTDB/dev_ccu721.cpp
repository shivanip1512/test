/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2008/10/15 17:46:00 $
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


using Cti::Protocol::Klondike;
using Cti::Protocol::Emetcon;
using namespace std;


namespace Cti       {
namespace Device    {

CCU721::CCU721() :
    _initialized(false)
{
}


CCU721::~CCU721()
{
}


Protocol::Interface *CCU721::getProtocol()
{
    return (Protocol::Interface *)&_klondike;
}


INT CCU721::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
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


INT CCU721::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, INT ScanPriority )
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


INT CCU721::ErrorDecode( INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList, bool &overrideExpectMore )
{
    return 0;
}


INT CCU721::ResultDecode( INMESS *InMessage, CtiTime &Now, list<CtiMessage *> &vgList, list<CtiMessage *> &retList, list<OUTMESS *> &outList )
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    if( !ErrReturn )
    {
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(InMessage->Return.CommandStr),
                                                  string((char *)(InMessage->Buffer.InMessage + 96), 4000),
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


bool CCU721::needsReset() const
{
    return !_initialized;
}

void CCU721::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);

    _address.getSQL(db, keyTable, selector);

    selector.where( (keyTable["type"] == "CCU-721") && selector.where() );
}


void CCU721::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    _address.DecodeDatabaseReader(rdr);

    _klondike.setAddresses(_address.getSlaveAddress(), _address.getMasterAddress());
    _klondike.setName(getName().data());
}


LONG CCU721::getAddress() const
{
    return _address.getSlaveAddress();
}


bool CCU721::hasQueuedWork()  const {  return _klondike.hasQueuedWork();    }
bool CCU721::hasWaitingWork() const {  return _klondike.hasWaitingWork();   }
bool CCU721::hasRemoteWork()  const {  return _klondike.hasRemoteWork();    }

INT CCU721::queuedWorkCount() const {  return _klondike.queuedWorkCount();  }

string CCU721::queueReport() const  {  return _klondike.queueReport();      }


INT CCU721::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    int retval = NORMAL;

    if( OutMessage->TargetID &&
        OutMessage->TargetID != OutMessage->DeviceID &&
        !(OutMessage->EventCode & DTRAN) )
    {
        vector<unsigned char> queued_message;

        writeDLCMessage(queued_message, OutMessage);

        //  the OM is now owned by _klondike
        _klondike.addQueuedWork(OutMessage,
                                queued_message,
                                OutMessage->Priority,
                                Klondike::DLCParms_None,
                                OutMessage->Buffer.BSt.DlcRoute.Stages);

        OutMessage = 0;  //  we control the horizontal and the vertical

        *dqcnt = _klondike.queuedWorkCount();

        retval = QUEUED_TO_DEVICE;
    }

    return retval;
}


//  called by KickerThread() via applyKick() and by LoadRemoteRoutes()
bool CCU721::buildCommand(CtiOutMessage *&OutMessage, Commands command)
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
                    //  routes for which this device is the transmitter
                    if( itr->second &&
                        itr->second->getCommRoute().getTrxDeviceID() == getID() )
                    {
                        _klondike.addRoute(boost::static_pointer_cast<CtiRouteCCU>(itr->second));
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

                    OutMessage->Priority = _klondike.getRemoteWorkPriority();
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
            OutMessage->Port     = getPortID();
        }
    }

    return command_built;
}


int CCU721::recvCommRequest(OUTMESS *OutMessage)
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
                                    (_current_om->InLength)?(Cti::Protocol::Emetcon::determineDWordCount(_current_om->InLength) * DWORDLEN):(0),
                                    _current_om->Priority,
                                    _current_om->Buffer.BSt.DlcRoute.Stages);
    }
    else
    {
        return _klondike.setCommand(_current_om->Sequence);
    }
}


int CCU721::sendCommResult(INMESS *InMessage)
{
    int status = NoError;

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
        InMessage->EventCode = _klondike.errorCode();

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

                InMessage->EventCode = _klondike.errorCode();
                InMessage->InLength  = dtran_result.size();
                copy(dtran_result.begin(),
                     dtran_result.end(),
                     InMessage->Buffer.InMessage);

                if( !InMessage->EventCode )
                {
                    processInbound(_current_om, InMessage);
                }

                break;
            }
            case Klondike::Command_ReadQueue:
            {
                std::queue<Klondike::queue_result_t> queued_results;

                _klondike.getQueuedResults(queued_results);

                status = _klondike.errorCode();
                /*
                if( queued_results.empty() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Cti::Device::CCU721::sendCommResult() : results.empty() : \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                */
                while( !queued_results.empty() )
                {
                    Klondike::queue_result_t result = queued_results.front();
                    queued_results.pop();

                    const OUTMESS *om = result.om;
                    INMESS *im = CTIDBG_new INMESS;

                    OutEchoToIN(om, im);

                    im->Port      = om->Port;
                    im->Remote    = om->Remote;

                    im->Buffer.DSt.Address = om->Buffer.BSt.Address;

                    im->EventCode = result.error;
                    im->Time      = result.timestamp;
                    im->InLength  = result.message.size();

                    processInbound(om, im);

                    int socket_error;
                    unsigned long bytes_written;

                    OUTMESS statistics_report;

                    statistics_report.Port          = im->Port;
                    statistics_report.DeviceID      = im->DeviceID;
                    statistics_report.TargetID      = im->TargetID;
                    statistics_report.EventCode     = im->EventCode & 0x3fff;
                    statistics_report.MessageFlags  = im->MessageFlags;

                    _statistics.push_back(statistics_report);

                    /* this is a completed result so send it to originating process */
                    im->EventCode |= DECODED;
                    if( (socket_error = im->ReturnNexus->CTINexusWrite(im, sizeof(INMESS), &bytes_written, 60L)) != NORMAL )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error writing to nexus. Cti::Device::CCU721::sendCommResult() on device \"" << getName() << "\".  "
                                 << "Wrote " << bytes_written << "/" << sizeof(INMESS) << " bytes" << endl;
                        }

                        if( CTINEXUS::CTINexusIsFatalSocketError(socket_error) )
                        {
                            status = socket_error;
                        }
                    }

                    delete om;
                }

                break;
            }
        }
    }

    return status;
}


void CCU721::getTargetDeviceStatistics(std::vector< OUTMESS > &om_statistics)
{
    om_statistics.insert(om_statistics.end(),
                         _statistics.begin(),
                         _statistics.end());

    _statistics.clear();
}


void CCU721::writeDLCMessage( byte_buffer_t &buf, const OUTMESS *om )
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
            dout << CtiTime() << " **** Checkpoint - Cti::Protocol::Klondike::writeDLCMessage() : unhandled word type (" << (om->EventCode & (AWORD | BWORD)) << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


void CCU721::writeDLCTimesync( byte_buffer_t &buf )
{
    BSTRUCT timesync_bst;

    timesync_bst.Address           = CtiDeviceMCT4xx::UniversalAddress;
    timesync_bst.IO                = Emetcon::IO_Function_Write;
    timesync_bst.Function          = CtiDeviceMCT4xx::FuncWrite_TSyncPos;
    timesync_bst.Length            = CtiDeviceMCT4xx::FuncWrite_TSyncLen;

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


void CCU721::writeAWord( byte_buffer_t &buf, const ASTRUCT &ASt )
{
    buf.push_back(AWORDLEN);

    buf.insert(buf.end(), AWORDLEN, 0);

    A_Word(buf.end() - AWORDLEN, ASt);
}

void CCU721::writeBWord( byte_buffer_t &buf, const BSTRUCT &BSt )
{
    int words;

    if( BSt.IO == Emetcon::IO_Write ||
        BSt.IO == Emetcon::IO_Function_Write )
    {
        words = (BSt.Length + 4) / 5;

        buf.push_back(BWORDLEN + CWORDLEN * words);
    }
    else
    {
        words = Emetcon::determineDWordCount(BSt.Length);

        buf.push_back(BWORDLEN);
    }

    //  we insert relative to the end so that we can append to any buffer given to us
    buf.insert(buf.end(), BWORDLEN, 0);
    B_Word(buf.end() - BWORDLEN, BSt, words);

    if( BSt.IO == Emetcon::IO_Write ||
        BSt.IO == Emetcon::IO_Function_Write )
    {
        buf.insert(buf.end(), CWORDLEN * words, 0);

        //  I really don't know why C_Words takes a const pointer to unsigned char instead of a pointer to const unsigned char...
        BSTRUCT tmpBSt = BSt;
        C_Words(buf.end() - CWORDLEN * words, tmpBSt.Message, tmpBSt.Length, 0);
    }
}


void CCU721::processInbound(const OUTMESS *om, INMESS *im)
{
    if( (om->EventCode & BWORD) &&
        (om->Buffer.BSt.IO & Protocol::Emetcon::IO_Read ) )
    {
        DSTRUCT tmp_d_struct;

        //  inbound command - decode the D words
        im->EventCode  = decodeDWords(im->Buffer.InMessage, im->InLength, om->Remote, &tmp_d_struct);
        im->Buffer.DSt = tmp_d_struct;
        im->InLength   = (im->InLength / DWORDLEN) * 5 - 2;  //  calculate the number of bytes we get back
        im->Buffer.DSt.Length = im->InLength;

        im->Buffer.DSt.Time    = im->Time;
        im->Buffer.DSt.DSTFlag = im->MilliTime & DSTACTIVE;
    }
    else
    {
        im->InLength = 0;
    }
}


int CCU721::decodeDWords(const unsigned char *input, unsigned input_length, unsigned Remote, DSTRUCT *DSt) const
{
    int status = NoError;
    unsigned short unused;

    if( input_length % DWORDLEN )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Cti::Device::CCU721::decodeDWords() : input_length % DWORDLEN > 0 : " << input_length << " : \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    byte_buffer_t input_buffer(input, input + input_length);

    for( int i = 1; (i * DWORDLEN) <= input_length && !status; i++ )
    {
        switch( i )
        {
            //  old code is stuck in its non-const ways
            case 1:  status = D1_Word (input_buffer.begin(),                &DSt->Message[0], &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm);  break;
            case 2:  status = D23_Word(input_buffer.begin() + DWORDLEN,     &DSt->Message[3], &DSt->TSync, &unused);  break;
            case 3:  status = D23_Word(input_buffer.begin() + DWORDLEN * 2, &DSt->Message[8], &unused, &unused);  break;
        }
    }

    return status;
}


}
}
