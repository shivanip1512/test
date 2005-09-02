
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/09/02 16:19:47 $
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

    int _lastSTime;     // Holds the last sent message for the restore graceful command.
    int _lastCTime;
    RWTime _onePeriodLeft;

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
};

#endif // #ifndef __DEV_GRP_SA205_H__
