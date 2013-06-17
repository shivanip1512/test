#pragma once

#include "message.h"

class CtiCCServerResponse : public CtiMessage
{
    RWDECLARE_COLLECTABLE( CtiCCServerResponse )

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

        std::string getResponse() const;
        long getResponseType() const;

        void restoreGuts(RWvistream&);
        void saveGuts(RWvostream&) const;

        CtiCCServerResponse& operator=(const CtiCCServerResponse& right);

        virtual CtiMessage* replicateMessage() const;
    private:

        CtiCCServerResponse() { }; //provided for polymorphic persitence only

        long _messageId;
        long _responseType;
        std::string _response;
};


