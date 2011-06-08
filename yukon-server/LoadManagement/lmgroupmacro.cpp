/*---------------------------------------------------------------------------
        Filename:  lmgroupmacro.cpp

        Programmer:  Aaron Lauinger
        
        Description:    Source file for CtiLMGroupMacro
	                Represents a lm macro group.

        Initial Date:  6/24/2004
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001-2004
---------------------------------------------------------------------------*/
#include "yukon.h"

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

