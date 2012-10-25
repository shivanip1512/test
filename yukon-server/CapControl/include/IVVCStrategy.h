#pragma once

#include <string>
#include <utility>

#include "IVVCAlgorithm.h"
#include "guard.h"
#include "mutex.h"
#include "ControlStrategy.h"
#include "IVVCState.h"
#include "PointDataRequestFactory.h"

class IVVCStrategy : public ControlStrategy
{

public:

    IVVCStrategy(const PointDataRequestFactoryPtr& factory);

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
    const double getVoltageRegulationMargin(const bool isPeak) const;

    // the following don't distinguish between peak and off-peak
    const double getLowVoltageViolationBandwidth() const;
    const double getHighVoltageViolationBandwidth() const;
    const double getEmergencyLowVoltageViolationCost() const;
    const double getLowVoltageViolationCost() const;
    const double getHighVoltageViolationCost() const;
    const double getEmergencyHighVoltageViolationCost() const;

    // power factor correction values
    const double getPowerFactorCorrectionBandwidth() const;
    const double getPowerFactorCorrectionCost() const;
    const double getPowerFactorCorrectionMaxCost() const;

    const unsigned getMaxConsecutiveCapBankOps(const bool isPeak) const;

    virtual void registerControllable(const long paoid);
    virtual void unregisterControllable(const long paoid);

    virtual double getPeakLag() const;
    virtual double getOffPeakLag() const;
    virtual double getPeakLead() const;
    virtual double getOffPeakLead() const;
    virtual double getPeakPFSetPoint() const;

    // The following is for saving and restoring the embedded states for a database reload.
    virtual void restoreStates(const ControlStrategy * backup);

    //Allows unit test to change this out
    void setPointDataRequestFactory(const PointDataRequestFactoryPtr& factory);

private:

    IVVCAlgorithm _ivvcAlgorithm;

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
    double _peakVoltageRegulationMargin;
    double _offpeakVoltageRegulationMargin;
    unsigned _peakMaxConsecutiveCapBankOps;
    unsigned _offpeakMaxConsecutiveCapBankOps;

    double _lowVoltageViolationBandwidth;
    double _highVoltageViolationBandwidth;
    double _emergencyLowVoltageViolationCost;
    double _lowVoltageViolationCost;
    double _highVoltageViolationCost;
    double _emergencyHighVoltageViolationCost;

    double _powerFactorCorrectionBandwidth;
    double _powerFactorCorrectionCost;
    double _powerFactorCorrectionMaxCost;

    std::string _controlMethod;

    typedef std::map<long, std::pair<unsigned, IVVCStatePtr> >   PaoToStateMap;     // PaoID -> { reference count, state }

    PaoToStateMap   _paoStateMap;

    CtiMutex    _mapMutex;
};

