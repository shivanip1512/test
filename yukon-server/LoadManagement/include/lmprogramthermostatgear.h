/*---------------------------------------------------------------------------
        Filename:  lmprogramthermostatgear.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramThermoStatGear
                        CtiLMProgramThermoStatGear

        Initial Date:  11/5/2002
        
        COPYRIGHT:  Copyright (C) Cooper Industries, 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMTHERMOSTATGEARIMPL_H
#define CTILMPROGRAMTHERMOSTATGEARIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmprogramdirectgear.h"
                
class CtiLMProgramThermoStatGear : public CtiLMProgramDirectGear
{

public:

    CtiLMProgramThermoStatGear(Cti::RowReader &rdr);
    CtiLMProgramThermoStatGear(const CtiLMProgramThermoStatGear& thermogear);

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

    //Members inherited from RWCollectable
    //void restoreGuts(RWvistream& );
    //void saveGuts(RWvostream& ) const;

    CtiLMProgramThermoStatGear& operator=(const CtiLMProgramThermoStatGear& right);

    int operator==(const CtiLMProgramThermoStatGear& right) const;
    int operator!=(const CtiLMProgramThermoStatGear& right) const;

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
#endif

