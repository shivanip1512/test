
#include "yukon.h"

#include <string>

#include "logger.h"
#include "ControlStrategy.h"
#include "ccid.h"

extern ULONG _CC_DEBUG;


ControlStrategy::ControlStrategy() :
    _strategyID(0),
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
    _endDaySettings("(none)")
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


void ControlStrategy::printError(const char *function) const
{
    static bool _printOnce = true;

    if ( _printOnce && ( _CC_DEBUG & CC_DEBUG_STANDARD ) )
    {
        _printOnce = false;

        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout <<  "\n ** WARNING: Calling deprecated function: " << function << "()\n" << std::endl;
    }
}


const std::string ControlStrategy::NoControlMethod                  = "(none)";
const std::string ControlStrategy::IndividualFeederControlMethod    = "IndividualFeeder";
const std::string ControlStrategy::SubstationBusControlMethod       = "SubstationBus";
const std::string ControlStrategy::BusOptimizedFeederControlMethod  = "BusOptimizedFeeder";
const std::string ControlStrategy::ManualOnlyControlMethod          = "ManualOnly";
const std::string ControlStrategy::TimeOfDayControlMethod           = "TimeOfDay";

const std::string ControlStrategy::NoControlUnit                    = "(none)";
const std::string ControlStrategy::KVarControlUnit                  = "kVAr";
const std::string ControlStrategy::VoltsControlUnit                 = "VOLTS";
const std::string ControlStrategy::MultiVoltControlUnit             = "Multi Volt";
const std::string ControlStrategy::MultiVoltVarControlUnit          = "Multi Volt/VAR";
const std::string ControlStrategy::PFactorKWKVarControlUnit         = "P-Factor kW/kVAr";
const std::string ControlStrategy::PFactorKWKQControlUnit           = "P-Factor kW/kQ";
const std::string ControlStrategy::TimeOfDayControlUnit             = "Time of Day";
const std::string ControlStrategy::IntegratedVoltVarControlUnit     = "Integrated Volt/Var";

