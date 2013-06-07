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

        GenerateRandom( imsg._errorCode );
        GenerateRandom( imsg._doubleParams );
        GenerateRandom( imsg._integerParams );
        GenerateRandom( imsg._datetimeParams );
    }
};

/*-----------------------------------------------------------------------------
    LMManualControlResponse
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMManualControlResponse> : public TestCase<CtiLMMessage>
{
    enum
    {
        CONSTRAINT_VIOLATION_NBR = 3
    };

    void Create()
    {
        _imsg.reset( new CtiLMManualControlResponse );
    }

    void Populate()
    {
        TestCase<CtiLMMessage>::Populate();

        CtiLMManualControlResponse &imsg = dynamic_cast<CtiLMManualControlResponse&>(*_imsg);

        GenerateRandom( imsg._paoid );

        for(int i = 0; i < CONSTRAINT_VIOLATION_NBR; i++)
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

        for( int i =0 ; i < HOURS_IN_DAY; i++)
        {
            double val;
            imsg._amountsrequested[i] = GenerateRandom( val );
        }

        for( int i =0 ; i < HOURS_IN_DAY; i++)
        {
            long val;
            imsg._pricesoffered[i] = GenerateRandom( val );
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

        for( int i =0 ; i < HOURS_IN_DAY; i++)
        {
            double val;
            imsg._amountscommitted[i] = GenerateRandom( val );
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

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._triggernumber );
        GenerateRandom( imsg._triggertype );
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
    LMControlAreaItem
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlArea> : public TestCaseBase<CtiLMControlArea>
{
    enum
    {
        CONTROL_AREA_TRIGGER_NBR = 3
    };

    void Populate()
    {
        CtiLMControlArea &imsg = *_imsg;

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._paocategory );
        GenerateRandom( imsg._paoclass );
        GenerateRandom( imsg._paoname );
        GenerateRandom( imsg._paoType );
        GenerateRandom( imsg._paoTypeString );
        GenerateRandom( imsg._paodescription );
        GenerateRandom( imsg._disableflag );
        GenerateRandom( imsg._defoperationalstate );
        GenerateRandom( imsg._controlinterval );
        GenerateRandom( imsg._minresponsetime );
        GenerateRandom( imsg._defdailystarttime );
        GenerateRandom( imsg._defdailystoptime );
        GenerateRandom( imsg._requirealltriggersactiveflag );
        GenerateRandom( imsg._nextchecktime );
        GenerateRandom( imsg._newpointdatareceivedflag );
        GenerateRandom( imsg._updatedflag );
        GenerateRandom( imsg._controlareastatuspointid );
        GenerateRandom( imsg._controlareastate );
        GenerateRandom( imsg._currentpriority );
        GenerateRandom( imsg._currentdailystarttime );
        GenerateRandom( imsg._currentdailystoptime );

        for( int i =0 ; i < CONTROL_AREA_TRIGGER_NBR; i++)
        {
            imsg._lmcontrolareatriggers.push_back( new CtiLMControlAreaTrigger );
            TestCaseItem<CtiLMControlAreaTrigger> tc_item;
            tc_item.Populate( imsg._lmcontrolareatriggers.back() );
        }

        // TODO:
        // std::vector<CtiLMProgramBaseSPtr> _lmprograms;
    }
};

/*-----------------------------------------------------------------------------
    LMControlAreas
-----------------------------------------------------------------------------*/
template<>
struct TestCase<CtiLMControlAreaMsg> : public TestCase<CtiLMMessage>
{
    enum
    {
        CONTROL_AREA_NBR = 3
    };

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

        for( int i=0 ; i < CONTROL_AREA_NBR; i++)
        {
            imsg._controlAreas->push_back( new CtiLMControlArea );
            TestCaseItem<CtiLMControlArea> tc_item;
            tc_item.Populate( *(imsg._controlAreas->end()));
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
        CtiLMDynamicGroupDataMsg &imsg = dynamic_cast<CtiLMDynamicGroupDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._disableflag );
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
        CtiLMDynamicProgramDataMsg &imsg = dynamic_cast<CtiLMDynamicProgramDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._disableflag );
        GenerateRandom( imsg._currentgearnumber );
        GenerateRandom( imsg._lastgroupcontrolled );
        GenerateRandom( imsg._programstate );
        GenerateRandom( imsg._reductiontotal );
        GenerateRandom( imsg._directstarttime );
        GenerateRandom( imsg._directstoptime );
        GenerateRandom( imsg._notify_active_time );
        GenerateRandom( imsg._notify_inactive_time );
        GenerateRandom( imsg._startedrampingouttime );
    }
};

/*-----------------------------------------------------------------------------
    LMDynamicTriggerDataMsg
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
    enum
    {
        DYNAMIC_TRIGGER_DATA_NBR = 3
    };

    void Create()
    {
        _imsg.reset( new CtiLMDynamicControlAreaDataMsg );
    }

    void Populate()
    {
        CtiLMDynamicControlAreaDataMsg &imsg = dynamic_cast<CtiLMDynamicControlAreaDataMsg&>(*_imsg);

        GenerateRandom( imsg._paoid );
        GenerateRandom( imsg._disableflag );
        GenerateRandom( imsg._nextchecktime );
        GenerateRandom( imsg._controlareastate );
        GenerateRandom( imsg._currentpriority );
        GenerateRandom( imsg._currentdailystarttime );
        GenerateRandom( imsg._currentdailystoptime );

        for(int i= 0; i < DYNAMIC_TRIGGER_DATA_NBR; i++)
        {
            imsg._triggers.push_back( CtiLMDynamicTriggerDataMsg() );
            TestCaseItem<CtiLMDynamicTriggerDataMsg> tc_item;
            tc_item.Populate( &imsg._triggers.back() );
        }
    }
};
