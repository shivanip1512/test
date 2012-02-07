/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_62
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
#include "std_ansi_tbl_62.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable62::CtiAnsiTable62( BYTE *dataBlob, bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info,
                                        bool scalarDivisorFlag1, bool scalarDivisorFlag2, bool scalarDivisorFlag3,
                                        bool scalarDivisorFlag4, int stdVersionNumber, DataOrder dataOrder)
{
    _lp_ctrl_tbl.lp_sel[0].lp_sel_set = NULL;
    _lp_ctrl_tbl.lp_sel[0].scalars_set = NULL;
    _lp_ctrl_tbl.lp_sel[0].divisor_set = NULL;

    _lp_ctrl_tbl.lp_sel[1].lp_sel_set = NULL;
    _lp_ctrl_tbl.lp_sel[1].scalars_set = NULL;
    _lp_ctrl_tbl.lp_sel[1].divisor_set = NULL;

    _lp_ctrl_tbl.lp_sel[2].lp_sel_set = NULL;
    _lp_ctrl_tbl.lp_sel[2].scalars_set = NULL;
    _lp_ctrl_tbl.lp_sel[2].divisor_set = NULL;

    _lp_ctrl_tbl.lp_sel[3].lp_sel_set = NULL;
    _lp_ctrl_tbl.lp_sel[3].scalars_set = NULL;
    _lp_ctrl_tbl.lp_sel[3].divisor_set = NULL;

    int index;
    int offset = 0;

    for (int x = 0; x < 4; x++)
    {
        _lpCtrlDataSetUsed[x] = dataSetUsedFlag[x];


        if (_lpCtrlDataSetUsed[x])
        {
            _numChansSet[x] = lp_data_set_info[offset].nbr_chns_set;
            offset++;
        } else
            _numChansSet[x] = 0;
    }

    _scalarDivisorFlagSet[0] = scalarDivisorFlag1;
    _scalarDivisorFlagSet[1] = scalarDivisorFlag2;
    _scalarDivisorFlagSet[2] = scalarDivisorFlag3;
    _scalarDivisorFlagSet[3] = scalarDivisorFlag4;

    _stdVerNumber = stdVersionNumber;

    for (int x = 0; x < 4; x++)
    {
    
        if (_lpCtrlDataSetUsed[x])
        {
            _lp_ctrl_tbl.lp_sel[x].lp_sel_set = new LP_SOURCE_SEL_RCD[_numChansSet[0]];

            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                dataBlob += toAnsiIntParser(dataBlob, &_lp_ctrl_tbl.lp_sel[x].lp_sel_set[index], sizeof( LP_SOURCE_SEL_RCD ));
            }
            dataBlob += toAnsiIntParser(dataBlob, &_lp_ctrl_tbl.lp_sel[x].int_fmt_cde, sizeof( unsigned char ));

            if (_scalarDivisorFlagSet[x])
            {
                _lp_ctrl_tbl.lp_sel[x].scalars_set = new UINT16[_numChansSet[0]];
                _lp_ctrl_tbl.lp_sel[x].divisor_set = new UINT16[_numChansSet[0]];

                for ( index = 0; index < _numChansSet[0]; index++ )
                {
                    dataBlob += toAnsiIntParser(dataBlob, &_lp_ctrl_tbl.lp_sel[x].scalars_set[index], sizeof( UINT16 ), dataOrder);
                }
                for ( index = 0; index < _numChansSet[0]; index++ )
                {
                    dataBlob += toAnsiIntParser(dataBlob, &_lp_ctrl_tbl.lp_sel[x].divisor_set[index], sizeof( UINT16 ), dataOrder);
                }
            }
        }
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable62::~CtiAnsiTable62()
{
    for (int x = 0; x < 4; x++)
    {
        if (_lp_ctrl_tbl.lp_sel[x].lp_sel_set != NULL)
        {
            delete []_lp_ctrl_tbl.lp_sel[x].lp_sel_set;
            _lp_ctrl_tbl.lp_sel[x].lp_sel_set = NULL;
        }
        if (_lp_ctrl_tbl.lp_sel[x].scalars_set != NULL)
        {
            delete []_lp_ctrl_tbl.lp_sel[x].scalars_set;
            _lp_ctrl_tbl.lp_sel[x].scalars_set = NULL;
        }
        if (_lp_ctrl_tbl.lp_sel[x].divisor_set != NULL)
        {
            delete []_lp_ctrl_tbl.lp_sel[x].divisor_set;
            _lp_ctrl_tbl.lp_sel[x].divisor_set = NULL;
        }
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable62& CtiAnsiTable62::operator=(const CtiAnsiTable62& aRef)
{
    if (this != &aRef)
    {
    }
    return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable62::printResult( const string& deviceName )
{
    int index;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<" Std Table 62  ========================" << endl;
        dout << endl << "   --- Load Profile Control Table ---" << endl;
    }

    for ( index = 0; index < 4; index++ )
    {
        if (_lpCtrlDataSetUsed[index])
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "   Load Profile Ctrl Data Set "<<index + 1<<endl;
            }
            printLPSelSet(index, _numChansSet[index]);
            if (_scalarDivisorFlagSet[index])
            {
                printScalarsDivisorSet(index, _numChansSet[index]);
            }
        }
    }
}

void CtiAnsiTable62::printLPSelSet(int index, int numChans)
{
    int x;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "       EndRdgFlg:  ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(bool)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.end_rdg_flag;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "       NoMultFlg: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(bool)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.no_multiplier_flag;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "       LPAlgorithm: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(int)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.lp_algorithm;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "       IntSrcSel: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(int)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].lp_source_sel;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "       EndBlkRdgSrcSel:";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<(int) _lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].end_blk_rdg_source_select;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "       IntFmtCde: "<<(int)_lp_ctrl_tbl.lp_sel[index].int_fmt_cde<<endl;
    }
}
void CtiAnsiTable62::printScalarsDivisorSet(int index, int numChans)
{
    int x;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<"       Scalars Set: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_lp_ctrl_tbl.lp_sel[index].scalars_set[x];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<<"       Divisor Set: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " "<<_lp_ctrl_tbl.lp_sel[index].divisor_set[x];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl;
    }

}

bool  CtiAnsiTable62::getNoMultiplierFlag(int setNbr)
{
    bool retVal = false;
    return (bool)_lp_ctrl_tbl.lp_sel[setNbr - 1].lp_sel_set[0].chnl_flag.no_multiplier_flag;

}
UINT8 CtiAnsiTable62::getIntervalFmtCde(int setNbr)
{
    return _lp_ctrl_tbl.lp_sel[setNbr - 1].int_fmt_cde;
}

UINT8* CtiAnsiTable62::getLPDemandSelect(int setNbr)
{
    UINT8 *lpSrcSel = NULL;

    int index = setNbr - 1;
    lpSrcSel = new UINT8[_numChansSet[index]]; //needs to be deallocated
    for (int x = 0; x < _numChansSet[index]; x++)
    {
        memcpy(lpSrcSel + x, (void *)&(_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].lp_source_sel), sizeof (UINT8));
    }
    return lpSrcSel;
}
