
#include "precompiled.h"

#include <cstdlib>

#include "MultiVoltVarStrategy.h"
#include "std_helper.h"
#include "string_util.h"
#include "logger.h"


MultiVoltVarStrategy::MultiVoltVarStrategy()
    : ControlStrategy(),
    _peakUpperVoltLimit(0.0),
    _offpeakUpperVoltLimit(0.0),
    _peakLowerVoltLimit(0.0),
    _offpeakLowerVoltLimit(0.0),
    _peakKVarLeading(0.0),
    _offpeakKVarLeading(0.0),
    _peakKVarLagging(0.0),
    _offpeakKVarLagging(0.0),
    _maximumDeltaVoltage(10.0)
{
    // empty!
}


MultiVoltVarStrategy::~MultiVoltVarStrategy()
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


void MultiVoltVarStrategy::restoreParameters(const std::string &name, const std::string &type, const std::string &value)
{
    static const std::map< std::string, std::map< std::string, double MultiVoltVarStrategy::* > >   directLookup
    {
        {   "Upper Volt Limit",             {
                { "PEAK",               &MultiVoltVarStrategy::_peakUpperVoltLimit              },
                { "OFFPEAK",            &MultiVoltVarStrategy::_offpeakUpperVoltLimit           }   }
        },
        {   "Lower Volt Limit",             {
                { "PEAK",               &MultiVoltVarStrategy::_peakLowerVoltLimit              },
                { "OFFPEAK",            &MultiVoltVarStrategy::_offpeakLowerVoltLimit           }   }
        },
        {   "KVAR Leading",                 {
                { "PEAK",               &MultiVoltVarStrategy::_peakKVarLeading                 },
                { "OFFPEAK",            &MultiVoltVarStrategy::_offpeakKVarLeading              }   }
        },
        {   "KVAR Lagging",                 {
                { "PEAK",               &MultiVoltVarStrategy::_peakKVarLagging                 },
                { "OFFPEAK",            &MultiVoltVarStrategy::_offpeakKVarLagging              }   }
        },
        {   "Maximum Delta Voltage",        {
                { "MAX_DELTA",          &MultiVoltVarStrategy::_maximumDeltaVoltage             }   }
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


double MultiVoltVarStrategy::getPeakLag() const
{
    return getLowerVoltLimit( true );
}


double MultiVoltVarStrategy::getOffPeakLag() const
{
    return getLowerVoltLimit( false );
}


double MultiVoltVarStrategy::getPeakLead() const
{
    return getUpperVoltLimit( true );
}


double MultiVoltVarStrategy::getOffPeakLead() const
{
    return getUpperVoltLimit( false );
}


double MultiVoltVarStrategy::getUpperVoltLimit( const bool isPeak ) const
{
    return isPeak ? _peakUpperVoltLimit : _offpeakUpperVoltLimit;
}


double MultiVoltVarStrategy::getLowerVoltLimit( const bool isPeak ) const
{
    return isPeak ? _peakLowerVoltLimit : _offpeakLowerVoltLimit;
}


double MultiVoltVarStrategy::getPeakVARLag() const
{
    return _peakKVarLagging;
}


double MultiVoltVarStrategy::getOffPeakVARLag() const
{
    return _offpeakKVarLagging;
}


double MultiVoltVarStrategy::getPeakVARLead() const
{
    return _peakKVarLeading;
}


double MultiVoltVarStrategy::getOffPeakVARLead() const
{
    return _offpeakKVarLeading;
}


double MultiVoltVarStrategy::getPeakPFSetPoint() const
{
    return getPeakTimeFlag() ? getPeakVARLead() : getOffPeakVARLead();
}


double MultiVoltVarStrategy::getOffPeakPFSetPoint() const
{
    return getPeakTimeFlag() ? getPeakVARLag() : getOffPeakVARLag();;
}


double MultiVoltVarStrategy::getMaximumDeltaVoltage() const
{
    return _maximumDeltaVoltage;
}


void MultiVoltVarStrategy::setPeakLag(const double value)
{
    _peakLowerVoltLimit = value;
}


void MultiVoltVarStrategy::setOffPeakLag(const double value)
{
    _offpeakLowerVoltLimit = value;
}


void MultiVoltVarStrategy::setPeakLead(const double value)
{
    _peakUpperVoltLimit = value;
}


void MultiVoltVarStrategy::setOffPeakLead(const double value)
{
    _offpeakUpperVoltLimit = value;
}


void MultiVoltVarStrategy::setPeakVARLag(const double value)
{
    _peakKVarLagging = value;
}


void MultiVoltVarStrategy::setOffPeakVARLag(const double value)
{
    _offpeakKVarLagging = value;
}


void MultiVoltVarStrategy::setPeakVARLead(const double value)
{
    _peakKVarLeading = value;
}


void MultiVoltVarStrategy::setOffPeakVARLead(const double value)
{
    _offpeakKVarLeading = value;
}


void MultiVoltVarStrategy::setControlMethod(const std::string & method)
{
    if (method != ControlStrategy::TimeOfDayControlMethod)
    {
        _controlMethod = method;
    }
}


const ControlStrategy::ControlMethodType MultiVoltVarStrategy::getMethodType() const
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


const std::string MultiVoltVarStrategy::getControlMethod() const
{
    return _controlMethod;
}


const ControlStrategy::ControlUnitType MultiVoltVarStrategy::getUnitType() const
{
    return ControlStrategy::MultiVoltVar;
}


const std::string MultiVoltVarStrategy::getControlUnits() const
{
    return ControlStrategy::MultiVoltVarControlUnit;
}


void MultiVoltVarStrategy::execute()
{
    // empty for now....
}

