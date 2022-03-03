#pragma once

#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiTagMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE(CtiTagMsg)

public:
   typedef enum
     {
       AddAction,
       RemoveAction,
       UpdateAction,
       ReportAction

     } CtiTagAction;

protected:

   int _instanceid;        // no two tags share the same one
   int _pointid;
   int _tagid;             // refers to id in tag table
   std::string _descriptionStr;
   int _action;    // one of CtiTagAction
   CtiTime _tagtime;         // when was tag created
   std::string _referenceStr; // job id, etc, user field
   std::string _taggedForStr; // user field


   int _clientMsgId;        // id sourced and returned to clients.  Untouched and unused by dispatch.

public:

   typedef CtiMessage Inherited;

   CtiTagMsg();
   CtiTagMsg(const CtiTagMsg& aRef);
   virtual ~CtiTagMsg();

   CtiTagMsg& operator=(const CtiTagMsg& aRef);

   int getInstanceID() const;
   CtiTagMsg& setInstanceID(int id);

   int getPointID() const;
   CtiTagMsg& setPointID(int id);

   int getTagID() const;
   CtiTagMsg& setTagID(int id);

   const std::string& getDescriptionStr() const;
   CtiTagMsg& setDescriptionStr(const std::string& desc);

   int getAction() const;
   CtiTagMsg& setAction(int action);

   const CtiTime& getTagTime() const;
   CtiTagMsg& setTagTime(const CtiTime& tagtime);

   const std::string& getReferenceStr() const;
   CtiTagMsg& setReferenceStr(const std::string& refStr);

   const std::string& getTaggedForStr() const;
   CtiTagMsg& setTaggedForStr(const std::string& forStr);

   int getClientMsgId() const;
   CtiTagMsg& setClientMsgId(int id);

   virtual CtiMessage* replicateMessage() const;

   std::size_t getFixedSize() const override    { return sizeof( *this ); }
   std::size_t getVariableSize() const override;

   std::string toString() const override;
};
