/*---------------------------------------------------------------------------------*
*
* File:   verification_objects
*
* Class:
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/10/12 20:12:05 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __VERIFICATION_OBJECTS_H__
#define __VERIFICATION_OBJECTS_H__

#include "dlldefs.h"
#include "dsm2.h"

#include <string>
using namespace std;

#include "boost_time.h"

//  the verification objects inherit from VerificationBase so that they can both be submitted to the same thread queue
class IM_EX_CTIBASE CtiVerificationBase
{
    enum Protocol;
    enum Type;

private:

    /*  we can change all instances of "_code" to refer to an extendable class that could contain multiple
          types of data, in case a single string isn't enough;  which part to examine could be decided by
          _protocol later.
        creating a seperate protocol class for new types of verification data doesn't make a lot of sense,
          since we're decoding the sent and received parts in the transmitter and verifier anyway (so far).
        point to ponder when extending this:  does it make sense to move any of the decoding away from the device?
     */
    Protocol _protocol;
    Type     _type;
    string   _code;

    CtiVerificationBase(const CtiVerificationBase& aRef);

protected:

    const ptime _birth;

    CtiVerificationBase(Type t, Protocol p, const string &code);

    static const string String_CodeStatus_Sent;
    static const string String_CodeStatus_Success;
    static const string String_CodeStatus_Retry;
    static const string String_CodeStatus_Fail;
    static const string String_CodeStatus_Unexpected;
    static const string String_CodeStatus_Invalid;

public:

    virtual ~CtiVerificationBase();

    enum Protocol
    {
        Protocol_Golay,
        Protocol_SA205
    };

    enum Type
    {
        Type_Work,
        Type_Report
    };

    enum CodeStatus
    {
        CodeStatus_Uninitialized,
        CodeStatus_Sent,
        CodeStatus_Success,
        CodeStatus_Retry,
        CodeStatus_Fail,
        CodeStatus_Unexpected
    };

    Type          getType()     const   {  return _type;      };
    Protocol      getProtocol() const   {  return _protocol;  };
    const string &getCode()     const   {  return _code;      };

    static const string &getCodeStatusName(CodeStatus cs);

    bool operator<(const CtiVerificationBase &rhs) const;
};


class IM_EX_CTIBASE CtiVerificationReport : public CtiVerificationBase
{
private:

    long  _receiver_id;
    ptime _receipt_time;

    CtiVerificationReport( const CtiVerificationReport& aRef );

protected:
public:

    CtiVerificationReport(Protocol p, long id, const string &code, ptime time);
    virtual ~CtiVerificationReport();

    long  getReceiverID()   const    {  return _receiver_id;   };
    ptime getReceiptTime()  const    {  return _receipt_time;  };
};


class IM_EX_CTIBASE CtiVerificationWork : public CtiVerificationBase
{
private:

    typedef map< long, bool >  expectation_map;
    typedef map< long, ptime > receipt_map;

    long _transmitter_id;
    bool _retry;

    ptime::time_duration_type _patience;
    ptime                     _expiration;
    const CtiOutMessage _retry_om;
    long                _sequence;

    CodeStatus _codeDisposition;

    expectation_map _expectations;
    receipt_map     _receipts;

    CtiVerificationWork(const CtiVerificationWork& aRef);

protected:
public:

    struct earlier : binary_function<const CtiVerificationWork *, const CtiVerificationWork *, bool>
    {
        bool operator()(const CtiVerificationWork *lhs, const CtiVerificationWork *rhs) const
        {
            if( lhs && rhs )
            {
                return lhs->getExpiration() < rhs->getExpiration();
            }
            else
            {
                return lhs < rhs;
            }
        }
    };

    struct later : binary_function<const CtiVerificationWork *, const CtiVerificationWork *, bool>
    {
        bool operator()(const CtiVerificationWork *lhs, const CtiVerificationWork *rhs) const
        {
            if( lhs && rhs )
            {
                return lhs->getExpiration() > rhs->getExpiration();
            }
            else
            {
                return lhs > rhs;
            }
        }
    };

    CtiVerificationWork(Protocol p, const CtiOutMessage &om, const string &code, ptime::time_duration_type patience);  //  patience is how long we should wait for verification before logging a failure or retrying
    virtual ~CtiVerificationWork();

    long  getTransmitterID()    const   {  return _transmitter_id;  };
    ptime getExpiration()       const   {  return _expiration;      };
    long  getSequence()         const   {  return _sequence;        };
    ptime getSubmissionTime()   const   {  return _birth;           };

    CtiOutMessage *getRetryOM() const;
    string         getCommand() const;

    void  addExpectation(long receiver_id, bool retransmit);
    bool  checkReceipt(const CtiVerificationReport &receipt);

    CodeStatus processResult();

    CodeStatus getCodeStatus()   const  {  return _codeDisposition;    };
    vector< long > getExpectations() const;
    vector< pair< long, ptime > > getReceipts () const;
};


#endif // #ifndef __VERIFICATION_H__
