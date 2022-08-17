#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupmacro.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

using std::vector;

extern ULONG _LM_DEBUG;


CtiLMGroupMacro::CtiLMGroupMacro()
{
}

CtiLMGroupMacro::CtiLMGroupMacro(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupMacro::~CtiLMGroupMacro()
{
}

const vector<CtiLMGroupBase*> CtiLMGroupMacro::getChildren() const
{
    return _children;
}

void CtiLMGroupMacro::setChildren(const vector<CtiLMGroupBase*>& children)
{
    _children = children;
}

CtiLMGroupBase* CtiLMGroupMacro::replicate() const
{
    return (CTIDBG_new CtiLMGroupMacro(*this));
}

void CtiLMGroupMacro::restore(Cti::RowReader &rdr)
{
    CtiLMGroupBase::restore(rdr);
}

