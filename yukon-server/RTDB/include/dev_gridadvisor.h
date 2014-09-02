#pragma once

#include "dev_dnp.h"
#include "tbl_dv_address.h"
#include "tbl_direct.h"

// THIS DEVICE SHOULD INHERIT FROM CTIDEVICEREMOTE
class IM_EX_DEVDB CtiDeviceGridAdvisor : public Cti::Devices::DnpDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGridAdvisor(const CtiDeviceGridAdvisor&);
    CtiDeviceGridAdvisor& operator=(const CtiDeviceGridAdvisor&);

    typedef CtiDeviceSingle Inherited;

    CtiTableDeviceAddress    _address;
    CtiTableDeviceDirectComm _commport;

public:

    CtiDeviceGridAdvisor();

    virtual std::string getSQLCoreStatement() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    //  virtual in case different GridAdvisor devices need to form up alternate requests for the same command
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
};

