
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sa305
*
* Class:  CtiDeviceGroupSA305
* Date:   3/15/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/08/24 20:49:00 $
* HISTORY      :
* $Log: dev_grp_sa305.h,v $
* Revision 1.2  2005/08/24 20:49:00  cplender
* Restore commands were expiring inappropriately.
*
* Revision 1.1  2004/03/18 19:46:43  cplender
* Added code to support the SA305 protocol and load group
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_SA305_H__
#define __DEV_GRP_SA305_H__


#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sa305.h"
#include "tbl_lmg_sa305.h"


class IM_EX_DEVDB CtiDeviceGroupSA305 : public CtiDeviceGroupBase
{
public:
    typedef enum
    {
        SA305_DI_Control = 1,
        SA305_DLC_Control = 2
    } CtiSACommand_t;

protected:

    CtiTableSA305LoadGroup _loadGroup;

private:

    CtiSACommand_t _lastSACommandType;


public:


    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupSA305();
    CtiDeviceGroupSA305(const CtiDeviceGroupSA305& aRef);
    virtual ~CtiDeviceGroupSA305();

    CtiDeviceGroupSA305& operator=(const CtiDeviceGroupSA305& aRef);

    CtiTableSA305LoadGroup getLoadGroup() const;
    CtiTableSA305LoadGroup& getLoadGroup();
    CtiDeviceGroupSA305& setLoadGroup(const CtiTableSA305LoadGroup& aRef);

    CtiSACommand_t getLastSACommandType() const { return _lastSACommandType; }

    virtual LONG getRouteID();
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);

};
#endif // #ifndef __DEV_GRP_SA305_H__
