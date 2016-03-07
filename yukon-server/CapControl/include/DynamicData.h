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

    bool isDirty() const;

    void setDirty( const bool flag );
    bool taint( const bool flag );

    bool hasDynamicData( Cti::RowReader & columnValue );

    void writeDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime );

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

