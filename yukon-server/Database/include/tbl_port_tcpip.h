#pragma once

#include <limits.h>
#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePortTCPIP : public CtiMemDBObject, private boost::noncopyable
{
   LONG _portID;
   std::string _ipAddress;
   INT _ipPort;
   std::string _encodingKey;
   std::string _encodingType;

public:

   CtiTablePortTCPIP();
   virtual ~CtiTablePortTCPIP();

   INT getIPPort() const;
   void setIPPort(const INT i);

   const std::string &getIPAddress() const;
   void setIPAddress(const std::string &str);

   const std::string & getEncodingKey() const;
   void setEncodingKey(const std::string& encodingKey);

   const std::string & getEncodingType() const;
   void setEncodingType(const std::string& encodingType);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static std::string getTableName();

};
