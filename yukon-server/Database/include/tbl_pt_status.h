#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTablePointStatus : public CtiMemDBObject, private boost::noncopyable
{
    int  _initialState;

public:

    CtiTablePointStatus();

    int getInitialState() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);
};
