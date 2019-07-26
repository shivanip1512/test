#include "precompiled.h"

#include "logger.h"
#include "ControlStrategy.h"
#include "ccid.h"
#include "ctitime.h"
#include "mgr_holiday.h"

extern unsigned long _CC_DEBUG;


ControlStrategy::ControlStrategy() :
    _strategyID(-1),
    _strategyName("(none)"),
    _maxDailyOperation(0),
    _maxOperationDisableFlag(false),
    _peakStartTime(0),
    _peakStopTime(0),
    _controlInterval(0),
    _maxConfirmTime(0),
    _minConfirmPercent(0),
    _failurePercent(0),
    _daysOfWeek("NNNNNNNN"),
    _controlDelayTime(0),
    _controlSendRetries(0),
    _integrateFlag(false),
    _integratePeriod(0),
    _likeDayFallBack(false),
    _endDaySettings("(none)"),
    _isPeakTime(false)
{
    // empty!
}


ControlStrategy::~ControlStrategy()
{
    // empty!
}


const bool ControlStrategy::operator == ( const ControlStrategy& rhs ) const
{
    return _strategyID == rhs._strategyID;
}


const bool ControlStrategy::operator != ( const ControlStrategy& rhs ) const
{
    return _strategyID != rhs._strategyID;
}


const bool ControlStrategy::operator <  ( const ControlStrategy& rhs ) const
{
    return _strategyID <  rhs._strategyID;
}


bool ControlStrategy::getMaxOperationDisableFlag() const
{
    return _maxOperationDisableFlag;
}


bool ControlStrategy::getIntegrateFlag() const
{
    return _integrateFlag;
}


bool ControlStrategy::getLikeDayFallBack() const
{
    return _likeDayFallBack;
}


long ControlStrategy::getStrategyId() const
{
    return _strategyID;
}


long ControlStrategy::getMaxDailyOperation() const
{
    return _maxDailyOperation;
}


long ControlStrategy::getPeakStartTime() const
{
    return _peakStartTime;
}


long ControlStrategy::getPeakStopTime() const
{
    return _peakStopTime;
}


long ControlStrategy::getControlInterval() const
{
    return _controlInterval;
}


long ControlStrategy::getMaxConfirmTime() const
{
    return _maxConfirmTime;
}


long ControlStrategy::getMinConfirmPercent() const
{
    return _minConfirmPercent;
}


long ControlStrategy::getFailurePercent() const
{
    return _failurePercent;
}


long ControlStrategy::getControlDelayTime() const
{
    return _controlDelayTime;
}


long ControlStrategy::getControlSendRetries() const
{
    return _controlSendRetries;
}


long ControlStrategy::getIntegratePeriod() const
{
    return _integratePeriod;
}


const std::string ControlStrategy::getStrategyName() const
{
    return _strategyName;
}


const std::string ControlStrategy::getDaysOfWeek() const
{
    return _daysOfWeek;
}


