#pragma once

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
