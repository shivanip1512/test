#include "logger.h"
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_multiwrap
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_multiwrap.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/11/15 14:07:56 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "msg_multiwrap.h"

CtiMultiWrapper::~CtiMultiWrapper()
{
   freeMulti();
}

CtiMultiWrapper& CtiMultiWrapper::operator=(const CtiMultiWrapper& aRef)
{
   if(this != &aRef)
   {
      if(aRef.getMulti() != NULL)
      {
         _pMulti = (CtiMultiMsg*) aRef.getMulti()->replicateMessage();
      }
   }
   return *this;
}

CtiMultiMsg* CtiMultiWrapper::getMulti()
{
   try
   {
      if(_pMulti == NULL)
      {
         _pMulti = CTIDBG_new CtiMultiMsg;
      }
   }
   catch(...)
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   return _pMulti;
}

CtiMultiMsg* CtiMultiWrapper::getMulti() const
{
   return _pMulti;
}

CtiMultiMsg* CtiMultiWrapper::releaseMulti()
{
   CtiMultiMsg* pTemp = _pMulti;
   _pMulti = NULL;
   return pTemp;
}

void CtiMultiWrapper::freeMulti()
{
   if(_pMulti != NULL)
   {
      delete _pMulti;
      _pMulti = NULL;
   }
}

bool CtiMultiWrapper::isNotNull() const
{
   return (_pMulti != NULL);
}

size_t CtiMultiWrapper::entries() const
{
   size_t cnt = 0;

   if(_pMulti != NULL)
   {
      cnt = _pMulti->getCount();
   }

   return cnt;
}

CtiMultiWrapper::CtiMultiWrapper(CtiMultiMsg *pMsg) :
   _pMulti(pMsg)
{}

CtiMultiWrapper::CtiMultiWrapper(const CtiMultiWrapper& aRef)
{
   *this = aRef;
}


