/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_tou
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_type_mct_tou.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef _CONFIG_TYPE_MCT_TOU_H__
#define _CONFIG_TYPE_MCT_TOU_H__

#include "yukon.h"
#include "config_base.h"

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG MCTTOU : public Base
{
public:
    MCTTOU();
    ~MCTTOU();

    string getOutputStrings();
    int getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value,const int &key);
    RWCString getValueFromKey(const int &key);

    enum dataParts
    {
        Invalid = 0,
        DayTable,
        DaySchedule1,
        DaySchedule2,
        DaySchedule3,
        DaySchedule4,
        CurAndDefRate,
        SwtchSchdAndTime,
        CritPkEndTime

    };

private:
    typedef map<int,RWCString> StringValueMap;
    StringValueMap valueMap;

    RWCString DayTableData;
    RWCString DaySchedule1Data;
    RWCString DaySchedule2Data;
    RWCString DaySchedule3Data;
    RWCString DaySchedule4Data;
    RWCString CurAndDefRateData;
    RWCString SwtchSchdAndTimeData;
    RWCString CritPkEndTimeData;
};

#ifdef VSLICK_TAG_WORKAROUND
typedef MCTTOU * MCTTOU_SPtr;
#else
typedef shared_ptr< MCTTOU > MCTTOU_SPtr;
#endif

}//Config
}//Cti

#endif //_CONFIG_TYPE_MCT_TOU_H__
