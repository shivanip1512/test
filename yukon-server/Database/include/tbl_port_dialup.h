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

class IM_EX_CTIYUKONDB CtiTablePortDialup : public CtiMemDBObject, private boost::noncopyable
{
   // PortDialupModem
   LONG             _portID;
   std::string      _modemType;
   std::string      _modemInitString;           // in struct [MODEMINITLENGTH];
   std::string      _prefixString;              // Pre Dial        - FIX FIX FIX
   std::string      _suffixString;              // Post Dial       - FIX FIX FIX

public:

   CtiTablePortDialup();
   virtual ~CtiTablePortDialup();

   std::string                     getModemType() const;
   std::string&                    getModemType();
   CtiTablePortDialup&             setModemType(const std::string& str);

   std::string                     getModemInitString() const;
   std::string&                    getModemInitString();
   CtiTablePortDialup&             setModemInitString(const std::string& str);

   std::string                     getPrefixString() const;
   std::string&                    getPrefixString();
   CtiTablePortDialup&             setPrefixString(const std::string& str);

   std::string                     getSuffixString() const;
   std::string&                    getSuffixString();
   CtiTablePortDialup&             setSuffixString(const std::string& str);

   static std::string              getTableName();

   static std::string              getSQLCoreStatement();

   LONG                          getPortID() const;
   CtiTablePortDialup&           setPortID( const LONG ptid );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

};
