#pragma once

#include "mgr_device.h"

namespace Cti {

class ScannableDeviceManager : public CtiDeviceManager
{
private:

    void refreshScanRates    (Database::id_set &paoids);
    void refreshDeviceWindows(Database::id_set &paoids);

protected:

    typedef CtiDeviceManager Inherited;

    void refreshDeviceProperties(Database::id_set &paoids, int type) override;
    void refreshDnpChildDevices(Cti::Database::id_set &paoids) override;

    bool shouldDiscardDevice(CtiDeviceSPtr dev) const override;

public:

    ScannableDeviceManager &operator=(const ScannableDeviceManager &) = delete;

    virtual void refreshAllDevices();
};

}
