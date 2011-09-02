#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceMCTIEDPort : public CtiMemDBObject
{
protected:

    LONG        _deviceID;
    std::string _password;
    INT         _connectedIED,
                _iedScanRate,
                _defaultDataClass,
                _defaultDataOffset,
                _realTimeScan;

private:

public:

    CtiTableDeviceMCTIEDPort();

    CtiTableDeviceMCTIEDPort(const CtiTableDeviceMCTIEDPort& aRef);

    virtual ~CtiTableDeviceMCTIEDPort();

    enum IEDTypes
    {
        InvalidIEDType,
        AlphaPowerPlus,
        LandisGyrS4,
        GeneralElectricKV
    };

    CtiTableDeviceMCTIEDPort& operator=(const CtiTableDeviceMCTIEDPort& aRef);

    long                      getDeviceID() const;
    CtiTableDeviceMCTIEDPort  setDeviceID(long id);

    int                       getIEDType() const;
    int                      &getIEDType();
    CtiTableDeviceMCTIEDPort  setIEDType(IEDTypes type);

    std::string                 getPassword() const;
    std::string                &getPassword();
    CtiTableDeviceMCTIEDPort  setPassword(std::string &password);

    int                       getIEDScanRate() const;
    int                      &getIEDScanRate();
    CtiTableDeviceMCTIEDPort  setIEDScanRate(int scanrate);

    int                       getDefaultDataClass() const;
    int                      &getDefaultDataClass();
    CtiTableDeviceMCTIEDPort  setDefaultDataClass(int dataclass);

    int                       getDefaultDataOffset() const;
    int                      &getDefaultDataOffset();
    CtiTableDeviceMCTIEDPort  setDefaultDataOffset(int dataoffset);

    int                       getRealTimeScanFlag() const;
    int                      &getRealTimeScanFlag();
    CtiTableDeviceMCTIEDPort  setRealTimeScanFlag(int flag);

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    static std::string getTableName();

    virtual bool Insert();
    virtual bool Update();
    virtual bool Delete();

};
