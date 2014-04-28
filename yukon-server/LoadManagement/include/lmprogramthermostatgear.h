#pragma once

#include "lmprogramdirectgear.h"

class CtiLMProgramThermoStatGear : public CtiLMProgramDirectGear
{

public:

    CtiLMProgramThermoStatGear(Cti::RowReader &rdr);

    virtual ~CtiLMProgramThermoStatGear();

    const std::string& getSettings() const;
    LONG getMinValue() const;
    LONG getMaxValue() const;
    LONG getPrecoolTemp() const;
    LONG getControlTemp() const;
    LONG getRestoreTemp() const;
    LONG getRandom() const;
    LONG getDelayTime() const;
    LONG getPrecoolTime() const;
    LONG getPrecoolHoldTime() const;
    LONG getControlTime() const;
    LONG getControlHoldTime() const;
    LONG getRestoreTime() const;
    float getRampRate() const;

    virtual CtiLMProgramDirectGear* replicate() const;

protected:
    void restore(Cti::RowReader &rdr);

private:

    std::string _settings;
    LONG _minvalue;
    LONG _maxvalue;
    LONG _valueb;
    LONG _valued;
    LONG _valuef;
    LONG _random;
    LONG _valueta;
    LONG _valuetb;
    LONG _valuetc;
    LONG _valuetd;
    LONG _valuete;
    LONG _valuetf;
    float _rampRate;
};
