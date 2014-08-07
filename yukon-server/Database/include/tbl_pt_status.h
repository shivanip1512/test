#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointStatus(const CtiTablePointStatus&);
    CtiTablePointStatus& operator=(const CtiTablePointStatus&);

    int  _initialState;

public:

    CtiTablePointStatus();

    int getInitialState() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);
};
