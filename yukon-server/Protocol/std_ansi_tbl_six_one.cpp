/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_one
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "math.h"
#include "std_ansi_tbl_six_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableSixOne::CtiAnsiTableSixOne( unsigned char *stdTblsUsed, int dimStdTblsUsed )
{
    int x = 0;
    int lpTbl[] = {64, 65, 66, 67};

    _lpDataSetUsed[0] = false;
    _lpDataSetUsed[1] = false;
    _lpDataSetUsed[2] = false;
    _lpDataSetUsed[3] = false;

    if (dimStdTblsUsed > 8)
    {
        int y, yy;
        while (x < 4)
        {
            y =   1;
            for (yy = 0; yy < lpTbl[x]%8; yy++)
            {
                y = y*2;
            }
            if (stdTblsUsed[(lpTbl[x]/8)] & y)
            {
                _lpDataSetUsed[x] = true;
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << "       *******  lpDataSetUsed[ "<<x<<" ]"<<endl;
                }
            }
            else
                _lpDataSetUsed[x] = false;
            x++;
        }
    }
    _lp_tbl.lp_data_set_info = new LP_DATA_SET[4];
}


CtiAnsiTableSixOne::CtiAnsiTableSixOne( BYTE *dataBlob,  unsigned char *stdTblsUsed, int dimStdTblsUsed )
{
    int x = 0;
    int offset = 0;
    int lpTbl[] = {64, 65, 66, 67};

    _lpDataSetUsed[0] = false;
    _lpDataSetUsed[1] = false;
    _lpDataSetUsed[2] = false;
    _lpDataSetUsed[3] = false;

    if (dimStdTblsUsed > 8)
    {
        x=0;
        int y, yy;
        while (x < 4)
        {
            y =   1;
            for (yy = 0; yy < lpTbl[x]%8; yy++)
            {
                y = y*2;
            }
            if (stdTblsUsed[(lpTbl[x]/8)] & y)
            {
                _lpDataSetUsed[x] = true;
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << "       *******  lpDataSetUsed[ "<<x<<" ]"<<endl;
                }
            }
            else
                _lpDataSetUsed[x] = false;
            x++;
        }
    }

    memcpy( (void *)&_lp_tbl, dataBlob, sizeof( unsigned char ) * 7);
    dataBlob +=   sizeof( unsigned char ) * 7;
    _lp_tbl.lp_data_set_info = new LP_DATA_SET[4];
    int xx = 0;
    for (x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            memcpy( (void *)&_lp_tbl.lp_data_set_info[xx], dataBlob, sizeof( LP_DATA_SET ));
            dataBlob +=   sizeof( LP_DATA_SET );
            xx++;
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableSixOne::~CtiAnsiTableSixOne()
{
   //delete clock_table;
   delete []_lp_tbl.lp_data_set_info;
    _lp_tbl.lp_data_set_info = NULL;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableSixOne& CtiAnsiTableSixOne::operator=(const CtiAnsiTableSixOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixOne::generateResultPiece( BYTE **dataBlob )
{
    int xx = 0;

    memcpy( *dataBlob, (void *)&_lp_tbl, sizeof( unsigned char ) * 7);
    *dataBlob +=   sizeof( unsigned char ) * 7;
    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            memcpy( *dataBlob, (void *)&_lp_tbl.lp_data_set_info[xx], sizeof( LP_DATA_SET ));
            *dataBlob +=   sizeof( LP_DATA_SET );
            xx++;
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixOne::decodeResultPiece( BYTE **dataBlob )
{
    int xx = 0;

    memcpy( (void *)&_lp_tbl, *dataBlob, sizeof( unsigned char ) * 7);
    *dataBlob +=   sizeof( unsigned char ) * 7;
    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            memcpy( (void *)&_lp_tbl.lp_data_set_info[xx], *dataBlob, sizeof( LP_DATA_SET ));
            *dataBlob +=   sizeof( LP_DATA_SET );
            xx++;
        }
    }

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixOne::printResult(  )
{

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 61  ========================" << endl;
        dout << " ** Actual Load Profile Table ** "<<endl;
        dout << "           LP Memory Length        "<<_lp_tbl.lp_memory_len<<endl;
        dout << "           LP Flags:      "<<endl;
        dout << "                   lp set1 inhibit ovf flags "<<(bool)_lp_tbl.lp_flags.lp_set1_inhibit_ovf_flag<<endl;
        dout << "                   lp set2 inhibit ovf flags "<<(bool)_lp_tbl.lp_flags.lp_set2_inhibit_ovf_flag<<endl;
        dout << "                   lp set3 inhibit ovf flags "<<(bool)_lp_tbl.lp_flags.lp_set3_inhibit_ovf_flag<<endl;
        dout << "                   lp set4 inhibit ovf flags "<<(bool)_lp_tbl.lp_flags.lp_set4_inhibit_ovf_flag<<endl;
        dout << "                   blk_end_read_flag         "<<(bool)_lp_tbl.lp_flags.blk_end_read_flag<<endl;
        dout << "                   blk_end_pulse_flag        "<<(bool)_lp_tbl.lp_flags.blk_end_pulse_flag<<endl;
        dout << "                   scalar_divisor_flag_set1  "<<(bool)_lp_tbl.lp_flags.scalar_divisor_flag_set1<<endl;
        dout << "                   scalar_divisor_flag_set2  "<<(bool)_lp_tbl.lp_flags.scalar_divisor_flag_set2<<endl;
        dout << "                   scalar_divisor_flag_set3  "<<(bool)_lp_tbl.lp_flags.scalar_divisor_flag_set3<<endl;
        dout << "                   scalar_divisor_flag_set4  "<<(bool)_lp_tbl.lp_flags.scalar_divisor_flag_set4<<endl;
        dout << "                   extended_int_status_flag  "<<(bool)_lp_tbl.lp_flags.extended_int_status_flag<<endl;
        dout << "                   simple_int_status_flag    "<<(bool)_lp_tbl.lp_flags.simple_int_status_flag<<endl;
        dout << "                   closure_status_flag       "<<(bool)_lp_tbl.lp_flags.closure_status_flag<<endl;
        dout << "           LP Formats:      "<<endl;
        dout << "                   inv_uint8_flag       "<<(bool)_lp_tbl.lp_fmats.inv_uint8_flag<<endl;
        dout << "                   inv_uint16_flag      "<<(bool)_lp_tbl.lp_fmats.inv_uint16_flag<<endl;
        dout << "                   inv_uint32_flag      "<<(bool)_lp_tbl.lp_fmats.inv_uint32_flag<<endl;
        dout << "                   inv_int8_flag        "<<(bool)_lp_tbl.lp_fmats.inv_int8_flag<<endl;
        dout << "                   inv_int16_flag       "<<(bool)_lp_tbl.lp_fmats.inv_int16_flag<<endl;
        dout << "                   inv_int32_flag       "<<(bool)_lp_tbl.lp_fmats.inv_int32_flag<<endl;
        dout << "                   inv_ni_fmat1_flag    "<<(bool)_lp_tbl.lp_fmats.inv_ni_fmat1_flag<<endl;
        dout << "                   inv_ni_fmat2_flag    "<<(bool)_lp_tbl.lp_fmats.inv_ni_fmat2_flag<<endl;
    }
    int offset = 0;
    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            printLPDataSetInfo(x,offset);
            offset += 1;
        }
    }
}
void CtiAnsiTableSixOne::printLPDataSetInfo(int set, int offset )
{
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "           LP SET "<<set+1<<" : "<<endl;
        dout << "                   nbr blocks set             "<<_lp_tbl.lp_data_set_info[offset].nbr_blks_set<<endl;
        dout << "                   nbr block intervals set    "<<_lp_tbl.lp_data_set_info[offset].nbr_blk_ints_set<<endl;
        dout << "                   nbr channels set           "<<_lp_tbl.lp_data_set_info[offset].nbr_chns_set<<endl;
        dout << "                   max interval time set      "<<_lp_tbl.lp_data_set_info[offset].max_int_time_set<<endl;
    }

}

