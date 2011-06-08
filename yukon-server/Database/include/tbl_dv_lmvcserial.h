
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmvcserial
*
* Date:   5/9/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_lmvcserial.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_LMVCSERIAL_H__
#define __TBL_DV_LMVCSERIAL_H__

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

#include "vcomdefs.h"

class IM_EX_CTIYUKONDB CtiTableLMGroupVersacomSerial : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _serial;
   INT      _groupID;            // DEvice ID of the group this guy belongs to, bookkeeping.
   INT      _addressUsage;       // This IS a mask 3 - 0 bits mean USCD bit fields!
   INT      _relayMask;          // This IS a mask 31 - 0 bits means 32 - 1 relays!
   LONG     _routeID;            // the route (macro) which defines this device.

private:

public:

   CtiTableLMGroupVersacomSerial();

   CtiTableLMGroupVersacomSerial(const CtiTableLMGroupVersacomSerial& aRef);

   virtual ~CtiTableLMGroupVersacomSerial();

   CtiTableLMGroupVersacomSerial& operator=(const CtiTableLMGroupVersacomSerial& aRef);

   INT  getSerial() const;

   CtiTableLMGroupVersacomSerial& setSerial( const INT a_ser );

   INT  getGroupID() const;

   CtiTableLMGroupVersacomSerial& setGroupID( const INT i );

   LONG getDeviceID() const;
   CtiTableLMGroupVersacomSerial& setDeviceID( const LONG did);

   INT  getAddressUsage() const;
   BOOL useUtilityID() const;
   BOOL useSection() const;
   BOOL useClass() const;
   BOOL useDivision() const;

   CtiTableLMGroupVersacomSerial& setAddressUsage( const INT a_addressUsage );

   INT  getRelayMask() const;
   BOOL useRelay(const INT r) const;

   CtiTableLMGroupVersacomSerial& setRelayMask( const INT a_relayMask );

   LONG  getRouteID() const;
   CtiTableLMGroupVersacomSerial& setRouteID( const LONG a_routeID );

   static std::string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

};
#endif // #ifndef __TBL_DV_LMVCSERIAL_H__
