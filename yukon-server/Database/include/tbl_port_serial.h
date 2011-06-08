/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_serial
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_serial.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_SERIAL_H__
#define __TBL_PORT_SERIAL_H__

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
#endif // #ifndef __TBL_PORT_SERIAL_H__
