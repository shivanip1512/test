
#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA_205105_H__
#define __TBL_LMG_SA_205105_H__

/*---------------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa_205105
*
* Class:  
* Date:   3/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/05 19:50:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <windows.h>

#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/db.h>

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"


class IM_EX_CTIYUKONDB CtiTableSA205105Group : public CtiMemDBObject
{
protected:

    LONG        _lmGroupId;
    LONG        _routeId;

    RWCString   _operationalAddress;
    RWCString   _loadNumber;
    int         _function;

private:

    public:

    CtiTableSA205105Group();
    CtiTableSA205105Group( const CtiTableSA205105Group& aRef );

    virtual ~CtiTableSA205105Group();

    CtiTableSA205105Group& operator=( const CtiTableSA205105Group& aRef );

    LONG getLmGroupId( void ) const;
    LONG getRouteId( void ) const;
    RWCString getOperationalAddress( void ) const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    RWCString getLoadNumber( void ) const;
    int getFunction( void ) const;

    CtiTableSA205105Group& setLmGroupId( LONG newVal );
    CtiTableSA205105Group& setRouteId( LONG newVal );
    CtiTableSA205105Group& setOperationalAddress( RWCString newVal );
    CtiTableSA205105Group& setFunction( int newVal );          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSA205105Group& setLoadNumber( RWCString newVal );
    static RWCString getTableName( void );

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_LMG_SA_205105_H__
