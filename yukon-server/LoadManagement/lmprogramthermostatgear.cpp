/*---------------------------------------------------------------------------
        Filename:  lmprogramthermostatgear.cpp

        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMProgramThermoStatGear.
                        CtiLMProgramThermoStatGear maintains the state and handles
                        the persistence of direct program gears in Load
                        Management.

        Initial Date:  2/9/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmprogramthermostatgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramThermoStatGear, CTILMPROGRAMTHERMOSTATGEAR_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramThermoStatGear::CtiLMProgramThermoStatGear()
{   
}

CtiLMProgramThermoStatGear::CtiLMProgramThermoStatGear(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMProgramThermoStatGear::CtiLMProgramThermoStatGear(const CtiLMProgramThermoStatGear& thermogear)
{
    operator=(thermogear);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramThermoStatGear::~CtiLMProgramThermoStatGear()
{
}

const RWCString& CtiLMProgramThermoStatGear::getSettings() const
{
    return _settings;
}
ULONG CtiLMProgramThermoStatGear::getMinValue() const
{
    return _minvalue;
}
ULONG CtiLMProgramThermoStatGear::getMaxValue() const
{
    return _maxvalue;
}
ULONG CtiLMProgramThermoStatGear::getValueB() const
{
    return _valueb;
}
ULONG CtiLMProgramThermoStatGear::getValueD() const
{
    return _valued;
}
ULONG CtiLMProgramThermoStatGear::getValueF() const
{
    return _valuef;
}
ULONG CtiLMProgramThermoStatGear::getRandom() const
{
    return _random;
}
ULONG CtiLMProgramThermoStatGear::getValueTa() const
{
    return _valueta;
}
ULONG CtiLMProgramThermoStatGear::getValueTb() const
{
    return _valuetb;
}
ULONG CtiLMProgramThermoStatGear::getValueTc() const
{
    return _valuetc;
}
ULONG CtiLMProgramThermoStatGear::getValueTd() const
{
    return _valuetd;
}
ULONG CtiLMProgramThermoStatGear::getValueTe() const
{
    return _valuete;
}
ULONG CtiLMProgramThermoStatGear::getValueTf() const
{
    return _valuetf;
}


CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setSettings(const RWCString& settings)
{
    _settings = settings;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setMinValue(ULONG val)
{
    _minvalue = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setMaxValue(ULONG val)
{
    _maxvalue = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueB(ULONG val)
{
    _valueb = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueD(ULONG val)
{
    _valued = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueF(ULONG val)
{
    _valuef = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setRandom(ULONG val)
{
    _random = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTa(ULONG val)
{
    _valueta = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTb(ULONG val)
{
    _valuetb = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTc(ULONG val)
{
    _valuetc = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTd(ULONG val)
{
    _valuetd = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTe(ULONG val)
{
    _valuete = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTf(ULONG val)
{
    _valuetf = val;
    return *this;
}


/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramThermoStatGear::restoreGuts(RWvistream& istrm)
{
    CtiLMProgramDirectGear::restoreGuts( istrm );

    istrm >> _settings
          >> _minvalue
          >> _maxvalue
          >> _valueb
          >> _valued
          >> _valuef
          >> _random
          >> _valueta
          >> _valuetb
          >> _valuetc
          >> _valuetd
          >> _valuete
          >> _valuetf;
}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramThermoStatGear::saveGuts(RWvostream& ostrm ) const  
{
    CtiLMProgramDirectGear::saveGuts( ostrm );

    ostrm << _settings
          << _minvalue
          << _maxvalue
          << _valueb
          << _valued
          << _valuef
          << _random
          << _valueta
          << _valuetb
          << _valuetc
          << _valuetd
          << _valuete
          << _valuetf;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::operator=(const CtiLMProgramThermoStatGear& right)
{

    if( this != &right )
    {
        CtiLMProgramDirectGear::operator=(right);
        _settings = right._settings;
        _minvalue = right._minvalue;
        _maxvalue = right._maxvalue;
        _valueb   = right._valueb  ;
        _valued   = right._valued  ;
        _valuef   = right._valuef  ;
        _random   = right._random  ;
        _valueta  = right._valueta ;
        _valuetb  = right._valuetb ;
        _valuetc  = right._valuetc ;
        _valuetd  = right._valuetd ;
        _valuete  = right._valuete ;
        _valuetf  = right._valuetf ;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramThermoStatGear::operator==(const CtiLMProgramThermoStatGear& right) const
{

    return CtiLMProgramDirectGear::operator==(right);
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramThermoStatGear::operator!=(const CtiLMProgramThermoStatGear& right) const
{

    return CtiLMProgramDirectGear::operator!=(right);
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramThermoStatGear* CtiLMProgramThermoStatGear::replicate() const
{
    return (new CtiLMProgramThermoStatGear(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramThermoStatGear::restore(RWDBReader& rdr)
{
    rdr["settings"] >> _settings;
    rdr["minvalue"] >> _minvalue;
    rdr["maxvalue"] >> _maxvalue;
    rdr["valueb"] >> _valueb;
    rdr["valued"] >> _valued;
    rdr["valuef"] >> _valuef;
    rdr["random"] >> _random;
    rdr["valueta"] >> _valueta;
    rdr["valuetb"] >> _valuetb;
    rdr["valuetc"] >> _valuetc;
    rdr["valuetd"] >> _valuetd;
    rdr["valuete"] >> _valuete;
    rdr["valuetf"] >> _valuetf;
}

// Static Members

