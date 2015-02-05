#pragma once

#include "dev_dnp.h"
#include "tbl_dv_address.h"
#include "tbl_direct.h"

// THIS DEVICE SHOULD INHERIT FROM CTIDEVICEREMOTE
class IM_EX_DEVDB CtiDeviceGridAdvisor : public Cti::Devices::DnpDevice
{
    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceAddress    _address;
    CtiTableDeviceDirectComm _commport;

public:

    CtiDeviceGridAdvisor();

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr) override;

    //  virtual in case different GridAdvisor devices need to form up alternate requests for the same command
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
};

