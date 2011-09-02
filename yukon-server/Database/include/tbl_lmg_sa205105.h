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


class IM_EX_CTIYUKONDB CtiTableSA205105Group : public CtiMemDBObject
{
protected:

    LONG        _lmGroupId;
    LONG        _routeId;

    std::string   _operationalAddress;
    std::string   _loadNumber;

private:

public:
    CtiTableSA205105Group();
    CtiTableSA205105Group( const CtiTableSA205105Group& aRef );

    virtual ~CtiTableSA205105Group();

    CtiTableSA205105Group& operator=( const CtiTableSA205105Group& aRef );

    LONG getLmGroupId( void ) const;
    LONG getRouteId( void ) const;
    std::string getOperationalAddress( void ) const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string getLoadNumber( void ) const;
    int getFunction( bool shed ) const;

    CtiTableSA205105Group& setLmGroupId( LONG newVal );
    CtiTableSA205105Group& setRouteId( LONG newVal );
    CtiTableSA205105Group& setOperationalAddress( std::string newVal );
    CtiTableSA205105Group& setLoadNumber( std::string newVal );
    static std::string getTableName( void );

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
