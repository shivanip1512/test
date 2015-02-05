#pragma once

#include "dbaccess.h"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePortLocalSerial : public CtiMemDBObject, private boost::noncopyable
{
   std::string     _physicalPort;              // in struct [6];

public:

   CtiTablePortLocalSerial();
   virtual ~CtiTablePortLocalSerial();

   std::string getPhysicalPort() const;
   std::string& getPhysicalPort();

   CtiTablePortLocalSerial& setPhysicalPort(const std::string& str );
   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
