#ifndef __LMFACTORY_H__
#define __LMFACTORY_H__

#include "lmgroupbase.h"

class CtiLMGroupFactory
{
public:
    CtiLMGroupPtr createLMGroup(RWDBReader& rdr);
};

#endif
