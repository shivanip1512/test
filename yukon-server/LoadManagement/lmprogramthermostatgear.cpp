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
    : CtiLMProgramDirectGear(rdr)
{
    restore(rdr);
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

