#pragma once

#include "ansi_focus_mtable_004.h"
#include "ansi_focus_mtable_013.h"
#include "ansi_focus_mtable_024.h"
#include "prot_ansi.h"

typedef enum
{
     DeliveredKWH = 0,
     ReceivedKWH,
     NetKWH,
     AddedKWH,
     PowerFactor,
     VhPhaseA = 5,
     VhPhaseB,
     VhPhaseC,
     IhPhaseA,
     IhPhaseB,
     IhPhaseC = 10,
     SagVPhaseA,
     SagVPhaseB,
     SagVPhaseC,
     SagAnyPhase,
     SwellVPhaseA = 15,
     SwellVPhaseB,
     SwellVPhaseC,
     SwellAnyPhase,
     TemperatureDelta,
     TemperatureDegreesC = 20,
     Frequency,
     PowerOutage,
     NbrOfDemandResets,
     NbrOfTimesProgrammed,
     BatteryCarryOverTimeDays = 25,
     DeliveredKVARH,
     ReceivedKVARH,
     NetKVARH,
     AddedKVARH,
     DeliveredKVAH = 30,
     ReceivedKVAH,
     NetKVAH,
     AddedKVAH
} FocusAxUnitOfMeasureIndex;


class IM_EX_PROT  CtiProtocolANSI_focus: public Cti::Protocols::Ansi::CtiProtocolANSI
{
    typedef Cti::Protocols::Ansi::CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_focus();
        virtual ~CtiProtocolANSI_focus();

        virtual void setAnsiDeviceType();
        virtual int calculateLPDataBlockStartIndex(ULONG lastLPTime);
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID );
        virtual void updateMfgBytesExpected();
        virtual bool retrieveMfgPresentValue( int offset, double *value );
        virtual float getMfgConstants( );
        virtual bool doesSegmentationMatch(int index, Cti::Protocols::Ansi::AnsiSegmentation segmentation);

        bool retrieveFocusKwPresentValue( int offset, double *value );
        bool retrieveFocusAXPresentValue( int offset, double *value );

   private:

        CtiAnsiFocusMfgTable04  *_table04;
        CtiAnsiFocusMfgTable13  *_table13;
        CtiAnsiFocusMfgTable24  *_table24;


};


