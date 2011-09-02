#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>


#include "dbaccess.h"
#include "dbmemobject.h"
#include "rwutil.h"

class IM_EX_PNTDB CtiDeviceBaseLite : public CtiMemDBObject
{
protected:

    LONG        _deviceID;
    LONG        _portID;
    std::string   _class;
    std::string   _name;
    std::string   _description;
    std::string   _objectType;
    std::string   _disableFlag;
    std::string   _controlInhibitFlag;

    private:

    public:

    CtiDeviceBaseLite(LONG id = -1);
    CtiDeviceBaseLite(const CtiDeviceBaseLite& aRef);

    virtual ~CtiDeviceBaseLite();

    CtiDeviceBaseLite& operator=(const CtiDeviceBaseLite& aRef);
    LONG getID() const;
    LONG getPortID() const;
    std::string getClass() const;
    std::string getName() const;
    std::string getDescription() const;

    CtiDeviceBaseLite& setID( LONG id );
    CtiDeviceBaseLite& setPortID( LONG id );
    CtiDeviceBaseLite& setClass( const std::string &str );
    CtiDeviceBaseLite& setName( const std::string &str );
    CtiDeviceBaseLite& setDescription( const std::string &str );
    CtiDeviceBaseLite& setDisableFlag( const std::string &str );
    CtiDeviceBaseLite& setControlInhibitFlag( const std::string &str );

    std::string getObjectType() const;
    std::string getDisableFlag() const;
    bool isDisabled() const;
    std::string getControlInhibitFlag() const;
    bool isControlInhibited() const;

    bool operator<( const CtiDeviceBaseLite &rhs ) const;
    bool operator==( const CtiDeviceBaseLite &rhs ) const;
    bool operator()(const CtiDeviceBaseLite& aRef) const;

    virtual std::string getSQLCoreStatement(long paoid);

    static std::string getTableName();
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual bool Restore();
};
