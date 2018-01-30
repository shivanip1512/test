#include "precompiled.h"

#include "ccid.h"
#include "MsgTriggerDmvTest.h"



DEFINE_COLLECTABLE( MsgTriggerDmvTest, TRIGGER_DMV_TEST_MSG_ID )

MsgTriggerDmvTest::~MsgTriggerDmvTest()
{

}

MsgTriggerDmvTest::MsgTriggerDmvTest()
    :   ItemCommand()
{

}

MsgTriggerDmvTest::MsgTriggerDmvTest( const long busID, const std::string & dmvTestName )
    :   ItemCommand( CapControlCommand::SCHEDULE_DMV_TEST, busID ),
        _dmvTestName( dmvTestName )
{

}

MsgTriggerDmvTest::MsgTriggerDmvTest( const MsgTriggerDmvTest & msg )
{
    operator=( msg );
}

long MsgTriggerDmvTest::getBusID() const
{
    return getItemId();
}

std::string MsgTriggerDmvTest::getDmvTestName() const
{
    return _dmvTestName;
}

MsgTriggerDmvTest& MsgTriggerDmvTest::operator=(const MsgTriggerDmvTest& right)
{
    ItemCommand::operator=( right );

    if ( this != &right )
    {
        _dmvTestName    = right._dmvTestName;
    }

    return *this;
}

CtiMessage * MsgTriggerDmvTest::replicateMessage() const
{
    return new MsgTriggerDmvTest( *this );
}

