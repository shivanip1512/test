#ifndef __MGR_DEVICE_SCANNABLE_H__
#define __MGR_DEVICE_SCANNABLE_H__

#include "mgr_device.h"

namespace Cti {

class ScannableDeviceManager : public CtiDeviceManager
{
private:

    void refreshScanRates(LONG id = 0);
    void refreshDeviceWindows(LONG id = 0);

protected:

    typedef CtiDeviceManager Inherited;

    virtual void refreshDeviceProperties(LONG paoID = 0);

public:

    ScannableDeviceManager(CtiApplication_t app_id) :
        CtiDeviceManager(app_id)
    {
    };

    virtual void refresh(LONG paoID = 0, string category = string(""), string devicetype = string(""));
};

}

#endif                  // #ifndef __MGR_DEVICE_SCANNABLE_H__
