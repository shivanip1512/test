#pragma once

#include "dev_dnp.h"
#include "tbl_dv_address.h"
#include "tbl_direct.h"

// THIS DEVICE SHOULD INHERIT FROM CTIDEVICEREMOTE
class IM_EX_DEVDB CtiDeviceGridAdvisor : public Cti::Devices::DnpDevice
{
private:

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceAddress    _address;
    CtiTableDeviceDirectComm _commport;

protected:

public:

    CtiDeviceGridAdvisor();
    CtiDeviceGridAdvisor(const CtiDeviceGridAdvisor& aRef);

    virtual ~CtiDeviceGridAdvisor();

    CtiDeviceGridAdvisor& operator=(const CtiDeviceGridAdvisor& aRef);

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    //  virtual in case different GridAdvisor devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
};

