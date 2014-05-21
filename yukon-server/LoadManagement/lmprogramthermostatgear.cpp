#include "precompiled.h"

#include "dbaccess.h"
#include "lmprogramthermostatgear.h"
#include "lmid.h"
#include "lmprogrambase.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

#include <sstream>

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


/*
    Looks at the internal type of thermostat gear and builds the appropriate mode string
        based on its internal settings.
        Returns an empty string on error.
*/
std::string CtiLMProgramThermoStatGear::getMode() const
{
    // Validate

    std::string settings( _settings );
    CtiToUpper( settings );

    if ( settings.length() != 4 )
    {
        return "";
    }

    if ( getControlMethod() == CtiLMProgramDirectGear::ThermostatRampingMethod )
    {
        //  settings is a string of the form '(A|D)(F|C)(H|-)(I|-)'
        //      where either 'H' or 'I' or both are required.  It must not be '??--'

        if ( settings[ 2 ] == '-' && settings[ 3 ] == '-' )
        {
            return "";
        }
    }
    else if ( getControlMethod() == CtiLMProgramDirectGear::SimpleThermostatRampingMethod )
    {
        //  settings is a string of the form '--H-' or '---I'
        //      '--H-' is 'delta mode heat'
        //      '---I' is 'delta mode cool'

        if ( ! ( settings[ 2 ] == '-' ^ settings[ 3 ] == '-' ) )    // either-or but not both or neither
        {
            return "";
        }

        // set our required fields
        settings[ 0 ] = 'D';    // delta
        settings[ 1 ] = 'F';    // fahrenheit
    }
    else
    {
        //  something is wrong here...

        return "";
    }

    // Build mode string

    std::ostringstream  mode;

    if ( settings[ 0 ] == 'D' )
    {
        mode << " delta";
    }

    if ( settings[ 1 ] == 'C' )
    {
        mode << " celsius";
    }

    if ( settings[ 2 ] == 'H' && settings[ 3 ] == 'I' )
    {
        mode << " mode both";
    }
    else if ( settings[ 2 ] == 'H' )
    {
        mode << " mode heat";
    }
    else    // settings[ 3 ] == 'I'
    {
        mode << " mode cool";
    }

    return mode.str();
}

