
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_pttag
*
* Class:  CtiTableDynamicTag
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/12/30 21:57:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DYN_PTTAG_H__
#define __TBL_DYN_PTTAG_H__

#include <rw/db/db.h>
#include <rw/rwtime.h>

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


#define CTITABLEDYNAMICTAG_MAX_USERNAME       60
#define CTITABLEDYNAMICTAG_MAX_ACTION         20
#define CTITABLEDYNAMICTAG_MAX_DESCRIPTION    120

#define CTITABLEDYNAMICTAG_MAX_REFSTR         60
#define CTITABLEDYNAMICTAG_MAX_FORSTR         60

class IM_EX_CTIYUKONDB CtiTableDynamicTag : public CtiMemDBObject
{
protected:

    int             _instanceId;        // no two tags share the same one
    int             _pointId;           //
    int             _tagid;             // refers to id in tag table

    RWCString       _userName;          // VC(60)
    RWCString       _actionStr;         // VC(20)
    RWCString       _descriptionStr;    // VC(120)

    RWDBDateTime    _tagtime;           // when was tag created
    RWCString       _referenceStr;      // job id, etc, user field
    RWCString       _taggedForStr;      // user field


private:

public:

    typedef CtiMemDBObject Inherited;


    CtiTableDynamicTag();
    CtiTableDynamicTag(const CtiTableDynamicTag& aRef);

    virtual ~CtiTableDynamicTag();

    CtiTableDynamicTag& operator=(const CtiTableDynamicTag& aRef);
    virtual int operator==(const CtiTableDynamicTag&) const;
    bool operator<(const CtiTableDynamicTag& aRef) const;

    static RWCString getTableName();

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static RWDBStatus Delete(int instance);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);


    int getInstanceId() const;        // no two tags share the same one
    int getPointId() const;           //
    int getTagId() const;             // refers to id in tag table

    RWCString getUserName() const;          // VC(60)  Console user name
    RWCString getActionStr() const;         // VC(20)
    RWCString getDescriptionStr() const;    // VC(120)

    RWDBDateTime getTagTime() const;        // when was tag created
    RWCString getReferenceStr() const;      // job id, etc, user field
    RWCString getTaggedForStr() const;

    CtiTableDynamicTag& setInstanceId(int id);        // no two tags share the same one
    CtiTableDynamicTag& setPointId(int id);           //
    CtiTableDynamicTag& setTagId(int id);             // refers to id in tag table

    CtiTableDynamicTag& setUserName(const RWCString& str);          // VC(60)  Console user name
    CtiTableDynamicTag& setActionStr(const RWCString& str);         // VC(20)
    CtiTableDynamicTag& setDescriptionStr(const RWCString& str);    // VC(120)

    CtiTableDynamicTag& setTagTime(const RWDBDateTime &dbdt);        // when was tag created
    CtiTableDynamicTag& setReferenceStr(const RWCString& str);      // job id, etc, user field
    CtiTableDynamicTag& setTaggedForStr(const RWCString& str);

    virtual void dump();

};
#endif // #ifndef __TBL_DYN_PTTAG_H__
