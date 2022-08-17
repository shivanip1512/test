#pragma once

#include <string>

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
    DECLARE_COLLECTABLE( CtiServerRequestMsg )

public:

    typedef  CtiMessage  Inherited;

    CtiServerRequestMsg();
    CtiServerRequestMsg(const CtiServerRequestMsg& req);
    CtiServerRequestMsg(int id, CtiMessage* payload);
    virtual ~CtiServerRequestMsg();

    int getID() const;
    CtiServerRequestMsg& setID(int id);

    CtiMessage* getPayload() const;
    CtiServerRequestMsg& setPayload(CtiMessage* payload);

    virtual CtiMessage* replicateMessage() const;

    virtual std::string toString() const override;

protected:
    int _id;
    CtiMessage* _payload;
};
