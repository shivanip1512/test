
#pragma warning( disable : 4786)
#ifndef __TBL_ALM_NGROUP_H__
#define __TBL_ALM_NGROUP_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ngroup
*
* Class:  CtiTableNotificationGroup
* Date:   10/11/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_ngroup.h-arc  $
* REVISION     :  $Revision: 1.5.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <set>

#include <rw/thr/recursiv.h>

#include "dlldefs.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableNotificationGroup
{
protected:

   LONG                 _notificationGroupID;
   std::string          _groupName;
   BOOL                 _groupDisabled;
   bool                 _isDirty;


private:
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

   void dump() const;

   static std::string getTableName();
   virtual bool Restore();

   virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

};
#endif // #ifndef __TBL_ALM_NGROUP_H__
