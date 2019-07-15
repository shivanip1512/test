#pragma once

#include <string>

#include <boost/noncopyable.hpp>

class CtiTime;


class ControlStrategy : private boost::noncopyable
{

public:

    enum ControlUnitType
    {
        None,
        KVar,
        Volts,
        MultiVolt,
        MultiVoltVar,
        PFactorKWKVar,
        TimeOfDayUnit,
        IntegratedVoltVar
    };

    static const std::string NoControlUnit;
    static const std::string KVarControlUnit;
    static const std::string VoltsControlUnit;
    static const std::string MultiVoltControlUnit;
    static const std::string MultiVoltVarControlUnit;
    static const std::string PFactorKWKVarControlUnit;
    static const std::string TimeOfDayControlUnit;
    static const std::string IntegratedVoltVarControlUnit;

    enum ControlMethodType
    {
        NoMethod,
        IndividualFeeder,
        SubstationBus,
        BusOptimizedFeeder,
        ManualOnly,
        TimeOfDayMethod
    };

    static const std::string NoControlMethod;
    static const std::string IndividualFeederControlMethod;
    static const std::string SubstationBusControlMethod;
    static const std::string BusOptimizedFeederControlMethod;
    static const std::string ManualOnlyControlMethod;
    static const std::string TimeOfDayControlMethod;

    ControlStrategy();

    const bool operator == ( const ControlStrategy& rhs ) const;
    const bool operator != ( const ControlStrategy& rhs ) const;
    const bool operator <  ( const ControlStrategy& rhs ) const;
     
    virtual ~ControlStrategy();

    virtual void execute() = 0;
    virtual void restoreParameters( const std::string &name, const std::string &type, const std::string &value ) = 0;

    // Accessors

    bool getMaxOperationDisableFlag() const;
    bool getIntegrateFlag() const;
    bool getLikeDayFallBack() const;

    long getStrategyId() const;
    long getMaxDailyOperation() const;
    long getPeakStartTime() const;
    long getPeakStopTime() const;
    long getControlInterval() const;
    long getMaxConfirmTime() const;
    long getMinConfirmPercent() const;
    long getFailurePercent() const;
    long getControlDelayTime() const;
    long getControlSendRetries() const;
    long getIntegratePeriod() const;

    const std::string getStrategyName() const;
    const std::string getDaysOfWeek() const;
    const std::string getEndDaySettings() const;

    virtual const ControlUnitType   getUnitType() const = 0;
    virtual const std::string       getControlUnits() const = 0;
    virtual const ControlMethodType getMethodType() const = 0;
    virtual const std::string       getControlMethod() const = 0;

    // These are here for backwards compatability with the old style strategy. Once the execute() functions are
    //  fully implemented for all the strategies, these should go away.

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

    virtual void setPeakLag(const double value);
    virtual void setOffPeakLag(const double value);
    virtual void setPeakLead(const double value);
    virtual void setOffPeakLead(const double value);
    virtual void setPeakVARLag(const double value);
    virtual void setOffPeakVARLag(const double value);
    virtual void setPeakVARLead(const double value);
    virtual void setOffPeakVARLead(const double value);
    virtual void setPeakPFSetPoint(const double value);
    virtual void setOffPeakPFSetPoint(const double value);

    // End of backwards compatability section.

    // Mutators

    void setMaxOperationDisableFlag(const bool flag);
    void setIntegrateFlag(const bool flag);
    void setLikeDayFallBack(const bool flag);

    void setStrategyId(const long ID);
    void setMaxDailyOperation(const long op);
    void setPeakStartTime(const long start);
    void setPeakStopTime(const long stop);
    void setControlInterval(const long interval);
    void setMaxConfirmTime(const long confirmTime);
    void setMinConfirmPercent(const long percent);
    void setFailurePercent(const long percent);
    void setControlDelayTime(const long delayTime);
    void setControlSendRetries(const long retries);
    void setIntegratePeriod(const long period);

    void setStrategyName(const std::string & name);
    void setDaysOfWeek(const std::string & days);
    void setEndDaySettings(const std::string &settings);

    virtual void setControlMethod(const std::string & method) = 0;

    virtual void registerControllable(const long paoid);
    virtual void unregisterControllable(const long paoid);

    void       setPeakTimeFlag(const bool flag);
    const bool getPeakTimeFlag() const;

    // The following is for saving and restoring the embedded states for a database reload.
    virtual void restoreStates(const ControlStrategy * backup);

    bool isPeakTime( const CtiTime & now );

    virtual double getUpperVoltLimit( const bool isPeak ) const;
    virtual double getLowerVoltLimit( const bool isPeak ) const;

    virtual double getMaximumDeltaVoltage() const;

private:

    long        _strategyID;
    std::string _strategyName;
    long        _maxDailyOperation;
    bool        _maxOperationDisableFlag;
    long        _peakStartTime;
    long        _peakStopTime;
    long        _controlInterval;
    long        _maxConfirmTime;
    long        _minConfirmPercent;
    long        _failurePercent;
    std::string _daysOfWeek;
    long        _controlDelayTime;
    long        _controlSendRetries;
    bool        _integrateFlag;
    long        _integratePeriod;
    bool        _likeDayFallBack;
    std::string _endDaySettings;

    bool _isPeakTime;

    void printError(const char *function) const;
};

