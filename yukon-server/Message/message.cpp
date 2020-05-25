#include "precompiled.h"

#include "message.h"
#include "logger.h"

using std::string;
using std::endl;

using boost::shared_ptr;

std::size_t calculateMemoryConsumption( const CtiMessage * m )
{
    return  m->getFixedSize()
        +   m->getVariableSize();
}

#define DEFAULT_SYSTEM_USER "(yukon system)"
DEFINE_COLLECTABLE( CtiMessage, MSG_DEFAULT );

CtiMessage* CtiMessage::replicateMessage() const
{
   return (CTIDBG_new CtiMessage(*this));
}

std::string CtiMessage::toString() const
{
    Cti::FormattedList itemList;

    itemList << formatMessageName(typeString());
    itemList <<"CtiMessage";
    itemList.add("Time")     << MessageTime;
    itemList.add("Priority") << MessagePriority;
    itemList.add("SOE")      << _soe;
    itemList.add("User")     << _usr;
    itemList.add("Source")   << _src;

    return itemList.toString();
}

INT  CtiMessage::getSOE() const
{
   return _soe;
}

CtiMessage& CtiMessage::setSOE( const INT & soe )
{
   _soe = soe;
   return *this;
}


const string& CtiMessage::getUser() const
{
   return _usr;
}
CtiMessage& CtiMessage::setUser(const string& usr)
{
   _usr  = usr;
   return *this;
}

CtiMessage::CtiMessage(int Pri) :
   MessagePriority(Pri & 0x0000000f),
   _soe(0),
   _usr(DEFAULT_SYSTEM_USER),
   _src("")
{}

CtiMessage::CtiMessage(const CtiMessage& aRef) :
   MessagePriority(aRef.getMessagePriority()),
   _soe(0),
   _usr(DEFAULT_SYSTEM_USER),
   _src("")
{}

CtiMessage& CtiMessage::operator=(const CtiMessage& aRef)
{
   if(this != &aRef)
   {
      MessageTime       = aRef.getMessageTime();
      MessagePriority   = aRef.getMessagePriority();
      _soe              = aRef.getSOE();
      _usr              = aRef.getUser();
      _src              = aRef.getSource();
      //  Note that _connectionHandle is NOT copied
   }

   return *this;
}

bool CtiMessage::operator==(const CtiMessage &aRef) const
{
   return (this == &aRef);
}

bool CtiMessage::operator<(const CtiMessage& aRef) const
{
    // Higher priority is "less".  Sorts ahead of lower priority.
    return MessagePriority < aRef.getMessagePriority();
}

bool CtiMessage::operator>(const CtiMessage& aRef) const
{
    // Lower priority sorts behind greater.  Lower == is "more" in the lists.
    return MessagePriority > aRef.getMessagePriority();
}

void CtiMessage::setConnectionHandle(const Cti::ConnectionHandle newHandle)
{
   _connectionHandle = newHandle;
}

Cti::ConnectionHandle CtiMessage::getConnectionHandle() const
{
   return _connectionHandle;
}

void CtiMessage::setMessagePriority(INT n)   { MessagePriority = n & 0x0000000f; }
INT  CtiMessage::getMessagePriority() const  { return MessagePriority;           }


CtiTime CtiMessage::getMessageTime() const    { return MessageTime;}
CtiMessage&  CtiMessage::setMessageTime(const CtiTime &mTime)
{
   MessageTime = mTime;
   return *this;
}

// Adjust the time to now.
void CtiMessage::resetTime()
{
    MessageTime = MessageTime.now();
}

const string& CtiMessage::getSource() const
{
    return _src;
}

CtiMessage& CtiMessage::setSource(const string& src)
{
    _src = src;
    return *this;
}

bool CtiMessage::isValid()
{
    return true;
}

string CtiMessage::typeString() const
{
    string rstr;

    rstr.swap(boost::lexical_cast<string>(isA()));

    /*
    #define MSG_BASE                          1500
    #define MSG_NULL                          (MSG_BASE)
    #define MSG_DEFAULT                       ((MSG_BASE) + 5 )
    #define MSG_TRACE                         ((MSG_BASE) + 20)
    #define MSG_COMMAND                       ((MSG_BASE) + 30)
    #define MSG_REGISTER                      ((MSG_BASE) + 40)
    #define MSG_SERVER_REQUEST                ((MSG_BASE) + 50)
    #define MSG_SERVER_RESPONSE               ((MSG_BASE) + 51)
    #define MSG_POINTREGISTRATION             ((MSG_BASE) + 70)
    #define MSG_DBCHANGE                      ((MSG_BASE) + 80)
    #define MSG_PCREQUEST                     ((MSG_BASE) + 85)
    #define MSG_PCRETURN                      ((MSG_BASE) + 90)

    #define MSG_MULTI                         ((MSG_BASE) + 91)
    #define MSG_TAG                           ((MSG_BASE) + 94)
    #define MSG_POINTDATA                     ((MSG_BASE) + 95)
    #define MSG_SIGNAL                        ((MSG_BASE) + 96)
    #define MSG_EMAIL                         ((MSG_BASE) + 97)
    #define MSG_LMCONTROLHISTORY              ((MSG_BASE) + 98)
    */

    switch(isA())
    {
    case MSG_BASE:
        {
            rstr += ": MSG_BASE";
            break;
        }
    case MSG_DEFAULT:
        {
            rstr += ": MSG_DEFAULT";
            break;
        }
    case MSG_TRACE:
        {
            rstr += ": MSG_TRACE";
            break;
        }
    case MSG_COMMAND:
        {
            rstr += ": MSG_COMMAND";
            break;
        }
    case MSG_REGISTER:
        {
            rstr += ": MSG_REGISTER";
            break;
        }
    case MSG_SERVER_REQUEST:
        {
            rstr += ": MSG_SERVER_REQUEST";
            break;
        }
    case MSG_SERVER_RESPONSE:
        {
            rstr += ": MSG_SERVER_RESPONSE";
            break;
        }
    case MSG_POINTREGISTRATION:
        {
            rstr += ": MSG_POINTREGISTRATION";
            break;
        }
    case MSG_DBCHANGE:
        {
            rstr += ": MSG_DBCHANGE";
            break;
        }
    case MSG_PCREQUEST:
        {
            rstr += ": MSG_PCREQUEST";
            break;
        }
    case MSG_PCRETURN:
        {
            rstr += ": MSG_PCRETURN";
            break;
        }
    case MSG_MULTI:
        {
            rstr += ": MSG_MULTI";
            break;
        }
    case MSG_TAG:
        {
            rstr += ": MSG_TAG";
            break;
        }
    case MSG_POINTDATA:
        {
            rstr += ": MSG_POINTDATA";
            break;
        }
    case MSG_SIGNAL:
        {
            rstr += ": MSG_SIGNAL";
            break;
        }

    case MSG_LMCONTROLHISTORY:
        {
            rstr += ": MSG_LMCONTROLHISTORY";
            break;
        }
    }

    return rstr;
}

std::string CtiMessage::formatMessageName(const std::string &name)
{
    std::string formattedName = "------- Message ------- ";
    
    formattedName += name;
    
    return formattedName;
}

std::size_t CtiMessage::getVariableSize() const
{
    return dynamic_sizeof( _usr )
        +  dynamic_sizeof( _src );
}

