#pragma once

#include "collectable.h"
#include "dlldefs.h"
#include "msg_pdata.h"       // get the base class
#include "message.h"       // get the base class

class IM_EX_MSG CtiMultiMsg : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiMultiMsg )

public:
   CtiMultiMsg_vec  _bag;

public:

   typedef CtiMessage Inherited;

   CtiMultiMsg(CtiMultiMsg_vec &pointData = CtiMultiMsg_vec(), int Pri = 7);
   virtual ~CtiMultiMsg();
   CtiMultiMsg(const CtiMultiMsg &aRef);

   CtiMultiMsg& operator=(const CtiMultiMsg& aRef);

   CtiMessage* replicateMessage() const override;

   int  getCount() const;

   CtiPointDataMsg* operator[](size_t i);
   CtiPointDataMsg* operator[](size_t i) const;
   // Clear out the list.
   void clear();
   void insert(CtiMessage* a);
   const CtiMultiMsg_vec& getData() const;
   CtiMultiMsg_vec& getData();
   CtiMultiMsg& setData(const CtiMultiMsg_vec& point_data);

   void setConnectionHandle(const Cti::ConnectionHandle handle) override;

   virtual std::string toString() const override;

};
