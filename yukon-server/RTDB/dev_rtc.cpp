/*-----------------------------------------------------------------------------*
*
* File:   dev_rtc
*
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.34 $
* DATE         :  $Date: 2005/08/24 20:48:49 $
*
* HISTORY      :
* $Log: dev_rtc.cpp,v $
* Revision 1.34  2005/08/24 20:48:49  cplender
* Improved some debug output.
*
* Revision 1.33  2005/08/18 22:05:30  cplender
* Reformat the printouts
*
* Revision 1.32  2005/08/15 15:12:17  cplender
* Minor change for the verification log table writes.
*
* Revision 1.31  2005/07/29 16:26:02  cplender
* Making slight adjustments to better serve GRE's simple protocols.  Need to verify and have a plain text decode to review.
*
* Revision 1.30  2005/07/25 16:37:38  cplender
* Expiration time is printed as an RWTime asString.
*
* Revision 1.29  2005/06/16 21:25:14  cplender
* Adding the RTC scan command and decode. Must be trested with a device.
*
* Revision 1.28  2005/05/24 00:38:38  cplender
* Prevent verification objects if they are not valid.
*
* Revision 1.27  2005/04/15 19:04:10  mfisher
* got rid of magic number debuglevel checks
*
* Revision 1.26  2005/03/14 01:30:22  cplender
* Make certain we don't walk out of the OutMessage.Buffer!
*
* Revision 1.25  2005/02/17 23:03:30  cplender
* Make certain we do not GIGO the OutMessage
*
* Revision 1.24  2005/02/10 23:24:00  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.23  2005/01/13 17:49:57  mfisher
* Returning ErrReturn for error instead of InMessage->EventCode
*
* Revision 1.22  2005/01/04 22:16:03  cplender
* Completed the asString() method.
*
* Revision 1.21  2004/12/14 22:27:58  cplender
* Added 305
*
* Revision 1.20  2004/12/08 21:17:38  cplender
* Expiration Code was shady.  Expired commands which had not set an expiration time...
*
* Revision 1.19  2004/12/02 22:15:14  cplender
* Added OM-ExpirationTime to the device queue processing.
*
* Revision 1.18  2004/12/01 20:14:37  cplender
* Verification Thread now looks for '\0'.
*
* Revision 1.17  2004/11/24 17:11:39  cplender
* SA305 Verification was not complete.
*
* Revision 1.16  2004/11/09 06:15:34  cplender
* Improved the destructor if the verificationobjects are not empty..
*
* Revision 1.15  2004/11/08 16:24:54  mfisher
* implemented getVerificationObjects() instead of just thinking about it
*
* Revision 1.14  2004/10/29 20:02:11  mfisher
* added verification support
*
* Revision 1.13  2004/10/08 20:32:57  cplender
* Added method queuedWorkCount()
*
* Revision 1.12  2004/06/24 13:16:11  cplender
* Some cleanup on the simulator to make RTC and LMIRTU trx sessions look the same.
* Added PORTER_SA_RTC_MAXCODES the maimum number of codes that can be sent in one block
*
* Revision 1.11  2004/06/23 18:38:05  cplender
* Memory leak removal and slog addition.
*
* Revision 1.10  2004/06/03 21:46:17  cplender
* Simulator mods.
*
* Revision 1.9  2004/05/24 20:25:36  cplender
* Scramble
*
* Revision 1.8  2004/05/24 19:08:27  cplender
* Must have exclusions to queue work into self.
*
* Revision 1.7  2004/05/24 13:49:19  cplender
* Changed destructor to use value_type, not reference.  Harder to destruct a reference...
*
* Revision 1.6  2004/05/20 22:43:12  cplender
* Support for repeating 205 messages after n minutes.
*
* Revision 1.5  2004/05/19 14:48:53  cplender
* Exclusion changes
*
* Revision 1.4  2004/05/10 21:35:50  cplender
* Exclusions a'la GRE are a bit closer here.  The proximity exclusions should work ok now.
*
* Revision 1.3  2004/04/29 19:59:09  cplender
* Initial sa protocol/load group support
*
* Revision 1.2  2004/03/19 15:56:16  cplender
* Adding the RTC and non-305 SA protocols.
*
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "cparms.h"
#include "dsm2err.h"
#include "dev_rtc.h"

#include "msg_cmd.h"
#include "msg_lmcontrolhistory.h"
#include "porter.h"
#include "protocol_sa.h"
#include "prot_sa305.h"
#include "prot_sa3rdparty.h"
#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"

#include "verification_objects.h"

#include "numstr.h"


CtiDeviceRTC::CtiDeviceRTC() :
_millis(0)
{
}

CtiDeviceRTC::CtiDeviceRTC(const CtiDeviceRTC &aRef)
{
    *this = aRef;
}

CtiDeviceRTC::~CtiDeviceRTC()
{
    _workQueue.clearAndDestroy();

    while(!_repeatList.empty())
    {
        CtiRepeatCol::value_type repeat = _repeatList.front();
        _repeatList.pop_front();

        CtiOutMessage *pOM = repeat.second;
        delete pOM;
    }

    while(!_verification_objects.empty())
    {
        CtiVerificationBase * pV = (CtiVerificationBase *)_verification_objects.front();
        _verification_objects.pop();
        delete pV;
    }
}

CtiDeviceRTC &CtiDeviceRTC::operator=(const CtiDeviceRTC &aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

INT CtiDeviceRTC::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
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


INT CtiDeviceRTC::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
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


INT CtiDeviceRTC::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NORMAL;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    OutMessage->Buffer.SASt._commandType = parse.getCommand();

    switch(parse.getCommand())
    {
    case LoopbackRequest:
    case ScanRequest:
        {
            OutMessage->DeviceID = getID();
            OutMessage->Port = getPortID();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " Status scan request" << endl;
            }

            outList.insert(OutMessage);
            OutMessage = 0;
            break;
        }
    case ControlRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    case GetStatusRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = NoExecuteRequestMethod;
            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                    RWCString(OutMessage->Request.CommandStr),
                                                    RWCString("RTC Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.TrxID,
                                                    OutMessage->Request.UserID,
                                                    OutMessage->Request.SOE,
                                                    RWOrdered()));

            if(OutMessage)                // And get rid of our memory....
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            break;
        }
    }


    return nRet;
}


INT CtiDeviceRTC::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    RWCString resultString;
    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        if(InMessage->InLength >= 7 && InMessage->Buffer.InMessage[1] == 0x23)  // This is the STATUS POLL RESPONSE.
        {
            BYTE relayStatus = InMessage->Buffer.InMessage[2] & 0x3f;           // Bit 0 is relay 1 Bit 5 is relay 6.
            BYTE statusInput = (InMessage->Buffer.InMessage[3] & 0x0c) >> 2;    // Bit 0 is input 1, Bit 1 is input 2.
            BYTE latchedStatusInput = (InMessage->Buffer.InMessage[3] & 0x03);  // Bit 0 is input 1, Bit 1 is input 2.
            bool timeSlotAbort = (InMessage->Buffer.InMessage[3] & 0x10);       // true if the key-up aborted due to LBT.  Cleared by successful keyup.
            BYTE statusChanged = (InMessage->Buffer.InMessage[3] & 0x60) >> 5;  // Bit 0 is input 1, Bit 1 is input 2.

        }
        else if(InMessage->InLength == 2 && ((InMessage->Buffer.InMessage[0] & 0x0f) == getAddress()))
        {
            // This is quite likely a MESSAGE ACKNOWLEGEMENT.
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** MESSAGE ACKNOWLEGDED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Unknown response type. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }



        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       getName() + " / operation complete",
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

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + RWCString(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        CtiReturnMsg *retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                                       RWCString(InMessage->Return.CommandStr),
                                                       resultString,
                                                       ErrReturn,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

        retList.append(retMsg);
    }

    return ErrReturn;
}


INT CtiDeviceRTC::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
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

        delete pPIL;
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
RWCString CtiDeviceRTC::getDescription(const CtiCommandParser &parse) const
{
    return getName();
}


void CtiDeviceRTC::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceRTC::getSQL(db, keyTable, selector);
}

void CtiDeviceRTC::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _rtcTable.DecodeDatabaseReader(rdr);

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

const CtiTableDeviceRTC& CtiDeviceRTC::getRTCTable() const
{
    return _rtcTable;
}

LONG CtiDeviceRTC::getAddress() const
{
    return getRTCTable().getRTCAddress();
}

INT CtiDeviceRTC::queueRepeatToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    INT status = NORMAL;

    if(OutMessage->Buffer.SASt._groupType == SA205 || OutMessage->Buffer.SASt._groupType == SA305 || OutMessage->Buffer.SASt._groupType == SA105)
    {
        _millis += messageDuration(OutMessage->Buffer.SASt._groupType);

        OutMessage->MessageFlags |= MSGFLG_QUEUED_TO_DEVICE;
        _repeatList.push_back( make_pair(_repeatTime, OutMessage) );

        if(OutMessage->Buffer.SASt._groupType == SA305)
        {
            CtiLockGuard<CtiLogger> slog_guard(slog);
            slog << RWTime() << " "  << getName() << ": Requeued for " << _repeatTime << " repeat transmit. ACH... " << endl;
        }
        else
        {
            CtiLockGuard<CtiLogger> slog_guard(slog);
            slog << RWTime() << " "  << getName() << ": Requeued for " << _repeatTime << " repeat transmit: " << CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt) << endl;
        }

        OutMessage= 0;

        if(dqcnt)
        {
            *dqcnt = (UINT)_repeatList.size();
        }

        status = QUEUED_TO_DEVICE;
    }

    return status;
}

INT CtiDeviceRTC::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt)
{
    INT status = NORMAL;

    if( !(MSGFLG_QUEUED_TO_DEVICE & OutMessage->MessageFlags) && hasExclusions())
    {
        _millis += messageDuration(OutMessage->Buffer.SASt._groupType);

        OutMessage->MessageFlags |= MSGFLG_QUEUED_TO_DEVICE; // This OM has already been queued to the device once.

        _workQueue.putQueue(OutMessage);
        OutMessage= 0;

        if(dqcnt)
        {
            *dqcnt = (UINT)_workQueue.entries();
        }

        status = QUEUED_TO_DEVICE;
    }

    return status;
}

INT CtiDeviceRTC::queuedWorkCount() const
{
    return _workQueue.entries();
}

bool CtiDeviceRTC::hasQueuedWork() const
{
    bool hasq = false;
    RWTime now;

    if(!_repeatList.empty())
    {
        CtiRepeatCol::const_reference repeat = _repeatList.front();

        if(repeat.first <= now)
        {
            // This is a ready list entry.
            hasq = true;
        }
    }

    if( !hasq && _workQueue.entries() > 0  )
    {
        hasq = true;
    }

    return hasq;
}

bool CtiDeviceRTC::getOutMessage(CtiOutMessage *&OutMessage)
{
    bool stat = false;

    RWTime now;
    CtiRepeatCol::value_type repeat;

    OutMessage = 0;

    if(!_repeatList.empty() && (repeat = _repeatList.front()).first <= now)
    {
        _repeatList.pop_front();
        OutMessage = repeat.second;
        OutMessage->Buffer.SASt._retransmit = TRUE;     // This makes the slog say retransmit.
        stat = true;
    }
    else
    {
        do
        {
            OutMessage = _workQueue.getQueue( 500 );

            if(OutMessage && OutMessage->ExpirationTime > 0 && OutMessage->ExpirationTime < now.seconds())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Expired command on " << getName() << "'s device queue.  Expired at " << RWTime(OutMessage->ExpirationTime) << "  Targeted PAOId = " << OutMessage->TargetID << " Cmd: " << OutMessage->Request.CommandStr << endl;
                }
                delete OutMessage;
                OutMessage = 0;
            }

        } while( !OutMessage && _workQueue.entries() );

        if(OutMessage)
            stat = true;
    }

    if(stat)
    {
        _millis -= messageDuration(OutMessage->Buffer.SASt._groupType);

        if(_millis < 0) _millis = 0;
    }

    if(OutMessage)
    {
        incQueueProcessed(1, RWTime());
    }

    return stat;
}

LONG CtiDeviceRTC::deviceQueueCommunicationTime() const
{
    LONG millis = _millis;

    if(millis > 0)
    {
        millis += gConfigParms.getValueAsULong("PORTER_RTC_TIME_TRANSMIT", 0);
    }

    LONG fudge = gConfigParms.getValueAsULong("PORTER_RTC_FUDGE", 0);

    if(fudge > 0)
    {
        millis += fudge;
    }

    return millis;
}

LONG CtiDeviceRTC::deviceMaxCommunicationTime() const
{
    LONG maxtime = gConfigParms.getValueAsULong("PORTER_MAX_TRANSMITTER_TIME", 0);

    return maxtime;
}

CtiDeviceRTC& CtiDeviceRTC::setRepeatTime(const RWTime& aRef)
{
    _repeatTime = aRef;
    return *this;
}

INT CtiDeviceRTC::prepareOutMessageForComms(CtiOutMessage *&OutMessage)
{
    RWTime now;
    INT status = NORMAL;

    CtiOutMessage *rtcOutMessage = 0;       // This pointer is used to process the queued outmessages on this device.

    INT codecount = 1;      // One is for The one in OutMessage.

    ULONG msgMillis = 0;

    try
    {
        if(OutMessage->Buffer.SASt._commandType == ControlRequest)
        {
            char codestr[256];
            string cmdStr;

            switch(OutMessage->Buffer.SASt._groupType)
            {
            case SA305:
                {
                    CtiProtocolSA305 prot( OutMessage->Buffer.SASt._buffer, OutMessage->Buffer.SASt._bufferLen );
                    cmdStr = prot.asString();
                    prot.setTransmitterType(getType());
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << RWTime() << " " <<  getName() << ": " << prot.asString() << endl;
                    }
                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(slog);
                        slog << RWTime() << " " <<  getName() << ": " << CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt) << endl;
                    }
                    break;
                }
            }

            // Any repeats that are generated here should be sent out in the next cycle, or 5 minutes from now!
            setRepeatTime( getExclusion().getNextTimeSlotOpen() );

            if(OutMessage->Retry-- > 0)
            {
                // This OM needs to be plopped on the retry queue!
                CtiOutMessage *omcopy = new CtiOutMessage(*OutMessage);
                queueRepeatToDevice(omcopy, NULL);
            }

            msgMillis += messageDuration(OutMessage->Buffer.SASt._groupType);

            if( !OutMessage->VerificationSequence )
            {
                OutMessage->VerificationSequence = VerificationSequenceGen();
            }

            CtiVerificationWork *work = 0;
            if( OutMessage->Buffer.SASt._code305[0] != '\0' )
            {
                memcpy((void*)codestr, (void*)(OutMessage->Buffer.SASt._code305), OutMessage->Buffer.SASt._bufferLen);
                codestr[OutMessage->Buffer.SASt._bufferLen + 1] = 0;
            }
            else if( OutMessage->Buffer.SASt._code205 )
            {
                strncpy(codestr, CtiNumStr(OutMessage->Buffer.SASt._code205), 12);
                codestr[12] = 0;
                cmdStr = CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt);
                if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << ": " << cmdStr << endl;
                }
                work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SA205, *OutMessage, cmdStr, codestr, seconds(60));
            }
            else
            {
                strncpy(codestr, OutMessage->Buffer.SASt._codeSimple, 6);
                codestr[6] = 0;
                cmdStr = CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt);
                if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << ": " << cmdStr << endl;
                }
                work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, *OutMessage, cmdStr, codestr, seconds(60));
            }

            if(work) _verification_objects.push(work);

            while( codecount <= gConfigParms.getValueAsULong("PORTER_SA_RTC_MAXCODES",35) && ((now + (msgMillis / 1000) + 1) < getExclusion().getExecutingUntil()) && getOutMessage(rtcOutMessage) )
            {
                if( OutMessage->OutLength + 10 + rtcOutMessage->OutLength <= sizeof(OutMessage->Buffer.OutMessage) )     // 10 is for the time slot message.
                {
                    work = 0;
                    if( !rtcOutMessage->VerificationSequence )
                    {
                        rtcOutMessage->VerificationSequence = VerificationSequenceGen();
                    }

                    if( rtcOutMessage->Buffer.SASt._code305[0] != '\0' )
                    {
                        memcpy((void*)codestr, (void*)(rtcOutMessage->Buffer.SASt._code305), rtcOutMessage->Buffer.SASt._bufferLen);
                        codestr[rtcOutMessage->Buffer.SASt._bufferLen + 1] = 0;
                    }
                    else if( rtcOutMessage->Buffer.SASt._code205 )
                    {
                        strncpy(codestr, CtiNumStr(rtcOutMessage->Buffer.SASt._code205), 12);
                        codestr[12] = 0;
                        cmdStr = CtiProtocolSA3rdParty::asString(rtcOutMessage->Buffer.SASt);
                        if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << cmdStr << endl;
                        }
                        work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SA205, *rtcOutMessage, cmdStr, codestr, seconds(60));
                    }
                    else
                    {
                        strncpy(codestr, rtcOutMessage->Buffer.SASt._codeSimple, 6);
                        codestr[6] = 0;
                        cmdStr = CtiProtocolSA3rdParty::asString(rtcOutMessage->Buffer.SASt);
                        if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << cmdStr << endl;
                        }
                        work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_Golay, *rtcOutMessage, cmdStr, codestr, seconds(60));
                    }

                    if(work) _verification_objects.push(work);

                    now = now.now();
                    codecount++;
                    memcpy((char*)(&OutMessage->Buffer.OutMessage[OutMessage->OutLength]), (char*)rtcOutMessage->Buffer.SASt._buffer, rtcOutMessage->OutLength);
                    OutMessage->OutLength += rtcOutMessage->OutLength;

                    msgMillis += messageDuration(rtcOutMessage->Buffer.SASt._groupType);

                    switch(rtcOutMessage->Buffer.SASt._groupType)
                    {
                    case SA305:
                        {
                            CtiProtocolSA305 prot( rtcOutMessage->Buffer.SASt._buffer, rtcOutMessage->Buffer.SASt._bufferLen );
                            prot.setTransmitterType(getType());
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << RWTime() << " " <<  getName() << ": " << prot.asString() << endl;
                            }
                            if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " <<  getName() << ": " << prot.asString() << endl;
                            }
                            break;
                        }
                    default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(slog);
                                slog << RWTime() << " " <<  getName() << ": " << CtiProtocolSA3rdParty::asString(OutMessage->Buffer.SASt) << endl;
                            }
                            break;
                        }
                    }

                    if(rtcOutMessage->Retry-- > 0)
                    {
                        // This OM needs to be plopped on the retry queue!
                        queueRepeatToDevice(rtcOutMessage, NULL);
                        if(rtcOutMessage) delete rtcOutMessage;
                    }
                    else
                    {
                        delete rtcOutMessage;
                    }

                    rtcOutMessage = 0;
                }
                else
                {
                    if( gConfigParms.getValueAsULong("DEBUGLEVEL_DEVICE", 0) == TYPE_RTC )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    _workQueue.putQueue(rtcOutMessage); // Put it back, the OutMessage buffer is FULL.
                    rtcOutMessage = 0;

                    break;
                }
            }

            if(rtcOutMessage)   // This means we be leaking.
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** MEMORY LEAK Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            CtiProtocolSA3rdParty().appendVariableLengthTimeSlot(getAddress(),
                                                                 OutMessage->Buffer.OutMessage,
                                                                 OutMessage->OutLength,
                                                                 OutMessage->Buffer.SASt._delayToTx,
                                                                 OutMessage->Buffer.SASt._maxTxTime,
                                                                 OutMessage->Buffer.SASt._lbt);

            getExclusion().setEvaluateNextAt((now + (msgMillis / 1000) + 1));
            {
                CtiLockGuard<CtiLogger> doubt_guard(slog);
                slog << RWTime() << " " << getName() << " transmitting " << msgMillis << " \"RF\" milliseconds of codes.  Completes at " << (now + (msgMillis / 1000) + 1) << " < " << getExclusion().getExecutingUntil() << endl;
            }
        }
        else if(OutMessage->Buffer.SASt._commandType == ScanRequest || OutMessage->Buffer.SASt._commandType == LoopbackRequest)
        {
            CtiProtocolSA3rdParty().statusScan(getAddress(), OutMessage->Buffer.OutMessage, OutMessage->OutLength);
            OutMessage->InLength = 7;
            OutMessage->EventCode |= RESULT | WAIT;
            OutMessage->TimeOut = 2;
        }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


void CtiDeviceRTC::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}



ULONG CtiDeviceRTC::messageDuration(int groupType)
{
    ULONG millitime;

    if(groupType == SA205)
    {
        millitime = gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_205_CODE", 1000);
    }
    else if(groupType == SA305)
    {
        millitime = gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_305_CODE", 1000);
    }
    else
    {
        millitime = gConfigParms.getValueAsULong("PORTER_RTC_TIME_PER_SIMPLE_CODE", 250);
    }

    return millitime;
}
