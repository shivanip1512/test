
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_mct
*
* Class:  CtiDeviceGroupMCT
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/05/23 22:33:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_MCT_H__
#define __DEV_GRP_MCT_H__

class CtiDeviceGroupMCT
{
protected:

private:

public:
    CtiDeviceGroupMCT() {}

    CtiDeviceGroupMCT(const CtiDeviceGroupMCT& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiDeviceGroupMCT() {}

    CtiDeviceGroupMCT& operator=(const CtiDeviceGroupMCT& aRef)
    {
        if(this != &aRef)
        {
        }
        return *this;
    }

};
#endif // #ifndef __DEV_GRP_MCT_H__
