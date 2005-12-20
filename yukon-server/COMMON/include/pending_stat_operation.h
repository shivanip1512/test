
/*-----------------------------------------------------------------------------*
*
* File:   pending_stat_operation
*
* Class:  CtiPendingStatOperation
* Date:   7/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:25:49 $
*
*   This object represents a single operation to a thermostat
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PENDING_STAT_OPERATION_H__
#define __PENDING_STAT_OPERATION_H__

#include <windows.h>
#include <vector>


#include "ctitypes.h"
#include "dsm2.h"
#include "dlldefs.h"

using std::vector;
using std::string;

class IM_EX_CTIBASE CtiPendingStatOperation
{
public:

    typedef vector< string > PGRReplyVector_t;

    typedef enum
    {
        NoReport = 0,           // NoReport specified.
        StatsCommanded,         // Data returned represets a count of the number of stats that were sent a command.
        PreliminaryResults,     // Data returned represents confirmed, commanded, matched counts.
        CommandComplete,        // Data returned represents confirmed, commanded, matched counts.
        CommandExpired

    } EnergyProResponse_t;

protected:

    ULONG _serial;                      // What serial number is this?
    UINT _operation;

    CtiTime _submitted;                  // Time when request was sent out.
    CtiTime _expires;                    // Time at which results should have been confirmed from the stat.

    CtiTime _responded;                  // This operation has been responded to if _responded >= _submitted.
    CtiTime _confirmed;                  // This operation has been confirmed to if _confirmed >= _submitted.
    bool _match;                        // This operation matched this device's established operational parameters (it was a no-op).  No confirm is expected.

    int _reportType;                    // This is a report type for the porter thread.

    CtiOutMessage *_outMessage;         // The outbound message we sent.

    PGRReplyVector_t _replyVector;      // Vector of responses to any client?

private:

public:

    CtiPendingStatOperation(ULONG ser = 0, UINT op = 0);
    CtiPendingStatOperation(const CtiPendingStatOperation& aRef);
    virtual ~CtiPendingStatOperation();

    CtiPendingStatOperation& operator=(const CtiPendingStatOperation& aRef);
    bool operator<(const CtiPendingStatOperation& aRef) const;
    bool operator()(const CtiPendingStatOperation& aRef) const;

    ULONG getSerial() const;
    CtiPendingStatOperation& setSerial(ULONG ul);

    UINT getOperation() const;
    CtiPendingStatOperation& setOperation(UINT ui);

    const CtiTime& getTimeSubmitted() const;
    CtiPendingStatOperation& setTimeSubmitted(const CtiTime &rwt);

    const CtiTime& getTimeExpires() const;
    CtiPendingStatOperation& setTimeExpires(const CtiTime &rwt);

    const CtiTime& getTimeResponded() const;
    CtiPendingStatOperation& setTimeResponded(const CtiTime &rwt);

    const CtiTime& getTimeConfirmed() const;
    CtiPendingStatOperation& setTimeConfirmed(const CtiTime &rwt);

    bool getMatched() const;
    CtiPendingStatOperation& setMatched(bool match);

    const CtiOutMessage* CtiPendingStatOperation::getOutMessage() const;
    CtiPendingStatOperation& setOutMessage(const CtiOutMessage *&OM);

    INT getReportType() const;
    CtiPendingStatOperation& setReportType(INT i);

    PGRReplyVector_t& getReplyVector();
    PGRReplyVector_t getConstReplyVector() const;
    void addReplyVector(const string &str);

    bool isResponded() const
    {
        return getTimeResponded() >= getTimeSubmitted();
    }

    bool isConfirmed() const
    {
        return getTimeConfirmed() >= getTimeSubmitted();
    }
};
#endif // #ifndef __PENDING_STAT_OPERATION_H__
