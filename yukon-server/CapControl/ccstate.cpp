/*---------------------------------------------------------------------------
        Filename:  ccstate.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiCCState.
                        CtiCCState is a copy of each entry in the state table

        Initial Date:  9/04/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "ccid.h"
#include "ccstate.h"
#include "logger.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCState, CTICCSTATE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCState::CtiCCState()
{   
}

CtiCCState::CtiCCState(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiCCState::CtiCCState(const CtiCCState& state)
{
    operator=( state );
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCState::~CtiCCState()
{
}

/*---------------------------------------------------------------------------
    Text
    
    Returns the text of the state
---------------------------------------------------------------------------*/
const RWCString& CtiCCState::getText() const
{   
    return _text;
}
/*---------------------------------------------------------------------------
    ForegroundColor
    
    Returns the foreground color of the state
---------------------------------------------------------------------------*/
LONG CtiCCState::getForegroundColor() const
{   
    return _foregroundcolor;
}

/*---------------------------------------------------------------------------
    BackgroundColor
    
    Returns the background color of the state
---------------------------------------------------------------------------*/
LONG CtiCCState::getBackgroundColor() const
{   
    return _backgroundcolor;
}

/*---------------------------------------------------------------------------
    setText
    
    Sets the Text of the state
---------------------------------------------------------------------------*/    
CtiCCState& CtiCCState::setText(const RWCString& text)
{
    _text = text;

    return *this;
}

/*---------------------------------------------------------------------------
    setForegroundColor
    
    Sets the foreground color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setForegroundColor(LONG foregroundcolor)
{
    _foregroundcolor = foregroundcolor;

    return *this;
}

/*---------------------------------------------------------------------------
    setBackgroundColor
    
    Sets the background color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setBackgroundColor(LONG backgroundcolor)
{
    _backgroundcolor = backgroundcolor;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCState::restoreGuts(RWvistream& istrm)
{
    RWCollectable::restoreGuts( istrm );

    istrm >> _text
          >> _foregroundcolor
          >> _backgroundcolor;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCState::saveGuts(RWvostream& ostrm ) const  
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _text
          << _foregroundcolor
          << _backgroundcolor;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::operator=(const CtiCCState& right)
{
    if( this != &right )
    {
        _text = right._text;
        _foregroundcolor = right._foregroundcolor;
        _backgroundcolor = right._backgroundcolor;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCState* CtiCCState::replicate() const
{
    return (new CtiCCState(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCState::restore(RWDBReader& rdr)
{
    rdr["text"] >> _text;
    rdr["foregroundcolor"] >> _foregroundcolor;
    rdr["backgroundcolor"] >> _backgroundcolor;
}
