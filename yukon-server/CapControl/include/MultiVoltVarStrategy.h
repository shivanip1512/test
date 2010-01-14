/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_CONTROLSTRATEGY_MULTIVOLTVARSTRATEGY_H
#define CTI_CONTROLSTRATEGY_MULTIVOLTVARSTRATEGY_H

#include <string>

#include "ControlStrategies.h"


class MultiVoltVarStrategy : public ControlStrategy
{

public:

    MultiVoltVarStrategy();

    virtual ~MultiVoltVarStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    virtual double getPeakLag() const;
    virtual double getOffPeakLag() const;
    virtual double getPeakLead() const;
    virtual double getOffPeakLead() const;
    virtual double getPeakVARLag() const;
    virtual double getOffPeakVARLag() const;
    virtual double getPeakVARLead() const;
    virtual double getOffPeakVARLead() const;

    virtual void setPeakLag(const double value);
    virtual void setOffPeakLag(const double value);
    virtual void setPeakLead(const double value);
    virtual void setOffPeakLead(const double value);
    virtual void setPeakVARLag(const double value);
    virtual void setOffPeakVARLag(const double value);
    virtual void setPeakVARLead(const double value);
    virtual void setOffPeakVARLead(const double value);

    virtual void setControlMethod(const std::string & method);

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual const ControlStrategy::ControlUnitType getUnitType() const;

    virtual const std::string getControlUnits() const;

private:

    std::string _controlMethod;

    double _peakUpperVoltLimit;
    double _offpeakUpperVoltLimit;
    double _peakLowerVoltLimit;
    double _offpeakLowerVoltLimit;
    double _peakKVarLeading;
    double _offpeakKVarLeading;
    double _peakKVarLagging;
    double _offpeakKVarLagging;
};


#endif

