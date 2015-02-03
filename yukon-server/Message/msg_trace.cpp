#include "precompiled.h"

#include "dllbase.h"
#include "collectable.h"
#include "dlldefs.h"
#include "logger.h"
#include "msg_trace.h"

using namespace std;

DEFINE_COLLECTABLE( CtiTraceMsg, MSG_TRACE );

CtiTraceMsg::CtiTraceMsg() :
    _end(true),
    _attributes( FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED )
{}

CtiTraceMsg::CtiTraceMsg(const CtiTraceMsg& aRef)
{
    *this = aRef;
}

CtiTraceMsg::~CtiTraceMsg() {}

CtiTraceMsg& CtiTraceMsg::operator=(const CtiTraceMsg& aRef)
{
    if(this != &aRef)
    {
        setEnd(aRef.isEnd());
        setAttributes(aRef.getAttributes());
        setTrace(aRef.getTrace());
    }
    return *this;
}

// Return a new'ed copy of this message!
CtiMessage* CtiTraceMsg::replicateMessage() const
{
   CtiTraceMsg *ret = CTIDBG_new CtiTraceMsg(*this);

   return( (CtiMessage*)ret );
}

std::string CtiTraceMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTraceMsg";
    itemList.add("Message Attributes") << _attributes;
    itemList.add("Message End")        << _end;
    itemList.add("Message Trace")      << _trace;

    return (Inherited::toString() += itemList.toString());
}


INT CtiTraceMsg::getAttributes() const
{
    return _attributes;
}
const string &CtiTraceMsg::getTrace() const
{
    return _trace;
}

/*
 * #define FOREGROUND_BLUE      0x0001 // text color contains blue.
 * #define FOREGROUND_GREEN     0x0002 // text color contains green.
 * #define FOREGROUND_RED       0x0004 // text color contains red.
 * #define FOREGROUND_INTENSITY 0x0008 // text color is intensified.
 * #define BACKGROUND_BLUE      0x0010 // background color contains blue.
 * #define BACKGROUND_GREEN     0x0020 // background color contains green.
 * #define BACKGROUND_RED       0x0040 // background color contains red.
 * #define BACKGROUND_INTENSITY 0x0080 // background color is intensified.
 */
CtiTraceMsg& CtiTraceMsg::setAttributes(const INT& attr)
{
    _attributes = attr;
    return *this;
}
CtiTraceMsg& CtiTraceMsg::setTrace(const string& str)
{
    _trace = str;
    return *this;
}

CtiTraceMsg& CtiTraceMsg::setEnd(bool nd)
{
    _end = nd;
    return *this;
}

