
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SA205_H__
#define __DEV_GRP_SA205_H__

/*---------------------------------------------------------------------------------*
*
* File:   dev_grp_sa205
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
#include "prot_sa205.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA205 : public CtiDeviceGroupBase
{
protected:

    CtiTableSA205105Group _loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSA205();
    CtiDeviceGroupSA205(const CtiDeviceGroupSA205& aRef);
    virtual ~CtiDeviceGroupSA205();

    CtiDeviceGroupSA205& operator=(const CtiDeviceGroupSA205& aRef);

    CtiTableSA205105Group getLoadGroup() const;
    CtiTableSA205105Group& getLoadGroup();
    CtiDeviceGroupSA205& setLoadGroup(const CtiTableSA205105Group& aRef);

    virtual LONG getRouteID();
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);
};

#endif // #ifndef __DEV_GRP_SA205_H__
