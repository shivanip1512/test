
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

class IM_EX_CTIYUKONDB CtiTblPAO : public CtiMemDBObject
{

protected:

    LONG           _paObjectID;
    string      _category;
    INT            _class;
    string      _classStr;
    string      _name;
    INT            _type;
    string      _typeStr;
    string      _description;
    string      _paostatistics;

    bool           _disableFlag;

public:

    typedef CtiMemDBObject Inherited;


    CtiTblPAO();

    CtiTblPAO(const CtiTblPAO& aRef);

    virtual ~CtiTblPAO();

    CtiTblPAO& operator=(const CtiTblPAO& aRef);

    LONG getID() const;
    CtiTblPAO& setID( LONG paoid );

    string getCategory() const;
    string& getCategory();
    CtiTblPAO& setCategory(const string &catStr);

    INT getClass() const;
    INT& getClass();
    CtiTblPAO& setClass(const INT &clsStr);

    const string& getClassStr() const;
    CtiTblPAO& setClassStr(const string& classStr);

    string getName() const;
    string& getName();
    CtiTblPAO& setName(const string &nmStr);

    INT getType() const;
    CtiTblPAO& setType(const INT &tpStr);

    const string& getTypeStr() const;
    CtiTblPAO& setTypeStr(const string& typeStr);

    string getDescription() const;
    string& getDescription();
    CtiTblPAO& setDescription(const string &desStr);

    bool getDisableFlag() const;
    string getDisableFlagStr() const;

    bool isInhibited() const;
    CtiTblPAO& setDisableFlag(const bool flag);
    CtiTblPAO& setDisableFlagStr(const string& flag);

    void resetDisableFlag(bool b = FALSE);

    string getStatisticsStr() const;
    CtiTblPAO& setStatisticsStr(const string& );


    static string getTableName();
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

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
