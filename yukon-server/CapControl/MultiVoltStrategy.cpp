
#include "precompiled.h"

#include <cstdlib>

#include "MultiVoltStrategy.h"
#include "std_helper.h"
#include "string_util.h"
#include "logger.h"


MultiVoltStrategy::MultiVoltStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0),
    _maximumDeltaVoltage(10.0)
{
    // empty!
}


MultiVoltStrategy::~MultiVoltStrategy()
{
    // empty!
}


namespace
{

template<typename T>
boost::optional<T> findAccessor( const std::map< std::string, std::map< std::string, T > > lookup,
                                 const std::string & name, const std::string & type )
{
    if ( const auto nameSearch = Cti::mapFind( lookup, name ) )
    {
        return Cti::mapFind( *nameSearch, type );
    }

    return boost::none;
}

}


void MultiVoltStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
{
    static const std::map< std::string, std::map< std::string, double MultiVoltStrategy::* > >   directLookup
    {
        {   "Upper Volt Limit",             {
                { "PEAK",               &MultiVoltStrategy::_peakUpperVoltLimit                 },
                { "OFFPEAK",            &MultiVoltStrategy::_offpeakUpperVoltLimit              }   }
        },
        {   "Lower Volt Limit",             {
                { "PEAK",               &MultiVoltStrategy::_peakLowerVoltLimit                 },
                { "OFFPEAK",            &MultiVoltStrategy::_offpeakLowerVoltLimit              }   }
        },
        {   "Maximum Delta Voltage",        {
                { "MAX_DELTA",          &MultiVoltStrategy::_maximumDeltaVoltage                }   }
        }
    };

    double  aValue = std::atof( value.c_str() );

    if ( const auto varptr = findAccessor( directLookup, name, type ) )
    {
        this->*(*varptr) = aValue;
    }
    else
    {
        Cti::FormattedList  error;

        error << "Unknown setting for strategy: " << getStrategyName();
        error.add("StrategyId")   << getStrategyId();
        error.add("SettingName")  << name;
        error.add("SettingValue") << value;
        error.add("SettingType")  << type;

        CTILOG_ERROR( dout, error );
    }
}


double MultiVoltStrategy::getPeakLag() const
{
    return getLowerVoltLimit( true );
}


double MultiVoltStrategy::getOffPeakLag() const
{
    return getLowerVoltLimit( false );
}


double MultiVoltStrategy::getPeakLead() const
{
    return getUpperVoltLimit( true );
}


double MultiVoltStrategy::getOffPeakLead() const
{
    return getUpperVoltLimit( false );
}


double MultiVoltStrategy::getUpperVoltLimit( const bool isPeak ) const
{
    return isPeak ? _peakUpperVoltLimit : _offpeakUpperVoltLimit;
}


double MultiVoltStrategy::getLowerVoltLimit( const bool isPeak ) const
{
    return isPeak ? _peakLowerVoltLimit : _offpeakLowerVoltLimit;
}


void MultiVoltStrategy::setPeakLag(const double value)
{
    _peakLowerVoltLimit = value;
}


void MultiVoltStrategy::setOffPeakLag(const double value)
{
    _offpeakLowerVoltLimit = value;
}


void MultiVoltStrategy::setPeakLead(const double value)
{
    _peakUpperVoltLimit = value;
}


void MultiVoltStrategy::setOffPeakLead(const double value)
{
    _offpeakUpperVoltLimit = value;
}


double MultiVoltStrategy::getMaximumDeltaVoltage() const
{
    return _maximumDeltaVoltage;
}


void MultiVoltStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType MultiVoltStrategy::getMethodType() const
{
    if ( _controlMethod == ControlStrategy::IndividualFeederControlMethod )
    {
        return ControlStrategy::IndividualFeeder;
    }
    if ( _controlMethod == ControlStrategy::SubstationBusControlMethod )
    {
        return ControlStrategy::SubstationBus;
    }
    if ( _controlMethod == ControlStrategy::BusOptimizedFeederControlMethod )
    {
        return ControlStrategy::BusOptimizedFeeder;
    }
    if ( _controlMethod == ControlStrategy::ManualOnlyControlMethod )
    {
        return ControlStrategy::ManualOnly;
    }

    return ControlStrategy::NoMethod;
}


const std::string MultiVoltStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType MultiVoltStrategy::getUnitType() const
{
    return ControlStrategy::MultiVolt;
}


const std::string MultiVoltStrategy::getControlUnits() const
{
    return ControlStrategy::MultiVoltControlUnit;
}


void MultiVoltStrategy::execute()
{
    // empty for now....
}

