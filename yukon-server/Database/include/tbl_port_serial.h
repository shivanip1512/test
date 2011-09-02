#pragma once

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePortLocalSerial : public CtiMemDBObject
{
protected:

   std::string     _physicalPort;              // in struct [6];

private:

public:

   CtiTablePortLocalSerial();
   CtiTablePortLocalSerial(const CtiTablePortLocalSerial& aRef);

   virtual ~CtiTablePortLocalSerial();

   CtiTablePortLocalSerial& operator=(const CtiTablePortLocalSerial& aRef);

   std::string getPhysicalPort() const;
   std::string& getPhysicalPort();

   CtiTablePortLocalSerial& setPhysicalPort(const std::string& str );
   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
