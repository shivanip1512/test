#pragma once

#include "dev_dlcbase.h"
#include "tbl_carrier.h"
#include "tbl_metergrp.h"
#include "tbl_loadprofile.h"
#include "tbl_dv_mctiedport.h"
#include "da_load_profile.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CarrierDevice : public DlcBaseDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CarrierDevice(const CarrierDevice&);
    CarrierDevice& operator=(const CarrierDevice&);

    typedef DlcBaseDevice Inherited;

protected:

   boost::shared_ptr<CtiTableDeviceLoadProfile> loadProfile;

public:

   CarrierDevice();
   virtual ~CarrierDevice();

   virtual boost::shared_ptr<Cti::DataAccessLoadProfile> getLoadProfile();

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual LONG getLastIntervalDemandRate();

   virtual bool isMeter() const;
};

}
}

