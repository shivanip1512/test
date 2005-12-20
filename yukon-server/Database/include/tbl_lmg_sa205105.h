
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
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

    string   _operationalAddress;
    string   _loadNumber;

private:

public:
    CtiTableSA205105Group();
    CtiTableSA205105Group( const CtiTableSA205105Group& aRef );

    virtual ~CtiTableSA205105Group();

    CtiTableSA205105Group& operator=( const CtiTableSA205105Group& aRef );

    LONG getLmGroupId( void ) const;
    LONG getRouteId( void ) const;
    string getOperationalAddress( void ) const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    string getLoadNumber( void ) const;
    int getFunction( bool shed ) const;

    CtiTableSA205105Group& setLmGroupId( LONG newVal );
    CtiTableSA205105Group& setRouteId( LONG newVal );
    CtiTableSA205105Group& setOperationalAddress( string newVal );
    CtiTableSA205105Group& setLoadNumber( string newVal );
    static string getTableName( void );

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_LMG_SA_205105_H__
