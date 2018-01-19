#pragma once

#include "MsgItemCommand.h"



class MsgTriggerDmvTest : public ItemCommand
{
public:

    DECLARE_COLLECTABLE( MsgTriggerDmvTest );

    ~MsgTriggerDmvTest();

    MsgTriggerDmvTest();
    MsgTriggerDmvTest( const long busID, const std::string & dmvTestName );
    MsgTriggerDmvTest( const MsgTriggerDmvTest & msg );

    long getBusID() const;
    std::string getDmvTestName() const;

    MsgTriggerDmvTest & operator=( const MsgTriggerDmvTest & right );

    virtual CtiMessage * replicateMessage() const;

private:

    std::string _dmvTestName;
};

