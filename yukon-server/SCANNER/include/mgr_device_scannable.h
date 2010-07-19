#ifndef __MGR_DEVICE_SCANNABLE_H__
#define __MGR_DEVICE_SCANNABLE_H__

#include "mgr_device.h"

namespace Cti {

class ScannableDeviceManager : public CtiDeviceManager
{
private:

    ScannableDeviceManager &operator=(const ScannableDeviceManager &);

    void refreshScanRates    (Database::id_set &paoids);
    void refreshDeviceWindows(Database::id_set &paoids);

protected:

    typedef CtiDeviceManager Inherited;

    virtual void refreshDeviceProperties(Database::id_set &paoids, int type);

public:

    ScannableDeviceManager(CtiApplication_t app_id) :
        CtiDeviceManager(app_id)
    {
    };

    virtual void refresh(LONG paoID = 0, string category = string(""), string devicetype = string(""));
};

}

#endif                  // #ifndef __MGR_DEVICE_SCANNABLE_H__
