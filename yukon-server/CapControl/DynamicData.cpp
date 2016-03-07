#include "precompiled.h"

//#include "ctitime.h"
//#include "database_connection.h"

#include "DynamicData.h"
#include "database_reader.h"



DynamicData::DynamicData()
    :   _dirty( false ),
        _action( Insert )
{
    // empty
}

bool DynamicData::isDirty() const
{
    return _dirty;
}

void DynamicData::setDirty( const bool flag )
{
    _dirty = flag;
}

bool DynamicData::taint( const bool flag )
{
    _dirty |= flag;

    return flag;
}

bool DynamicData::hasDynamicData( Cti::RowReader & columnValue )
{
    if ( ! columnValue.isNull() )
    {
        _action = Update;
    }

    return _action == Update;
}

void DynamicData::writeDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    if ( _dirty )
    {
        if ( _action == Update )
        {
            if ( updateDynamicData( conn, currentDateTime ) )
            {
                _dirty = false;
            }
        }
        else    // _action == Insert
        {
            if ( insertDynamicData( conn, currentDateTime ) )
            {
                _action = Update;
                _dirty  = false;
            }
        }
    }
}

