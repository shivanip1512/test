/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_device.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:33:18 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_DEVICE_H__
#define __CONFIG_DEVICE_H__

#include "yukon.h"

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
using namespace std;

#include "logger.h"
#include "dllbase.h"
#include "config_base.h"
#include <map>

namespace Cti {
namespace Config {

class IM_EX_CONFIG CtiConfigDevice
{
protected:
    typedef CtiLockGuard<CtiMutex> LockGuard;//This must be used in every class that sets the value with key, or returns values.
    mutable CtiMutex    _mux;

private:
    typedef map <int,Cti::Config::CtiConfigBaseSPtr> BasePointerMap;

    BasePointerMap _baseConfigurations;
public:
        
    CtiConfigDevice();
    ~CtiConfigDevice();
    CtiConfigBaseSPtr getConfigFromType(int type);
    void insertConfig(CtiConfigBaseSPtr configuration);//Type should be set in this pointer, so its not necessary
    string getAllOutputStrings();//Returns strings from every base configuration
};

#ifdef VSLICK_TAG_WORKAROUND
typedef CtiConfigDevice * CtiConfigDeviceSPtr;
#else
typedef shared_ptr< CtiConfigDevice > CtiConfigDeviceSPtr;
#endif

}
}

#endif//__CONFIG_DEVICE_H
