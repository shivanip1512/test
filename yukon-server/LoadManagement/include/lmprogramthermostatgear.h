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
    ULONG getMinValue() const;
    ULONG getMaxValue() const;
    ULONG getValueB() const;
    ULONG getValueD() const;
    ULONG getValueF() const;
    ULONG getRandom() const;
    ULONG getValueTa() const;
    ULONG getValueTb() const;
    ULONG getValueTc() const;
    ULONG getValueTd() const;
    ULONG getValueTe() const;
    ULONG getValueTf() const;

    CtiLMProgramThermoStatGear& setSettings(const RWCString& settings);
    CtiLMProgramThermoStatGear& setMinValue(ULONG val);
    CtiLMProgramThermoStatGear& setMaxValue(ULONG val);
    CtiLMProgramThermoStatGear& setValueB(ULONG val);
    CtiLMProgramThermoStatGear& setValueD(ULONG val);
    CtiLMProgramThermoStatGear& setValueF(ULONG val);
    CtiLMProgramThermoStatGear& setRandom(ULONG val);
    CtiLMProgramThermoStatGear& setValueTa(ULONG val);
    CtiLMProgramThermoStatGear& setValueTb(ULONG val);
    CtiLMProgramThermoStatGear& setValueTc(ULONG val);
    CtiLMProgramThermoStatGear& setValueTd(ULONG val);
    CtiLMProgramThermoStatGear& setValueTe(ULONG val);
    CtiLMProgramThermoStatGear& setValueTf(ULONG val);

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
    ULONG _minvalue;
    ULONG _maxvalue;
    ULONG _valueb;
    ULONG _valued;
    ULONG _valuef;
    ULONG _random;
    ULONG _valueta;
    ULONG _valuetb;
    ULONG _valuetc;
    ULONG _valuetd;
    ULONG _valuete;
    ULONG _valuetf;

    void restore(RWDBReader& rdr);
};
#endif

