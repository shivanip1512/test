
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_point
*
* Class:  CtiTablePointGroup
* Date:   4/3/2006
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2006/04/05 16:22:18 $
* HISTORY      :
* $Log: tbl_lmg_point.h,v $
* Revision 1.2  2006/04/05 16:22:18  cplender
* Initial Revision
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_POINT_H__
#define __TBL_LMG_POINT_H__

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


class IM_EX_CTIYUKONDB CtiTablePointGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _controlDevice;
    LONG _controlPoint;
    LONG _controlStartRawState;

    string _rawstate[2];             // These are the state strings.

private:

public:

    CtiTablePointGroup();
    CtiTablePointGroup(const CtiTablePointGroup& aRef);
    virtual ~CtiTablePointGroup();

    LONG getLmGroupId() const;
    LONG getControlDevice() const;
    LONG getControlPoint() const;
    LONG getControlStartRawState() const;
    string getControlStartString() const;
    string getControlStopString() const;

    static string getTableName( void );

    static void getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector );
    virtual void DecodeDatabaseReader( RWDBReader &rdr );
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};

#endif // #ifndef __TBL_LMG_POINT_H__
