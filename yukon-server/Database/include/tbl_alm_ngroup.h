#pragma once

#include "dlldefs.h"
#include "yukon.h"
#include "row_reader.h"
#include "loggable.h"
#include "mutex.h"

#include <set>


class IM_EX_CTIYUKONDB CtiTableNotificationGroup : public Cti::Loggable
{
   LONG                 _notificationGroupID;
   std::string          _groupName;
   BOOL                 _groupDisabled;
   bool                 _isDirty;

    mutable CtiMutex _classMutex;

public:

   CtiTableNotificationGroup( LONG gid = 0L);
   CtiTableNotificationGroup(const CtiTableNotificationGroup& aRef);
   virtual ~CtiTableNotificationGroup();

   CtiTableNotificationGroup& operator=(const CtiTableNotificationGroup& aRef);
   bool operator<( const CtiTableNotificationGroup &rhs ) const;
   bool operator==( const CtiTableNotificationGroup &rhs ) const;
   bool operator()(const CtiTableNotificationGroup& aRef) const;

   size_t   entries() const;

   LONG getGroupID() const;
   std::string getGroupName() const;
   BOOL isDisabled() const;
   std::vector<int> getRecipientVector();


   CtiTableNotificationGroup& setGroupID( const LONG &aRef );
   CtiTableNotificationGroup& setGroupName( const std::string &aStr );
   CtiTableNotificationGroup& setDisabled( const BOOL b = TRUE );
   CtiTableNotificationGroup& setDirty( bool dirt );

   bool isDirty() const;

   virtual std::string toString() const override;

   static std::string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

};
