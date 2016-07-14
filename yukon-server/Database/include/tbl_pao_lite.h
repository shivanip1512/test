#pragma once

#include "dbmemobject.h"
#include "row_reader.h"
#include "loggable.h"

#include <windows.h>
#include <limits.h>


//This is the lite version of CtiTblPAO. The only string stored by this object is the name.
class IM_EX_CTIYUKONDB CtiTblPAOLite : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
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

    void setID( LONG paoid );
    void setType(const int type);

    bool isInhibited() const;

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string toString() const override;
};

inline bool CtiTblPAOLite::isInhibited() const { return _disableFlag; }
