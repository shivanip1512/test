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
    typedef DlcBaseDevice Inherited;

protected:

   boost::shared_ptr<CtiTableDeviceLoadProfile> loadProfile;

public:

   CarrierDevice();

   virtual boost::shared_ptr<Cti::DataAccessLoadProfile> getLoadProfile();

   virtual std::string getSQLCoreStatement() const;

   void DecodeDatabaseReader(Cti::RowReader &rdr) override;

   virtual LONG getLastIntervalDemandRate();

   virtual bool isMeter() const;
};

}
}

