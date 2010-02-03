
#pragma once

#include <string>
#include <map>
#include <utility>
#include <list>

#include <boost/shared_ptr.hpp>

#include "guard.h"
#include "mutex.h"
#include "ControlStrategy.h"
#include "IVVCState.h"

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

    const double getUpperVoltLimit(const bool isPeak) const;
    const double getLowerVoltLimit(const bool isPeak) const;
    const double getTargetPF(const bool isPeak) const;
    const double getMinBankOpen(const bool isPeak) const;
    const double getMinBankClose(const bool isPeak) const;
    const double getVoltWeight(const bool isPeak) const;
    const double getPFWeight(const bool isPeak) const;
    const double getDecisionWeight(const bool isPeak) const;

    virtual void registerUser(const int paoid);
    virtual void unregisterUser(const int paoid);

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

    typedef std::map<int, std::pair<unsigned, IVVCStatePtr> >   PaoToStateMap;

    PaoToStateMap   _paoStateMap;

    CtiMutex    _mapMutex;
};

