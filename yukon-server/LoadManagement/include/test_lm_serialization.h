#pragma once

#include "test_serialization.h"

#include "lmmessage.h"
#include "lmprogrambase.h"
#include "lmprogramcontrolwindow.h"
#include "lmprogramcurtailment.h"
#include "lmcurtailcustomer.h"
#include "lmcicustomerbase.h"
#include "lmprogramdirect.h"
#include "lmprogramdirectgear.h"
#include "lmprogramenergyexchange.h"
#include "lmenergyexchangeoffer.h"
#include "lmenergyexchangeofferrevision.h"
#include "lmenergyexchangehourlyoffer.h"
#include "lmenergyexchangecustomer.h"
#include "lmenergyexchangecustomerreply.h"
#include "lmenergyexchangehourlycustomer.h"
#include "lmgroupbase.h"
#include "lmgroupdigisep.h"
#include "lmgroupemetcon.h"
#include "lmgroupexpresscom.h"
#include "lmgroupgolay.h"
#include "lmgroupmacro.h"
#include "lmgroupmct.h"
#include "lmgrouppoint.h"
#include "lmgroupripple.h"
#include "lmgroupsa105.h"
#include "lmgroupsa205.h"
#include "lmgroupsa305.h"
#include "lmgroupsadigital.h"
#include "lmgroupversacom.h"

/*-----------------------------------------------------------------------------
    LMMessage
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMMessage> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMMessage );
    }

    void Populate()
    {
        TestCase<CtiMessage>::Populate();

        CtiLMMessage &imsg = dynamic_cast<CtiLMMessage&>(*_imsg);

        GenerateRandom( imsg._message );
    }
};

/*-----------------------------------------------------------------------------
    LMCommand
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMCommand> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMCommand( 0, 0, 0, 0, 0, 0 ));
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMCommand &imsg = dynamic_cast<CtiLMCommand&>(*_imsg);

        GenerateRandom( imsg._command );
        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._number );
        GenerateRandom( imsg._value );
        GenerateRandom( imsg._count );
        GenerateRandom( imsg._auxid );
    }
};

/*-----------------------------------------------------------------------------
    LMManualControlRequest
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMManualControlRequest> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMManualControlRequest );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMManualControlRequest &imsg = dynamic_cast<CtiLMManualControlRequest&>(*_imsg);

        GenerateRandom( imsg._command );
        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._notifytime );
        GenerateRandom( imsg._starttime );
        GenerateRandom( imsg._stoptime );
        GenerateRandom( imsg._startgear );
        GenerateRandom( imsg._startpriority );
        GenerateRandom( imsg._additionalinfo );
        GenerateRandom( imsg._constraint_cmd );
        GenerateRandom( imsg._origin );
    }
};

/*-----------------------------------------------------------------------------
    LMConstraintViolation
-----------------------------------------------------------------------------*/
template<>
struct TestCase<ConstraintViolation> : public TestCaseBase<ConstraintViolation>
{
    void Populate()
    {
        ConstraintViolation &imsg = *_imsg;

        GenerateRandom( imsg._errorCode, 100, 149 ); // errorCode <=> [100, 149]
        GenerateRandom( imsg._doubleParams );
        GenerateRandom( imsg._integerParams );
        GenerateRandom( imsg._stringParams );
        GenerateRandom( imsg._datetimeParams );
    }
};

