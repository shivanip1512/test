#pragma warning( disable : 4786 )

#ifndef __MSG_SERVER_RESP_H_
#define __MSG_SERVER_RESP_H_

#include <string>

#include <rw/collect.h>

using namespace std;

#include "dlldefs.h"
#include "message.h"

/*
 * CtiServerResponseMsg is used to respond back to a particular
 * client request with a status and a textual message as
 * well as a collectable payload.  That might be some updated
 * state info or whatever is relevant to the response to a request.
 */

class IM_EX_MSG CtiServerResponseMsg : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiServerResponseMsg );

    typedef  CtiMessage Inherited;
    
    /* Possible values for status */
    enum {
	OK,
	ERR,
	UNINIT
    };
    
    CtiServerResponseMsg();
    CtiServerResponseMsg(const CtiServerResponseMsg& req);
    CtiServerResponseMsg(int id, int status, string message);
    virtual ~CtiServerResponseMsg();

    int getID() const;
    CtiServerResponseMsg& setID(int id);
    
    int getStatus() const;
    CtiServerResponseMsg& setStatus(int status);
    
    const string& getMessage() const;
    CtiServerResponseMsg& setMessage(const string& message);

    RWCollectable* getPayload() const;
    CtiServerResponseMsg& setPayload(RWCollectable* payload);
    
    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);

    virtual CtiMessage* replicateMessage() const;

    void What() const;
    virtual void dump() const;
    
protected:
    int _id;
    int _status;
    string _message;

    RWCollectable* _payload;
};


#endif
