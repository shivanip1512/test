#ifndef __MSG_MULTI_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_multi
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_multi.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __MSG_MULTI_H__

#include <rw/ordcltn.h>

#include "collectable.h"
#include "dlldefs.h"
#include "msg_pdata.h"       // get the base class
#include "message.h"       // get the base class


class IM_EX_MSG CtiMultiMsg : public CtiMessage
{
protected:

   RWOrdered  _bag;

public:
   RWDECLARE_COLLECTABLE( CtiMultiMsg );

   typedef CtiMessage Inherited;

  CtiMultiMsg(const RWOrdered &pointData = RWOrdered(), int Pri = 7);
  virtual ~CtiMultiMsg();
   CtiMultiMsg(const CtiMultiMsg &aRef);

   CtiMultiMsg& operator=(const CtiMultiMsg& aRef);
   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;
   void What() const;

   int  getCount() const;

   CtiPointDataMsg* operator[](size_t i);
   CtiPointDataMsg* operator[](size_t i) const;
   // Clear out the list.
   void clear();
   void insert(RWCollectable* a);
   const RWOrdered& getData() const;
   RWOrdered& getData();
   CtiMultiMsg& setData(const RWOrdered& point_data);


   virtual CtiMessage& setConnectionHandle(VOID *p);
   virtual VOID* getConnectionHandle();

   virtual void dump() const;

};

#endif   // #ifndef __MSG_MULTI_H__