/*-----------------------------------------------------------------------------
    LMManualControlResponse
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMManualControlResponse> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMManualControlResponse );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMManualControlResponse &imsg = dynamic_cast<CtiLMManualControlResponse&>(*_imsg);

        GenerateRandom( imsg._paoid );

        const int maxContraintViolations = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxContraintViolations; item_nbr++ )
        {
            imsg._constraintViolations.push_back( ConstraintViolation() );
            TestCaseItem<ConstraintViolation> tc_item;
            tc_item.Populate( &imsg._constraintViolations.back() );
        }

        GenerateRandom( imsg._best_fit_action );
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeControl
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeControlMsg> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMEnergyExchangeControlMsg );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMEnergyExchangeControlMsg &imsg = dynamic_cast<CtiLMEnergyExchangeControlMsg&>(*_imsg);

        GenerateRandom( imsg._command );
        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._offerdate );
        GenerateRandom( imsg._notificationdatetime );
        GenerateRandom( imsg._expirationdatetime );
        GenerateRandom( imsg._additionalinfo );

        for( int hour_nbr = 0 ; hour_nbr < HOURS_IN_DAY; hour_nbr++ )
        {
            imsg._amountsrequested[hour_nbr] = GenerateRandom<double>();
        }

        for( int hour_nbr = 0 ; hour_nbr < HOURS_IN_DAY; hour_nbr++)
        {
            imsg._pricesoffered[hour_nbr] = GenerateRandom<long>();
        }
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeAccept
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeAcceptMsg> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMEnergyExchangeAcceptMsg );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMEnergyExchangeAcceptMsg &imsg = dynamic_cast<CtiLMEnergyExchangeAcceptMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._revisionnumber );
        GenerateRandom( imsg._acceptstatus );
        GenerateRandom( imsg._ipaddressofacceptuser );
        GenerateRandom( imsg._useridname );
        GenerateRandom( imsg._nameofacceptperson );
        GenerateRandom( imsg._energyexchangenotes );

        for( int hour_nbr = 0 ; hour_nbr < HOURS_IN_DAY; hour_nbr++ )
        {
            imsg._amountscommitted[hour_nbr] = GenerateRandom<double>();
        }
    }
};

/*-----------------------------------------------------------------------------
    LMControlAreaTrigger
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlAreaTrigger> : public TestCaseBase<CtiLMControlAreaTrigger>
{
    void Populate()
    {
        CtiLMControlAreaTrigger &imsg = *_imsg;

        const std::string triggertype_items[]  = { "Threshold Point",
                                                   "Threshold",
                                                   "Status" };

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._triggernumber );
        GenerateRandom( imsg._triggertype, triggertype_items );
        GenerateRandom( imsg._pointid );
        GenerateRandom( imsg._pointvalue );
        GenerateRandom( imsg._lastpointvaluetimestamp );
        GenerateRandom( imsg._normalstate );
        GenerateRandom( imsg._threshold );
        GenerateRandom( imsg._projectiontype );
        GenerateRandom( imsg._projectionpoints );
        GenerateRandom( imsg._projectaheadduration );
        GenerateRandom( imsg._thresholdkickpercent );
        GenerateRandom( imsg._minrestoreoffset );
        GenerateRandom( imsg._peakpointid );
        GenerateRandom( imsg._peakpointvalue );
        GenerateRandom( imsg._lastpeakpointvaluetimestamp );
        GenerateRandom( imsg._projectedpointvalue );
    }
};

/*-----------------------------------------------------------------------------
    LMProgramControlWindow
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramControlWindow> : public TestCaseBase<CtiLMProgramControlWindow>
{
    void Populate()
    {
        CtiLMProgramControlWindow &imsg = *_imsg;

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._windownumber );
        GenerateRandom( imsg._availablestarttime );
        GenerateRandom( imsg._availablestoptime );
    }
};

/*-----------------------------------------------------------------------------
    LMProgramBase
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramBase> : public TestCaseBase<CtiLMProgramBase>
{
    void Populate()
    {
        CtiLMProgramBase &imsg = *_imsg;

        const std::string paoTypeString_items[] = { "mct-440-2131b",
                                                    "mct-440-2132b",
                                                    "mct-440-2133b" };

        const LONG programState_items[]         = { CtiLMProgramBase::InactiveState,
                                                    CtiLMProgramBase::ActiveState,
                                                    CtiLMProgramBase::ManualActiveState,
                                                    CtiLMProgramBase::ScheduledState,
                                                    CtiLMProgramBase::NotifiedState,
                                                    CtiLMProgramBase::FullyActiveState,
                                                    CtiLMProgramBase::StoppingState,
                                                    CtiLMProgramBase::GearChangeState,
                                                    CtiLMProgramBase::NonControllingState,
                                                    CtiLMProgramBase::TimedActiveState };

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._paocategory );
        GenerateRandom( imsg._paoclass );
        GenerateRandom( imsg._paoname );
        GenerateRandom( imsg._paoTypeString, paoTypeString_items );
        GenerateRandom( imsg._paodescription );
        imsg._disableflag = GenerateRandom<bool>();             // generate a bool and assign to a BOOL
        GenerateRandom( imsg._start_priority );
        GenerateRandom( imsg._stop_priority );
        GenerateRandom( imsg._controltype );
        GenerateRandom( imsg._availableweekdays );
        GenerateRandom( imsg._maxhoursdaily );
        GenerateRandom( imsg._maxhoursmonthly );
        GenerateRandom( imsg._maxhoursseasonal );
        GenerateRandom( imsg._maxhoursannually );
        GenerateRandom( imsg._minactivatetime );
        GenerateRandom( imsg._minrestarttime );
        GenerateRandom( imsg._programstatuspointid );
        GenerateRandom( imsg._programstate, programState_items ); // <=> [STATEZERO, STATENINE]
        GenerateRandom( imsg._reductionanalogpointid );
        GenerateRandom( imsg._reductiontotal );
        GenerateRandom( imsg._startedcontrolling );
        GenerateRandom( imsg._lastcontrolsent );
        imsg._manualcontrolreceivedflag = GenerateRandom<bool>(); // generate a bool and assign to a BOOL

        const int maxProgramControlWindows = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxProgramControlWindows; item_nbr++ )
        {
            imsg._lmprogramcontrolwindows.push_back( new CtiLMProgramControlWindow );
            TestCaseItem<CtiLMProgramControlWindow> tc_item;
            tc_item.Populate( imsg._lmprogramcontrolwindows.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMCICustomerBase
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMCICustomerBase> : public TestCaseBase<CtiLMCICustomerBase>
{
    void Populate()
    {
        CtiLMCICustomerBase &imsg = *_imsg;

        GenerateRandom( imsg._customerid );
        GenerateRandom( imsg._companyname );
        GenerateRandom( imsg._customerdemandlevel );
        GenerateRandom( imsg._curtailamount );
        GenerateRandom( imsg._curtailmentagreement );
        GenerateRandom( imsg._time_zone );
        GenerateRandom( imsg._customerorder );
    }
};

/*-----------------------------------------------------------------------------
    LMCurtailCustomer
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMCurtailCustomer> : public TestCase<CtiLMCICustomerBase>
{
    void Populate()
    {
        TestCase<CtiLMCICustomerBase>::Populate();

        CtiLMCurtailCustomer &imsg = static_cast<CtiLMCurtailCustomer&>(*_imsg);

        imsg._requireack = GenerateRandom<bool>();      // generate a bool and assign to a BOOL
        GenerateRandom( imsg._curtailreferenceid );
        GenerateRandom( imsg._acknowledgestatus );
        GenerateRandom( imsg._ackdatetime );
        GenerateRandom( imsg._ipaddressofackuser );
        GenerateRandom( imsg._useridname );
        GenerateRandom( imsg._nameofackperson );
        GenerateRandom( imsg._curtailmentnotes );
        imsg._acklateflag = GenerateRandom<bool>();     // generate a bool and assign to a BOOL
    }
};

/*-----------------------------------------------------------------------------
    LMProgramCurtailment
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramCurtailment> : public TestCase<CtiLMProgramBase>
{
    void Populate()
    {
        TestCase<CtiLMProgramBase>::Populate();

        CtiLMProgramCurtailment &imsg = static_cast<CtiLMProgramCurtailment&>(*_imsg);

        GenerateRandom( imsg._minnotifytime );
        GenerateRandom( imsg._heading );
        GenerateRandom( imsg._messageheader );
        GenerateRandom( imsg._messagefooter );
        GenerateRandom( imsg._acktimelimit );
        GenerateRandom( imsg._canceledmsg );
        GenerateRandom( imsg._stoppedearlymsg );
        GenerateRandom( imsg._curtailreferenceid );
        GenerateRandom( imsg._actiondatetime );
        GenerateRandom( imsg._notificationdatetime );
        GenerateRandom( imsg._curtailmentstarttime );
        GenerateRandom( imsg._curtailmentstoptime );
        GenerateRandom( imsg._runstatus );
        GenerateRandom( imsg._additionalinfo );

        const int maxProgramcurtailmentcustomers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxProgramcurtailmentcustomers; item_nbr++ )
        {
            imsg._lmprogramcurtailmentcustomers.push_back( new CtiLMCurtailCustomer );
            TestCaseItem<CtiLMCurtailCustomer> tc_item;
            tc_item.Populate( imsg._lmprogramcurtailmentcustomers.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMProgramDirectGear
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramDirectGear> : public TestCaseBase<CtiLMProgramDirectGear>
{
    void Populate()
    {
        CtiLMProgramDirectGear &imsg = *_imsg;

        const std::string controlmethod_items[]  = { "TimeRefresh",
                                                     "SmartCycle",
                                                     "SepCycle" };

        GenerateRandom( imsg._program_paoid );
        GenerateRandom( imsg._gearname );
        GenerateRandom( imsg._gearnumber );
        GenerateRandom( imsg._controlmethod, controlmethod_items );
        GenerateRandom( imsg._methodrate );
        GenerateRandom( imsg._methodperiod );
        GenerateRandom( imsg._methodratecount );
        GenerateRandom( imsg._cyclerefreshrate );
        GenerateRandom( imsg._methodstoptype );
        GenerateRandom( imsg._changecondition );
        GenerateRandom( imsg._changeduration );
        GenerateRandom( imsg._changepriority );
        GenerateRandom( imsg._changetriggernumber );
        GenerateRandom( imsg._changetriggeroffset );
        GenerateRandom( imsg._percentreduction );
        GenerateRandom( imsg._groupselectionmethod );
        GenerateRandom( imsg._methodoptiontype );
        GenerateRandom( imsg._methodoptionmax );
        GenerateRandom( imsg._rampininterval );
        GenerateRandom( imsg._rampinpercent );
        GenerateRandom( imsg._rampoutinterval );
        GenerateRandom( imsg._rampoutpercent );
        GenerateRandom( imsg._kw_reduction );
    }
};

/*-----------------------------------------------------------------------------
    LMGroupBase
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupBase> : public TestCaseBase<CtiLMGroupBase>
{
    void Populate()
    {
        CtiLMGroupBase &imsg = *_imsg;

        const std::string paoTypeString_items[]  = { "mct-440-2131b",
                                                     "mct-440-2132b",
                                                     "mct-440-2133b" };

        GenerateRandom( imsg._paoid  );
        GenerateRandom( imsg._paocategory );
        GenerateRandom( imsg._paoclass );
        GenerateRandom( imsg._paoname );
        GenerateRandom( imsg._paoTypeString, paoTypeString_items ); // pao type string
        GenerateRandom( imsg._paodescription );
        imsg._disableflag = GenerateRandom<bool>();                 // generate a bool and assign to a BOOL
        GenerateRandom( imsg._grouporder );
        GenerateRandom( imsg._kwcapacity );
        GenerateRandom( imsg._childorder );
        imsg._alarminhibit = GenerateRandom<bool>();                // generate a bool and assign to a BOOL
        imsg._controlinhibit = GenerateRandom<bool>();              // generate a bool and assign to a BOOL
        GenerateRandom( imsg._groupcontrolstate );
        GenerateRandom( imsg._currenthoursdaily );
        GenerateRandom( imsg._currenthoursmonthly );
        GenerateRandom( imsg._currenthoursseasonal );
        GenerateRandom( imsg._currenthoursannually );
        GenerateRandom( imsg._lastcontrolsent );
        GenerateRandom( imsg._controlstarttime );
        GenerateRandom( imsg._controlcompletetime );
        GenerateRandom( imsg._next_control_time );
        GenerateRandom( imsg._internalState );
        GenerateRandom( imsg._daily_ops );
        GenerateRandom( imsg._lastStopTimeSent );
    }
};

/*-----------------------------------------------------------------------------
    LMGroupDigiSEP
-----------------------------------------------------------------------------*/
template<>
struct TestCase<LMGroupDigiSEP> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        LMGroupDigiSEP& imsg = static_cast<LMGroupDigiSEP&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupEmetcon¸
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupEmetcon> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupEmetcon& imsg = static_cast<CtiLMGroupEmetcon&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupExpresscom
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupExpresscom> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupExpresscom& imsg = static_cast<CtiLMGroupExpresscom&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupGolay
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupGolay> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupGolay& imsg = static_cast<CtiLMGroupGolay&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupMacro
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupMacro> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupMacro& imsg = static_cast<CtiLMGroupMacro&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupMCT
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupMCT> : public TestCase<CtiLMGroupEmetcon>
{
    void Populate()
    {
        TestCase<CtiLMGroupEmetcon>::Populate();

        CtiLMGroupMCT& imsg = static_cast<CtiLMGroupMCT&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupPoint
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupPoint> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupPoint& imsg = static_cast<CtiLMGroupPoint&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupRipple
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupRipple> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupRipple& imsg = static_cast<CtiLMGroupRipple&>(*_imsg);

        GenerateRandom( imsg._shedtime );
    }
};

