#pragma once

#include "lmprogramdirectgear.h"

class CtiLMProgramThermostatGear : public CtiLMProgramDirectGear
{

public:

    struct ProfileSettings
    {
        LONG    minValue;
        LONG    maxValue;        
        LONG    valueB;     // precool temp
        LONG    valueD;     // control temp
        LONG    valueF;     // restore temp
        LONG    random;
        LONG    valueTA;    // delay time
        LONG    valueTB;    // precool time
        LONG    valueTC;    // precool hold time
        LONG    valueTD;    // control time
        LONG    valueTE;    // control hold time
        LONG    valueTF;    // restore time
        float   rampRate;
    };

    CtiLMProgramThermostatGear(Cti::RowReader &rdr);

    virtual ~CtiLMProgramThermostatGear();

    const std::string& getSettings() const;
    LONG getRandom() const;
    LONG getDelayTime() const;
    LONG getPrecoolTime() const;
    LONG getPrecoolHoldTime() const;
    LONG getControlTime() const;
    LONG getControlHoldTime() const;
    LONG getRestoreTime() const;
    float getRampRate() const;

    ProfileSettings getProfileSettings() const;

    virtual CtiLMProgramDirectGear* replicate() const;

protected:
    void restore(Cti::RowReader &rdr);

private:

    std::string     _settings;
    ProfileSettings _profile;
};
