/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_demand_LP
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_type_mct_demand_LP.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef _CONFIG_TYPE_MCT_DEMAND_LP_H__
#define _CONFIG_TYPE_MCT_DEMAND_LP_H__

#include "yukon.h"
#include "config_base.h"

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG MCTDemandLoadProfile : public Base
{
public:
    MCTDemandLoadProfile();
    ~MCTDemandLoadProfile();

    string getOutputStrings();
    int getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value,const int &key);
    RWCString getValueFromKey(const int &key);

    enum dataParts
    {
        Invalid = 0,
        DemandInterval,
        LPInterval,
        VoltageLPInterval,
        VoltageDemandInterval,
        LoadProfileInterval,
        LLPStartTime,
        LLPChannel
    };

private:
    typedef map<int,RWCString> StringValueMap;
    StringValueMap valueMap;

    RWCString DemandIntervalData;
    RWCString LPIntervalData;
    RWCString VoltageLPIntervalData;
    RWCString VoltageDemandIntervalData;
    RWCString LoadProfileIntervalData;
    RWCString LLPStartTimeData;
    RWCString LLPChannelData;
};

#ifdef VSLICK_TAG_WORKAROUND
typedef MCTDemandLoadProfile * MCTDemandLoadProfile_SPtr;
#else
typedef shared_ptr< MCTDemandLoadProfile > MCTDemandLoadProfile_SPtr;
#endif

}//Config
}//Cti

#endif //_CONFIG_TYPE_MCT_TOU_H__
