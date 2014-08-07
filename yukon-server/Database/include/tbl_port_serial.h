#pragma once

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePortLocalSerial : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePortLocalSerial(const CtiTablePortLocalSerial&);
    CtiTablePortLocalSerial& operator=(const CtiTablePortLocalSerial&);

protected:

   std::string     _physicalPort;              // in struct [6];

public:

   CtiTablePortLocalSerial();
   virtual ~CtiTablePortLocalSerial();

   std::string getPhysicalPort() const;
   std::string& getPhysicalPort();

   CtiTablePortLocalSerial& setPhysicalPort(const std::string& str );
   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
