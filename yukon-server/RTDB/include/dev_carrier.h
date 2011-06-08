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

    typedef DlcBaseDevice Inherited;

protected:

   boost::shared_ptr<CtiTableDeviceLoadProfile> loadProfile;

public:

   CarrierDevice();
   CarrierDevice(const CarrierDevice &aRef);

   virtual ~CarrierDevice();

   CarrierDevice &operator=(const CarrierDevice &aRef);

   virtual boost::shared_ptr<Cti::DataAccessLoadProfile> getLoadProfile();

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   virtual LONG getLastIntervalDemandRate();

   virtual bool isMeter() const;
};

}
}

