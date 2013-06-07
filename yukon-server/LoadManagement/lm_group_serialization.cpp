#include "precompiled.h"

#include "message_serialization_util.h"
#include "lm_group_serialization.h"

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

MessageFactory<CtiLMGroupBase> g_lmGroupFactory;

namespace {

struct LMGroupFactoryRegister
{
    LMGroupFactoryRegister()
    {
        g_lmGroupFactory.registerSerializer <::LMGroupDigiSEP,       Thrift::LMGroupDigiSEP>    ( &serialize, NULL, "LMGroupDigiSEP" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupEmetcon,    Thrift::LMGroupEmetcon>    ( &serialize, NULL, "LMGroupEmetcon" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupExpresscom, Thrift::LMGroupExpresscom> ( &serialize, NULL, "LMGroupExpresscom" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupGolay,      Thrift::LMGroupGolay>      ( &serialize, NULL, "LMGroupGolay" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupMacro,      Thrift::LMGroupMacro>      ( &serialize, NULL, "LMGroupMacro" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupMCT,        Thrift::LMGroupMCT>        ( &serialize, NULL, "LMGroupMCT" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupPoint,      Thrift::LMGroupPoint>      ( &serialize, NULL, "LMGroupPoint" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupRipple,     Thrift::LMGroupRipple>     ( &serialize, NULL, "LMGroupRipple" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA105,      Thrift::LMGroupSA105>      ( &serialize, NULL, "LMGroupSA105" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA205,      Thrift::LMGroupSA205>      ( &serialize, NULL, "LMGroupSA205" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSA305,      Thrift::LMGroupSA305>      ( &serialize, NULL, "LMGroupSA305" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupSADigital,  Thrift::LMGroupSADigital>  ( &serialize, NULL, "LMGroupSADigital" );
        g_lmGroupFactory.registerSerializer <::CtiLMGroupVersacom,   Thrift::LMGroupVersacom>   ( &serialize, NULL, "LMGroupVersacom" );
    }
};

const LMGroupFactoryRegister g_lmGroupFactoryRegister;

}

//=============================================================================
//  LMGroupBase
//=============================================================================

MessagePtr<Thrift::LMGroupBase>::type serialize( const ::CtiLMGroupBase& imsg )
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

MessagePtr<Thrift::LMGroupDigiSEP>::type serialize( const ::LMGroupDigiSEP& imsg )
{
    MessagePtr<Thrift::LMGroupDigiSEP>::type omsg( new Thrift::LMGroupDigiSEP );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupEmetcon
//=============================================================================

MessagePtr<Thrift::LMGroupEmetcon>::type serialize( const ::CtiLMGroupEmetcon& imsg )
{
    MessagePtr<Thrift::LMGroupEmetcon>::type omsg( new Thrift::LMGroupEmetcon );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupExpresscom
//=============================================================================

MessagePtr<Thrift::LMGroupExpresscom>::type serialize( const ::CtiLMGroupExpresscom& imsg )
{
    MessagePtr<Thrift::LMGroupExpresscom>::type omsg( new Thrift::LMGroupExpresscom );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupGolay
//=============================================================================

MessagePtr<Thrift::LMGroupGolay>::type serialize( const ::CtiLMGroupGolay& imsg )
{
    MessagePtr<Thrift::LMGroupGolay>::type omsg( new Thrift::LMGroupGolay );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupMacro
//=============================================================================

MessagePtr<Thrift::LMGroupMacro>::type serialize( const ::CtiLMGroupMacro& imsg )
{
    MessagePtr<Thrift::LMGroupMacro>::type omsg( new Thrift::LMGroupMacro );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupMCT
//=============================================================================

MessagePtr<Thrift::LMGroupMCT>::type serialize( const ::CtiLMGroupMCT& imsg )
{
    MessagePtr<Thrift::LMGroupMCT>::type omsg( new Thrift::LMGroupMCT );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupEmetcon&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupPoint
//=============================================================================

MessagePtr<Thrift::LMGroupPoint>::type serialize( const ::CtiLMGroupPoint& imsg )
{
    MessagePtr<Thrift::LMGroupPoint>::type omsg( new Thrift::LMGroupPoint );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupRipple
//=============================================================================

MessagePtr<Thrift::LMGroupRipple>::type serialize( const ::CtiLMGroupRipple& imsg )
{
    MessagePtr<Thrift::LMGroupRipple>::type omsg( new Thrift::LMGroupRipple );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));
    omsg->__set__shedTime                       ( imsg.getShedTime() );

    return omsg;
}

//=============================================================================
//  LMGroupSA105
//=============================================================================

MessagePtr<Thrift::LMGroupSA105>::type serialize( const ::CtiLMGroupSA105& imsg )
{
    MessagePtr<Thrift::LMGroupSA105>::type omsg( new Thrift::LMGroupSA105 );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSA205
//=============================================================================

MessagePtr<Thrift::LMGroupSA205>::type serialize( const ::CtiLMGroupSA205& imsg )
{
    MessagePtr<Thrift::LMGroupSA205>::type omsg( new Thrift::LMGroupSA205 );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSA305
//=============================================================================

MessagePtr<Thrift::LMGroupSA305>::type serialize( const ::CtiLMGroupSA305& imsg )
{
    MessagePtr<Thrift::LMGroupSA305>::type omsg( new Thrift::LMGroupSA305 );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupSADigital
//=============================================================================

MessagePtr<Thrift::LMGroupSADigital>::type serialize( const ::CtiLMGroupSADigital& imsg )
{
    MessagePtr<Thrift::LMGroupSADigital>::type omsg( new Thrift::LMGroupSADigital );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}

//=============================================================================
//  LMGroupVersacom
//=============================================================================

MessagePtr<Thrift::LMGroupVersacom>::type serialize( const ::CtiLMGroupVersacom& imsg )
{
    MessagePtr<Thrift::LMGroupVersacom>::type omsg( new Thrift::LMGroupVersacom );

    omsg->__set__baseMessage                    ( *serialize( static_cast<const ::CtiLMGroupBase&>(imsg) ));

    return omsg;
}


}
}
}

