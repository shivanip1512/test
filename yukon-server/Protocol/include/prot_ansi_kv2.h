#pragma once

#include "prot_ansi.h"
#include "pointdefs.h"
#include "ansi_kv2_mtable_000.h"
#include "ansi_kv2_mtable_070.h"
#include "ansi_kv2_mtable_110.h"


class IM_EX_PROT CtiProtocolANSI_kv2 : public Cti::Protocols::Ansi::CtiProtocolANSI
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiProtocolANSI_kv2(const CtiProtocolANSI_kv2&);
    CtiProtocolANSI_kv2& operator=(const CtiProtocolANSI_kv2&);

    typedef Cti::Protocols::Ansi::CtiProtocolANSI Inherited;

   public:

        CtiProtocolANSI_kv2();
        virtual ~CtiProtocolANSI_kv2();

        virtual void destroyManufacturerTables( void );
        virtual void convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID );
        virtual void updateMfgBytesExpected();

        virtual void setAnsiDeviceType();
        virtual bool snapshotData();
        virtual bool retrieveMfgPresentValue( int offset, double *value );



   private:

      CtiAnsiKV2ManufacturerTable000  *_table000;
      CtiAnsiKV2ManufacturerTable070  *_table070;
      CtiAnsiKV2ManufacturerTable110  *_table110;


};


