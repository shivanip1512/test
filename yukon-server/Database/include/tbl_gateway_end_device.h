#pragma once

#include "row_reader.h"


#include "resolvers.h"
#include "yukon.h"
#include "logger.h"
#include "dbmemobject.h"
#include "database_connection.h"

#define MAX_SERIAL_NUMBER_LENGTH    30
#define MAX_DATA_VALUE_LENGTH       100

class IM_EX_CTIYUKONDB CtiTableGatewayEndDevice  : public CtiMemDBObject
{
protected:

    string _serialNumber;
    UINT _hardwareType;
    UINT _dataType;
    string _dataValue;


public:

    typedef CtiMemDBObject Inherited;

    CtiTableGatewayEndDevice();

    CtiTableGatewayEndDevice(const CtiTableGatewayEndDevice& aRef);

    virtual ~CtiTableGatewayEndDevice();

    CtiTableGatewayEndDevice& operator=(const CtiTableGatewayEndDevice& aRef);

    CtiTableGatewayEndDevice& setSerialNumber(ULONG sn);
    CtiTableGatewayEndDevice& setSerialNumber(string sn);
    CtiTableGatewayEndDevice& setHardwareType(UINT hwt);
    CtiTableGatewayEndDevice& setDataType(UINT dt);
    CtiTableGatewayEndDevice& setDataValue(string dv);

    string getSerialNumber() const;
    UINT  getHardwareType() const;
    UINT  getDataType() const;
    string getDataValue() const;

    static string getTableName();
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual bool Insert(Cti::Database::DatabaseConnection &conn);
    virtual bool Update();
};

