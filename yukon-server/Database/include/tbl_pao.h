
#pragma warning( disable : 4786)
#ifndef __TBL_PAO_H__
#define __TBL_PAO_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pao
*
* Class:  CtiTblPAO
* Date:   9/12/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_pao.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <limits.h>

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"

class IM_EX_CTIYUKONDB CtiTblPAO : public CtiMemDBObject
{

protected:

    LONG           _paObjectID;
    RWCString      _category;
    INT            _class;
    RWCString      _classStr;
    RWCString      _name;
    INT            _type;
    RWCString      _typeStr;
    RWCString      _description;

    bool           _disableFlag;

public:

    typedef CtiMemDBObject Inherited;


    CtiTblPAO();

    CtiTblPAO(const CtiTblPAO& aRef);

    virtual ~CtiTblPAO();

    CtiTblPAO& operator=(const CtiTblPAO& aRef);

    LONG getID() const;
    CtiTblPAO& setID( LONG paoid );

    RWCString getCategory() const;
    RWCString& getCategory();
    CtiTblPAO& setCategory(const RWCString &catStr);

    INT getClass() const;
    INT& getClass();
    CtiTblPAO& setClass(const INT &clsStr);

    const RWCString& getClassStr() const;
    CtiTblPAO& setClassStr(const RWCString& classStr);

    RWCString getName() const;
    RWCString& getName();
    CtiTblPAO& setName(const RWCString &nmStr);

    INT getType() const;
    CtiTblPAO& setType(const INT &tpStr);

    const RWCString& getTypeStr() const;
    CtiTblPAO& setTypeStr(const RWCString& typeStr);

    RWCString getDescription() const;
    RWCString& getDescription();
    CtiTblPAO& setDescription(const RWCString &desStr);

    bool getDisableFlag() const;
    RWCString getDisableFlagStr() const;

    bool isInhibited() const;
    CtiTblPAO& setDisableFlag(const bool flag);
    CtiTblPAO& setDisableFlagStr(const RWCString& flag);

    void resetDisableFlag(bool b = FALSE);

    static RWCString getTableName();
    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    virtual RWDBStatus Restore();
    virtual RWDBStatus Update();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Delete();
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual void DumpData();
};

inline bool CtiTblPAO::getDisableFlag() const { return _disableFlag; }
inline bool CtiTblPAO::isInhibited() const { return getDisableFlag(); }

#endif // #ifndef __TBL_PAO_H__
