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
CtiLMProgramThermostatGear::CtiLMProgramThermostatGear(Cti::RowReader &rdr)
    : CtiLMProgramDirectGear(rdr)
{
    restore(rdr);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramThermostatGear::~CtiLMProgramThermostatGear()
{
}

const std::string& CtiLMProgramThermostatGear::getSettings() const
{
    return _settings;
}
LONG CtiLMProgramThermostatGear::getRandom() const
{
    return _profile.random;
}
LONG CtiLMProgramThermostatGear::getDelayTime() const
{
    return _profile.valueTA;
}
LONG CtiLMProgramThermostatGear::getPrecoolTime() const
{
    return _profile.valueTB;
}
LONG CtiLMProgramThermostatGear::getPrecoolHoldTime() const
{
    return _profile.valueTC;
}
LONG CtiLMProgramThermostatGear::getControlTime() const
{
    return _profile.valueTD;
}
LONG CtiLMProgramThermostatGear::getControlHoldTime() const
{
    return _profile.valueTE;
}
LONG CtiLMProgramThermostatGear::getRestoreTime() const
{
    return _profile.valueTF;
}
float CtiLMProgramThermostatGear::getRampRate() const
{
    return _profile.rampRate;
}

CtiLMProgramThermostatGear::ProfileSettings CtiLMProgramThermostatGear::getProfileSettings() const
{
    return _profile;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramDirectGear* CtiLMProgramThermostatGear::replicate() const
{
    return(CTIDBG_new CtiLMProgramThermostatGear(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramThermostatGear::restore(Cti::RowReader &rdr)
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

std::size_t CtiLMProgramThermostatGear::getVariableSize() const
{
    return  CtiLMProgramDirectGear::getVariableSize()
        +   dynamic_sizeof( _settings );
}

