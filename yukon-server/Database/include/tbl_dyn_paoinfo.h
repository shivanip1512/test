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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/04/11 16:15:47 $
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

    enum EntityID
    {
        Entity_Invalid  =  -1,
        Entity_Dispatch = 100,
        Entity_Porter,
        Entity_Scanner,
        Entity_CapControl,
        Entity_LoadManagement,
        Entity_Calc
    };

protected:

    long     _pao_id;
    EntityID _entity_id;

    static const map< int, EntityID > _entity_map;
    static map< int, EntityID > initEntityMap();

    string   _string_parameter;
    long     _long_parameter;

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

    long     getPaoID()    const;
    EntityID getEntityID() const;
    const string &getStringParameter() const;
    long          getLongParameter()   const;

    CtiTableDynamicPaoInfo &setPaoID(long pao_id);
    CtiTableDynamicPaoInfo &setEntityID(EntityID entity_id);
    CtiTableDynamicPaoInfo &setStringParameter(const string &s);
    CtiTableDynamicPaoInfo &setLongParameter(long l);

    virtual void dump();

};
#endif // #ifndef __TBL_DYN_PAOINFO_H__
