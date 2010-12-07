

#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceTapPaging : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   string      _pagerNumber;                    // a.k.a. CAPCODE

   string    _senderID;
   string    _securityCode;
   string    _postPath;

public:

   CtiTableDeviceTapPaging(string pn = string());

   CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef);

   virtual ~CtiTableDeviceTapPaging();

   CtiTableDeviceTapPaging& operator=(const CtiTableDeviceTapPaging& aRef);

   string                  getPagerNumber() const;
   string&                 getPagerNumber();
   CtiTableDeviceTapPaging&   setPagerNumber(const string &aStr);

   string                  getSenderID() const;
   string                  getSecurityCode() const;
   string                  getPOSTPath() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceTapPaging& setDeviceID(const LONG did);

   static string getTableName();
};