/*-----------------------------------------------------------------------------
    LMGroupSA105
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupSA105> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupSA105& imsg = static_cast<CtiLMGroupSA105&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupSA205
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupSA205> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupSA205& imsg = static_cast<CtiLMGroupSA205&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupSA305
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupSA305> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupSA305& imsg = static_cast<CtiLMGroupSA305&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupSADigital
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupSADigital> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupSADigital& imsg = static_cast<CtiLMGroupSADigital&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMGroupVersacom
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMGroupVersacom> : public TestCase<CtiLMGroupBase>
{
    void Populate()
    {
        TestCase<CtiLMGroupBase>::Populate();

        CtiLMGroupVersacom& imsg = static_cast<CtiLMGroupVersacom&>(*_imsg);
    }
};

/*-----------------------------------------------------------------------------
    LMProgramDirect
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramDirect> : public TestCase<CtiLMProgramBase>
{
    bool generate_master_subordinate;

    TestCase() : generate_master_subordinate(true)
    {
    }

    void Populate()
    {
        TestCase<CtiLMProgramBase>::Populate();

        CtiLMProgramDirect &imsg = static_cast<CtiLMProgramDirect&>(*_imsg);

        GenerateRandom( imsg._currentgearnumber );
        GenerateRandom( imsg._lastgroupcontrolled );
        GenerateRandom( imsg._directstarttime );
        GenerateRandom( imsg._directstoptime );
        GenerateRandom( imsg._notify_active_time );
        GenerateRandom( imsg._notify_inactive_time );
        GenerateRandom( imsg._startedrampingout );
        GenerateRandom( imsg._trigger_offset );
        GenerateRandom( imsg._trigger_restore_offset );
        imsg._constraint_override = GenerateRandom<bool>(); // generate a bool and assign to a BOOL

        const int maxProgramDirectGears = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0 ; item_nbr < maxProgramDirectGears ; item_nbr++ )
        {
            imsg._lmprogramdirectgears.push_back( new CtiLMProgramDirectGear );
            TestCaseItem<CtiLMProgramDirectGear> tc_item;
            tc_item.Populate( imsg._lmprogramdirectgears.back() );
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new LMGroupDigiSEP ));
            TestCaseItem<LMGroupDigiSEP> tc_item;
            tc_item.Populate( static_cast<LMGroupDigiSEP*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupEmetcon ));
            TestCaseItem<CtiLMGroupEmetcon> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupEmetcon*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupExpresscom ));
            TestCaseItem<CtiLMGroupExpresscom> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupExpresscom*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupGolay ));
            TestCaseItem<CtiLMGroupGolay> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupGolay*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        // No Implemenation found in JAVA client
//        {
//            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupMacro ));
//            TestCaseItem<CtiLMGroupMacro> tc_item;
//            tc_item.Populate( static_cast<CtiLMGroupMacro*>( imsg._lmprogramdirectgroups.back().get() ));
//        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupMCT ));
            TestCaseItem<CtiLMGroupMCT> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupMCT*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupPoint ));
            TestCaseItem<CtiLMGroupPoint> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupPoint*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupRipple ));
            TestCaseItem<CtiLMGroupRipple> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupRipple*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupSA105 ));
            TestCaseItem<CtiLMGroupSA105> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupSA105*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupSA205 ));
            TestCaseItem<CtiLMGroupSA205> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupSA205*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupSA305 ));
            TestCaseItem<CtiLMGroupSA305> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupSA305*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupSADigital ));
            TestCaseItem<CtiLMGroupSADigital> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupSADigital*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        {
            imsg._lmprogramdirectgroups.push_back( CtiLMGroupPtr( new CtiLMGroupVersacom ));
            TestCaseItem<CtiLMGroupVersacom> tc_item;
            tc_item.Populate( static_cast<CtiLMGroupVersacom*>( imsg._lmprogramdirectgroups.back().get() ));
        }

        if( generate_master_subordinate )
        {
            const LONG programState_items[] = { // CtiLMProgramBase::InactiveState, // only send active state
                                                CtiLMProgramBase::ActiveState,
                                                CtiLMProgramBase::ManualActiveState,
                                                CtiLMProgramBase::ScheduledState,
                                                CtiLMProgramBase::NotifiedState,
                                                CtiLMProgramBase::FullyActiveState,
                                                CtiLMProgramBase::StoppingState,
                                                CtiLMProgramBase::GearChangeState,
                                                CtiLMProgramBase::NonControllingState,
                                                CtiLMProgramBase::TimedActiveState };

            // _master_programs
            {
                imsg._master_programs.insert( CtiLMProgramDirectSPtr( new CtiLMProgramDirect ));
                TestCaseItem<CtiLMProgramDirect> tc_item;
                tc_item._tc.generate_master_subordinate = false;
                tc_item.Populate( (*imsg._master_programs.rbegin()).get() );
                GenerateRandom( (*imsg._master_programs.rbegin())->_programstate, programState_items ); // re-call random generator on the program state
            }

            // _subordinate_programs
            {
                imsg._subordinate_programs.insert( CtiLMProgramDirectSPtr( new CtiLMProgramDirect ));
                TestCaseItem<CtiLMProgramDirect> tc_item;
                tc_item._tc.generate_master_subordinate = false;
                tc_item.Populate( (*imsg._subordinate_programs.rbegin()).get() );
                GenerateRandom( (*imsg._subordinate_programs.rbegin())->_programstate, programState_items ); // re-call random generator on the program state
            }
        }

        GenerateRandom( imsg._origin );
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeHourlyOffer
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeHourlyOffer> : public TestCaseBase<CtiLMEnergyExchangeHourlyOffer>
{
    void Populate()
    {
        CtiLMEnergyExchangeHourlyOffer &imsg = *_imsg;

        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._revisionnumber );
        GenerateRandom( imsg._hour );
        GenerateRandom( imsg._price );
        GenerateRandom( imsg._amountrequested );
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeOfferRevision
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeOfferRevision> : public TestCaseBase<CtiLMEnergyExchangeOfferRevision>
{
    void Populate()
    {
        CtiLMEnergyExchangeOfferRevision &imsg = *_imsg;

        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._revisionnumber );
        GenerateRandom( imsg._actiondatetime );
        GenerateRandom( imsg._notificationdatetime );
        GenerateRandom( imsg._offerexpirationdatetime );
        GenerateRandom( imsg._additionalinfo );

        std::vector<CtiLMEnergyExchangeHourlyOffer*> _lmenergyexchangehourlyoffers;

        const int maxEnergyExchangeHourlyOffers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeHourlyOffers; item_nbr++ )
        {
            imsg._lmenergyexchangehourlyoffers.push_back( new CtiLMEnergyExchangeHourlyOffer );
            TestCaseItem<CtiLMEnergyExchangeHourlyOffer> tc_item;
            tc_item.Populate( imsg._lmenergyexchangehourlyoffers.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeOffer
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeOffer> : public TestCaseBase<CtiLMEnergyExchangeOffer>
{
    void Populate()
    {
        CtiLMEnergyExchangeOffer &imsg = *_imsg;

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._runstatus );
        GenerateRandom( imsg._offerdate );

        const int maxEnergyExchangeOfferRevisions = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeOfferRevisions; item_nbr++ )
        {
            imsg._lmenergyexchangeofferrevisions.push_back( new CtiLMEnergyExchangeOfferRevision );
            TestCaseItem<CtiLMEnergyExchangeOfferRevision> tc_item;
            tc_item.Populate( imsg._lmenergyexchangeofferrevisions.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeHourlyCustomer
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeHourlyCustomer> : public TestCaseBase<CtiLMEnergyExchangeHourlyCustomer>
{
    void Populate()
    {
        CtiLMEnergyExchangeHourlyCustomer  &imsg = *_imsg;

        GenerateRandom( imsg._customerid );
        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._revisionnumber );
        GenerateRandom( imsg._hour );
        GenerateRandom( imsg._amountcommitted );
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeCustomerReply
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeCustomerReply> : public TestCaseBase<CtiLMEnergyExchangeCustomerReply>
{
    void Populate()
    {
        CtiLMEnergyExchangeCustomerReply &imsg = *_imsg;

        GenerateRandom( imsg._customerid );
        GenerateRandom( imsg._offerid );
        GenerateRandom( imsg._acceptstatus );
        GenerateRandom( imsg._acceptdatetime );
        GenerateRandom( imsg._revisionnumber );
        GenerateRandom( imsg._ipaddressofacceptuser );
        GenerateRandom( imsg._useridname );
        GenerateRandom( imsg._nameofacceptperson );
        GenerateRandom( imsg._energyexchangenotes );

        const int maxEnergyExchangeHourlyCustomers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeHourlyCustomers; item_nbr++ )
        {
            imsg._lmenergyexchangehourlycustomers.push_back( new CtiLMEnergyExchangeHourlyCustomer );
            TestCaseItem<CtiLMEnergyExchangeHourlyCustomer> tc_item;
            tc_item.Populate( imsg._lmenergyexchangehourlycustomers.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMEnergyExchangeCustomer
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMEnergyExchangeCustomer> : public TestCase<CtiLMCICustomerBase>
{
    void Populate()
    {
        TestCase<CtiLMCICustomerBase>::Populate();

        CtiLMEnergyExchangeCustomer &imsg = static_cast<CtiLMEnergyExchangeCustomer&>(*_imsg);

        const int maxEnergyExchangeCustomerReplies = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeCustomerReplies ; item_nbr++ )
        {
            imsg._lmenergyexchangecustomerreplies.push_back( new CtiLMEnergyExchangeCustomerReply );
            TestCaseItem<CtiLMEnergyExchangeCustomerReply> tc_item;
            tc_item.Populate( imsg._lmenergyexchangecustomerreplies.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMProgramEnergyExchange
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMProgramEnergyExchange> : public TestCase<CtiLMProgramBase>
{
    void Populate()
    {
        TestCase<CtiLMProgramBase>::Populate();

        CtiLMProgramEnergyExchange &imsg = static_cast<CtiLMProgramEnergyExchange&>(*_imsg);

        GenerateRandom( imsg._minnotifytime );
        GenerateRandom( imsg._heading );
        GenerateRandom( imsg._messageheader );
        GenerateRandom( imsg._messagefooter );
        GenerateRandom( imsg._canceledmsg );
        GenerateRandom( imsg._stoppedearlymsg );

        const int maxEnergyExchangeOffers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeOffers; item_nbr++ )
        {
            imsg._lmenergyexchangeoffers.push_back( new CtiLMEnergyExchangeOffer );
            TestCaseItem<CtiLMEnergyExchangeOffer> tc_item;
            tc_item.Populate( imsg._lmenergyexchangeoffers.back() );
        }

        const int maxEnergyExchangeCustomers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxEnergyExchangeCustomers; item_nbr++ )
        {
            imsg._lmenergyexchangecustomers.push_back( new CtiLMEnergyExchangeCustomer );
            TestCaseItem<CtiLMEnergyExchangeCustomer> tc_item;
            tc_item.Populate( imsg._lmenergyexchangecustomers.back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMControlAreaItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlArea> : public TestCaseBase<CtiLMControlArea>
{
    void Populate()
    {
        CtiLMControlArea &imsg = *_imsg;

        const std::string paoTypeString_items[]  = { "mct-440-2131b",
                                                     "mct-440-2132b",
                                                     "mct-440-2133b" };

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._paocategory );
        GenerateRandom( imsg._paoclass );
        GenerateRandom( imsg._paoname );
        GenerateRandom( imsg._paoTypeString, paoTypeString_items );
        GenerateRandom( imsg._paodescription );
        imsg._disableflag = GenerateRandom<bool>();                  // generate a bool and assign to a BOOL
        GenerateRandom( imsg._defoperationalstate );
        GenerateRandom( imsg._controlinterval );
        GenerateRandom( imsg._minresponsetime );
        GenerateRandom( imsg._defdailystarttime );
        GenerateRandom( imsg._defdailystoptime );
        imsg._requirealltriggersactiveflag = GenerateRandom<bool>(); // generate a bool and assign to a BOOL
        GenerateRandom( imsg._nextchecktime );
        imsg._newpointdatareceivedflag = GenerateRandom<bool>();     // generate a bool and assign to a BOOL
        imsg._updatedflag = GenerateRandom<bool>();                  // generate a bool and assign to a BOOL
        GenerateRandom( imsg._controlareastatuspointid );
        GenerateRandom( imsg._controlareastate );
        GenerateRandom( imsg._currentpriority );
        GenerateRandom( imsg._currentdailystarttime );
        GenerateRandom( imsg._currentdailystoptime );

        const int maxControlAreaTriggers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxControlAreaTriggers; item_nbr++ )
        {
            imsg._lmcontrolareatriggers.push_back( new CtiLMControlAreaTrigger );
            TestCaseItem<CtiLMControlAreaTrigger> tc_item;
            tc_item.Populate( imsg._lmcontrolareatriggers.back() );
        }

        {
            imsg._lmprograms.push_back( CtiLMProgramBaseSPtr( new CtiLMProgramCurtailment ));
            TestCaseItem<CtiLMProgramCurtailment> tc_item;
            tc_item.Populate( static_cast<CtiLMProgramCurtailment*>( imsg._lmprograms.back().get() ));
        }

        {
            imsg._lmprograms.push_back( CtiLMProgramBaseSPtr( new CtiLMProgramDirect ));
            TestCaseItem<CtiLMProgramDirect> tc_item;
            tc_item.Populate( static_cast<CtiLMProgramDirect*>( imsg._lmprograms.back().get() ));
        }

        {
            imsg._lmprograms.push_back( CtiLMProgramBaseSPtr( new CtiLMProgramEnergyExchange ));
            TestCaseItem<CtiLMProgramEnergyExchange> tc_item;
            tc_item.Populate( static_cast<CtiLMProgramEnergyExchange*>( imsg._lmprograms.back().get() ));
        }
    }
};

/*-----------------------------------------------------------------------------
    LMControlAreas
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlAreaMsg> : public TestCase<CtiLMMessage>
{
    std::vector<CtiLMControlArea*> controlAreas;

    void Create()
    {
        _imsg.reset( new CtiLMControlAreaMsg( controlAreas ));
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMControlAreaMsg &imsg = dynamic_cast<CtiLMControlAreaMsg&>(*_imsg);

        GenerateRandom( imsg._msgInfoBitMask );

        const int maxControlAreas = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxControlAreas; item_nbr++ )
        {
            imsg._controlAreas->push_back( new CtiLMControlArea );
            TestCaseItem<CtiLMControlArea> tc_item;
            tc_item.Populate( imsg._controlAreas->back() );
        }
    }
};

/*-----------------------------------------------------------------------------
    LMCurtailmentAcknowledge
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMCurtailmentAcknowledgeMsg> : public TestCase<CtiLMMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMCurtailmentAcknowledgeMsg );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMCurtailmentAcknowledgeMsg &imsg = dynamic_cast<CtiLMCurtailmentAcknowledgeMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._curtailreferenceid );
        GenerateRandom( imsg._acknowledgestatus );
        GenerateRandom( imsg._ipaddressofackuser );
        GenerateRandom( imsg._useridname );
        GenerateRandom( imsg._nameofackperson );
        GenerateRandom( imsg._curtailmentnotes );
    }

};

/*-----------------------------------------------------------------------------
    LMDynamicGroupData
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMDynamicGroupDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMDynamicGroupDataMsg );
    }

    void Populate()
    {
        // TestCase<CtiMessage>::Populate(); parent message is not serialized

        CtiLMDynamicGroupDataMsg &imsg = dynamic_cast<CtiLMDynamicGroupDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        imsg._disableflag = GenerateRandom<bool>();
        GenerateRandom( imsg._groupcontrolstate );
        GenerateRandom( imsg._currenthoursdaily );
        GenerateRandom( imsg._currenthoursmonthly );
        GenerateRandom( imsg._currenthoursseasonal );
        GenerateRandom( imsg._currenthoursannually );
        GenerateRandom( imsg._lastcontrolsent );
        GenerateRandom( imsg._controlstarttime );
        GenerateRandom( imsg._controlcompletetime );
        GenerateRandom( imsg._next_control_time );
        GenerateRandom( imsg._internalState );
        GenerateRandom( imsg._daily_ops );
    }
};

/*-----------------------------------------------------------------------------
    LMDynamicProgramData
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMDynamicProgramDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMDynamicProgramDataMsg );
    }

    void Populate()
    {
        // TestCase<CtiMessage>::Populate(); parent message is not serialized

        CtiLMDynamicProgramDataMsg &imsg = dynamic_cast<CtiLMDynamicProgramDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        imsg._disableflag = GenerateRandom<bool>(); // generate a bool and assign to a BOOL
        GenerateRandom( imsg._currentgearnumber );
        GenerateRandom( imsg._lastgroupcontrolled );
        GenerateRandom( imsg._programstate );
        GenerateRandom( imsg._reductiontotal );
        GenerateRandom( imsg._directstarttime );
        GenerateRandom( imsg._directstoptime );
        GenerateRandom( imsg._notify_active_time );
        GenerateRandom( imsg._notify_inactive_time );
        GenerateRandom( imsg._startedrampingouttime );
        GenerateRandom( imsg._origin );
    }
};

/*-----------------------------------------------------------------------------
    LMDynamicTriggerData
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMDynamicTriggerDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMDynamicTriggerDataMsg );
    }

    void Populate()
    {
        // TestCase<CtiMessage>::Populate(); parent message is not serialized

        CtiLMDynamicTriggerDataMsg &imsg = dynamic_cast<CtiLMDynamicTriggerDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._triggernumber );
        GenerateRandom( imsg._pointvalue );
        GenerateRandom( imsg._lastpointvaluetimestamp );
        GenerateRandom( imsg._normalstate );
        GenerateRandom( imsg._threshold );
        GenerateRandom( imsg._peakpointvalue );
        GenerateRandom( imsg._lastpeakpointvaluetimestamp );
        GenerateRandom( imsg._projectedpointvalue );
    }
};


/*-----------------------------------------------------------------------------
    LMDynamicControlAreaData
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMDynamicControlAreaDataMsg> : public TestCase<CtiMessage>
{
    void Create()
    {
        _imsg.reset( new CtiLMDynamicControlAreaDataMsg );
    }

    void Populate()
    {
        // TestCase<CtiMessage>::Populate(); parent message is not serialized

        CtiLMDynamicControlAreaDataMsg &imsg = dynamic_cast<CtiLMDynamicControlAreaDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        imsg._disableflag = GenerateRandom<bool>(); // generate a bool and assign to a BOOL
        GenerateRandom( imsg._nextchecktime );
        GenerateRandom( imsg._controlareastate );
        GenerateRandom( imsg._currentpriority );
        GenerateRandom( imsg._currentdailystarttime );
        GenerateRandom( imsg._currentdailystoptime );

        const int maxTriggers = GenerateRandom<int>( 2, 5 );

        for( int item_nbr=0; item_nbr < maxTriggers; item_nbr++ )
        {
            imsg._triggers.push_back( CtiLMDynamicTriggerDataMsg() );
            TestCaseItem<CtiLMDynamicTriggerDataMsg> tc_item;
            tc_item.Populate( &imsg._triggers.back() );
        }
    }
};
