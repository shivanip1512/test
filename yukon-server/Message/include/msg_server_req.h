#pragma warning( disable : 4786 )

#ifndef __MSG_SERVER_REQ_H_
#define __MSG_SERVER_REQ_H_

#include <string>

#include <rw/collect.h>

using namespace std;

#include "dlldefs.h"
#include "message.h"

/*
 * CtiRequestMsg is used to send a request to a server
 * application.  
 * A client submitting a request msg should keep track
 * of the id used in order to determine whether any subsequent
 * requests are intended for them.  
 */

class IM_EX_MSG CtiServerRequestMsg : public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiServerRequestMsg );

    typedef  CtiMessage Inherited;
    
    CtiServerRequestMsg();
    CtiServerRequestMsg(const CtiServerRequestMsg& req);
    CtiServerRequestMsg(int id, RWCollectable* payload);
    virtual ~CtiServerRequestMsg();

    int getID() const;
    CtiServerRequestMsg& setID(int id);

    RWCollectable* getPayload() const;
    CtiServerRequestMsg& setPayload(RWCollectable* payload);
	
    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);
    virtual CtiMessage* replicateMessage() const;

    void What() const;
    virtual void dump() const;


    
protected:
    int _id;
    RWCollectable* _payload;
};


#endif
