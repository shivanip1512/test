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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
* HISTORY      :
* $Log: dev_grp_point.h,v $
* Revision 1.4  2008/10/28 19:21:43  mfisher
* YUK-6589 Scanner should not load non-scannable devices
* refreshList() now takes a list of paoids, which may be empty if it's a full reload
* Changed CtiDeviceBase, CtiPointBase, and CtiRouteBase::getSQL() (and all inheritors) to be const
* Removed a couple unused Porter functions
* Added logger unit test
* Simplified DebugTimer's variables
*
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
    std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    CtiTablePointGroup& getLoadGroup() { return _loadGroup; }

};
#endif // #ifndef __DEV_GRP_POINT_H__
