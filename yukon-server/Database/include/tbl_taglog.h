#pragma once

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "row_reader.h"
#include "database_connection.h"


#define CTITABLETAGLOG_MAX_USERNAME       60
#define CTITABLETAGLOG_MAX_ACTION         20
#define CTITABLETAGLOG_MAX_DESCRIPTION    120

#define CTITABLETAGLOG_MAX_REFSTR         60
#define CTITABLETAGLOG_MAX_FORSTR         60

class IM_EX_CTIYUKONDB CtiTableTagLog : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableTagLog(const CtiTableTagLog&);
    CtiTableTagLog& operator=(const CtiTableTagLog&);

protected:

    int             _logId;             // no two tags share the same one
    int             _instanceId;        // Matches the dynamictag entry.  Follows the life cycle of the tag.
    int             _pointId;           //
    int             _tagid;             // refers to id in tag table

    std::string       _userName;          // VC(60)
    std::string       _actionStr;         // VC(20)
    std::string       _descriptionStr;    // VC(120)

    CtiTime    _tagtime;           // when was tag created
    std::string       _referenceStr;      // job id, etc, user field
    std::string       _taggedForStr;      // user field

    static int _maxInstanceId;
    static int _nextLogId;

public:

    typedef CtiMemDBObject Inherited;

    CtiTableTagLog();
    virtual ~CtiTableTagLog();

    virtual int operator==(const CtiTableTagLog&) const;
    bool operator<(const CtiTableTagLog& aRef) const;

    static std::string getTableName();

    bool Insert(Cti::Database::DatabaseConnection &conn);
    bool Update(Cti::Database::DatabaseConnection &conn);

    int getLogId() const;           // no two tags share the same one
    int getInstanceId() const;      // Matches the dynamictag entry.  Follows the life cycle of the tag.
    int getPointId() const;           //
    int getTagId() const;             // refers to id in tag table

    std::string getUserName() const;          // VC(60)  Console user name
    std::string getActionStr() const;         // VC(20)
    std::string getDescriptionStr() const;    // VC(120)

    CtiTime getTagTime() const;        // when was tag created
    std::string getReferenceStr() const;      // job id, etc, user field
    std::string getTaggedForStr() const;

    CtiTableTagLog& setLogId(int id);        // no two tags share the same one
    CtiTableTagLog& setInstanceId(int id);        // Matches the dynamictag entry.  Follows the life cycle of the tag.
    CtiTableTagLog& setPointId(int id);           //
    CtiTableTagLog& setTagId(int id);             // refers to id in tag table

    CtiTableTagLog& setUserName(const std::string& str);          // VC(60)  Console user name
    CtiTableTagLog& setActionStr(const std::string& str);         // VC(20)
    CtiTableTagLog& setDescriptionStr(const std::string& str);    // VC(120)

    CtiTableTagLog& setTagTime(const CtiTime &dbdt);        // when was tag created
    CtiTableTagLog& setReferenceStr(const std::string& str);      // job id, etc, user field
    CtiTableTagLog& setTaggedForStr(const std::string& str);

    static int getNextLogId();
    static int getLastNextLogId();

    static int getMaxInstanceId();
    static int getLastMaxInstanceId();

    virtual void dump();

};

