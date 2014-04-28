#include "precompiled.h"

#include "dbaccess.h"
#include "lmgroupmct.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMGroupMCT, CTILMGROUPMCT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupMCT::CtiLMGroupMCT()
{
}

CtiLMGroupMCT::CtiLMGroupMCT(Cti::RowReader &rdr)
{
    restore(rdr);
}

CtiLMGroupMCT::CtiLMGroupMCT(const CtiLMGroupMCT& groupmct)
{
    operator=(groupmct);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMGroupMCT::~CtiLMGroupMCT()
{
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMGroupMCT& CtiLMGroupMCT::operator=(const CtiLMGroupMCT& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupMCT::replicate() const
{
    return (CTIDBG_new CtiLMGroupMCT(*this));
}


