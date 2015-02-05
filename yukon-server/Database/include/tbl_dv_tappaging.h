#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceTapPaging : public CtiMemDBObject, private boost::noncopyable
{
   LONG           _deviceID;
   std::string    _pagerNumber;                    // a.k.a. CAPCODE

   std::string    _senderID;
   std::string    _securityCode;
   std::string    _postPath;

public:

   CtiTableDeviceTapPaging(std::string pn = std::string());
   virtual ~CtiTableDeviceTapPaging();

   std::string                  getPagerNumber() const;
   std::string&                 getPagerNumber();
   CtiTableDeviceTapPaging&     setPagerNumber(const std::string &aStr);

   std::string                  getSenderID() const;
   std::string                  getSecurityCode() const;
   std::string                  getPOSTPath() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceTapPaging& setDeviceID(const LONG did);

   static std::string getTableName();
};

