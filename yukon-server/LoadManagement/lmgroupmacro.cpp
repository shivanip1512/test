/*---------------------------------------------------------------------------
        Filename:  lmgroupmacro.cpp

        Programmer:  Aaron Lauinger
        
        Description:    Source file for CtiLMGroupMacro
	                Represents a lm macro group.

        Initial Date:  6/24/2004
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001-2004
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmgroupmacro.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;


CtiLMGroupMacro::CtiLMGroupMacro()
{   
}

CtiLMGroupMacro::CtiLMGroupMacro(RWDBReader& rdr)
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
    return (new CtiLMGroupMacro(*this));
}

void CtiLMGroupMacro::restore(RWDBReader& rdr)
{
    CtiLMGroupBase::restore(rdr);
}

