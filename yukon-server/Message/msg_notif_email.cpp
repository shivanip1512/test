#include "precompiled.h"

#include "msg_notif_email.h"
#include "utility.h"

using namespace std;

DEFINE_COLLECTABLE( CtiNotifEmailMsg, NOTIF_EMAIL_MSG_ID );

//=====================================================================================================================
//=====================================================================================================================

CtiNotifEmailMsg::CtiNotifEmailMsg() :
_notifGroupID(0)
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiNotifEmailMsg::~CtiNotifEmailMsg()
{
}

//=====================================================================================================================
//=====================================================================================================================

std::string CtiNotifEmailMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList << "CtiNotifEmailMsg";
    itemList.add("Notification Group ID") << _notifGroupID;
    itemList.add("To")                    << _to;
    itemList.add("Subject")               << _subject;
    itemList.add("CC")                    << _toCC;
    itemList.add("BCC")                   << _toBCC;

    return (Inherited::toString() += itemList.toString());
}

//=====================================================================================================================
//=====================================================================================================================

int CtiNotifEmailMsg::getNotifGroupId() const
{
   return( _notifGroupID );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setNotifGroupId( int id )
{
   _notifGroupID = id;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiNotifEmailMsg::getTo() const
{
   return( _to );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setTo( string to )
{
   _to = to;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiNotifEmailMsg::getSubject() const
{
   return( _subject );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setSubject( string sub )
{
   _subject = sub;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiNotifEmailMsg::getBody() const
{
   return( _body );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setBody( string body )
{
   _body = body;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiNotifEmailMsg::getToCC() const
{
   return( _toCC );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setToCC( string toCC )
{
   _toCC = toCC;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiNotifEmailMsg::getToBCC() const
{
   return( _toBCC );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailMsg::setToBCC( string toBCC )
{
   _toBCC = toBCC;
}

//=====================================================================================================================
// Return a new'ed copy of this message!
//=====================================================================================================================

CtiMessage* CtiNotifEmailMsg::replicateMessage() const
{
   CtiNotifEmailMsg *ret = CTIDBG_new CtiNotifEmailMsg( *this );

   return( ( CtiMessage*)ret );
}

/* Start of deprecated email msg*/

DEFINE_COLLECTABLE( CtiCustomerNotifEmailMsg, NOTIF_CUST_EMAIL_MSG_ID );


//=====================================================================================================================
//=====================================================================================================================

CtiCustomerNotifEmailMsg::CtiCustomerNotifEmailMsg() :
_customerID(0)
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiCustomerNotifEmailMsg::~CtiCustomerNotifEmailMsg()
{
}

//=====================================================================================================================
//=====================================================================================================================

std::string CtiCustomerNotifEmailMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"-- Customer Notification Email --";
    itemList.add("Customer ID") << _customerID;
    itemList.add("To")          << _to;
    itemList.add("Subject")     << _subject;
    itemList.add("CC")          << _toCC;
    itemList.add("BCC")         << _toBCC;

    return (Inherited::toString() += itemList.toString());
}

//=====================================================================================================================
//=====================================================================================================================

int CtiCustomerNotifEmailMsg::getCustomerId() const
{
   return( _customerID );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setCustomerId( int id )
{
   _customerID = id;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiCustomerNotifEmailMsg::getTo() const
{
   return( _to );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setTo( string to )
{
   _to = to;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiCustomerNotifEmailMsg::getSubject() const
{
   return( _subject );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setSubject( string sub )
{
   _subject = sub;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiCustomerNotifEmailMsg::getBody() const
{
   return( _body );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setBody( string body )
{
   _body = body;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiCustomerNotifEmailMsg::getToCC() const
{
   return( _toCC );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setToCC( string toCC )
{
   _toCC = toCC;
}

//=====================================================================================================================
//=====================================================================================================================

string CtiCustomerNotifEmailMsg::getToBCC() const
{
   return( _toBCC );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiCustomerNotifEmailMsg::setToBCC( string toBCC )
{
   _toBCC = toBCC;
}

//=====================================================================================================================
// Return a new'ed copy of this message!
//=====================================================================================================================

CtiMessage* CtiCustomerNotifEmailMsg::replicateMessage() const
{
   CtiCustomerNotifEmailMsg *ret = CTIDBG_new CtiCustomerNotifEmailMsg( *this );

   return( ( CtiMessage*)ret );
}



