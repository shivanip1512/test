/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi_kv2
*
* Date:   2/7/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_ansi_kv2.cpp-arc  $
* REVISION     :  $Revision: 1.10.2.1 $
* DATE         :  $Date: 2008/11/17 23:06:31 $
*    History:
      $Log: prot_ansi_kv2.cpp,v $
      Revision 1.10.2.1  2008/11/17 23:06:31  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
      **************************************************************************************************************
      Removed "CTITYPES.H" from every file in the project, so far there were no
      known side-effects or even compile errors, however, they could still happen.

      Also, made many other changes for compiling.

      The project now apparently compiles until reching the database
      subdirectory, however, I have seen cases where there is apparent
      regressing and need to re-work things.

      However, enough changes have happened, that I felt it was good to
      committ.
      **************************************************************************************************************
      Possibly other misc. changes since last commit.
      *******************************************************
 
      Revision 1.10  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.9  2005/12/20 17:19:55  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.7  2005/02/17 19:02:58  mfisher
      Removed space before CVS comment header, moved #include "yukon.h" after CVS header

      Revision 1.6  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.4  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.3  2004/12/10 21:58:40  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.2  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.1  2003/04/25 15:13:05  dsutton
      Manufacturer additions to the base ansi protocol for the kv2


*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "guard.h"
#include "logger.h"
#include "prot_ansi_kv2.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI_kv2::CtiProtocolANSI_kv2( void )
: CtiProtocolANSI()
{
    _table000=NULL;
    _table070=NULL;
    _table110=NULL;
}

CtiProtocolANSI_kv2::~CtiProtocolANSI_kv2( void )
{
    CtiProtocolANSI_kv2::destroyManufacturerTables();   // virtual function call: specify which one we are calling
}


void CtiProtocolANSI_kv2::destroyManufacturerTables( void )
{
   if( _table000 != NULL )
   {
      delete _table000;
      _table000 = NULL;
   }

   if( _table070 != NULL )
   {
      delete _table070;
      _table070 = NULL;
   }
   if( _table110 != NULL )
   {
      delete _table110;
      _table110 = NULL;
   }


}

void CtiProtocolANSI_kv2::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
{

    switch( aTableID - 0x0800)
    {
        case 0:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << CtiTime() << " Creating KV2 table 0" << endl;
                }
                _table000 = new CtiAnsiKV2ManufacturerTable000( data );
                _table000->printResult();
                break;
            }
        case 70:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << CtiTime() << " Creating KV2 table 70" << endl;
                }

                _table070 = new CtiAnsiKV2ManufacturerTable070( data );
                _table070->printResult();
                break;
            }
        case 110:
            {
                {
                   CtiLockGuard<CtiLogger> doubt_guard(dout);
                   dout << CtiTime() << " Creating KV2 table 110" << endl;
                }

                _table110 = new CtiAnsiKV2ManufacturerTable110( data );
                _table110->printResult();
                break;
            }
        default:
            break;

    }
}



void CtiProtocolANSI_kv2::setAnsiDeviceType()
{
    getApplicationLayer().setAnsiDeviceType(CtiANSIApplication::kv2);
    return;
}

bool CtiProtocolANSI_kv2::snapshotData()
{
    //setWriteProcedureInProgress(true);

    setCurrentAnsiWantsTableValues(Ansi::ProcedureInitiate,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (Ansi::ProcedureInitiate, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);

    REQ_DATA_RCD reqData;
    reqData.proc.tbl_proc_nbr = 84;
    reqData.proc.std_vs_mfg_flag = 1;
    reqData.proc.selector = 0;


    getApplicationLayer().setProcBfld( reqData.proc );

    reqData.seq_nbr = getWriteSequenceNbr();
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


    getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) );

    return true;

}

bool CtiProtocolANSI_kv2::retreiveMfgPresentValue( int offset, double *value )
{
    bool retVal = false;
    switch(offset)
    {
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        {
            *value = (_table110->getVlnFundOnly()[0])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
        {
            *value = (_table110->getVlnFundOnly()[1])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
        {
            *value = (_table110->getVlnFundOnly()[2])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
        {
            *value = (_table110->getCurrFundOnly()[0])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
        {
            *value = (_table110->getCurrFundOnly()[1])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
        {
            *value = (_table110->getCurrFundOnly()[2])/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
        {
            *value = (_table110->getImputedNeutralCurr())/(float)10; //raw voltage quantities need to be scaled by factor of 1/10
            retVal = true;
            break;
        }
        default:
        {
            value = NULL;
            break;
        }
    }
    return retVal;
}



