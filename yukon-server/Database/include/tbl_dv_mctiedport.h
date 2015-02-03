#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceMCTIEDPort : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceMCTIEDPort(const CtiTableDeviceMCTIEDPort&);
    CtiTableDeviceMCTIEDPort& operator=(const CtiTableDeviceMCTIEDPort&);

protected:

    LONG        _deviceID;
    std::string _password;
    INT         _connectedIED,
                _iedScanRate,
                _defaultDataClass,
                _defaultDataOffset,
                _realTimeScan;

public:

    CtiTableDeviceMCTIEDPort();
    virtual ~CtiTableDeviceMCTIEDPort();

    enum IEDTypes
    {
        InvalidIEDType,
        AlphaPowerPlus,
        LandisGyrS4,
        GeneralElectricKV
    };

    long  getDeviceID() const;
    void  setDeviceID(long id);

    int   getIEDType() const;
    int  &getIEDType();
    void  setIEDType(IEDTypes type);

    std::string  getPassword() const;
    std::string& getPassword();
    void         setPassword(std::string &password);

    int  getIEDScanRate() const;
    int& getIEDScanRate();
    void setIEDScanRate(int scanrate);

    int                       getDefaultDataClass() const;
    int& getDefaultDataClass();
    void setDefaultDataClass(int dataclass);

    int  getDefaultDataOffset() const;
    int& getDefaultDataOffset();
    void setDefaultDataOffset(int dataoffset);

    int  getRealTimeScanFlag() const;
    int& getRealTimeScanFlag();
    void setRealTimeScanFlag(int flag);

    void DecodeDatabaseReader(Cti::RowReader &rdr);

    static std::string getTableName();

    virtual bool Insert();
    virtual bool Update();
    virtual bool Delete();

};
