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

CtiAnsiTable63::CtiAnsiTable63( BYTE *dataBlob, bool *dataSetUsedFlag, bool lsbDataOrder)
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

            ULONG tempLong;
            memcpy( (void *)&_lp_status_tbl.lp_status_set[index].lp_set_status_flags, dataBlob, sizeof( LP_SET_STATUS_BFLD ));
            dataBlob += sizeof( LP_SET_STATUS_BFLD );

            if (!lsbDataOrder)
            {
                reverseOrder(dataBlob, sizeof (short));
                reverseOrder(dataBlob + 2, sizeof (short));
            }
            memcpy( (void *)&_lp_status_tbl.lp_status_set[index].nbr_valid_blocks, dataBlob, sizeof (short));
            memcpy( (void *)&_lp_status_tbl.lp_status_set[index].last_block_element, dataBlob + 2, sizeof (short));
            dataBlob += (sizeof (short) * 2);

            double tempResult;
            dataBlob += toDoubleParser( dataBlob, tempResult, ANSI_NI_FORMAT_INT32, lsbDataOrder );
            _lp_status_tbl.lp_status_set[index].last_block_seq_nbr = tempResult;

            if (!lsbDataOrder)
            {
                reverseOrder(dataBlob, sizeof (short));
                reverseOrder(dataBlob + 2, sizeof (short));
            }
            memcpy( (void *)&_lp_status_tbl.lp_status_set[index].nbr_unread_blocks, dataBlob, sizeof (short));
            memcpy( (void *)&_lp_status_tbl.lp_status_set[index].nbr_valid_int, dataBlob + 2, sizeof (short));
            dataBlob += (sizeof (short) * 2);

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
    int index= 0;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 63  ========================" << endl;
        dout << endl << "   --- Load Profile Status Table ---" << endl;
    }
    for (int x = 0; x < 4; x++)
    {
        if (_lpCtrlDataSetUsed[x])
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "        LP STATUS SET "<<x + 1<<" : "<<endl;
                dout << "            FLAGS: block order "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.block_order<<endl;
                dout << "            overflow    "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.overflow_flag<<endl;
                dout << "             list type "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.list_type<<endl;
                dout << "             block inhibit overflow "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.block_inhibit_overflow_flag<<endl;
                dout << "             interval order "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.interval_order<<endl;
                dout << "             active mode "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.active_mode_flag<<endl;
                dout << "             test mode "<<(bool)_lp_status_tbl.lp_status_set[index].lp_set_status_flags.test_mode<<endl;
                dout << "            Number Valid Blocks:  "<<_lp_status_tbl.lp_status_set[index].nbr_valid_blocks<<endl;
                dout << "            Last Block Element: "<<_lp_status_tbl.lp_status_set[index].last_block_element<<endl;
                dout << "            Last Block Seq Num: "<<_lp_status_tbl.lp_status_set[index].last_block_seq_nbr<<endl;
                dout << "            Number Unread Blocks: "<<_lp_status_tbl.lp_status_set[index].nbr_unread_blocks<<endl;
                dout << "            Number Valid Intvls: "<<_lp_status_tbl.lp_status_set[index].nbr_valid_int<<endl;
            }
            index++;
        }
    }
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

