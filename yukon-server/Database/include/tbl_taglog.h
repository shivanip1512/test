
/*-----------------------------------------------------------------------------*
*
* File:   tbl_taglog
*
* Class:  CtiTableTagLog
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:16:09 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_TAGLOG_H__
#define __TBL_TAGLOG_H__

#include <rw/db/db.h>


#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


#define CTITABLETAGLOG_MAX_USERNAME       60
#define CTITABLETAGLOG_MAX_ACTION         20
#define CTITABLETAGLOG_MAX_DESCRIPTION    120

#define CTITABLETAGLOG_MAX_REFSTR         60
#define CTITABLETAGLOG_MAX_FORSTR         60

class IM_EX_CTIYUKONDB CtiTableTagLog : public CtiMemDBObject
{
protected:

    int             _logId;             // no two tags share the same one
    int             _instanceId;        // Matches the dynamictag entry.  Follows the life cycle of the tag.
    int             _pointId;           //
    int             _tagid;             // refers to id in tag table

    string       _userName;          // VC(60)
    string       _actionStr;         // VC(20)
    string       _descriptionStr;    // VC(120)

    CtiTime    _tagtime;           // when was tag created
    string       _referenceStr;      // job id, etc, user field
    string       _taggedForStr;      // user field

    static int _maxInstanceId;
    static int _nextLogId;

private:

public:

    typedef CtiMemDBObject Inherited;


    CtiTableTagLog();
    CtiTableTagLog(const CtiTableTagLog& aRef);

    virtual ~CtiTableTagLog();

    CtiTableTagLog& operator=(const CtiTableTagLog& aRef);
    virtual int operator==(const CtiTableTagLog&) const;
    bool operator<(const CtiTableTagLog& aRef) const;

    static string getTableName();

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static RWDBStatus Delete(int log);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);


    int getLogId() const;           // no two tags share the same one
    int getInstanceId() const;      // Matches the dynamictag entry.  Follows the life cycle of the tag.
    int getPointId() const;           //
    int getTagId() const;             // refers to id in tag table

    string getUserName() const;          // VC(60)  Console user name
    string getActionStr() const;         // VC(20)
    string getDescriptionStr() const;    // VC(120)

    CtiTime getTagTime() const;        // when was tag created
    string getReferenceStr() const;      // job id, etc, user field
    string getTaggedForStr() const;

    CtiTableTagLog& setLogId(int id);        // no two tags share the same one
    CtiTableTagLog& setInstanceId(int id);        // Matches the dynamictag entry.  Follows the life cycle of the tag.
    CtiTableTagLog& setPointId(int id);           //
    CtiTableTagLog& setTagId(int id);             // refers to id in tag table

    CtiTableTagLog& setUserName(const string& str);          // VC(60)  Console user name
    CtiTableTagLog& setActionStr(const string& str);         // VC(20)
    CtiTableTagLog& setDescriptionStr(const string& str);    // VC(120)

    CtiTableTagLog& setTagTime(const CtiTime &dbdt);        // when was tag created
    CtiTableTagLog& setReferenceStr(const string& str);      // job id, etc, user field
    CtiTableTagLog& setTaggedForStr(const string& str);

    static int getNextLogId();
    static int getLastNextLogId();

    static int getMaxInstanceId();
    static int getLastMaxInstanceId();

    virtual void dump();

};
#endif // #ifndef __TBL_TAGLOG_H__
