
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmgmct
*
* Class:  CtiTableLMGroupMCT
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/05/23 22:32:22 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_LMGMCT_H__
#define __TBL_DV_LMGMCT_H__

class CtiTableLMGroupMCT
{
protected:

private:

public:
    CtiTableLMGroupMCT() {}

    CtiTableLMGroupMCT(const CtiTableLMGroupMCT& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiTableLMGroupMCT() {}

    CtiTableLMGroupMCT& operator=(const CtiTableLMGroupMCT& aRef)
    {
        if(this != &aRef)
        {
        }
        return *this;
    }

};
#endif // #ifndef __TBL_DV_LMGMCT_H__
