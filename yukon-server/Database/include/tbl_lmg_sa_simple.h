
#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA_SIMPLE_H__
#define __TBL_LMG_SA_SIMPLE_H__

/*---------------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa_simple
*
* Class:  
* Date:   3/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/03/10 18:44:35 $
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


class IM_EX_CTIYUKONDB CtiTableSASimpleGroup : public CtiMemDBObject
{
protected:

    LONG        _lmGroupId;
    LONG        _routeId;

    RWCString   _operationalAddress;
    int         _nominalTimeout;        // Switch is hardcoded to be off for this duration in seconds!
    int         _virtualTimeout;        // Group is desired to control for this duration in seconds.  If these are not equal multiple control messages must be sent to make it occur (master cycle-like)
    int         _function;

public:

    CtiTableSASimpleGroup();
    CtiTableSASimpleGroup( const CtiTableSASimpleGroup& aRef );
    virtual ~CtiTableSASimpleGroup();

    CtiTableSASimpleGroup& operator=( const CtiTableSASimpleGroup& aRef );

    LONG getLmGroupId( void ) const;
    LONG getRouteId( void ) const;
    RWCString getOperationalAddress( void ) const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction( void ) const;
    int getNominalTimeout( void ) const;
    int getVirtualTimeout( void ) const;

    CtiTableSASimpleGroup& setLmGroupId( LONG newVal );
    CtiTableSASimpleGroup& setRouteId( LONG newVal );
    CtiTableSASimpleGroup& setOperationalAddress( RWCString newVal );
    CtiTableSASimpleGroup& setFunction( int newVal );          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSASimpleGroup& setNominalTimeout( int newVal );
    CtiTableSASimpleGroup& setVirtualTimeout( int newVal );

    static RWCString getTableName( void );

    static void getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector );
    virtual void DecodeDatabaseReader( RWDBReader &rdr );
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_LMG_SA_SIMPLE_H__
