#pragma once

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
