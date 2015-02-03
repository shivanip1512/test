#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"

namespace Cti {

class RowReader;

class IM_EX_PNTDB DeviceBaseLite : public CtiMemDBObject
{
    long          _deviceID;
    long          _portID;
    bool          _isGroup;
    std::string   _name;
    std::string   _objectType;
    bool          _disableFlag;
    bool          _controlInhibitFlag;

public:

    DeviceBaseLite(long id = -1);
    DeviceBaseLite(const DeviceBaseLite& aRef);

    virtual ~DeviceBaseLite();

    DeviceBaseLite& operator=(const DeviceBaseLite& aRef);
    long getID() const;
    long getPortID() const;

    std::string getName() const;
    std::string getObjectType() const;

    bool isDisabled() const;
    bool isControlInhibited() const;
    bool isGroup() const;

    void setDisableFlag( const bool flag );
    void setControlInhibitFlag( const bool flag );

    bool operator<( const DeviceBaseLite &rhs ) const;
    bool operator==( const DeviceBaseLite &rhs ) const;
    bool operator()(const DeviceBaseLite& aRef) const;

    virtual std::string getSQLCoreStatement(long paoid);

    static std::string getTableName();
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual bool Restore();
};

}

