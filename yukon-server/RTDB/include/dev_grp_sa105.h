
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SA105_H__
#define __DEV_GRP_SA105_H__

/*---------------------------------------------------------------------------------*
*
* File:   dev_grp_sa105
*
* Class:
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:20:29 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa105.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA105 : public CtiDeviceGroupBase
{
protected:

    CtiTableSA205105Group _loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSA105();
    CtiDeviceGroupSA105(const CtiDeviceGroupSA105& aRef);
    virtual ~CtiDeviceGroupSA105();

    CtiDeviceGroupSA105& operator=(const CtiDeviceGroupSA105& aRef);

    CtiTableSA205105Group getLoadGroup() const;
    CtiTableSA205105Group& getLoadGroup();
    CtiDeviceGroupSA105& setLoadGroup(const CtiTableSA205105Group& aRef);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
};

#endif // #ifndef __DEV_GRP_SA105_H__
