/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_point
*
* Class:  CtiDeviceGroupPoint
* Date:   3/22/2006
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2006/09/21 21:31:38 $
* HISTORY      :
* $Log: dev_grp_point.h,v $
* Revision 1.3  2006/09/21 21:31:38  mfisher
* privatized Inherited typedef
*
* Revision 1.2  2006/04/05 16:22:18  cplender
* Initial Revision
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_POINT_H__
#define __DEV_GRP_POINT_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_lmg_point.h"


class IM_EX_DEVDB CtiDeviceGroupPoint : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

    CtiTablePointGroup _loadGroup;

public:

    CtiDeviceGroupPoint();
    CtiDeviceGroupPoint(const CtiDeviceGroupPoint& aRef);
    virtual ~CtiDeviceGroupPoint();
    virtual LONG getRouteID();
    CtiDeviceGroupPoint& operator=(const CtiDeviceGroupPoint& aRef);

    INT generateRequest(CtiRequestMsg *pReq, CtiCommandParser &parse);
    string getDescription(const CtiCommandParser & parse) const;

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    CtiTablePointGroup& getLoadGroup() { return _loadGroup; }

};
#endif // #ifndef __DEV_GRP_POINT_H__
