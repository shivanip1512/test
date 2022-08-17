#include "precompiled.h"

#include "collectable.h"
#include "msg_multi.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"
#include "utility.h"

using namespace std;

using boost::shared_ptr;

DEFINE_COLLECTABLE( CtiMultiMsg, MSG_MULTI );

std::string CtiMultiMsg::toString() const
{
   Cti::FormattedList itemList;

   itemList <<"CtiMultiMsg";
   for(int i = 0; i < _bag.size(); i++)
   {
       itemList <<"Message "<< i;
       itemList << *((CtiMessage*)_bag[i]);
   }

   return (Inherited::toString() += itemList.toString());
}

CtiMultiMsg&  CtiMultiMsg::setData(const CtiMultiMsg_vec& Data)
{
   for(int i = 0; i < Data.size(); i++)
   {
       unique_ptr<CtiMessage> pNew( Data[i]->replicateMessage() ); // CBM TODO: check if there a reason we should use replicate message?

       if( pNew.get() != NULL )
       {
          _bag.push_back( pNew.release() );
       }
       else
       {
          CTILOG_ERROR(dout, "setPointData failed to copy an element of type "<< Data[i]->isA() );
       }
   }

   return *this;
}

// Return a new'ed copy of this message!
CtiMessage* CtiMultiMsg::replicateMessage() const
{
   CtiMultiMsg *ret = CTIDBG_new CtiMultiMsg(*this);

   return( (CtiMessage*)ret );
}

void CtiMultiMsg::setConnectionHandle(const Cti::ConnectionHandle handle)
{
    Inherited::setConnectionHandle(handle);

    for( auto msg : _bag )
    {
        msg->setConnectionHandle(handle);
    }
}

CtiMultiMsg::CtiMultiMsg(CtiMultiMsg_vec& pointData, int Pri) :
   CtiMessage(Pri)
{
    CtiMultiMsg_vec::iterator itr;
    itr = pointData.begin();
    for(;itr != pointData.end(); itr++ )
        _bag.push_back(*itr);
}

CtiMultiMsg::~CtiMultiMsg()
{
   delete_container(_bag);
   _bag.clear();    // Clean up any leftovers.
}

CtiMultiMsg::CtiMultiMsg(const CtiMultiMsg &aRef)
{
   *this = aRef;
}


CtiMultiMsg& CtiMultiMsg::operator=(const CtiMultiMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
      delete_container(_bag);
      _bag.clear();     // Make sure it is empty!

      for(int i = 0; i < aRef.getCount(); i++)
      {
         // This guy creates a copy of himself and returns a CtiMessage pointer to the copy!
         CtiMessage* newp = aRef[i]->replicateMessage();
         _bag.push_back(newp);
      }
   }

   return *this;
}

int CtiMultiMsg::getCount() const       { return _bag.size(); }

CtiPointDataMsg* CtiMultiMsg::operator[](size_t i)
{
   return (CtiPointDataMsg*)_bag[i];
}

CtiPointDataMsg* CtiMultiMsg::operator[](size_t i) const
{
   return (CtiPointDataMsg*)_bag[i];
}

// Clear out the list.
void CtiMultiMsg::clear()
{
    delete_container(_bag);
   _bag.clear();
}

void CtiMultiMsg::insert(CtiMessage* a)
{
   _bag.push_back(a);
}

const CtiMultiMsg_vec& CtiMultiMsg::getData() const     { return _bag; }
CtiMultiMsg_vec& CtiMultiMsg::getData()                 { return _bag; }

