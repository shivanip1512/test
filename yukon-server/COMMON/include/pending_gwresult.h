
/*-----------------------------------------------------------------------------*
*
* File:   pending_gwresult
*
* Class:  CtiPendingGatewayResult
* Date:   6/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/07/21 21:34:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PENDING_GWRESULT_H__
#define __PENDING_GWRESULT_H__

#include <windows.h>
#include <set>
#include <vector>
using namespace std;

#include <rw/rwtime.h>

#include "ctitypes.h"
#include "dsm2.h"
#include "dlldefs.h"


class IM_EX_CTIBASE CtiPendingGatewayResult
{
public:

    typedef set< LONG >     ResSet_t;
    typedef set< SOCKET >   PGWSet_t;

    typedef vector< RWCString > PGRReplyVector_t;

protected:

    USHORT _messageType;                // Helps us know what confirms we should expect.

    RWTime _submitted;                  // This time represents the time when all gateway devices to which it pertains have been notified.
    RWTime _expires;                    // This time represents the time at which all results should have been confirmed from the stats.

    PGWSet_t _activeGWSet;              // Set of gateways claiming to have actively processed this message.

    ResSet_t _postedSet;                // Set of stats claiming to have actively processed this message.
    ResSet_t _respondedSet;             // Set of stats claiming to have responded as having heard the command at the stat level.
    ResSet_t _confirmedSet;             // Set of stats claiming to have confirmed the action.

    ResSet_t _matchingOpSet;            // Set of stats claiming to already have executed this action.


    PGRReplyVector_t _replyVector;      // Vector of responses to any client?

    CtiOutMessage *_outMessage;         // The outbound message we sent.

private:

public:

    CtiPendingGatewayResult();
    CtiPendingGatewayResult(const CtiPendingGatewayResult& aRef);

    virtual ~CtiPendingGatewayResult();
    CtiPendingGatewayResult& operator=(const CtiPendingGatewayResult& aRef);
    bool operator<(const CtiPendingGatewayResult& aRef) const;
    bool operator()(const CtiPendingGatewayResult& aRef) const;

    USHORT getMessageType() const;
    RWTime getTimeSubmitted() const;
    RWTime getTimeExpires() const;

    CtiPendingGatewayResult& setMessageType(USHORT type);
    CtiPendingGatewayResult& setTimeSubmitted(const RWTime &rwt);
    CtiPendingGatewayResult& setTimeExpires(const RWTime &rwt);

    const CtiOutMessage* getLastControlMessage() const;
    CtiPendingGatewayResult& setLastControlMessage(const CtiOutMessage *&OM);

    PGWSet_t& getGWSet();
    PGWSet_t getConstGWSet() const;

    ResSet_t& getPostedSet();
    ResSet_t getConstPostedSet() const;

    ResSet_t& getRespondedSet();
    ResSet_t getConstRespondedSet() const;

    ResSet_t& getConfirmedSet();
    ResSet_t getConstConfirmedSet() const;

    ResSet_t& getMatchedSet();
    ResSet_t getConstMatchedSet() const;

    PGRReplyVector_t& getReplyVector();
    PGRReplyVector_t getConstReplyVector() const;
    void addReplyVector(RWCString &str);

    bool isPosted(LONG id) const;
    void addPostedSet(LONG id);
    void addRespondedSet(LONG id);
    bool isResponded(LONG id) const;
    void addConfirmedSet(LONG id);
    bool isConfirmed(LONG id) const;
    void addMatchedSet(LONG id);
    bool isMatched(LONG id) const;

    void addGWSet(SOCKET sock);
    bool includesGW(SOCKET sock) const;

    bool isExpired( RWTime &now ) const;
    bool isComplete( RWTime &now ) const;

    size_t postedCount() const;
    size_t respondedCount() const;
    size_t confirmedCount() const;
    size_t matchCount() const;

};
#endif // #ifndef __PENDING_GWRESULT_H__
