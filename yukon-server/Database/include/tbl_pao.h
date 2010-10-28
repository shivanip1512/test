#pragma once

#include "row_reader.h"

#include "dbmemobject.h"

class IM_EX_CTIYUKONDB CtiTblPAO : public CtiMemDBObject
{

protected:

    LONG        _paObjectID;
    string      _category;
    INT         _class;
    string      _classStr;
    string      _name;
    INT         _type;
    string      _typeStr;
    string      _description;
    string      _paostatistics;

    bool        _disableFlag;

public:

    typedef CtiMemDBObject Inherited;


    CtiTblPAO();

    CtiTblPAO(const CtiTblPAO& aRef);

    ~CtiTblPAO();

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

    string getDisableFlagStr() const;

    bool isInhibited() const;
    CtiTblPAO& setDisableFlag(const bool flag);
    CtiTblPAO& setDisableFlagStr(const string& flag);

    void resetDisableFlag(bool b = FALSE);

    string getStatisticsStr() const;
    CtiTblPAO& setStatisticsStr(const string& );

    static string getTableName();

    bool Update();
    bool Insert();
    bool Delete();
    void DecodeDatabaseReader(Cti::RowReader &rdr);

    void DumpData();
};

inline bool CtiTblPAO::isInhibited() const { return _disableFlag; }
