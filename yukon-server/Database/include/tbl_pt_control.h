#pragma once

#include "row_reader.h"

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"

class IM_EX_CTIYUKONDB CtiTablePointControl : public CtiMemDBObject
{
    int  _controlOffset;
    bool _controlInhibit;

public:

    CtiTablePointControl();

    int getControlOffset() const;
    void setControlOffset(int i);
    bool isControlInhibited() const;

    void DecodeDatabaseReader(Cti::RowReader &rdr);
};
