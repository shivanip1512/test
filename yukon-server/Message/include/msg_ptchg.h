#ifndef __MSG_PTCHG_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_ptchg
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __MSG_PTCHG_H__

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
};

#endif   // #ifndef __MSG_PTCHG_H__

