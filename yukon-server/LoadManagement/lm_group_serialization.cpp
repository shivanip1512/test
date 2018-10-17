#include "precompiled.h"

#include "message_serialization_util.h"
#include "lm_group_serialization.h"

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

MessageFactory<CtiLMGroupBase> g_lmGroupFactory(::Cti::Messaging::ActiveMQ::MessageType::prefix);

namespace {

struct LMGroupFactoryRegister
{
    LMGroupFactoryRegister()
    {
        g_lmGroupFactory.registerSerializer <::LMGroupDigiSEP,       Thrift::LMGroupDigiSEP>    ( &populateThrift, NULL, "LMGroupDigiSEP" );
        g_lmGroupFactory.registerSerializer <::LMGroupEcobee,        Thrift::LMGroupEcobee>     ( &populateThrift, NULL, "LMGroupEcobee" );
        g_lmGroupFactory.registerSerializer <::LMGroupHoneywell,     Thrift::LMGroupHoneywell>  ( &populateThrift, NULL, "LMGroupHoneywell" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupEmetcon,    Thrift::LMGroupEmetcon>    ( &populateThrift, NULL, "LMGroupEmetcon" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupExpresscom, Thrift::LMGroupExpresscom> ( &populateThrift, NULL, "LMGroupExpresscom" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupGolay,      Thrift::LMGroupGolay>      ( &populateThrift, NULL, "LMGroupGolay" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupMacro,      Thrift::LMGroupMacro>      ( &populateThrift, NULL, "LMGroupMacro" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupMCT,        Thrift::LMGroupMCT>        ( &populateThrift, NULL, "LMGroupMCT" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupPoint,      Thrift::LMGroupPoint>      ( &populateThrift, NULL, "LMGroupPoint" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupRipple,     Thrift::LMGroupRipple>     ( &populateThrift, NULL, "LMGroupRipple" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA105,      Thrift::LMGroupSA105>      ( &populateThrift, NULL, "LMGroupSA105" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA205,      Thrift::LMGroupSA205>      ( &populateThrift, NULL, "LMGroupSA205" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA305,      Thrift::LMGroupSA305>      ( &populateThrift, NULL, "LMGroupSA305" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSADigital,  Thrift::LMGroupSADigital>  ( &populateThrift, NULL, "LMGroupSADigital" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupVersacom,   Thrift::LMGroupVersacom>   ( &populateThrift, NULL, "LMGroupVersacom" );
    }
};

const LMGroupFactoryRegister g_lmGroupFactoryRegister;

}

//=============================================================================
//  LMGroupBase
//=============================================================================

MessagePtr<Thrift::LMGroupBase>::type populateThrift( const ::CtiLMGroupBase& imsg )
{
    MessagePtr<Thrift::LMGroupBase>::type omsg( new Thrift::LMGroupBase );

    // we have to clone the message for getDailyOps()
    MessagePtr<::CtiLMGroupBase>::type p_imsg( imsg.replicate());

    omsg->__set__paoId                          ( p_imsg->getPAOId() );
    omsg->__set__paoCategory                    ( p_imsg->getPAOCategory() );
    omsg->__set__paoClass                       ( p_imsg->getPAOClass() );
    omsg->__set__paoName                        ( p_imsg->getPAOName() );
    omsg->__set__paoTypeString                  ( p_imsg->getPAOTypeString() );
    omsg->__set__paoDescription                 ( p_imsg->getPAODescription() );
    omsg->__set__disableFlag                    ( p_imsg->getDisableFlag() );
    omsg->__set__groupOrder                     ( p_imsg->getGroupOrder() );
    omsg->__set__kwCapacity                     ( p_imsg->getKWCapacity() );
    omsg->__set__childOrder                     ( p_imsg->getChildOrder() );
    omsg->__set__alarmInhibit                   ( p_imsg->getAlarmInhibit() );
    omsg->__set__controlInhibit                 ( p_imsg->getControlInhibit() );
    omsg->__set__groupControlState              ( p_imsg->getGroupControlState() );
    omsg->__set__currentHoursDaily              ( p_imsg->getCurrentHoursDaily() );
    omsg->__set__currentHoursMonthly            ( p_imsg->getCurrentHoursMonthly() );
    omsg->__set__currentHoursSeasonal           ( p_imsg->getCurrentHoursSeasonal() );
    omsg->__set__currentHoursAnnually           ( p_imsg->getCurrentHoursAnnually() );
    omsg->__set__lastControlSent                ( CtiTimeToMilliseconds( p_imsg->getLastControlSent() ));
    omsg->__set__controlStartTime               ( CtiTimeToMilliseconds( p_imsg->getControlStartTime() ));
    omsg->__set__controlCompleteTime            ( CtiTimeToMilliseconds( p_imsg->getControlCompleteTime() ));
    omsg->__set__nextControlTime                ( CtiTimeToMilliseconds( p_imsg->getNextControlTime() ));
    omsg->__set__internalState                  ( imsg.getInternalState() );
    omsg->__set__dailyOps                       ( p_imsg->getDailyOps() );
    omsg->__set__lastStopTimeSent               ( CtiTimeToMilliseconds( p_imsg->getLastStopTimeSent() ));

    return omsg;
}

//=============================================================================
//  LMGroupDigiSEP
//=============================================================================

MessagePtr<Thrift::LMGroupDigiSEP>::type populateThrift( const ::LMGroupDigiSEP& imsg )
{
    MessagePtr<Thrift::LMGroupDigiSEP>::type omsg( new Thrift::LMGroupDigiSEP );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupEcobee
//=============================================================================

MessagePtr<Thrift::LMGroupEcobee>::type populateThrift( const ::LMGroupEcobee& imsg )
{
    MessagePtr<Thrift::LMGroupEcobee>::type omsg( new Thrift::LMGroupEcobee );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupHoneywell
//=============================================================================

MessagePtr<Thrift::LMGroupHoneywell>::type populateThrift(const ::LMGroupHoneywell& imsg)
{
    MessagePtr<Thrift::LMGroupHoneywell>::type omsg( new Thrift::LMGroupHoneywell );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupEmetcon
//=============================================================================

MessagePtr<Thrift::LMGroupEmetcon>::type populateThrift( const ::CtiLMGroupEmetcon& imsg )
{
    MessagePtr<Thrift::LMGroupEmetcon>::type omsg( new Thrift::LMGroupEmetcon );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupExpresscom
//=============================================================================

MessagePtr<Thrift::LMGroupExpresscom>::type populateThrift( const ::CtiLMGroupExpresscom& imsg )
{
    MessagePtr<Thrift::LMGroupExpresscom>::type omsg( new Thrift::LMGroupExpresscom );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupGolay
//=============================================================================

MessagePtr<Thrift::LMGroupGolay>::type populateThrift( const ::CtiLMGroupGolay& imsg )
{
    MessagePtr<Thrift::LMGroupGolay>::type omsg( new Thrift::LMGroupGolay );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupMacro
//=============================================================================

MessagePtr<Thrift::LMGroupMacro>::type populateThrift( const ::CtiLMGroupMacro& imsg )
{
    MessagePtr<Thrift::LMGroupMacro>::type omsg( new Thrift::LMGroupMacro );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupMCT
//=============================================================================

MessagePtr<Thrift::LMGroupMCT>::type populateThrift( const ::CtiLMGroupMCT& imsg )
{
    MessagePtr<Thrift::LMGroupMCT>::type omsg( new Thrift::LMGroupMCT );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupEmetcon&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupPoint
//=============================================================================

MessagePtr<Thrift::LMGroupPoint>::type populateThrift( const ::CtiLMGroupPoint& imsg )
{
    MessagePtr<Thrift::LMGroupPoint>::type omsg( new Thrift::LMGroupPoint );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupRipple
//=============================================================================

MessagePtr<Thrift::LMGroupRipple>::type populateThrift( const ::CtiLMGroupRipple& imsg )
{
    MessagePtr<Thrift::LMGroupRipple>::type omsg( new Thrift::LMGroupRipple );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));
    omsg->__set__shedTime    ( imsg.getShedTime() );

    return omsg;
}

//=============================================================================
//  LMGroupSA105
//=============================================================================

MessagePtr<Thrift::LMGroupSA105>::type populateThrift( const ::CtiLMGroupSA105& imsg )
{
    MessagePtr<Thrift::LMGroupSA105>::type omsg( new Thrift::LMGroupSA105 );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSA205
//=============================================================================

MessagePtr<Thrift::LMGroupSA205>::type populateThrift( const ::CtiLMGroupSA205& imsg )
{
    MessagePtr<Thrift::LMGroupSA205>::type omsg( new Thrift::LMGroupSA205 );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSA305
//=============================================================================

MessagePtr<Thrift::LMGroupSA305>::type populateThrift( const ::CtiLMGroupSA305& imsg )
{
    MessagePtr<Thrift::LMGroupSA305>::type omsg( new Thrift::LMGroupSA305 );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSADigital
//=============================================================================

MessagePtr<Thrift::LMGroupSADigital>::type populateThrift( const ::CtiLMGroupSADigital& imsg )
{
    MessagePtr<Thrift::LMGroupSADigital>::type omsg( new Thrift::LMGroupSADigital );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupVersacom
//=============================================================================

MessagePtr<Thrift::LMGroupVersacom>::type populateThrift( const ::CtiLMGroupVersacom& imsg )
{
    MessagePtr<Thrift::LMGroupVersacom>::type omsg( new Thrift::LMGroupVersacom );

    omsg->__set__baseMessage ( *populateThrift( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}


}
}
}

