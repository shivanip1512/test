/*-----------------------------------------------------------------------------*
*
* File:   tbl_pao_lite
*
* Date:   8/15/2008
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pao.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/10/28 19:21:40 $
*
* Copyright (c) 2008 Cooper Industries. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PAO_LITE_H__
#define __TBL_PAO_LITE_H__

#include <windows.h>
#include <limits.h>

#include <rw/db/reader.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"

//This is the lite version of CtiTblPAO. The only string stored by this object is the name.
class IM_EX_CTIYUKONDB CtiTblPAOLite : public CtiMemDBObject
{

protected:

    LONG           _paObjectID;
    INT            _class;
    string         _name;
    INT            _type;

    bool           _disableFlag;

public:

    typedef CtiMemDBObject Inherited;

    CtiTblPAOLite();

    CtiTblPAOLite(const CtiTblPAOLite& aRef);

    virtual ~CtiTblPAOLite();

    CtiTblPAOLite& operator=(const CtiTblPAOLite& aRef);

    LONG   getID()    const;
    INT    getClass() const;
    string getName()  const;
    INT    getType()  const;

    CtiTblPAOLite& setID( LONG paoid );
    CtiTblPAOLite& setType(const INT &type);

    bool isInhibited() const;

    static string getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual RWDBStatus Restore();
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual void DumpData();
};

inline bool CtiTblPAOLite::isInhibited() const { return _disableFlag; }

#endif // #ifndef __TBL_PAO_LITE_H__
