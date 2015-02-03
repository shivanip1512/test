#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDialup : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceDialup(const CtiTableDeviceDialup&);
    CtiTableDeviceDialup& operator=(const CtiTableDeviceDialup&);

protected:

    LONG          _deviceID;
    std::string   PhoneNumber;
    INT           MinConnectTime;
    INT           MaxConnectTime;
    std::string   LineSettings;
    INT           BaudRate;

public:

    CtiTableDeviceDialup();

    INT  getMinConnectTime() const;
    void setMinConnectTime(INT  i);

    INT  getMaxConnectTime() const;
    void setMaxConnectTime(INT  i);

    INT  getBaudRate() const;
    CtiTableDeviceDialup& setBaudRate(INT i);

    std::string getPhoneNumber() const;
    void setPhoneNumber(const std::string &str);

    std::string getLineSettings() const;
    void setLineSettings(const std::string &lstr);

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    LONG getDeviceID() const;
    CtiTableDeviceDialup& setDeviceID( const LONG did);

    static std::string getTableName();

    INT getStopBits() const;
    INT getParity() const;
    INT getBits() const;

};

