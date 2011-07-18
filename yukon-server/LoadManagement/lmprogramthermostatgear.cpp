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
#include "precompiled.h"

#include "dbaccess.h"
#include "lmprogramthermostatgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

using std::string;

extern ULONG _LM_DEBUG;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramThermoStatGear::CtiLMProgramThermoStatGear(Cti::RowReader &rdr)
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

const string& CtiLMProgramThermoStatGear::getSettings() const
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
LONG CtiLMProgramThermoStatGear::getPrecoolTemp() const
{
    return _valueb;
}
LONG CtiLMProgramThermoStatGear::getControlTemp() const
{
    return _valued;
}
LONG CtiLMProgramThermoStatGear::getRestoreTemp() const
{
    return _valuef;
}
LONG CtiLMProgramThermoStatGear::getRandom() const
{
    return _random;
}
LONG CtiLMProgramThermoStatGear::getDelayTime() const
{
    return _valueta;
}
LONG CtiLMProgramThermoStatGear::getPrecoolTime() const
{
    return _valuetb;
}
LONG CtiLMProgramThermoStatGear::getPrecoolHoldTime() const
{
    return _valuetc;
}
LONG CtiLMProgramThermoStatGear::getControlTime() const
{
    return _valuetd;
}
LONG CtiLMProgramThermoStatGear::getControlHoldTime() const
{
    return _valuete;
}
LONG CtiLMProgramThermoStatGear::getRestoreTime() const
{
    return _valuetf;
}
float CtiLMProgramThermoStatGear::getRampRate() const
{
    return _rampRate;
}

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
/*void CtiLMProgramThermoStatGear::restoreGuts(RWvistream& istrm)
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
    >> _valuetf
    >> _rampRate;
}*/

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
/*void CtiLMProgramThermoStatGear::saveGuts(RWvostream& ostrm ) const
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
    << _valuetf
    << _rampRate;

    return;
}*/

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
        _rampRate = right._rampRate;
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
CtiLMProgramDirectGear* CtiLMProgramThermoStatGear::replicate() const
{
    return(CTIDBG_new CtiLMProgramThermoStatGear(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramThermoStatGear::restore(Cti::RowReader &rdr)
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
    rdr["ramprate"] >> _rampRate;
}

// Static Members

