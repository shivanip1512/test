#pragma once

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB DevicePaging  : public CtiDeviceIED
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    DevicePaging(const DevicePaging&);
    DevicePaging& operator=(const DevicePaging&);

    typedef CtiDeviceIED Inherited;

protected:

   CtiTableDeviceTapPaging       _tapTable;

public:

   DevicePaging();

   const CtiTableDeviceTapPaging& getPaging() const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

}// namespace Device
}// namespace Cti
