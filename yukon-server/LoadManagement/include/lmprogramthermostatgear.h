/*---------------------------------------------------------------------------
        Filename:  lmprogramthermostatgear.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMProgramThermoStatGear
                        CtiLMProgramThermoStatGear

        Initial Date:  11/1/2002
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2002
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMPROGRAMTHERMOSTATGEARIMPL_H
#define CTILMPROGRAMTHERMOSTATGEARIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "observe.h"
#include "lmprogramdirectgear.h"
                
class CtiLMProgramThermoStatGear : public CtiLMProgramDirectGear
{

public:

RWDECLARE_COLLECTABLE( CtiLMProgramThermoStatGear )

    CtiLMProgramThermoStatGear();
    CtiLMProgramThermoStatGear(RWDBReader& rdr);
    CtiLMProgramThermoStatGear(const CtiLMProgramThermoStatGear& thermogear);

    virtual ~CtiLMProgramThermoStatGear();
    
    const RWCString& getSettings() const;
    LONG getMinValue() const;
    LONG getMaxValue() const;
    LONG getValueB() const;
    LONG getValueD() const;
    LONG getValueF() const;
    LONG getRandom() const;
    LONG getValueTa() const;
    LONG getValueTb() const;
    LONG getValueTc() const;
    LONG getValueTd() const;
    LONG getValueTe() const;
    LONG getValueTf() const;

    CtiLMProgramThermoStatGear& setSettings(const RWCString& settings);
    CtiLMProgramThermoStatGear& setMinValue(LONG val);
    CtiLMProgramThermoStatGear& setMaxValue(LONG val);
    CtiLMProgramThermoStatGear& setValueB(LONG val);
    CtiLMProgramThermoStatGear& setValueD(LONG val);
    CtiLMProgramThermoStatGear& setValueF(LONG val);
    CtiLMProgramThermoStatGear& setRandom(LONG val);
    CtiLMProgramThermoStatGear& setValueTa(LONG val);
    CtiLMProgramThermoStatGear& setValueTb(LONG val);
    CtiLMProgramThermoStatGear& setValueTc(LONG val);
    CtiLMProgramThermoStatGear& setValueTd(LONG val);
    CtiLMProgramThermoStatGear& setValueTe(LONG val);
    CtiLMProgramThermoStatGear& setValueTf(LONG val);

    CtiLMProgramThermoStatGear* replicate() const;

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMProgramThermoStatGear& operator=(const CtiLMProgramThermoStatGear& right);

    int operator==(const CtiLMProgramThermoStatGear& right) const;
    int operator!=(const CtiLMProgramThermoStatGear& right) const;

    /* Static Members */

private:

    RWCString _settings;
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

    void restore(RWDBReader& rdr);
};
#endif

