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
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging&);
    CtiTableDeviceTapPaging& operator=(const CtiTableDeviceTapPaging&);

protected:

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