const std::string ControlStrategy::getEndDaySettings() const
{
    return _endDaySettings;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getPeakLag() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getOffPeakLag() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getPeakLead() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getOffPeakLead() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getPeakVARLag() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getOffPeakVARLag() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getPeakVARLead() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getOffPeakVARLead() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getPeakPFSetPoint() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * @return      0.0
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
double ControlStrategy::getOffPeakPFSetPoint() const
{
    printError(__FUNCTION__);

    return 0.0;
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setPeakLag(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setOffPeakLag(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setPeakLead(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setOffPeakLead(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setPeakVARLag(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setOffPeakVARLag(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setPeakVARLead(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setOffPeakVARLead(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setPeakPFSetPoint(const double value)
{
    printError(__FUNCTION__);
}


/**
 * This function has no effect on the strategy.
 *
 * @param       value   The new input value.
 * @deprecated  This function is supplied for backwards
 *              compatability with the old style strategies.
 */
void ControlStrategy::setOffPeakPFSetPoint(const double value)
{
    printError(__FUNCTION__);
}


void ControlStrategy::setMaxOperationDisableFlag(const bool flag)
{
    _maxOperationDisableFlag = flag;
}


void ControlStrategy::setIntegrateFlag(const bool flag)
{
    _integrateFlag = flag;
}


void ControlStrategy::setLikeDayFallBack(const bool flag)
{
    _likeDayFallBack = flag;
}


void ControlStrategy::setStrategyId(const long ID)
{
    _strategyID = ID;
}


void ControlStrategy::setMaxDailyOperation(const long op)
{
    _maxDailyOperation = op;
}


void ControlStrategy::setPeakStartTime(const long start)
{
    _peakStartTime = start;
}


void ControlStrategy::setPeakStopTime(const long stop)
{
    _peakStopTime = stop;
}


void ControlStrategy::setControlInterval(const long interval)
{
    _controlInterval = interval;
}


void ControlStrategy::setMaxConfirmTime(const long confirmTime)
{
    _maxConfirmTime = confirmTime;
}


void ControlStrategy::setMinConfirmPercent(const long percent)
{
    _minConfirmPercent = percent;
}


void ControlStrategy::setFailurePercent(const long percent)
{
    _failurePercent = percent;
}


void ControlStrategy::setControlDelayTime(const long delayTime)
{
    _controlDelayTime = delayTime;
}


void ControlStrategy::setControlSendRetries(const long retries)
{
    _controlSendRetries = retries;
}


void ControlStrategy::setIntegratePeriod(const long period)
{
    _integratePeriod = period;
}


void ControlStrategy::setStrategyName(const std::string & name)
{
    _strategyName = name;
}


void ControlStrategy::setDaysOfWeek(const std::string & days)
{
    _daysOfWeek = days;
}


void ControlStrategy::setEndDaySettings(const std::string &settings)
{
    _endDaySettings = settings;
}


void ControlStrategy::registerControllable(const long paoid)
{
    // empty!
}


void ControlStrategy::unregisterControllable(const long paoid)
{
    // empty!
}


void ControlStrategy::printError(const char *function) const
{
    static bool _printOnce = true;

    if ( _printOnce && ( _CC_DEBUG & CC_DEBUG_STANDARD ) )
    {
        _printOnce = false;

        CTILOG_DEBUG(dout, "Deprecated function: " << function << " called in strategy " << _strategyName << " ID " << _strategyID);
    }
}

/**
 *  This flag exists for the sole purpose of allowing the IVVC
 *  strategy to return the appropriate data in the subbus and
 *  feeder messaging.  They set the flag in the messaging
 *  function and rely on the overloads to return the data.
 *
 *  It's ugly and needs to be rethought, but for now....
 */
void ControlStrategy::setPeakTimeFlag(const bool flag)
{
    _isPeakTime = flag;
}


const bool ControlStrategy::getPeakTimeFlag() const
{
    return _isPeakTime;
}


void ControlStrategy::restoreStates(const ControlStrategy * backup)
{
    // empty!
}


bool ControlStrategy::isPeakTime( const CtiTime & now )
{
    const unsigned secondsAfterMidnight = ( ( now.hour() * 60 ) + now.minute() ) * 60 + now.second();

    _isPeakTime = false;

    if ( getPeakStartTime() <= secondsAfterMidnight && secondsAfterMidnight <= getPeakStopTime() )
    {
        // we are in the peak time window

        if ( CtiHolidayManager::getInstance().isHoliday( now.date() ) )
        {
            // it's a holiday, return our holiday peak setting

            _isPeakTime = ( getDaysOfWeek()[ 7 ] == 'Y' );
        }
        else
        {

            // return day of the week peak setting

            tm  timeComponents;

            now.extract( &timeComponents );

            _isPeakTime = ( getDaysOfWeek()[ timeComponents.tm_wday ] == 'Y' );
        }
    }

    return _isPeakTime;
}

/*  
    These two functions provide the base voltage limit functionality for control strategies.  They need
        to be overridden in the voltage based strategies (Volt, MultiVolt, MultiVoltVar and IVVC) to return
        actual voltage target limits.  This base implementation provides a default NEMA nominal 120V +/- 5%
        window.  These functions should never be called for a strategy that is not one of the 4 types listed
        above.  A message is printed in the log if this happens.
*/
double ControlStrategy::getUpperVoltLimit( const bool isPeak ) const
{
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_ERROR( dout, "getUpperVoltLimit() called in strategy: " << _strategyName
                        << " (ID: " << _strategyID << ") with unsupported control unit type: "
                        << getControlUnits() );
    }

    return 126.0;   // NEMA standard 120V + 5%
}

double ControlStrategy::getLowerVoltLimit( const bool isPeak ) const
{
    if ( _CC_DEBUG & CC_DEBUG_STANDARD )
    {
        CTILOG_ERROR( dout, "getLowerVoltLimit() called in strategy: " << _strategyName
                        << " (ID: " << _strategyID << ") with unsupported control unit type: "
                        << getControlUnits() );
    }

    return 114.0;   // NEMA standard 120V - 5%
}

double ControlStrategy::getMaximumDeltaVoltage() const
{
    return 10.0;    // system default -- overridden in MultiVolt, MultiVoltVar and IVVC
}


const std::string ControlStrategy::NoControlMethod                  = "NONE";
const std::string ControlStrategy::IndividualFeederControlMethod    = "INDIVIDUAL_FEEDER";
const std::string ControlStrategy::SubstationBusControlMethod       = "SUBSTATION_BUS";
const std::string ControlStrategy::BusOptimizedFeederControlMethod  = "BUSOPTIMIZED_FEEDER";
const std::string ControlStrategy::ManualOnlyControlMethod          = "MANUAL_ONLY";
const std::string ControlStrategy::TimeOfDayControlMethod           = "TIME_OF_DAY";

const std::string ControlStrategy::NoControlUnit                    = "NONE";
const std::string ControlStrategy::KVarControlUnit                  = "KVAR";
const std::string ControlStrategy::VoltsControlUnit                 = "VOLTS";
const std::string ControlStrategy::MultiVoltControlUnit             = "MULTI_VOLT";
const std::string ControlStrategy::MultiVoltVarControlUnit          = "MULTI_VOLT_VAR";
const std::string ControlStrategy::PFactorKWKVarControlUnit         = "PFACTOR_KW_KVAR";
const std::string ControlStrategy::PFactorKWKQControlUnit           = "PFACTOR_KW_KQ";
const std::string ControlStrategy::TimeOfDayControlUnit             = "TIME_OF_DAY";
const std::string ControlStrategy::IntegratedVoltVarControlUnit     = "INTEGRATED_VOLT_VAR";

