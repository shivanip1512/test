#include "precompiled.h"

#include "collectable.h"
#include "msg_dbchg.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"

using namespace std;

DEFINE_COLLECTABLE( CtiDBChangeMsg, MSG_DBCHANGE );

// Return a new'ed copy of this message!
CtiMessage* CtiDBChangeMsg::replicateMessage() const
{
   CtiDBChangeMsg *ret = CTIDBG_new CtiDBChangeMsg(*this);

   return( (CtiMessage*)ret );
}

std::string CtiDBChangeMsg::toString() const
{
   Cti::FormattedList itemList;

   itemList <<"CtiDBChangeMsg";
   itemList.add("Object Id")       << _id;
   itemList.add("Object Database") << _database;
   itemList.add("Object Category") << _category;
   itemList.add("Object Type")     << _objecttype;
   itemList.add("Type of Change")  << _typeofchange;

   return (Inherited::toString() += itemList.toString());
}

CtiDBChangeMsg::CtiDBChangeMsg(LONG id,INT database, string category, string objecttype, INT typeofchange) :
   _id(id),
   _database(database),
   _category(category),
   _objecttype(objecttype),
   _typeofchange(typeofchange),
   CtiMessage(15)
{}

// constructor provided for deserialization
CtiDBChangeMsg::CtiDBChangeMsg(const CtiMessage& baseMessage, LONG id, INT database, string category, string objecttype, INT typeofchange) :
   _id(id),
   _database(database),
   _category(category),
   _objecttype(objecttype),
   _typeofchange(typeofchange)
{
    static_cast<CtiMessage&>(*this) = baseMessage;
}


// Default Constructor needed for Roque Wave !!!!!!!!!!NEVER USE!!!!!!!!!!!
CtiDBChangeMsg::CtiDBChangeMsg()
{}
// Default Constructor needed for Roque Wave !!!!!!!!!!NEVER USE!!!!!!!!!!!

CtiDBChangeMsg::CtiDBChangeMsg(const CtiDBChangeMsg& aRef)
{
   *this = aRef;
}

CtiDBChangeMsg::~CtiDBChangeMsg() {}

CtiDBChangeMsg& CtiDBChangeMsg::operator=(const CtiDBChangeMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _id             = aRef.getId();
      _database       = aRef.getDatabase();
      _category       = aRef.getCategory();
      _objecttype     = aRef.getObjectType();
      _typeofchange   = aRef.getTypeOfChange();
   }
   return *this;
}

LONG         CtiDBChangeMsg::getId() const              { return _id; }
INT          CtiDBChangeMsg::getDatabase() const        { return _database; }
string    CtiDBChangeMsg::getCategory() const        { return _category; }
string    CtiDBChangeMsg::getObjectType() const      { return _objecttype; }
INT          CtiDBChangeMsg::getTypeOfChange() const    { return _typeofchange; }

std::size_t CtiDBChangeMsg::getVariableSize() const
{
    return  Inherited::getVariableSize()
        +   dynamic_sizeof( _category )
        +   dynamic_sizeof( _objecttype );
}

