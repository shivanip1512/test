#include "precompiled.h"

#include "lmid.h"
#include "logger.h"
#include "SmartGroupBase.h"

#include <regex>



SmartGroupBase::SmartGroupBase( const std::string & typeName, Cti::RowReader & rdr )
    :   CtiLMGroupBase( rdr ),
        _groupTypeName( typeName ),
        _groupArticle( "a" )
{
    // if the group name starts with a vowel we change the article to "an"

    std::smatch m;  // ignored
    if ( std::regex_match( _groupTypeName, m, std::regex( "^[aeiou].*", std::regex::icase ) ) )
    {
        _groupArticle += "n";
    }
}

SmartGroupBase::~SmartGroupBase()
{
    // empty...
}

bool SmartGroupBase::doesStopRequireCommandAt(const CtiTime &currentTime) const
{
    return getLastStopTimeSent() > currentTime + 30 || getLastStopTimeSent() == gInvalidCtiTime;
}

CtiRequestMsg* SmartGroupBase::createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const
{
    CTILOG_INFO( dout, "Can not Time Refresh " << _groupArticle << " " << _groupTypeName << " Group." );

    return nullptr;
}

CtiRequestMsg* SmartGroupBase::createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const
{
    CTILOG_INFO( dout, "Can not Smart Cycle " << _groupArticle << " " << _groupTypeName << " Group." );

    return nullptr;
}

CtiRequestMsg* SmartGroupBase::createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const
{
    CTILOG_INFO( dout, "Can not Rotation " << _groupArticle << " " << _groupTypeName << " Group." );

    return nullptr;
}

CtiRequestMsg* SmartGroupBase::createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const
{
    CTILOG_INFO( dout, "Can not Master Cycle " << _groupArticle << " " << _groupTypeName << " Group." );

    return nullptr;
}

