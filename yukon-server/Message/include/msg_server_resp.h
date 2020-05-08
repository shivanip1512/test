#pragma once

#include <string>

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
    DECLARE_COLLECTABLE( CtiServerResponseMsg )

public:

    typedef  CtiMessage  Inherited;

    /* Possible values for status */
    enum {
    OK,
    ERR,
    UNINIT
    };

    CtiServerResponseMsg();
    CtiServerResponseMsg(const CtiServerResponseMsg& req);
    CtiServerResponseMsg(int id, int status, std::string message);
    virtual ~CtiServerResponseMsg();

    CtiServerResponseMsg& operator=(const CtiServerResponseMsg& aRef);

    int getID() const;
    CtiServerResponseMsg& setID(int id);

    int getStatus() const;
    CtiServerResponseMsg& setStatus(int status);

    const std::string& getMessage() const;
    CtiServerResponseMsg& setMessage(const std::string& message);

    CtiMessage* getPayload() const;
    CtiServerResponseMsg& setPayload(CtiMessage* payload);
    CtiMessage* releasePayload();                               // Removes from response and claims ownership of the memory.

    virtual CtiMessage* replicateMessage() const;

    std::size_t getFixedSize() const override    { return sizeof( *this ); }
    std::size_t getVariableSize() const override;

    std::string toString() const override;

protected:
    int _id;
    int _status;
    std::string _message;

    CtiMessage* _payload;
};
