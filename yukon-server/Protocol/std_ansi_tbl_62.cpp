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
#include <boost/lexical_cast.hpp>

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
    Cti::FormattedList itemList;

    for (int index = 0; index < 4; index++)
    {
        if (_lpCtrlDataSetUsed[index])
        {
            itemList <<"Load Profile Ctrl Data Set "<< index+1;

            appendLPSelSet(index, _numChansSet[index], itemList);
            if (_scalarDivisorFlagSet[index])
            {
                appendScalarsDivisorSet(index, _numChansSet[index], itemList);
            }
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 62") <<
            endl <<"** Load Profile Control Table **"<< 
            itemList
            );
}

void CtiAnsiTable62::appendLPSelSet(int index, int numChans, Cti::FormattedList& itemList)
{
    Cti::StreamBufferSink& end_rdg_flag = itemList.add("EndRdgFlg");
    for (int x = 0; x < numChans; x++)
    {
        end_rdg_flag << (bool)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.end_rdg_flag <<" ";
    }

    Cti::StreamBufferSink& no_multiplier_flag = itemList.add("NoMultFlg");
    for (int x = 0; x < numChans; x++)
    {
        no_multiplier_flag << (bool)_lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.no_multiplier_flag <<" ";
    }

    Cti::StreamBufferSink& lp_algorithm = itemList.add("LPAlgorithm");
    for (int x = 0; x < numChans; x++)
    {
        lp_algorithm << _lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].chnl_flag.lp_algorithm <<" ";
    }

    Cti::StreamBufferSink& lp_source_sel = itemList.add("IntSrcSel");
    for (int x = 0; x < numChans; x++)
    {
        lp_source_sel << _lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].lp_source_sel <<" ";
    }

    Cti::StreamBufferSink& end_blk_rdg_source_select = itemList.add("EndBlkRdgSrcSel");
    for (int x = 0; x < numChans; x++)
    {
        end_blk_rdg_source_select << _lp_ctrl_tbl.lp_sel[index].lp_sel_set[x].end_blk_rdg_source_select <<" ";
    }

    itemList.add("IntFmtCde") << _lp_ctrl_tbl.lp_sel[index].int_fmt_cde;
}
void CtiAnsiTable62::appendScalarsDivisorSet(int index, int numChans, Cti::FormattedList& itemList)
{
    Cti::StreamBufferSink& scalars_set = itemList.add("Scalars Set");
    for (int x = 0; x < numChans; x++)
    {
        scalars_set << _lp_ctrl_tbl.lp_sel[index].scalars_set[x] <<" ";
    }

    Cti::StreamBufferSink& divisor_set = itemList.add("Divisor Set");
    for (int x = 0; x < numChans; x++)
    {
        divisor_set << _lp_ctrl_tbl.lp_sel[index].divisor_set[x] <<" ";
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
