/*---------------------------------------------------------------------------
        Filename:  lmgroupemetcon.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMGroupMCT.
                        CtiLMGroupMCT maintains the state and handles
                        the persistence of mct groups in Load Management.

        Initial Date:  7/23/2003
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2003
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmgroupmct.h"
#include "lmid.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMGroupMCT, CTILMGROUPMCT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMGroupMCT::CtiLMGroupMCT()
{   
}

CtiLMGroupMCT::CtiLMGroupMCT(RWDBReader& rdr)
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

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMGroupMCT::restoreGuts(RWvistream& istrm)
{
    Inherited::restoreGuts( istrm );
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMGroupMCT::saveGuts(RWvostream& ostrm ) const  
{
    Inherited::saveGuts( ostrm );
    return;
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
    operator==
---------------------------------------------------------------------------*/
int CtiLMGroupMCT::operator==(const CtiLMGroupMCT& right) const
{
    return Inherited::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMGroupMCT::operator!=(const CtiLMGroupMCT& right) const
{

    return Inherited::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMGroupBase* CtiLMGroupMCT::replicate() const
{
    return (new CtiLMGroupMCT(*this));
}


