/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_CONTROLSTRATEGY_PFACTORKWKQSTRATEGY_H
#define CTI_CONTROLSTRATEGY_PFACTORKWKQSTRATEGY_H

#include <string>

#include "ControlStrategies.h"


class PFactorKWKQStrategy : public ControlStrategy
{

public:

    PFactorKWKQStrategy();

    virtual ~PFactorKWKQStrategy();

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


#endif

