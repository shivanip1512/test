/* 
    COPYRIGHT: Copyright (C) 2010
                    Cooper Power Systems EAS
                    Cannon Technologies, Inc.
---------------------------------------------------------------------------*/

#ifndef CTI_CONTROLSTRATEGY_IVVCSTRATEGY_H
#define CTI_CONTROLSTRATEGY_IVVCSTRATEGY_H

#include <string>

#include "ControlStrategies.h"


class IVVCStrategy : public ControlStrategy
{

public:

    IVVCStrategy();

    virtual ~IVVCStrategy();

    virtual void execute();

    virtual void restoreParameters(const std::string &name, const std::string &type, const std::string &value);

    virtual void setControlMethod(const std::string & method);

    virtual const ControlStrategy::ControlMethodType getMethodType() const;

    virtual const std::string getControlMethod() const;

    virtual const ControlStrategy::ControlUnitType getUnitType() const;

    virtual const std::string getControlUnits() const;

private:

    double _peakUpperVoltLimit;
    double _offpeakUpperVoltLimit;
    double _peakLowerVoltLimit;
    double _offpeakLowerVoltLimit;
    double _peakTargetPF;
    double _offpeakTargetPF;
    double _peakMinBankOpen;
    double _offpeakMinBankOpen;
    double _peakMinBankClose;
    double _offpeakMinBankClose;
    double _peakVoltWeight;
    double _offpeakVoltWeight;
    double _peakPFWeight;
    double _offpeakPFWeight;
    double _peakDecisionWeight;
    double _offpeakDecisionWeight;
};


#endif

