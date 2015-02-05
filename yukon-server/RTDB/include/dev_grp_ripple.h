#pragma once

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_lmg_ripple.h"

class IM_EX_DEVDB CtiDeviceGroupRipple : public CtiDeviceGroupBase
{
    typedef CtiDeviceGroupBase Inherited;

    bool matchRippleDoubleOrders(std::string parentDO, std::string childDO) const;

protected:

    CtiTableRippleLoadGroup _rippleTable;
    CtiMessage *_rsvp;

public:

    CtiDeviceGroupRipple();
    virtual ~CtiDeviceGroupRipple();

    virtual LONG getRouteID();
    virtual std::string getDescription(const CtiCommandParser & parse) const;

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    virtual INT processTrxID( int trx, CtiMessageList  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, CtiMessageList  &vgList );

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev);
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev);
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);
    void setRsvpToDispatch(CtiMessage *&rsvp);

    void contributeToBitPattern(BYTE *bptr, bool shed) const;
};
