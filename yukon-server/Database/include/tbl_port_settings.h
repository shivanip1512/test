#pragma once

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
