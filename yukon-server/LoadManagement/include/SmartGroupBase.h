#pragma once

#include "lmgroupbase.h"
#include "GroupControlInterface.h"



class SmartGroupBase : public CtiLMGroupBase,
                       public Cti::LoadManagement::GroupControlInterface
{
public:

    SmartGroupBase( const std::string & typeName, Cti::RowReader & rdr );

    virtual ~SmartGroupBase();

    virtual bool doesStopRequireCommandAt( const CtiTime & currentTime ) const override;

    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const override;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const override;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const override;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const override;

protected:

    std::string _groupTypeName,
                _groupArticle;
};

