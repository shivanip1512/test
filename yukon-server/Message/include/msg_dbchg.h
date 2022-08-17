#pragma once

#include "message.h"
#include "dlldefs.h"
#include "yukon.h"

class IM_EX_MSG CtiDBChangeMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiDBChangeMsg )

private:

   LONG          _id;                 // ID of the entity affected i.e. pao id, point id, state group id, etc
   INT           _database;           // What area of the database?  i.e. pao, point, state group, etc
   std::string     _category;           // Object was what pao category?
   std::string     _objecttype;         // Object was what pao/point type?
   INT           _typeofchange;       // What type of change: update, delete, etc?

public:
   CtiDBChangeMsg();

   typedef CtiMessage Inherited;

   CtiDBChangeMsg(LONG id,INT database, std::string category, std::string objecttype, INT typeofchange);
   CtiDBChangeMsg(const CtiMessage& baseMessage, LONG id, INT database, std::string category, std::string objecttype, INT typeofchange);
   CtiDBChangeMsg(const CtiDBChangeMsg& aRef);
   virtual ~CtiDBChangeMsg();

   CtiDBChangeMsg& operator=(const CtiDBChangeMsg& aRef);
   LONG         getId() const;
   INT          getDatabase() const;
   std::string  getCategory() const;
   std::string  getObjectType() const;
   INT          getTypeOfChange() const;

   CtiMessage* replicateMessage() const override;

   virtual std::string toString() const override;
};
