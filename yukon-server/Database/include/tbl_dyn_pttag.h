#pragma once


#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "row_reader.h"
#include "database_connection.h"


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

    std::string       _userName;          // VC(60)
    std::string       _actionStr;         // VC(20)
    std::string       _descriptionStr;    // VC(120)

    CtiTime    _tagtime;           // when was tag created
    std::string       _referenceStr;      // job id, etc, user field
    std::string       _taggedForStr;      // user field


public:

    typedef CtiMemDBObject Inherited;


    CtiTableDynamicTag();
    CtiTableDynamicTag(const CtiTableDynamicTag& aRef);

    virtual ~CtiTableDynamicTag();

    CtiTableDynamicTag& operator=(const CtiTableDynamicTag& aRef);
    virtual int operator==(const CtiTableDynamicTag&) const;
    bool operator<(const CtiTableDynamicTag& aRef) const;

    static std::string getTableName();

    bool Insert(Cti::Database::DatabaseConnection &conn);
    bool Update(Cti::Database::DatabaseConnection &conn);

    virtual bool Delete();
    static  bool Delete(int instance);

    static std::string getSQLCoreStatement();

    void DecodeDatabaseReader(Cti::RowReader& rdr);


    int getInstanceId() const;        // no two tags share the same one
    int getPointId() const;           //
    int getTagId() const;             // refers to id in tag table

    std::string getUserName() const;          // VC(60)  Console user name
    std::string getActionStr() const;         // VC(20)
    std::string getDescriptionStr() const;    // VC(120)

    CtiTime getTagTime() const;        // when was tag created
    std::string getReferenceStr() const;      // job id, etc, user field
    std::string getTaggedForStr() const;

    CtiTableDynamicTag& setInstanceId(int id);        // no two tags share the same one
    CtiTableDynamicTag& setPointId(int id);           //
    CtiTableDynamicTag& setTagId(int id);             // refers to id in tag table

    CtiTableDynamicTag& setUserName(const std::string& str);          // VC(60)  Console user name
    CtiTableDynamicTag& setActionStr(const std::string& str);         // VC(20)
    CtiTableDynamicTag& setDescriptionStr(const std::string& str);    // VC(120)

    CtiTableDynamicTag& setTagTime(const CtiTime &dbdt);        // when was tag created
    CtiTableDynamicTag& setReferenceStr(const std::string& str);      // job id, etc, user field
    CtiTableDynamicTag& setTaggedForStr(const std::string& str);

    virtual void dump();

};

