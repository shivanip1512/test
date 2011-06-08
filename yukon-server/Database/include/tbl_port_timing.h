/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_timing
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_timing.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_TIMING_H__
#define __TBL_PORT_TIMING_H__

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


class IM_EX_CTIYUKONDB CtiTablePortTimings : public CtiMemDBObject
{

protected:

   // PortTimings Table
   ULONG          _delays[LAST_DELAY];

private:

public:

   CtiTablePortTimings();

   CtiTablePortTimings(const CtiTablePortTimings& aRef);

   virtual ~CtiTablePortTimings();

   CtiTablePortTimings& operator=(const CtiTablePortTimings& aRef);

   CtiTablePortTimings&  setDelay(int Offset, int D);

   ULONG  getDelay(int Offset) const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
#endif // #ifndef __TBL_PORT_TIMING_H__
