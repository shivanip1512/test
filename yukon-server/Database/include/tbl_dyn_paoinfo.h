/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_pttag
*
* Class:  CtiTableDynamicPaoInfo
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/05/11 14:58:12 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DYN_PAOINFO_H__
#define __TBL_DYN_PAOINFO_H__

#include <rw/db/db.h>
#include <rw/rwtime.h>

#include <string>
#include <map>
using namespace std;

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfo : public CtiMemDBObject
{
public:

    enum Owners
    {
        Owner_Invalid  =  -1,
        Owner_Dispatch = 100,
        Owner_Porter,
        Owner_Scanner,
        Owner_CapControl,
        Owner_LoadManagement,
        Owner_CalcLogic
    };

    enum Keys
    {
        Key_Invalid  =  -1,
        Key_MCTSSpec = 100
        //  make sure to add any new enum values to the string map
    };

protected:

    static const string _owner_dispatch;
    static const string _owner_porter;
    static const string _owner_scanner;
    static const string _owner_capcontrol;
    static const string _owner_loadmanagement;
    static const string _owner_calc;

    static const string _key_mct_sspec;

    typedef map<Owners, const string *> owner_map_t;
    typedef map<Keys,   const string *> key_map_t;

    static const owner_map_t _owner_map;
    static const key_map_t   _key_map;

    static owner_map_t init_owner_map();
    static key_map_t   init_key_map();

    long   _pao_id;
    Owners _owner_id;

    Keys   _key;
    string _value;
    long   _value_as_long;
    double _value_as_double;

    static const string _empty_string;

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDynamicPaoInfo();
    CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo& aRef);

    virtual ~CtiTableDynamicPaoInfo();

    CtiTableDynamicPaoInfo& operator=(const CtiTableDynamicPaoInfo& aRef);

    static RWCString getTableName();

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);

    long          getPaoID() const;
    Owners        getOwner() const;
    Keys          getKey()   const;
    const string &getStringValue() const;
    double        getDoubleValue();
    long          getLongValue();

    CtiTableDynamicPaoInfo &setPaoID(long pao_id);
    CtiTableDynamicPaoInfo &setOwner(Owners o);
    CtiTableDynamicPaoInfo &setKey(Keys k);
    CtiTableDynamicPaoInfo &setValue(const string &s);
    CtiTableDynamicPaoInfo &setValue(double d);
    CtiTableDynamicPaoInfo &setValue(long l);

    virtual void dump();
};
#endif // #ifndef __TBL_DYN_PAOINFO_H__
