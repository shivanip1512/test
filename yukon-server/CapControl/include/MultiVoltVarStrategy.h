
#pragma once

#include <string>

#include "ControlStrategy.h"


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
    virtual double getPeakPFSetPoint() const;
    virtual double getOffPeakPFSetPoint() const;

    const double getMaximumDeltaVoltage() const;

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

    double getUpperVoltLimit( const bool isPeak ) const override;
    double getLowerVoltLimit( const bool isPeak ) const override;

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

    double _maximumDeltaVoltage;
};

