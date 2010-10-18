#pragma once

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"


class IM_EX_DEVDB DeviceTapPagingTerminalBase  : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceTapPaging       _tapTable;

public:

   DeviceTapPagingTerminalBase();
   DeviceTapPagingTerminalBase(const DeviceTapPagingTerminalBase& aRef);
   virtual ~DeviceTapPagingTerminalBase();

   DeviceTapPagingTerminalBase& operator=(const DeviceTapPagingTerminalBase& aRef);

   const CtiTableDeviceTapPaging&    getTap() const;

   virtual string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
