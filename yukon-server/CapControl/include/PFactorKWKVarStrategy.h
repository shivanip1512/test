
#pragma once

#include <string>

#include "ControlStrategy.h"


class PFactorKWKVarStrategy : public ControlStrategy
{

public:

    PFactorKWKVarStrategy();

    virtual ~PFactorKWKVarStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    virtual double getPeakLag() const;
    virtual double getOffPeakLag() const;
    virtual double getPeakLead() const;
    virtual double getOffPeakLead() const;
    virtual double getPeakPFSetPoint() const;
    virtual double getOffPeakPFSetPoint() const;

    virtual void setPeakLag(const double value);
    virtual void setOffPeakLag(const double value);
    virtual void setPeakLead(const double value);
    virtual void setOffPeakLead(const double value);
    virtual void setPeakPFSetPoint(const double value);
    virtual void setOffPeakPFSetPoint(const double value);

    virtual void setControlMethod(const std::string & method);

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual const ControlStrategy::ControlUnitType getUnitType() const;

    virtual const std::string getControlUnits() const;

private:

    std::string _controlMethod;

    double _peakTargetPF;
    double _offpeakTargetPF;
    double _peakMinBankOpen;
    double _offpeakMinBankOpen;
    double _peakMinBankClose;
    double _offpeakMinBankClose;
};

