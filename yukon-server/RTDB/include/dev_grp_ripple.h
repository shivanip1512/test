/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_ripple
*
* Class:  CtiDeviceGroupRipple
* Date:   10/4/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_grp_ripple.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_RIPPLE_H__
#define __DEV_GRP_RIPPLE_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_lmg_ripple.h"

class CtiDeviceGroupRipple : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    bool matchRippleDoubleOrders(string parentDO, string childDO) const;

protected:

    CtiTableRippleLoadGroup _rippleTable;
    CtiMessage *_rsvp;

public:

    CtiDeviceGroupRipple();
    CtiDeviceGroupRipple(const CtiDeviceGroupRipple& aRef);
    virtual ~CtiDeviceGroupRipple();

    CtiDeviceGroupRipple& operator=(const CtiDeviceGroupRipple& aRef);

    CtiTableRippleLoadGroup   getRippleTable() const;
    CtiTableRippleLoadGroup&  getRippleTable();
    CtiDeviceGroupRipple&     setRippleTable(const CtiTableRippleLoadGroup& aRef);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT processTrxID( int trx, list< CtiMessage* >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, list< CtiMessage* >  &vgList );

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev);
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev);
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);
    void setRsvpToDispatch(CtiMessage *&rsvp);

    void contributeToBitPattern(BYTE *bptr, bool shed) const;
};

inline CtiTableRippleLoadGroup CtiDeviceGroupRipple::getRippleTable() const     { LockGuard gd(monitor()); return _rippleTable;}
inline CtiTableRippleLoadGroup& CtiDeviceGroupRipple::getRippleTable()          { LockGuard gd(monitor()); return _rippleTable;}

#endif // #ifndef __DEV_GRP_RIPPLE_H__
