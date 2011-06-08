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

    virtual void refresh(LONG paoID = 0, std::string category = std::string(""), std::string devicetype = std::string(""));
};

}

#endif                  // #ifndef __MGR_DEVICE_SCANNABLE_H__
