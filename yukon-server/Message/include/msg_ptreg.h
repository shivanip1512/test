#pragma once

#include "dlldefs.h"
#include "message.h"       // get the base class

#define REG_NOTHING           0x00000000
#define REG_ALL_POINTS        0x00000001     // This message registers for all points
#define REG_EVENTS            0x00000010     // I wish to hear about events.
#define REG_ALARMS            0x00000020     // I wish to hear about alarms.

#define REG_ADD_POINTS        0x00000100     // This registration is intended to add points to an existing registration.
#define REG_REMOVE_POINTS     0x00000200     // This registration is intended to remove specified points from an existing registration.

#define REG_NO_UPLOAD         0x00010000     // Dispatch will not do an intial registration upload if set.
#define REG_TAG_UPLOAD        0x00020000     // Dispatch will mark each point in the registration upload with TAG_POINT_REG_UPLOAD


class IM_EX_MSG CtiPointRegistrationMsg final : public CtiMessage
{
public:
    DECLARE_COLLECTABLE( CtiPointRegistrationMsg )

private:

   int                     RegFlags;
   std::vector<LONG>         PointList;

public:

   typedef CtiMessage Inherited;

   CtiPointRegistrationMsg(int flags);
   CtiPointRegistrationMsg();

   CtiMessage* replicateMessage() const override;

   size_t getCount() const;

   void insert(const long ptId);
   int getFlags() const;
   void setFlags(int flags);

   bool isRequestingEvents() const;
   bool isRequestingAlarms() const;
   bool isDecliningUpload()  const;
   bool isRequestingAllPoints() const;
   bool isAddingPoints()     const;
   bool isRemovingPoints()   const;
   bool isRequestingUploadTag() const;

   const std::vector<LONG>& getPointList() const;
   void setPointList( const std::vector<LONG>& points );

   virtual std::string toString() const override;
};
