#pragma once

#include "row_reader.h"

#include "dbmemobject.h"

class IM_EX_CTIYUKONDB CtiTblPAO : public CtiMemDBObject
{

protected:

    LONG             _paObjectID;
    std::string      _category;
    INT              _class;
    std::string      _classStr;
    std::string      _name;
    INT              _type;
    std::string      _typeStr;
    std::string      _description;
    std::string      _paostatistics;

    bool             _disableFlag;

public:

    typedef CtiMemDBObject Inherited;


    CtiTblPAO();

    CtiTblPAO(const CtiTblPAO& aRef);

    ~CtiTblPAO();

    CtiTblPAO& operator=(const CtiTblPAO& aRef);

    LONG getID() const;
    CtiTblPAO& setID( LONG paoid );

    std::string getCategory() const;
    std::string& getCategory();
    CtiTblPAO& setCategory(const std::string &catStr);

    INT getClass() const;
    INT& getClass();
    CtiTblPAO& setClass(const INT &clsStr);

    const std::string& getClassStr() const;
    CtiTblPAO& setClassStr(const std::string& classStr);

    std::string getName() const;
    std::string& getName();
    CtiTblPAO& setName(const std::string &nmStr);

    INT getType() const;
    CtiTblPAO& setType(const INT &tpStr);

    const std::string& getTypeStr() const;
    CtiTblPAO& setTypeStr(const std::string& typeStr);

    std::string getDescription() const;
    std::string& getDescription();
    CtiTblPAO& setDescription(const std::string &desStr);

    std::string getDisableFlagStr() const;

    bool isInhibited() const;
    CtiTblPAO& setDisableFlag(const bool flag);
    CtiTblPAO& setDisableFlagStr(const std::string& flag);

    void resetDisableFlag(bool b = FALSE);

    std::string getStatisticsStr() const;
    CtiTblPAO& setStatisticsStr(const std::string& );

    static std::string getTableName();

    bool Update();
    bool Insert();
    bool Delete();
    void DecodeDatabaseReader(Cti::RowReader &rdr);

    void DumpData();
};

inline bool CtiTblPAO::isInhibited() const { return _disableFlag; }
