/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_61
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "math.h"
#include "std_ansi_tbl_61.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable61::CtiAnsiTable61( BYTE *dataBlob,  unsigned char *stdTblsUsed, int dimStdTblsUsed, DataOrder dataOrder )
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
            }
            else
                _lpDataSetUsed[x] = false;
            x++;
        }
    }
    double tempResult;
    offset = toDoubleParser( dataBlob, tempResult, ANSI_NI_FORMAT_INT32, dataOrder );
    _lp_tbl.lp_memory_len = tempResult;
    dataBlob += offset;

    dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_flags, sizeof( unsigned char )*2, dataOrder); //2 bytes
    dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_fmats, sizeof( unsigned char )); // 1 byte
    
    _lp_tbl.lp_data_set_info = new LP_DATA_SET[4];
    int xx = 0;
    for (x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {

            dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_data_set_info[xx].nbr_blks_set, sizeof(short), dataOrder); //2bytes
            dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_data_set_info[xx].nbr_blk_ints_set, sizeof(short), dataOrder );//2bytes
            dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_data_set_info[xx].nbr_chns_set, sizeof( UINT8 )); // 1byte
            dataBlob += toAnsiIntParser(dataBlob, &_lp_tbl.lp_data_set_info[xx].max_int_time_set, sizeof( UINT8 )); //1byte
            xx++;
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable61::~CtiAnsiTable61()
{
   //delete clock_table;
   if (_lp_tbl.lp_data_set_info != NULL)
   {
       delete []_lp_tbl.lp_data_set_info;
       _lp_tbl.lp_data_set_info = NULL;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable61& CtiAnsiTable61::operator=(const CtiAnsiTable61& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable61::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("LP Memory Length")          << _lp_tbl.lp_memory_len;

    itemList <<"LP Flags";
    itemList.add("lp set1 inhibit ovf flags") << (bool)_lp_tbl.lp_flags.lp_set1_inhibit_ovf_flag;
    itemList.add("lp set2 inhibit ovf flags") << (bool)_lp_tbl.lp_flags.lp_set2_inhibit_ovf_flag;
    itemList.add("lp set3 inhibit ovf flags") << (bool)_lp_tbl.lp_flags.lp_set3_inhibit_ovf_flag;
    itemList.add("lp set4 inhibit ovf flags") << (bool)_lp_tbl.lp_flags.lp_set4_inhibit_ovf_flag;
    itemList.add("blk_end_read_flag")         << (bool)_lp_tbl.lp_flags.blk_end_read_flag;
    itemList.add("blk_end_pulse_flag")        << (bool)_lp_tbl.lp_flags.blk_end_pulse_flag;
    itemList.add("scalar_divisor_flag_set1")  << (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set1;
    itemList.add("scalar_divisor_flag_set2")  << (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set2;
    itemList.add("scalar_divisor_flag_set3")  << (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set3;
    itemList.add("scalar_divisor_flag_set4")  << (bool)_lp_tbl.lp_flags.scalar_divisor_flag_set4;
    itemList.add("extended_int_status_flag")  << (bool)_lp_tbl.lp_flags.extended_int_status_flag;
    itemList.add("simple_int_status_flag")    << (bool)_lp_tbl.lp_flags.simple_int_status_flag;
    itemList.add("closure_status_flag")       << (bool)_lp_tbl.lp_flags.closure_status_flag;

    itemList <<"LP Formats";
    itemList.add("inv_uint8_flag")            << (bool)_lp_tbl.lp_fmats.inv_uint8_flag;
    itemList.add("inv_uint16_flag")           << (bool)_lp_tbl.lp_fmats.inv_uint16_flag;
    itemList.add("inv_uint32_flag")           << (bool)_lp_tbl.lp_fmats.inv_uint32_flag;
    itemList.add("inv_int8_flag")             << (bool)_lp_tbl.lp_fmats.inv_int8_flag;
    itemList.add("inv_int16_flag")            << (bool)_lp_tbl.lp_fmats.inv_int16_flag;
    itemList.add("inv_int32_flag")            << (bool)_lp_tbl.lp_fmats.inv_int32_flag;
    itemList.add("inv_ni_fmat1_flag")         << (bool)_lp_tbl.lp_fmats.inv_ni_fmat1_flag;
    itemList.add("inv_ni_fmat2_flag")         << (bool)_lp_tbl.lp_fmats.inv_ni_fmat2_flag;

    int offset = 0;
    for (int x = 0; x < 4; x++)
    {
        if (_lpDataSetUsed[x])
        {
            appendLPDataSetInfo(x, offset, itemList);
            offset += 1;
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 61") <<
            endl <<"** Actual Load Profile Table **"<<
            itemList
            );
}

void CtiAnsiTable61::appendLPDataSetInfo(int set, int offset, Cti::FormattedList &itemList)
{
    itemList <<"LP SET "<< set+1;
    itemList.add("nbr blocks set")          << _lp_tbl.lp_data_set_info[offset].nbr_blks_set;
    itemList.add("nbr block intervals set") << _lp_tbl.lp_data_set_info[offset].nbr_blk_ints_set;
    itemList.add("nbr channels set")        << _lp_tbl.lp_data_set_info[offset].nbr_chns_set;
    itemList.add("max interval time set")   << _lp_tbl.lp_data_set_info[offset].max_int_time_set;
}

UINT32 CtiAnsiTable61::getLPMemoryLength()
{
    return _lp_tbl.lp_memory_len;
}
LP_DATA_SET * CtiAnsiTable61::getLPDataSetInfo()
{
    return _lp_tbl.lp_data_set_info;
}

bool * CtiAnsiTable61::getLPDataSetUsedFlags()
{
    return _lpDataSetUsed;
}

bool CtiAnsiTable61::getLPScalarDivisorFlag( int setNo )
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

int CtiAnsiTable61::getNbrBlksSet (int setNbr )
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
int CtiAnsiTable61::getNbrChansSet(int setNbr)
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
int CtiAnsiTable61::getNbrBlkIntsSet( int setNbr)
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
int CtiAnsiTable61::getMaxIntTimeSet( int setNbr)
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
bool CtiAnsiTable61::getClosureStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.closure_status_flag;
}
bool CtiAnsiTable61::getSimpleIntStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.simple_int_status_flag;
}
bool CtiAnsiTable61::getBlkEndReadFlag()
{
    return (bool)_lp_tbl.lp_flags.blk_end_read_flag;
}
bool CtiAnsiTable61::getBlkEndPulseFlag()
{
    return (bool)_lp_tbl.lp_flags.blk_end_pulse_flag;
}
bool CtiAnsiTable61::getExtendedIntStatusFlag()
{
    return (bool)_lp_tbl.lp_flags.extended_int_status_flag;
}



