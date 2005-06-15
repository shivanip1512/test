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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/06/15 19:20:30 $
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

    enum Keys
    {
        Key_Invalid  =  -1,
        Key_MCTSSpec = 100,
        Key_MCTIEDLoadProfileRate,
        Key_MCTLoadProfileConfig
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
    static const string _key_mct_loadprofile_config;
    static const string _key_mct_ied_loadprofile_rate;

    typedef map<CtiApplication_t, const string *> owner_map_t;
    typedef map<Keys,             const string *> key_map_t;

    static const owner_map_t _owner_map;
    static const key_map_t   _key_map;

    static owner_map_t init_owner_map();
    static key_map_t   init_key_map();

    long             _entry_id;
    long             _pao_id;
    CtiApplication_t _owner_id;

    Keys   _key;
    string _value;

    static const string _empty_string;

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDynamicPaoInfo();
    CtiTableDynamicPaoInfo(const CtiTableDynamicPaoInfo &aRef);
    CtiTableDynamicPaoInfo(long paoid, Keys k);  //  owner doesn't matter until the new row gets written to the DB

    virtual ~CtiTableDynamicPaoInfo();

    CtiTableDynamicPaoInfo& operator=(const CtiTableDynamicPaoInfo &aRef);
    bool                    operator<(const CtiTableDynamicPaoInfo &rhs) const;  //  this is for the set in dev_base

    static RWCString getTableName();

    bool hasRow() const;

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector, CtiApplication_t app_id);
    void DecodeDatabaseReader(RWDBReader& rdr);

    long             getPaoID()   const;
    long             getEntryID() const;
    CtiApplication_t getOwner()   const;
    Keys             getKey()     const;
    string           getValue() const;
    void             getValue(long &destination)   const;
    void             getValue(double &destination) const;
    void             getValue(string &destination) const;

    CtiTableDynamicPaoInfo &setPaoID(long pao_id);
    CtiTableDynamicPaoInfo &setEntryID(long entry_id);
    CtiTableDynamicPaoInfo &setOwner(CtiApplication_t o);
    CtiTableDynamicPaoInfo &setKey(Keys k);
    CtiTableDynamicPaoInfo &setValue(const string &s);
    CtiTableDynamicPaoInfo &setValue(double d);
    CtiTableDynamicPaoInfo &setValue(long l);

    //CtiTableDynamicPaoInfo &setDirty(bool dirty);

    virtual void dump();
};


#endif // #ifndef __TBL_DYN_PAOINFO_H__
