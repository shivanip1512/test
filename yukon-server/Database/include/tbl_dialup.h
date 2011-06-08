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


class IM_EX_CTIYUKONDB CtiTableDeviceDialup : public CtiMemDBObject
{

protected:

    LONG          _deviceID;
    std::string   PhoneNumber;
    INT           MinConnectTime;
    INT           MaxConnectTime;
    std::string   LineSettings;
    INT           BaudRate;

public:

    CtiTableDeviceDialup();

    CtiTableDeviceDialup(const CtiTableDeviceDialup &aRef);

    CtiTableDeviceDialup& operator=(const CtiTableDeviceDialup &aRef);

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

