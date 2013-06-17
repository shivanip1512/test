#pragma once

#include "message.h"
#include "dlldefs.h"
#include "yukon.h"

class IM_EX_MSG CtiDBChangeMsg : public CtiMessage
{
protected:

   LONG          _id;                 // ID of the entity affected i.e. pao id, point id, state group id, etc
   INT           _database;           // What area of the database?  i.e. pao, point, state group, etc
   std::string     _category;           // Object was what pao category?
   std::string     _objecttype;         // Object was what pao/point type?
   INT           _typeofchange;       // What type of change: update, delete, etc?

private:

    CtiDBChangeMsg();

public:

   typedef CtiMessage Inherited;

   RWDECLARE_COLLECTABLE( CtiDBChangeMsg );

   CtiDBChangeMsg(LONG id,INT database, std::string category, std::string objecttype, INT typeofchange);
   CtiDBChangeMsg(const CtiDBChangeMsg& aRef);
   virtual ~CtiDBChangeMsg();

   CtiDBChangeMsg& operator=(const CtiDBChangeMsg& aRef);
   LONG         getId() const;
   INT          getDatabase() const;
   std::string  getCategory() const;
   std::string  getObjectType() const;
   INT          getTypeOfChange() const;

   void saveGuts(RWvostream &aStream) const;
   void restoreGuts(RWvistream& aStream);
   CtiMessage* replicateMessage() const;

   virtual void dump() const;
};
