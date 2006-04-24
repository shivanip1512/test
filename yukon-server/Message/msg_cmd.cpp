/*-----------------------------------------------------------------------------*
*
* File:   msg_cmd
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_cmd.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2006/04/24 20:47:30 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\mutex.h>
#include <rw/collect.h>

#include "dllbase.h"
#include "collectable.h"
#include "dlldefs.h"
#include "msg_cmd.h"
#include "logger.h"

RWDEFINE_COLLECTABLE( CtiCommandMsg, MSG_COMMAND );

void
CtiCommandMsg::restoreGuts(RWvistream& aStream)
{
   int Count, i, iTemp;
   CtiMessage::restoreGuts( aStream );         // Base class is not really a RWCollectible, but could be.

   aStream >> iOperation >> iOpString >> Count;

   for(i = 0; i < Count; i++)
   {
      aStream >> iTemp;
      iOpArgList.push_back(iTemp);
   }
}

void
CtiCommandMsg::saveGuts(RWvostream &aStream) const
{
   CtiMessage::saveGuts( aStream );            // Base class is not really a RWCollectible, but could be.

   aStream << iOperation << iOpString << iOpArgList.size();

   for(std::vector<int>::const_iterator itr = iOpArgList.begin(); itr != iOpArgList.end(); ++itr)
   {
      aStream << *itr;
   }

}

void
CtiCommandMsg::What() const
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "CtiCommandMsg.... " << " iOperation " << getOperation() << endl;
   }
}

void CtiCommandMsg::dump() const
{
   Inherited::dump();

   CtiLockGuard<CtiLogger> doubt_guard(dout);
   dout << " Message Operation             " << iOperation << endl;
   dout << " Message String                " << iOpString << endl;

   if(iOpArgList.size())
   {
      dout << " Message Vector Size           " << iOpArgList.size() << endl;

      for(std::vector<int>::const_iterator itr = iOpArgList.begin(); itr != iOpArgList.end(); ++itr)
      {
         dout << " Message Vector                " << *itr << endl;
      }
   }
   else
   {
      dout << " Message Vector                EMPTY" << endl;
   }

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

std::vector<int>& CtiCommandMsg::insert(INT i)
{
   iOpArgList.push_back(i);
   return iOpArgList;
}

INT CtiCommandMsg::removeFirst()
{
   int temp = iOpArgList.front(); 
   iOpArgList.erase(0); 
   return temp;
}

void CtiCommandMsg::clear()
{
   iOpArgList.clear();
}

INT CtiCommandMsg::getOpArgument(INT i) const
{
    return iOpArgList[i];
}

string CtiCommandMsg::getOpString() const
{
   return iOpString;
}

string& CtiCommandMsg::getOpString()
{
   return iOpString;
}

CtiCommandMsg&    CtiCommandMsg::setOpString(const string &aRef)
{
   iOpString = aRef;
   return *this;
}

CtiCommandMsg& CtiCommandMsg::setOperation(const INT &aInt)
{
   iOperation = aInt;
   return *this;
}

CtiCommandMsg& CtiCommandMsg::setOpArgList(const CtiOpArgList_t &aRef)
{
   iOpArgList = aRef;
   return *this;
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

CtiCommandMsg::~CtiCommandMsg() {};


