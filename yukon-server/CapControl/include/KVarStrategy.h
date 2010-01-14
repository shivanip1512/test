/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_CONTROLSTRATEGY_KVARSTRATEGY_H
#define CTI_CONTROLSTRATEGY_KVARSTRATEGY_H

#include <string>

#include "ControlStrategies.h"


class KVarStrategy : public ControlStrategy
{

public:

    KVarStrategy();

    virtual ~KVarStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    virtual double getPeakLag() const;
    virtual double getOffPeakLag() const;
    virtual double getPeakLead() const;
    virtual double getOffPeakLead() const;

    virtual void setPeakLag(const double value);
    virtual void setOffPeakLag(const double value);
    virtual void setPeakLead(const double value);
    virtual void setOffPeakLead(const double value);

    virtual void setControlMethod(const std::string & method);

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual const ControlStrategy::ControlUnitType getUnitType() const;

    virtual const std::string getControlUnits() const;

private:

    std::string _controlMethod;

    double _peakKVarLeading;
    double _offpeakKVarLeading;
    double _peakKVarLagging;
    double _offpeakKVarLagging;
};

#endif

