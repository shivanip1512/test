/*-----------------------------------------------------------------------------*
*
* File:   config_type_general
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_type_general.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:55 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef _CONFIG_TYPE_GENERAL_H__
#define _CONFIG_TYPE_GENERAL_H__

#include "yukon.h"
#include "config_base.h"

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG General : public Base
{
public:
    General();
    ~General();

    string getOutputStrings();
    int getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value,const int &key);
    RWCString getValueFromKey(const int &key);

private:
    typedef map<int,RWCString> StringValueMap;
    StringValueMap valueMap;


};

#ifdef VSLICK_TAG_WORKAROUND
typedef General * GeneralSPtr;
#else
typedef shared_ptr< General > GeneralSPtr;
#endif

}//Config
}//Cti

#endif //_CONFIG_TYPE_GENERAL_H__
