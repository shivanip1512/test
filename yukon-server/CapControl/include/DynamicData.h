#pragma once


class CtiTime;

namespace Cti
{
    class RowReader;

    namespace Database
    {
        class DatabaseConnection;
    }
}



class DynamicData
{
public:

    DynamicData();

    template<class T>
    bool updateStaticValue( T & currentValue, const T & newValue )
    {
        const bool hasChanged = currentValue != newValue;

        if ( hasChanged )
        {
            currentValue = newValue;
        }

        return hasChanged;
    }

    template<class T>
    bool updateDynamicValue( T & currentValue, const T & newValue )
    {
        const bool hasChanged = updateStaticValue( currentValue, newValue );

        _dirty |= hasChanged;

        return hasChanged;
    }

    bool hasDynamicData( Cti::RowReader & columnValue );

    void writeDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime );

protected:

    bool isDirty() const;

    void setDirty( const bool flag );

private:

    virtual bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) = 0;
    virtual bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) = 0;

    bool _dirty;

    enum Action
    {
        Insert,
        Update
    }
    _action;
};

