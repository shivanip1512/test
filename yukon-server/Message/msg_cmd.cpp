#include "precompiled.h"

#include "msg_cmd.h"

#include "dllbase.h"
#include "dlldefs.h"
#include "logger.h"



DEFINE_COLLECTABLE( CtiCommandMsg, MSG_COMMAND );

std::string CtiCommandMsg::toString() const
{
   Cti::FormattedList itemList;

   itemList <<"CtiCommandMsg";
   itemList.add("Message Operation") << iOperation;
   itemList.add("Message String")    << iOpString;

   if(iOpArgList.size())
   {
       itemList.add("Message Vector Size") << iOpArgList.size();

       for(std::vector<int>::const_iterator itr = iOpArgList.begin(); itr != iOpArgList.end(); ++itr)
       {
           itemList.add("Message Vector") << *itr;
       }
   }
   else
   {
       itemList.add("Message Vector Size") <<"EMPTY";
   }

   return (Inherited::toString() += itemList.toString());
}

// Return a new'ed copy of this message!
CtiMessage* CtiCommandMsg::replicateMessage() const
{
   CtiCommandMsg *ret = CTIDBG_new CtiCommandMsg(*this);

   return( (CtiMessage*)ret );
}



INT CtiCommandMsg::getOperation() const
{
   return iOperation;
}


CtiCommandMsg&    CtiCommandMsg::operator=(const CtiCommandMsg& aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      iOperation = aRef.getOperation();
      setOpArgList(aRef.getOpArgList());     // need the const'ness of the setter to make this work.
      iOpString  = aRef.getOpString();
   }

   return *this;
}

std::vector<int>& CtiCommandMsg::getOpArgList()
{
   return iOpArgList;
}

std::vector<int> CtiCommandMsg::getOpArgList() const
{
   return iOpArgList;
}

void CtiCommandMsg::insert(int i)
{
   iOpArgList.push_back(i);
}

std::string CtiCommandMsg::getOpString() const
{
   return iOpString;
}

void CtiCommandMsg::setOpString(const std::string &aRef)
{
   iOpString = aRef;
}

void CtiCommandMsg::setOperation(const INT &aInt)
{
   iOperation = aInt;
}

void CtiCommandMsg::setOpArgList(const OpArgList &aRef)
{
   iOpArgList = aRef;
}

void CtiCommandMsg::setOpArgList(OpArgList&& aRef)
{
   iOpArgList = aRef;
}

CtiCommandMsg::CtiCommandMsg(int Op, int Pri) :
   CtiMessage(Pri),
   iOpArgList(),
   iOperation(Op)
{}

CtiCommandMsg::CtiCommandMsg(const CtiCommandMsg &aRef, int Pri)
{
   *this = aRef;
}

std::size_t CtiCommandMsg::getVariableSize() const
{
    return  Inherited::getVariableSize()
        +   dynamic_sizeof( iOpString )
        +   iOpArgList.capacity() * sizeof( int );
}

