#include "yukon.h"
#include "logger.h"

/*-----------------------------------------------------------------------------*
*
* File:   msg_multiwrap
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_multiwrap.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
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


