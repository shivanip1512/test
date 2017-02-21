
#pragma once

#include <string>

#include "ControlStrategy.h"


class VoltStrategy : public ControlStrategy
{

public:

    VoltStrategy();

    virtual ~VoltStrategy();

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

    double getUpperVoltLimit( const bool isPeak ) const override;
    double getLowerVoltLimit( const bool isPeak ) const override;

private:

    std::string _controlMethod;

    double _peakUpperVoltLimit;
    double _offpeakUpperVoltLimit;
    double _peakLowerVoltLimit;
    double _offpeakLowerVoltLimit;
};

