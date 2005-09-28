/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_vthreshold
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_type_mct_vthreshold.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef _CONFIG_TYPE_MCT_VTHRESHOLD_H__
#define _CONFIG_TYPE_MCT_VTHRESHOLD_H__

#include "yukon.h"
#include "config_base.h"

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG MCTVThreshold : public Base
{
public:
    MCTVThreshold();
    ~MCTVThreshold();

    string getOutputStrings();
    int getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value,const int &key);
    RWCString getValueFromKey(const int &key);

    enum dataParts
    {
        Invalid = 0,
        UnderVoltageThreshold,
        OverVoltageThreshold
    };

private:
    typedef map<int,RWCString> StringValueMap;
    StringValueMap valueMap;

    RWCString UnderVoltageThresholdData;
    RWCString OverVoltageThresholdData;
};

#ifdef VSLICK_TAG_WORKAROUND
typedef MCTVThreshold * MCTVThreshold_SPtr;
#else
typedef shared_ptr< MCTVThreshold > MCTVThreshold_SPtr;
#endif

}//Config
}//Cti

#endif //_CONFIG_TYPE_MCT_TOU_H__