UINT32 CtiAnsiTableSixOne::getLPMemoryLength()
{
    return _lp_tbl.lp_memory_len;
}
LP_DATA_SET * CtiAnsiTableSixOne::getLPDataSetInfo()
{
    return _lp_tbl.lp_data_set_info;
}

bool * CtiAnsiTableSixOne::getLPDataSetUsedFlags()
{
    return _lpDataSetUsed;
}

bool CtiAnsiTableSixOne::getLPScalarDivisorFlag( int setNo )
{
    bool retVal = false;
    switch (setNo)
    {
        case 1:
            retVal = (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set1;
            break;
        case 2:
            retVal = (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set2;
            break;
        case 3:
            retVal = (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set3;
            break;
        case 4:
            retVal = (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set4;
            break;
        default:
            break;
    }
    return retVal;
}

int CtiAnsiTableSixOne::getNbrBlksSet (int setNbr )
{
    int retVal = 0;
    int offset[4] = {0xff, 0xff, 0xff, 0xff};
    int setIndex = 0;

    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            offset[x] = setIndex;
            setIndex++;
        }
    }

    if (_lpDataSetUsed[setNbr-1])
    {
        retVal = _lp_tbl.lp_data_set_info[offset[setNbr-1]].nbr_blks_set;
    }
    return retVal;

}
int CtiAnsiTableSixOne::getNbrChansSet(int setNbr)
{
    int retVal = 0;
    int offset[4] = {0xff, 0xff, 0xff, 0xff};
    int setIndex = 0;

    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            offset[x] = setIndex;
            setIndex++;
        }
    }

    if (_lpDataSetUsed[setNbr-1])
    {
        retVal =_lp_tbl.lp_data_set_info[offset[setNbr-1]].nbr_chns_set;
    }
    return retVal;
}
int CtiAnsiTableSixOne::getNbrBlkIntsSet( int setNbr)
{
    int retVal = 0;
    int offset[4] = {0xff, 0xff, 0xff, 0xff};
    int setIndex = 0;

    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            offset[x] = setIndex;
            setIndex++;
        }
    }

    if (_lpDataSetUsed[setNbr-1])
    {
        retVal = _lp_tbl.lp_data_set_info[offset[setNbr-1]].nbr_blk_ints_set;
    }
    return retVal;
}
int CtiAnsiTableSixOne::getMaxIntTimeSet( int setNbr)
{
    int retVal = 0;
    int offset[4] = {0xff, 0xff, 0xff, 0xff};
    int setIndex = 0;

    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            offset[x] = setIndex;
            setIndex++;
        }
    }

    if (_lpDataSetUsed[setNbr-1])
    {
        retVal = _lp_tbl.lp_data_set_info[offset[setNbr-1]].max_int_time_set;
    }
    return retVal;
}
bool CtiAnsiTableSixOne::getClosureStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.closure_status_flag;
}
bool CtiAnsiTableSixOne::getSimpleIntStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.simple_int_status_flag;
}
bool CtiAnsiTableSixOne::getBlkEndReadFlag()
{
    return (bool)_lp_tbl.lp_flags.blk_end_read_flag;
}
bool CtiAnsiTableSixOne::getBlkEndPulseFlag()
{
    return (bool)_lp_tbl.lp_flags.blk_end_pulse_flag;
}
bool CtiAnsiTableSixOne::getExtendedIntStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.extended_int_status_flag;
}



