#pragma once

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DevicePaging  : public CtiDeviceIED
{
    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceTapPaging       _tapTable;

public:

   DevicePaging();

   const CtiTableDeviceTapPaging& getPaging() const;

   virtual std::string getSQLCoreStatement() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;
};

}// namespace Device
}// namespace Cti
