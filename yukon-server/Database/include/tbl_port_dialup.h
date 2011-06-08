/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_dialup
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_dialup.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_DIALUP_H__
#define __TBL_PORT_DIALUP_H__

#include <limits.h>

#include "row_reader.h"
#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePortDialup : public CtiMemDBObject
{

protected:

   // PortDialupModem
   LONG             _portID;
   std::string      _modemType;
   std::string      _modemInitString;           // in struct [MODEMINITLENGTH];
   std::string      _prefixString;              // Pre Dial        - FIX FIX FIX
   std::string      _suffixString;              // Post Dial       - FIX FIX FIX

private:

public:

   CtiTablePortDialup();

   CtiTablePortDialup(const CtiTablePortDialup& aRef);

   virtual ~CtiTablePortDialup();

   CtiTablePortDialup& operator=(const CtiTablePortDialup& aRef);

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
#endif // #ifndef __TBL_PORT_DIALUP_H__
