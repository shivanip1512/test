/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_63
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_63.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable63::CtiAnsiTable63( BYTE *dataBlob, bool *dataSetUsedFlag, DataOrder dataOrder )
{
    int x;
    int cnt = 0;
    int index = 0;

    for (x = 0; x < 4; x++)
    {
        _lpCtrlDataSetUsed[x] = dataSetUsedFlag[x];
        if (_lpCtrlDataSetUsed[x])
            cnt++;
    }

    _lp_status_tbl.lp_status_set = new LP_SET_STATUS_RCD[cnt];

    for (x = 0; x < 4; x++)
    {
        if (_lpCtrlDataSetUsed[x])
        {

            dataBlob += toAnsiIntParser(dataBlob, &_lp_status_tbl.lp_status_set[index].lp_set_status_flags, sizeof( LP_SET_STATUS_BFLD )); // 1byte
            dataBlob += toAnsiIntParser(dataBlob, &_lp_status_tbl.lp_status_set[index].nbr_valid_blocks, sizeof (short), dataOrder); //2bytes
            dataBlob += toAnsiIntParser(dataBlob, &_lp_status_tbl.lp_status_set[index].last_block_element, sizeof (short), dataOrder); //2bytes

            double tempResult;
            dataBlob += toDoubleParser( dataBlob, tempResult, ANSI_NI_FORMAT_INT32, dataOrder );
            _lp_status_tbl.lp_status_set[index].last_block_seq_nbr = tempResult;

            dataBlob += toAnsiIntParser(dataBlob, &_lp_status_tbl.lp_status_set[index].nbr_unread_blocks, sizeof (short), dataOrder); //2bytes
            dataBlob += toAnsiIntParser(dataBlob, &_lp_status_tbl.lp_status_set[index].nbr_valid_int, sizeof (short), dataOrder); //2bytes

            index+=1;
        }

    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable63::~CtiAnsiTable63()
{
    if (_lp_status_tbl.lp_status_set != NULL)
    {
        delete[]  _lp_status_tbl.lp_status_set;
        _lp_status_tbl.lp_status_set = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable63& CtiAnsiTable63::operator=(const CtiAnsiTable63& aRef)
{
    if (this != &aRef)
    {
    }
    return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable63::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    for (int x = 0, index=0; x < 4; x++)
    {
        if (_lpCtrlDataSetUsed[x])
        {
            itemList <<"LP STATUS SET "<< x+1;
            itemList.add("FLAGS: block order")     << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.block_order;
            itemList.add("overflow")               << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.overflow_flag;
            itemList.add("list type")              << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.list_type;
            itemList.add("block inhibit overflow") << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.block_inhibit_overflow_flag;
            itemList.add("interval order")         << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.interval_order;
            itemList.add("active mode")            << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.active_mode_flag;
            itemList.add("test mode")              << (bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.test_mode;
            itemList.add("Number Valid Blocks")    <<       _lp_status_tbl.lp_status_set[index].nbr_valid_blocks;
            itemList.add("Last Block Element")     <<       _lp_status_tbl.lp_status_set[index].last_block_element;
            itemList.add("Last Block Seq Num")     <<       _lp_status_tbl.lp_status_set[index].last_block_seq_nbr;
            itemList.add("Number Unread Blocks")   <<       _lp_status_tbl.lp_status_set[index].nbr_unread_blocks;
            itemList.add("Number Valid Intvls")    <<       _lp_status_tbl.lp_status_set[index].nbr_valid_int;

            index++;
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 63") <<
            endl <<"** Load Profile Status Table **"<<
            itemList
            )
}

UINT16 CtiAnsiTable63::getNbrValidBlocks(int setNbr)
{
    return _lp_status_tbl.lp_status_set[setNbr-1].nbr_valid_blocks;
}
UINT16 CtiAnsiTable63::getLastBlkElmt(int setNbr)
{
    return _lp_status_tbl.lp_status_set[setNbr-1].last_block_element;
}
UINT16 CtiAnsiTable63::getLastBlkSeqNbr(int setNbr)
{
    return _lp_status_tbl.lp_status_set[setNbr-1].last_block_seq_nbr;
}
UINT16 CtiAnsiTable63::getNbrUnreadBlks(int setNbr)
{
    return _lp_status_tbl.lp_status_set[setNbr-1].nbr_unread_blocks;
}
UINT16 CtiAnsiTable63::getNbrValidIntvls(int setNbr)
{
    return _lp_status_tbl.lp_status_set[setNbr-1].nbr_valid_int;
}
bool CtiAnsiTable63::isDataBlockOrderDecreasing(int setNbr)
{
    return (bool)_lp_status_tbl.lp_status_set[setNbr - 1].lp_set_status_flags.block_order;
}

bool CtiAnsiTable63::isIntervalOrderDecreasing(int setNbr)
{
    return (bool)_lp_status_tbl.lp_status_set[setNbr - 1].lp_set_status_flags.interval_order;
}

