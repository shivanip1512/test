/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_settings
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_settings.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/01/07 17:48:34 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_SETTINGS_H__
#define __TBL_PORT_SETTINGS_H__

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTablePortSettings : public CtiMemDBObject
{
protected:

   INT         _baudRate;           // Comm channel bps
   ULONG       _cdWait;             // CD Wait time in ms
   RWCString   _lineSettings;       // Bits, Parity, Stop bits

private:

public:

   CtiTablePortSettings();
   CtiTablePortSettings(const CtiTablePortSettings& aRef);
   virtual ~CtiTablePortSettings();

   CtiTablePortSettings& operator=(const CtiTablePortSettings& aRef);

   INT                     getBaudRate() const;
   INT&                    getBaudRate();
   CtiTablePortSettings&   setBaudRate(const INT r);

   ULONG                   getCDWait() const;
   ULONG&                  getCDWait();
   CtiTablePortSettings&   setCDWait(const INT w);

   RWCString               getLineSettings() const;
   RWCString&              getLineSettings();
   CtiTablePortSettings&   setLineSettings(const RWCString str);

   INT getBits() const;
   INT getParity() const;
   INT getStopBits() const;

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
};
#endif // #ifndef __TBL_PORT_SETTINGS_H__

