
#pragma warning( disable : 4786)
#ifndef __MSG_MULTIWRAP_H__
#define __MSG_MULTIWRAP_H__

/*-----------------------------------------------------------------------------*
*
* File:   msg_multiwrap
*
* Class:  CtiMultiWrapper
* Date:   11/7/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_multiwrap.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:14 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "msg_multi.h"

class IM_EX_MSG CtiMultiWrapper
{
protected:

   CtiMultiMsg *_pMulti;

private:

public:
   CtiMultiWrapper(CtiMultiMsg *pMsg = NULL);
   CtiMultiWrapper(const CtiMultiWrapper& aRef);

   virtual ~CtiMultiWrapper();
   CtiMultiWrapper& operator=(const CtiMultiWrapper& aRef);

   CtiMultiMsg* getMulti();
   CtiMultiMsg* getMulti() const;

   CtiMultiMsg* releaseMulti();
   void freeMulti();

   bool isNotNull() const;
   size_t entries() const;

};
#endif // #ifndef __MSG_MULTIWRAP_H__
