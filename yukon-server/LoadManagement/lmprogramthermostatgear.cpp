#include "precompiled.h"

#include "dbaccess.h"
#include "lmprogramthermostatgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"


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

const std::string& CtiLMProgramThermoStatGear::getSettings() const
{
    return _settings;
}
LONG CtiLMProgramThermoStatGear::getMinValue() const
{
    return _profile.minValue;
}
LONG CtiLMProgramThermoStatGear::getMaxValue() const
{
    return _profile.maxValue;
}
LONG CtiLMProgramThermoStatGear::getPrecoolTemp() const
{
    return _profile.valueB;
}
LONG CtiLMProgramThermoStatGear::getControlTemp() const
{
    return _profile.valueD;
}
LONG CtiLMProgramThermoStatGear::getRestoreTemp() const
{
    return _profile.valueF;
}
LONG CtiLMProgramThermoStatGear::getRandom() const
{
    return _profile.random;
}
LONG CtiLMProgramThermoStatGear::getDelayTime() const
{
    return _profile.valueTA;
}
LONG CtiLMProgramThermoStatGear::getPrecoolTime() const
{
    return _profile.valueTB;
}
LONG CtiLMProgramThermoStatGear::getPrecoolHoldTime() const
{
    return _profile.valueTC;
}
LONG CtiLMProgramThermoStatGear::getControlTime() const
{
    return _profile.valueTD;
}
LONG CtiLMProgramThermoStatGear::getControlHoldTime() const
{
    return _profile.valueTE;
}
LONG CtiLMProgramThermoStatGear::getRestoreTime() const
{
    return _profile.valueTF;
}
float CtiLMProgramThermoStatGear::getRampRate() const
{
    return _profile.rampRate;
}

CtiLMProgramThermoStatGear::ProfileSettings CtiLMProgramThermoStatGear::getProfileSettings() const
{
    return _profile;
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

    rdr["minvalue"] >> _profile.minValue;
    rdr["maxvalue"] >> _profile.maxValue;
    rdr["valueb"]   >> _profile.valueB;
    rdr["valued"]   >> _profile.valueD;
    rdr["valuef"]   >> _profile.valueF;
    rdr["random"]   >> _profile.random;
    rdr["valueta"]  >> _profile.valueTA;
    rdr["valuetb"]  >> _profile.valueTB;
    rdr["valuetc"]  >> _profile.valueTC;
    rdr["valuetd"]  >> _profile.valueTD;
    rdr["valuete"]  >> _profile.valueTE;
    rdr["valuetf"]  >> _profile.valueTF;
    rdr["ramprate"] >> _profile.rampRate;
}

