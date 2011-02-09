#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   msg_dbchg
*
* Date:   10/10/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/INCLUDE/msg_dbchg.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:18:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __MSG_DBCHG_H__
#define __MSG_DBCHG_H__

#include "message.h"
#include "dlldefs.h"
#include "yukon.h"

class IM_EX_MSG CtiDBChangeMsg : public CtiMessage
{
protected:

   LONG          _id;                 // ID of the entity affected i.e. pao id, point id, state group id, etc
   INT           _database;           // What area of the database?  i.e. pao, point, state group, etc
   string     _category;           // Object was what pao category?
   string     _objecttype;         // Object was what pao/point type?
   INT           _typeofchange;       // What type of change: update, delete, etc?

private:

    CtiDBChangeMsg();

public:

   typedef CtiMessage Inherited;

   RWDECLARE_COLLECTABLE( CtiDBChangeMsg );

   CtiDBChangeMsg(LONG id,INT database, string category, string objecttype, INT typeofchange);
   CtiDBChangeMsg(const CtiDBChangeMsg& aRef);
   virtual ~CtiDBChangeMsg();

   CtiDBChangeMsg& operator=(const CtiDBChangeMsg& aRef);
   LONG         getId() const;
   INT          getDatabase() const;
   string    getCategory() const;
   string    getObjectType() const;
   INT          getTypeOfChange() const;

   void saveGuts(RWvostream &aStream) const;
   void restoreGuts(RWvistream& aStream);
   CtiMessage* replicateMessage() const;

   virtual void dump() const;
};
#endif // #ifndef __MSG_DBCHG_H__
