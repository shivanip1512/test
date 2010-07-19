#ifndef __LMFACTORY_H__
#define __LMFACTORY_H__

#include "lmgroupbase.h"
#include "row_reader.h"

class CtiLMGroupFactory
{
public:
    CtiLMGroupPtr createLMGroup(Cti::RowReader &rdr);
};

#endif
