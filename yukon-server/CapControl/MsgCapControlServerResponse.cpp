#include "precompiled.h"

#include "MsgCapControlServerResponse.h"
#include "ccid.h"

using std::string;

DEFINE_COLLECTABLE( CtiCCServerResponse, CTICCSERVERRESPONSE_ID )

CtiCCServerResponse::CtiCCServerResponse(long messageId, long type, string res) :
    Inherited()
{
    _messageId = messageId;
    _response = res;
    _responseType = type;
}

CtiCCServerResponse::CtiCCServerResponse(const CtiCCServerResponse& commandMsg)
{
    operator=( commandMsg );
}

CtiCCServerResponse::~CtiCCServerResponse()
{
}

long CtiCCServerResponse::getMessageId() const
{
    return _messageId;
}

long CtiCCServerResponse::getResponseType() const
{
    return _responseType;
}

string CtiCCServerResponse::getResponse() const
{
    return _response;
}

CtiCCServerResponse& CtiCCServerResponse::operator=(const CtiCCServerResponse& right)
{

    if( this != &right )
    {
        Inherited::operator=(right);
        _messageId = right._messageId;
        _response = right._response;
        _responseType = right._responseType;
    }

    return *this;
}

CtiMessage* CtiCCServerResponse::replicateMessage() const
{
    return new CtiCCServerResponse(*this);
}
