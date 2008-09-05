/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu721
*
* Date:   2004-feb-02
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/09/05 15:45:37 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "dev_ccu721.h"
#include "prot_emetcon.h"
#include "numstr.h"
#include "porter.h"
#include "portdecl.h"
#include "mgr_route.h"


using Cti::Protocol::Klondike;


namespace Cti       {
namespace Device    {

CCU721::CCU721()
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
            if( parse.isKeyValid("timesync") )
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
        //  the OM is now owned by _klondike
        _klondike.addQueuedWork(OutMessage);

        *dqcnt = _klondike.queuedWorkCount();

        retval = QUEUED_TO_DEVICE;
    }

    return retval;
}


//  called by KickerThread() via applyKick()
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
    int error = NoError;

    if( !OutMessage )
    {
        error = MEMORY;
    }
    else
    {
        //  Klondike will determine if this is a DTRAN command or if
        //    it should read the command from the outmessage's Sequence
        error = _klondike.setCommand(OutMessage);
    }

    return error;
}


extern bool addCommResult(long deviceID, bool wasFailure, bool retryGtZero);

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
            case Klondike::Command_DirectTransmission:
            case Klondike::Command_ReadQueue:
            {
                Klondike::result_queue_t results;

                _klondike.getResults(results);

                if( results.empty() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** No results in Cti::Device::CCU721::sendCommResult() for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                while( !results.empty() )
                {
                    Klondike::result_pair_t result = results.front();
                    results.pop();

                    const OUTMESS *om = result.first;
                    INMESS  *im = result.second;

                    processInbound(om, im);

                    int socket_error;
                    unsigned long bytes_written;

                    //We had a comm error and need to report it.
                    //addCommResult(im->TargetID, (im->EventCode & 0x3fff) != NORMAL, false);

                    //statisticsNewCompletion(im->Port, im->DeviceID, im->TargetID, im->EventCode & 0x3fff, im->MessageFlags);

                    //  WE NEED TO EXPORT THE STATISTICS TO PORTFIELD SO IT CAN UPDATE THEM FOR US

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

                    if( _klondike.getCommand() == Command_ReadQueue )
                    {
                        incQueueProcessed(1, CtiTime());
                    }

                    delete om;
                }

                break;
            }
        }
    }

    return status;
}


void CCU721::processInbound(const OUTMESS *om, INMESS *im)
{
    if( (om->EventCode & BWORD) &&
        (om->Buffer.BSt.IO & Protocol::Emetcon::IO_Read ) )
    {
        DSTRUCT tmp_d_struct;

        //  inbound command - decode the D words
        im->EventCode  = Klondike::decodeDWords(im->Buffer.InMessage, im->InLength, om->Remote, &tmp_d_struct);
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


}
}
