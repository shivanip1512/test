
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SASIMPLE_H__
#define __DEV_GRP_SASIMPLE_H__

/*---------------------------------------------------------------------------------*
*
* File:   dev_grp_sasimple
*
* Class:  
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/05 19:50:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"


class IM_EX_DEVDB CtiDeviceGroupSASimple : public CtiDeviceGroupBase
{
protected:

    CtiTableSASimpleGroup 	_loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSASimple();
    CtiDeviceGroupSASimple(const CtiDeviceGroupSASimple& aRef);
    virtual ~CtiDeviceGroupSASimple();

    CtiDeviceGroupSASimple& operator=(const CtiDeviceGroupSASimple& aRef);

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupSASimple& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual LONG getRouteID();
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);

};

#endif // #ifndef __DEV_GRP_SASIMPLE_H__
