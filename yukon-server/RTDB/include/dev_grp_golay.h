
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_golay
*
* Class:  CtiDeviceGroupGolay
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:20:29 $
* HISTORY      :
* $Log: dev_grp_golay.h,v $
* Revision 1.3  2005/12/20 17:20:29  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.2  2005/09/02 16:19:47  cplender
* Modified the getPutConfigAssignment() method to allow modifier parameters.
*
* Revision 1.1  2004/04/29 20:23:50  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_GOLAY_H__
#define __DEV_GRP_GOLAY_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "prot_sasimple.h"
#include "tbl_lmg_sasimple.h"

class IM_EX_DEVDB CtiDeviceGroupGolay : public CtiDeviceGroupBase
{
protected:

    CtiTableSASimpleGroup   _loadGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupGolay();
    CtiDeviceGroupGolay(const CtiDeviceGroupGolay& aRef);
    virtual ~CtiDeviceGroupGolay();

    CtiDeviceGroupGolay& operator=(const CtiDeviceGroupGolay& aRef);

    CtiTableSASimpleGroup getLoadGroup() const;
    CtiTableSASimpleGroup& getLoadGroup();
    CtiDeviceGroupGolay& setLoadGroup(const CtiTableSASimpleGroup& aRef);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};
#endif // #ifndef __DEV_GRP_GOLAY_H__
