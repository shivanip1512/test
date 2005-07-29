/*-----------------------------------------------------------------------------*
*
* File:   verification
*
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/07/29 16:26:02 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "verification_objects.h"
#include "ctidbgmem.h"

#include <rw/rwtime.h>  //  ONLY for rwEpoch, use ptime/date for everything else

const string CtiVerificationBase::String_CodeStatus_Sent       = "sent";
const string CtiVerificationBase::String_CodeStatus_Success    = "success";
const string CtiVerificationBase::String_CodeStatus_Retry      = "retry";
const string CtiVerificationBase::String_CodeStatus_Fail       = "fail";
const string CtiVerificationBase::String_CodeStatus_Unexpected = "unexpected";
const string CtiVerificationBase::String_CodeStatus_Invalid    = "(invalid status)";

CtiVerificationBase::CtiVerificationBase(Type t, Protocol p, const string &command, const string &code) :
    _type(t),
    _protocol(p),
    _command(command),
    _code(code),
    _birth(second_clock::universal_time())
{
}


CtiVerificationBase::~CtiVerificationBase()
{
}


bool CtiVerificationBase::operator<(const CtiVerificationBase &rhs) const
{
    return _birth < rhs._birth;
}


//  this is a static in the base class so that Report can call it without actually owning a "code status" proper
const string &CtiVerificationBase::getCodeStatusName(CodeStatus cs)
{
    //  this seems to be the only way to reassign to a string...
    const string *s;

    switch( cs )
    {
        case CodeStatus_Sent:       s = &String_CodeStatus_Sent;        break;
        case CodeStatus_Fail:       s = &String_CodeStatus_Fail;        break;
        case CodeStatus_Retry:      s = &String_CodeStatus_Retry;       break;
        case CodeStatus_Success:    s = &String_CodeStatus_Success;     break;
        case CodeStatus_Unexpected: s = &String_CodeStatus_Unexpected;  break;
        default:                    s = &String_CodeStatus_Invalid;     break;
    }

    return *s;
}

CtiVerificationWork::CtiVerificationWork(Protocol p, const CtiOutMessage &om, const string &command, const string &code, ptime::time_duration_type patience = seconds(0)) :
    CtiVerificationBase(Type_Work, p, command, code),
    _retry_om(om),
    _expiration(second_clock::universal_time() + patience),
    _codeDisposition(CodeStatus_Uninitialized)
{
    _transmitter_id = om.DeviceID;
    _retry          = (om.Retry > 0)?true:false;
    _sequence       = om.VerificationSequence;
}


CtiVerificationWork::~CtiVerificationWork()
{
}


CtiOutMessage *CtiVerificationWork::getRetryOM() const
{
    CtiOutMessage *retval = CTIDBG_new CtiOutMessage(_retry_om);

    //  only expire this message if the other one had an expiration time set...  this isn't quite foolproof,
    //    since the expiration time might need to be longer than _patience, but it's a good starting place...
    //    maybe we should make it an addtional constructor parameter, or have the constructor-submitted OM
    //    keep a relative value until we resubmit it and make it an absolute time
    if( _retry_om.ExpirationTime )
    {
        ptime::time_duration_type expiration = (second_clock::universal_time() - ptime(date(1970, 1, 1))) + _patience;

        retval->ExpirationTime = expiration.total_seconds() + rwEpoch;  //  ExpirationTime is an RWTime.seconds()
    }

    retval->Retry = 0;

    return retval;
}


void CtiVerificationWork::addExpectation(long receiver_id, bool retransmit)
{
    _expectations.insert(make_pair(receiver_id, retransmit));
}


bool CtiVerificationWork::checkReceipt(const CtiVerificationReport &report)
{
    bool retval = false;

    //  make sure we match on protocol and code...
    if( (report.getProtocol() == this->getProtocol()) &&
        (report.getCode()     == this->getCode()) )
    {
        expectation_map::iterator itr = _expectations.find(report.getReceiverID());

        //  and make sure we're still expecting to hear from this receiver
        if( itr != _expectations.end() )
        {
            _receipts.insert(make_pair(report.getReceiverID(), report.getReceiptTime()));

            _expectations.erase(itr);

            retval = true;
        }
    }

    return retval;
}


CtiVerificationWork::CodeStatus CtiVerificationWork::processResult()
{
    //  assume success, look for failure
    _codeDisposition = CodeStatus_Success;

    //  walk through the list of verification devices we never heard from...
    for( expectation_map::iterator itr = _expectations.begin(); (itr != _expectations.end()) && (_codeDisposition == CodeStatus_Success); itr++ )
    {
        //  looking for receivers that are marked as "retransmit"
        if( (*itr).second )
        {
            //  if we didn't receive a confirmation, send the retry
            //    (or if we've already retried, we've failed)
            if( _receipts.find((*itr).first) == _receipts.end() )
            {
                if( _retry )
                {
                    _codeDisposition = CodeStatus_Retry;
                }
                else
                {
                    _codeDisposition = CodeStatus_Fail;
                }
            }
        }
    }

    return _codeDisposition;
}


deque< long > CtiVerificationWork::getExpectations() const
{
    deque< long > e;
    expectation_map::const_iterator itr;

    for( itr = _expectations.begin(); itr != _expectations.end(); itr++ )
    {
        long tmp = itr->first;

        e.push_back(tmp);
    }

    return e;
}


deque< pair< long, ptime > > CtiVerificationWork::getReceipts() const
{
    deque< pair< long, ptime > > r;
    receipt_map::const_iterator itr;

    for( itr = _receipts.begin(); itr != _receipts.end(); itr++ )
    {
        r.push_back(*itr);
    }

    return r;
}


CtiVerificationReport::CtiVerificationReport(Protocol p, long id, const string &code, ptime time, const string &command) :
    CtiVerificationBase(Type_Report, p, command, code),
    _receiver_id(id),
    _receipt_time(time)
{
}


CtiVerificationReport::~CtiVerificationReport()
{
}

