/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_settings
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_settings.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_SETTINGS_H__
#define __TBL_PORT_SETTINGS_H__

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePortSettings : public CtiMemDBObject
{
protected:

   INT           _baudRate;           // Comm channel bps
   ULONG         _cdWait;             // CD Wait time in ms
   std::string   _lineSettings;       // Bits, Parity, Stop bits

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

   std::string               getLineSettings() const;
   std::string&              getLineSettings();
   CtiTablePortSettings&     setLineSettings(const std::string str);

   INT getBits() const;
   INT getParity() const;
   INT getStopBits() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_PORT_SETTINGS_H__

