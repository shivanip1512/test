/*---------------------------------------------------------------------------
        Filename:  ccstate.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiCCState.
                        CtiCCState is a copy of each entry in the state table

        Initial Date:  9/04/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#include "ccid.h"
#include "ccstate.h"
#include "logger.h"

extern BOOL _CC_DEBUG;

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
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _text;
}
/*---------------------------------------------------------------------------
    ForegroundColor
    
    Returns the foreground color of the state
---------------------------------------------------------------------------*/
ULONG CtiCCState::getForegroundColor() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _foregroundcolor;
}

/*---------------------------------------------------------------------------
    BackgroundColor
    
    Returns the background color of the state
---------------------------------------------------------------------------*/
ULONG CtiCCState::getBackgroundColor() const
{   
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _backgroundcolor;
}

/*---------------------------------------------------------------------------
    setText
    
    Sets the Text of the state
---------------------------------------------------------------------------*/    
CtiCCState& CtiCCState::setText(const RWCString& text)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _text = text;

    return *this;
}

/*---------------------------------------------------------------------------
    setForegroundColor
    
    Sets the foreground color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setForegroundColor(ULONG foregroundcolor)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _foregroundcolor = foregroundcolor;

    return *this;
}

/*---------------------------------------------------------------------------
    setBackgroundColor
    
    Sets the background color of the state
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::setBackgroundColor(ULONG backgroundcolor)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _backgroundcolor = backgroundcolor;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCState::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    RWCollectable::saveGuts( ostrm );

    ostrm << _text
          << _foregroundcolor
          << _backgroundcolor;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCState& CtiCCState::operator=(const CtiCCState& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

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
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    rdr["text"] >> _text;
    rdr["foregroundcolor"] >> _foregroundcolor;
    rdr["backgroundcolor"] >> _backgroundcolor;
}
