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

    LONG           _paObjectID;
    INT            _class;
    std::string         _name;
    INT            _type;

    bool           _disableFlag;

public:

    typedef CtiMemDBObject Inherited;

    CtiTblPAOLite();
    virtual ~CtiTblPAOLite();

    LONG   getID()    const;
    INT    getClass() const;
    std::string getName()  const;
    INT    getType()  const;

    CtiTblPAOLite& setID( LONG paoid );
    CtiTblPAOLite& setType(const INT &type);

    bool isInhibited() const;

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string toString() const override;
};

inline bool CtiTblPAOLite::isInhibited() const { return _disableFlag; }
