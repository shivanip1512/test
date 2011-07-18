
#include "precompiled.h"
#include "guard.h"
#include "logger.h"
#include "prot_ansi_kv2.h"

using std::endl;

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

    setCurrentAnsiWantsTableValues(Cti::Protocols::Ansi::ProcedureInitiate,0,1,ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);
    getApplicationLayer().initializeTableRequest (Cti::Protocols::Ansi::ProcedureInitiate, 0, 1, ANSI_TABLE_TYPE_STANDARD, ANSI_OPERATION_WRITE);

    Cti::Protocols::Ansi::REQ_DATA_RCD reqData;
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



