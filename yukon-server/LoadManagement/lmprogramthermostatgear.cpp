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
#include "yukon.h"

#include "dbaccess.h"
#include "lmprogramthermostatgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

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
LONG CtiLMProgramThermoStatGear::getMinValue() const
{
    return _minvalue;
}
LONG CtiLMProgramThermoStatGear::getMaxValue() const
{
    return _maxvalue;
}
LONG CtiLMProgramThermoStatGear::getValueB() const
{
    return _valueb;
}
LONG CtiLMProgramThermoStatGear::getValueD() const
{
    return _valued;
}
LONG CtiLMProgramThermoStatGear::getValueF() const
{
    return _valuef;
}
LONG CtiLMProgramThermoStatGear::getRandom() const
{
    return _random;
}
LONG CtiLMProgramThermoStatGear::getValueTa() const
{
    return _valueta;
}
LONG CtiLMProgramThermoStatGear::getValueTb() const
{
    return _valuetb;
}
LONG CtiLMProgramThermoStatGear::getValueTc() const
{
    return _valuetc;
}
LONG CtiLMProgramThermoStatGear::getValueTd() const
{
    return _valuetd;
}
LONG CtiLMProgramThermoStatGear::getValueTe() const
{
    return _valuete;
}
LONG CtiLMProgramThermoStatGear::getValueTf() const
{
    return _valuetf;
}


CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setSettings(const RWCString& settings)
{
    _settings = settings;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setMinValue(LONG val)
{
    _minvalue = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setMaxValue(LONG val)
{
    _maxvalue = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueB(LONG val)
{
    _valueb = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueD(LONG val)
{
    _valued = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueF(LONG val)
{
    _valuef = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setRandom(LONG val)
{
    _random = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTa(LONG val)
{
    _valueta = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTb(LONG val)
{
    _valuetb = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTc(LONG val)
{
    _valuetc = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTd(LONG val)
{
    _valuetd = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTe(LONG val)
{
    _valuete = val;
    return *this;
}
CtiLMProgramThermoStatGear& CtiLMProgramThermoStatGear::setValueTf(LONG val)
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
    CtiLMProgramDirectGear::restore(rdr);

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

