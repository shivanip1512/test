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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2006/09/21 21:31:38 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SA105_H__
#define __DEV_GRP_SA105_H__


#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa105.h"
#include "tbl_lmg_sa205105.h"


class IM_EX_DEVDB CtiDeviceGroupSA105 : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTableSA205105Group _loadGroup;

public:

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
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
};

#endif // #ifndef __DEV_GRP_SA105_H__
