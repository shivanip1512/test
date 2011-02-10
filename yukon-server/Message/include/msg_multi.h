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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/02/17 17:04:33 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __MSG_MULTI_H__

#include "collectable.h"
#include "dlldefs.h"
#include "msg_pdata.h"       // get the base class
#include "message.h"       // get the base class


class IM_EX_MSG CtiMultiMsg : public CtiMessage
{
protected:

   CtiMultiMsg_vec  _bag;

public:
   RWDECLARE_COLLECTABLE( CtiMultiMsg );

   typedef CtiMessage Inherited;

   CtiMultiMsg(CtiMultiMsg_vec &pointData = CtiMultiMsg_vec(), int Pri = 7);
   virtual ~CtiMultiMsg();
   CtiMultiMsg(const CtiMultiMsg &aRef);

   CtiMultiMsg& operator=(const CtiMultiMsg& aRef);
   virtual void saveGuts(RWvostream &aStream) const;
   virtual void restoreGuts(RWvistream& aStream);
   virtual CtiMessage* replicateMessage() const;

   int  getCount() const;

   CtiPointDataMsg* operator[](size_t i);
   CtiPointDataMsg* operator[](size_t i) const;
   // Clear out the list.
   void clear();
   void insert(RWCollectable* a);
   const CtiMultiMsg_vec& getData() const;
   CtiMultiMsg_vec& getData();
   CtiMultiMsg& setData(const CtiMultiMsg_vec& point_data);


   virtual CtiMessage& setConnectionHandle(VOID *p);
   virtual VOID* getConnectionHandle();

   virtual void dump() const;

};

#endif   // #ifndef __MSG_MULTI_H__

