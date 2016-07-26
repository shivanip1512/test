#pragma once

#include "dbmemobject.h"
#include "row_reader.h"
#include "loggable.h"
#include "utility.h"


//This is the lite version of CtiTblPAO. The only string stored by this object is the name.
class IM_EX_CTIYUKONDB CtiTblPAOLite : public CtiMemDBObject, public Cti::Loggable
{
private:
    CtiTblPAOLite(const CtiTblPAOLite&) = delete;
    CtiTblPAOLite(CtiTblPAOLite&&) = delete;
    CtiTblPAOLite& operator=(const CtiTblPAOLite&) = delete;
    CtiTblPAOLite& operator=(CtiTblPAOLite&&) = delete;

protected:

    long           _paObjectID;
    int            _class;
    std::string    _name;
    int            _type;

    bool           _disableFlag;

    virtual void setPaoType(const std::string& category, const std::string& type);

public:

    typedef CtiMemDBObject Inherited;

    CtiTblPAOLite();
    virtual ~CtiTblPAOLite();

    long   getID()    const;
    int    getClass() const;
    std::string getName()  const;
    int    getType()  const;

    void setID( long paoid, Cti::Test::use_in_unit_tests_only& );
    void setType(const int type);

    bool isInhibited() const;

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string toString() const override;
};

inline bool CtiTblPAOLite::isInhibited() const { return _disableFlag; }
