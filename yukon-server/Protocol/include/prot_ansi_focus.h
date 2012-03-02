#pragma once

#include "ansi_focus_mtable_004.h"
#include "ansi_focus_mtable_013.h"
#include "prot_ansi.h"

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
        virtual bool retreiveMfgPresentValue( int offset, double *value );

        bool retreiveFocusKwPresentValue( int offset, double *value );
        bool retreiveFocusAXPresentValue( int offset, double *value );

   private:

        CtiAnsiFocusMfgTable04  *_table04;
        CtiAnsiFocusMfgTable13  *_table13; 


};


