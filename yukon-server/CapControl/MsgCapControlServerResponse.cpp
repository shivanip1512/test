#include "precompiled.h"

#include "MsgCapControlServerResponse.h"
#include "ccid.h"

using std::string;

RWDEFINE_COLLECTABLE( CtiCCServerResponse, CTICCSERVERRESPONSE_ID )

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

long CtiCCServerResponse::getResponseType() const
{
    return _responseType;
}

string CtiCCServerResponse::getResponse() const
{
    return _response;
}

void CtiCCServerResponse::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
    strm >> _messageId
         >> _responseType
         >> _response;

    return;
}

void CtiCCServerResponse::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);

    strm << _messageId
         << _responseType
         << _response;

    return;
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
