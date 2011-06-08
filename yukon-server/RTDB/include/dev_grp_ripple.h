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
* REVISION     :  $Revision: 1.12.2.1 $
* DATE         :  $Date: 2008/11/19 15:21:28 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_RIPPLE_H__
#define __DEV_GRP_RIPPLE_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_lmg_ripple.h"

class IM_EX_DEVDB CtiDeviceGroupRipple : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    bool matchRippleDoubleOrders(std::string parentDO, std::string childDO) const;

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
    virtual std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual INT processTrxID( int trx, std::list< CtiMessage* >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, std::list< CtiMessage* >  &vgList );

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev);
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev);
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);
    void setRsvpToDispatch(CtiMessage *&rsvp);

    void contributeToBitPattern(BYTE *bptr, bool shed) const;
};

inline CtiTableRippleLoadGroup CtiDeviceGroupRipple::getRippleTable() const     { CtiLockGuard<CtiMutex> guard(_classMutex); return _rippleTable;}
inline CtiTableRippleLoadGroup& CtiDeviceGroupRipple::getRippleTable()          { CtiLockGuard<CtiMutex> guard(_classMutex); return _rippleTable;}

#endif // #ifndef __DEV_GRP_RIPPLE_H__
