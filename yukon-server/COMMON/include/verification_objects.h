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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/01/26 19:56:14 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __VERIFICATION_OBJECTS_H__
#define __VERIFICATION_OBJECTS_H__

#include "dlldefs.h"
#include "dsm2.h"

#include <string>
#include <queue>

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
    std::string   _code;
    std::string   _command;

    CtiVerificationBase(const CtiVerificationBase& aRef);

protected:

    const boost::posix_time::ptime _birth;

    CtiVerificationBase(Type t, Protocol p, const std::string &command, const std::string &code);

    static const std::string String_CodeStatus_Sent;
    static const std::string String_CodeStatus_Success;
    static const std::string String_CodeStatus_Timeout;
    static const std::string String_CodeStatus_Retry;
    static const std::string String_CodeStatus_Fail;
    static const std::string String_CodeStatus_Unexpected;
    static const std::string String_CodeStatus_Invalid;

public:

    virtual ~CtiVerificationBase();

    enum Protocol
    {
        Protocol_Invalid,
        Protocol_Golay,
        Protocol_SA205,
        Protocol_SA305,
        Protocol_SADigital,
        Protocol_SNPP,
        Protocol_Versacom,
        Protocol_Expresscom
    };

    enum Type
    {
        Type_Work,
        Type_Report,
    };

    enum CodeStatus
    {
        CodeStatus_Uninitialized,
        CodeStatus_Sent,
        CodeStatus_Success,
        CodeStatus_Timeout,
        CodeStatus_Retry,
        CodeStatus_Fail,
        CodeStatus_Unexpected,
    };

    Type getType()                   const  {  return _type;      };
    Protocol getProtocol()           const  {  return _protocol;  };
    const std::string &getCode()     const  {  return _code;      };
    const std::string &getCommand()  const  {  return _command;   };

    static const std::string &getCodeStatusName(CodeStatus cs);

    bool operator<(const CtiVerificationBase &rhs) const;
};


class IM_EX_CTIBASE CtiVerificationReport : public CtiVerificationBase
{
private:

    long  _receiver_id;
    boost::posix_time::ptime _receipt_time;

    CtiVerificationReport( const CtiVerificationReport& aRef );

protected:
public:

    CtiVerificationReport(Protocol p, long id, const std::string &code, boost::posix_time::ptime time, const std::string &command=std::string("-"));
    virtual ~CtiVerificationReport();

    long  getReceiverID()   const                       {  return _receiver_id;   };
    boost::posix_time::ptime getReceiptTime()  const    {  return _receipt_time;  };
};


class IM_EX_CTIBASE CtiVerificationWork : public CtiVerificationBase
{
private:

    typedef std::map< long, bool >                     expectation_map;
    typedef std::map< long, boost::posix_time::ptime > receipt_map;

    long _transmitter_id;
    bool _retry;

    boost::posix_time::ptime::time_duration_type _patience;
    boost::posix_time::ptime                     _expiration;
    const CtiOutMessage                          _retry_om;
    long                                         _sequence;

    CodeStatus _codeDisposition;

    expectation_map _expectations;
    receipt_map     _receipts;

    CtiVerificationWork(const CtiVerificationWork& aRef);

protected:
public:

    struct earlier : std::binary_function <const CtiVerificationWork *, const CtiVerificationWork *, bool>
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

    struct later : std::binary_function<const CtiVerificationWork *, const CtiVerificationWork *, bool>
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

    CtiVerificationWork(Protocol p, const CtiOutMessage &om, const std::string &command, const std::string &code, boost::posix_time::ptime::time_duration_type patience);  //  patience is how long we should wait for verification before logging a failure or retrying
    virtual ~CtiVerificationWork();

    long  getTransmitterID()                       const   {  return _transmitter_id;  };
    boost::posix_time::ptime getExpiration()       const   {  return _expiration;      };
    long  getSequence()                            const   {  return _sequence;        };
    boost::posix_time::ptime getSubmissionTime()   const   {  return _birth;           };

    CtiOutMessage *getRetryOM() const;
    //string         getCommand() const   { return _command; }

    void  addExpectation(long receiver_id, bool retransmit);
    bool  checkReceipt(const CtiVerificationReport &receipt);

    CodeStatus processResult();

    CodeStatus getCodeStatus()   const  {  return _codeDisposition;    };
    std::deque< long > getExpectations() const;
    std::deque< std::pair< long, boost::posix_time::ptime > > getReceipts () const;
};


#endif // #ifndef __VERIFICATION_H__
