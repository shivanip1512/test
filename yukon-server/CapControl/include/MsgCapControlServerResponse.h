#pragma once

#include "message.h"

class CtiCCServerResponse : public CtiMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCServerResponse );

    public:
        typedef CtiMessage Inherited;

        enum
        {
            RESP_UNSOLICITED = -1,
        };

        enum
        {
            RESULT_SUCCESS = 0,
            RESULT_COMMAND_REFUSED = 1,
            RESULT_TIMEOUT = 2
        };

        CtiCCServerResponse(long messageId, long responseType, std::string response);
        CtiCCServerResponse(const CtiCCServerResponse& commandMsg);

        virtual ~CtiCCServerResponse();

        long getMessageId() const;
        long getResponseType() const;
        std::string getResponse() const;

        CtiCCServerResponse& operator=(const CtiCCServerResponse& right);

        virtual CtiMessage* replicateMessage() const;
    private:

        CtiCCServerResponse() { }; //provided for polymorphic persitence only

        long _messageId;
        long _responseType;
        std::string _response;
};


