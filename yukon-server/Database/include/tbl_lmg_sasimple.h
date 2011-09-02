#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableSASimpleGroup : public CtiMemDBObject
{
protected:

    LONG        _lmGroupId;
    LONG        _routeId;

    std::string   _operationalAddress;
    int         _nominalTimeout;        // Switch is hardcoded to be off for this duration in seconds!
    int         _virtualTimeout;        // Group is desired to control for this duration in seconds.  If these are not equal multiple control messages must be sent to make it occur (master cycle-like)
    int         _function;
    int         _markIndex;
    int         _spaceIndex;

public:

    CtiTableSASimpleGroup();
    CtiTableSASimpleGroup( const CtiTableSASimpleGroup& aRef );
    virtual ~CtiTableSASimpleGroup();

    CtiTableSASimpleGroup& operator=( const CtiTableSASimpleGroup& aRef );

    LONG getLmGroupId( void ) const;
    LONG getRouteId( void ) const;
    std::string getOperationalAddress( void ) const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string getGolayOperationalAddress() const;
    int getFunction( bool control ) const;
    int getNominalTimeout( void ) const;
    int getVirtualTimeout( void ) const;
    int getMarkIndex( void ) const;
    int getSpaceIndex( void ) const;

    CtiTableSASimpleGroup& setLmGroupId( LONG newVal );
    CtiTableSASimpleGroup& setRouteId( LONG newVal );
    CtiTableSASimpleGroup& setOperationalAddress( std::string newVal );
    CtiTableSASimpleGroup& setFunction( int newVal );          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSASimpleGroup& setNominalTimeout( int newVal );
    CtiTableSASimpleGroup& setVirtualTimeout( int newVal );

    static std::string getTableName( void );

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

};
