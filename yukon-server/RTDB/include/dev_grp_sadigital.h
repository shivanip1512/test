
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sadigital
*
* Class:  CtiDeviceGroupSADigital
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/29 20:24:40 $
* HISTORY      :
* $Log: dev_grp_sadigital.h,v $
* Revision 1.1  2004/04/29 20:24:40  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SADIGITAL_H__
#define __DEV_GRP_SADIGITAL_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupSADigital : public CtiDeviceGroupBase
{
protected:

    CtiTableSASimpleGroup   _loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSADigital();
    CtiDeviceGroupSADigital(const CtiDeviceGroupSADigital& aRef);
    virtual ~CtiDeviceGroupSADigital();

    CtiDeviceGroupSADigital& operator=(const CtiDeviceGroupSADigital& aRef);

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupSADigital& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual LONG getRouteID();
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);

};
#endif // #ifndef __DEV_GRP_SADIGITAL_H__
