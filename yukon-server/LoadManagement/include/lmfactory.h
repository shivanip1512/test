#pragma once

#include "lmgroupbase.h"
#include "row_reader.h"

class CtiLMGroupFactory
{
public:
    CtiLMGroupPtr createLMGroup(Cti::RowReader &rdr);
};
