
#pragma warning( disable : 4786)
#ifndef __TBL_CONTACT_NOTIFICATION__
#define __TBL_CONTACT_NOTIFICATION__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_contact_notification
*
* Class:  CtiTableContactNotification
* Date:   02/05/2003
*
* Author: Corey G. Plender
*         Aaron Lauinger
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_contact_notification.h-arc  $
* REVISION     :  $Revision: 1.2.24.1 $
* DATE         :  $Date: 2008/11/18 20:11:29 $
*
* Copyright (c) 1999-2003 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/thr/recursiv.h>
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableContactNotification
{
protected:

  LONG _contactNotifID;
  LONG _contactID;
  LONG _notificationCategoryID;
  bool _disabled;
  std::string _notification; // email address would appear hear
  bool _dirty;

private:
    mutable CtiMutex _classMutex;
public:

  CtiTableContactNotification(LONG id=0);
  CtiTableContactNotification(const CtiTableContactNotification& aRef);
  virtual ~CtiTableContactNotification();

  CtiTableContactNotification& operator=(const CtiTableContactNotification& aRef);

  bool operator<( const CtiTableContactNotification &rhs ) const;
  bool operator==( const CtiTableContactNotification &rhs ) const;
  bool operator()(const CtiTableContactNotification& aRef) const;

  LONG getContactNotificationID() const;
  CtiTableContactNotification& setContactNotificationID(LONG id);

  LONG getContactID() const;
  CtiTableContactNotification& setContactID(LONG id);

  LONG getNotificationCategoryID() const;
  CtiTableContactNotification& setNotificationCategoryID(LONG id);

  BOOL isDisabled() const;
  CtiTableContactNotification& setDisabled(BOOL disabled);

  const std::string& getNotification() const;
  CtiTableContactNotification& setNotification(const std::string& notif);

  CtiTableContactNotification& setDirty( bool dirt );
  bool isDirty() const;

  void dump() const;

  static std::string getTableName();
  virtual bool Restore();

  virtual void DecodeDatabaseReader(Cti::RowReader& rdr);

};
#endif // #ifndef __TBL_CONTACT_NOTIFICATION__
