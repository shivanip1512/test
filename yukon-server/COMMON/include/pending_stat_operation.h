
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/05 12:47:20 $
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
using namespace std;

#include <rw/rwtime.h>

#include "ctitypes.h"
#include "dsm2.h"
#include "dlldefs.h"

class IM_EX_CTIBASE CtiPendingStatOperation
{
public:

    typedef vector< RWCString > PGRReplyVector_t;

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

    RWTime _submitted;                  // Time when request was sent out.
    RWTime _expires;                    // Time at which results should have been confirmed from the stat.

    RWTime _responded;                  // This operation has been responded to if _responded >= _submitted.
    RWTime _confirmed;                  // This operation has been confirmed to if _confirmed >= _submitted.
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

    RWTime getTimeSubmitted() const;
    CtiPendingStatOperation& setTimeSubmitted(const RWTime &rwt);

    RWTime getTimeExpires() const;
    CtiPendingStatOperation& setTimeExpires(const RWTime &rwt);

    RWTime getTimeResponded() const;
    CtiPendingStatOperation& setTimeResponded(const RWTime &rwt);

    RWTime getTimeConfirmed() const;
    CtiPendingStatOperation& setTimeConfirmed(const RWTime &rwt);

    bool getMatched() const;
    CtiPendingStatOperation& setMatched(bool match);

    const CtiOutMessage* CtiPendingStatOperation::getOutMessage() const;
    CtiPendingStatOperation& setOutMessage(const CtiOutMessage *&OM);

    INT getReportType() const;
    CtiPendingStatOperation& setReportType(INT i);

    PGRReplyVector_t& getReplyVector();
    PGRReplyVector_t getConstReplyVector() const;
    void addReplyVector(RWCString &str);

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
