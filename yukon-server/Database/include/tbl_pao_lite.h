#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include "dbmemobject.h"
#include "row_reader.h"
#include "loggable.h"

#include <windows.h>
#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


//This is the lite version of CtiTblPAO. The only string stored by this object is the name.
class IM_EX_CTIYUKONDB CtiTblPAOLite : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTblPAOLite(const CtiTblPAOLite&);
    CtiTblPAOLite& operator=(const CtiTblPAOLite&);

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
