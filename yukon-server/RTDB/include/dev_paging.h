#pragma once

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DevicePaging  : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceTapPaging       _tapTable;

public:

   DevicePaging();
   DevicePaging(const DevicePaging& aRef);
   virtual ~DevicePaging();

   DevicePaging& operator=(const DevicePaging& aRef);

   const CtiTableDeviceTapPaging& getPaging() const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

}// namespace Device
}// namespace Cti
