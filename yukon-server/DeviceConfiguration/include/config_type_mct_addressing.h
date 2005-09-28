/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_type_mct_addressing.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef _CONFIG_TYPE_MCT_ADDRESSING_H__
#define _CONFIG_TYPE_MCT_ADDRESSING_H__

#include "yukon.h"
#include "config_base.h"

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG MCTAddressing : public Base
{
public:
    MCTAddressing();
    ~MCTAddressing();

    string getOutputStrings();
    int getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value,const int &key);
    RWCString getValueFromKey(const int &key);

    enum dataParts
    {
        Invalid = 0,
        Bronze,
        Lead,
        Collection
    };

private:
    typedef map<int,RWCString> StringValueMap;
    StringValueMap valueMap;

    RWCString BronzeData;
    RWCString LeadData;
    RWCString CollectionData;
};

#ifdef VSLICK_TAG_WORKAROUND
typedef MCTAddressing * MCTAddressing_SPtr;
#else
typedef shared_ptr< MCTAddressing > MCTAddressing_SPtr;
#endif

}//Config
}//Cti

#endif //_CONFIG_TYPE_MCT_TOU_H__
