/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_six_two
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
#include "std_ansi_tbl_six_two.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableSixTwo::CtiAnsiTableSixTwo( bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, bool scalarDivisorFlag1, 
                                        bool scalarDivisorFlag2, bool scalarDivisorFlag3, bool scalarDivisorFlag4, 
                                        int stdVersionNumber  )
{  

    _lp_ctrl_tbl.lp_sel_set1 = NULL;
    _lp_ctrl_tbl.scalars_set1 = NULL;
    _lp_ctrl_tbl.divisor_set1 = NULL;

    _lp_ctrl_tbl.lp_sel_set2 = NULL;  
    _lp_ctrl_tbl.scalars_set2 = NULL; 
    _lp_ctrl_tbl.divisor_set2 = NULL; 

    _lp_ctrl_tbl.lp_sel_set3 = NULL;  
    _lp_ctrl_tbl.scalars_set3 = NULL; 
    _lp_ctrl_tbl.divisor_set3 = NULL; 

    _lp_ctrl_tbl.lp_sel_set4 = NULL;  
    _lp_ctrl_tbl.scalars_set4 = NULL; 
    _lp_ctrl_tbl.divisor_set4 = NULL;          

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

}




CtiAnsiTableSixTwo::CtiAnsiTableSixTwo( BYTE *dataBlob, bool *dataSetUsedFlag, LP_DATA_SET *lp_data_set_info, 
                                        bool scalarDivisorFlag1, bool scalarDivisorFlag2, bool scalarDivisorFlag3, 
                                        bool scalarDivisorFlag4, int stdVersionNumber)
{
    _lp_ctrl_tbl.lp_sel_set1 = NULL;
    _lp_ctrl_tbl.scalars_set1 = NULL;
    _lp_ctrl_tbl.divisor_set1 = NULL;

    _lp_ctrl_tbl.lp_sel_set2 = NULL;  
    _lp_ctrl_tbl.scalars_set2 = NULL; 
    _lp_ctrl_tbl.divisor_set2 = NULL; 

    _lp_ctrl_tbl.lp_sel_set3 = NULL;  
    _lp_ctrl_tbl.scalars_set3 = NULL; 
    _lp_ctrl_tbl.divisor_set3 = NULL; 

    _lp_ctrl_tbl.lp_sel_set4 = NULL;  
    _lp_ctrl_tbl.scalars_set4 = NULL; 
    _lp_ctrl_tbl.divisor_set4 = NULL; 

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

    if (_lpCtrlDataSetUsed[0])
    {
        _lp_ctrl_tbl.lp_sel_set1 = new LP_SOURCE_SEL_RCD[_numChansSet[0]];           

        for ( index = 0; index < _numChansSet[0]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set1[index], dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde1, dataBlob, sizeof( unsigned char ));
        dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[0])
        {
            _lp_ctrl_tbl.scalars_set1 = new UINT16[_numChansSet[0]];                     
            _lp_ctrl_tbl.divisor_set1 = new UINT16[_numChansSet[0]];                     

            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set1[index], dataBlob, sizeof( UINT16 ));
                dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set1[index], dataBlob, sizeof( UINT16 ) );
                dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[1])
    {
        _lp_ctrl_tbl.lp_sel_set2 = new LP_SOURCE_SEL_RCD[_numChansSet[1]];           

        for ( index = 0; index < _numChansSet[1]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set2[index], dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde2, dataBlob, sizeof( unsigned char ));
        dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[1])
        {
            _lp_ctrl_tbl.scalars_set2 = new UINT16[_numChansSet[1]];                     
            _lp_ctrl_tbl.divisor_set2 = new UINT16[_numChansSet[1]];                     

            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set2[index], dataBlob, sizeof( UINT16 ));
                dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set2[index], dataBlob, sizeof( UINT16 ) );
                dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[2])
    {
        _lp_ctrl_tbl.lp_sel_set3 = new LP_SOURCE_SEL_RCD[_numChansSet[2]];           

        for ( index = 0; index < _numChansSet[2]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set3[index], dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde3, dataBlob, sizeof( unsigned char ));
        dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[2])
        {
            _lp_ctrl_tbl.scalars_set3 = new UINT16[_numChansSet[2]];                     
            _lp_ctrl_tbl.divisor_set3 = new UINT16[_numChansSet[2]];                     

            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set3[index], dataBlob, sizeof( UINT16 ));
                dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set3[index], dataBlob, sizeof( UINT16 ) );
                dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[3])
    {
        _lp_ctrl_tbl.lp_sel_set4 = new LP_SOURCE_SEL_RCD[_numChansSet[3]];           

        for ( index = 0; index < _numChansSet[3]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set4[index], dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde4, dataBlob, sizeof( unsigned char ));
        dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[3])
        {
            _lp_ctrl_tbl.scalars_set4 = new UINT16[_numChansSet[3]];                     
            _lp_ctrl_tbl.divisor_set4 = new UINT16[_numChansSet[3]];                     

            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set4[index], dataBlob, sizeof( UINT16 ));
                dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set4[index], dataBlob, sizeof( UINT16 ) );
                dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableSixTwo::~CtiAnsiTableSixTwo()
{
    if (_lp_ctrl_tbl.lp_sel_set1 != NULL)
    {
        delete[]  _lp_ctrl_tbl.lp_sel_set1;
        _lp_ctrl_tbl.lp_sel_set1 = NULL;
    }
    if (_lp_ctrl_tbl.scalars_set1 != NULL)
    {
        delete[] _lp_ctrl_tbl.scalars_set1;
        _lp_ctrl_tbl.scalars_set1 = NULL;
    }
    if (_lp_ctrl_tbl.divisor_set1 != NULL)
    {
        delete[] _lp_ctrl_tbl.divisor_set1;
        _lp_ctrl_tbl.divisor_set1 = NULL;
    }

    if (_lp_ctrl_tbl.lp_sel_set2 != NULL)
    {
        delete  []_lp_ctrl_tbl.lp_sel_set2;
        _lp_ctrl_tbl.lp_sel_set2 = NULL;
    }
    if (_lp_ctrl_tbl.scalars_set2 != NULL)
    {
        delete []_lp_ctrl_tbl.scalars_set2;
        _lp_ctrl_tbl.scalars_set2 = NULL;
    }
    if (_lp_ctrl_tbl.divisor_set2 != NULL)
    {
        delete []_lp_ctrl_tbl.divisor_set2;
        _lp_ctrl_tbl.divisor_set2 = NULL;
    }

    if (_lp_ctrl_tbl.lp_sel_set3 != NULL)
    {
        delete  []_lp_ctrl_tbl.lp_sel_set3;
        _lp_ctrl_tbl.lp_sel_set3 = NULL;
    }
    if (_lp_ctrl_tbl.scalars_set3 != NULL)
    {
        delete []_lp_ctrl_tbl.scalars_set3;
        _lp_ctrl_tbl.scalars_set3 = NULL;
    }
    if (_lp_ctrl_tbl.divisor_set3 != NULL)
    {
        delete []_lp_ctrl_tbl.divisor_set3;
        _lp_ctrl_tbl.divisor_set3 = NULL;
    }

    if (_lp_ctrl_tbl.lp_sel_set4 != NULL)
    {
        delete [] _lp_ctrl_tbl.lp_sel_set4;
        _lp_ctrl_tbl.lp_sel_set4 = NULL;
    }
    if (_lp_ctrl_tbl.scalars_set4 != NULL)
    {
        delete []_lp_ctrl_tbl.scalars_set4;
        _lp_ctrl_tbl.scalars_set4 = NULL;
    }
    if (_lp_ctrl_tbl.divisor_set4 != NULL)
    {
        delete []_lp_ctrl_tbl.divisor_set4;
        _lp_ctrl_tbl.divisor_set4 = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableSixTwo& CtiAnsiTableSixTwo::operator=(const CtiAnsiTableSixTwo& aRef)
{
    if (this != &aRef)
    {
    }
    return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixTwo::generateResultPiece( BYTE **dataBlob )
{
    int index;
    if (_lpCtrlDataSetUsed[0])
    {
        for ( index = 0; index < _numChansSet[0]; index++ )
        {
            memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.lp_sel_set1[index], sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.int_fmt_cde1, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[0])
        {
            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.scalars_set1[index], sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.divisor_set1[index], sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[1])
    {
        for ( index = 0; index < _numChansSet[1]; index++ )
        {
            memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.lp_sel_set2[index], sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.int_fmt_cde2, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[1])
        {
            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.scalars_set2[index], sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.divisor_set2[index], sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[2])
    {
        for ( index = 0; index < _numChansSet[2]; index++ )
        {
            memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.lp_sel_set3[index], sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.int_fmt_cde3, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[2])
        {
            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.scalars_set3[index], sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.divisor_set3[index], sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[3])
    {
        for ( index = 0; index < _numChansSet[3]; index++ )
        {
            memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.lp_sel_set4[index], sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.int_fmt_cde4, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[3])
        {
            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.scalars_set4[index], sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( *dataBlob, (void *)&_lp_ctrl_tbl.divisor_set4[index], sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixTwo::decodeResultPiece( BYTE **dataBlob )
{
    int index;
    if (_lpCtrlDataSetUsed[0])
    {
        _lp_ctrl_tbl.lp_sel_set1 = new LP_SOURCE_SEL_RCD[_numChansSet[0]];           

        for ( index = 0; index < _numChansSet[0]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set1[index], *dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde1, *dataBlob, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[0])
        {
            _lp_ctrl_tbl.scalars_set1 = new UINT16[_numChansSet[0]];                     
            _lp_ctrl_tbl.divisor_set1 = new UINT16[_numChansSet[0]];                     

            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set1[index], *dataBlob, sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[0]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set1[index], *dataBlob, sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[1])
    {
        _lp_ctrl_tbl.lp_sel_set2 = new LP_SOURCE_SEL_RCD[_numChansSet[1]];           

        for ( index = 0; index < _numChansSet[1]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set2[index], *dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde2, *dataBlob, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[1])
        {
            _lp_ctrl_tbl.scalars_set2 = new UINT16[_numChansSet[1]];                     
            _lp_ctrl_tbl.divisor_set2 = new UINT16[_numChansSet[1]];                     

            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set2[index], *dataBlob, sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[1]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set2[index], *dataBlob, sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[2])
    {
        _lp_ctrl_tbl.lp_sel_set3 = new LP_SOURCE_SEL_RCD[_numChansSet[2]];           

        for ( index = 0; index < _numChansSet[2]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set3[index], *dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde3, *dataBlob, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[2])
        {
            _lp_ctrl_tbl.scalars_set3 = new UINT16[_numChansSet[2]];                     
            _lp_ctrl_tbl.divisor_set3 = new UINT16[_numChansSet[2]];                     

            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set3[index], *dataBlob, sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[2]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set3[index], *dataBlob, sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
    if (_lpCtrlDataSetUsed[3])
    {
        _lp_ctrl_tbl.lp_sel_set4 = new LP_SOURCE_SEL_RCD[_numChansSet[3]];           

        for ( index = 0; index < _numChansSet[3]; index++ )
        {
            memcpy( (void *)&_lp_ctrl_tbl.lp_sel_set4[index], *dataBlob, sizeof( LP_SOURCE_SEL_RCD ));
            *dataBlob += sizeof( LP_SOURCE_SEL_RCD );
        }
        memcpy( (void *)&_lp_ctrl_tbl.int_fmt_cde4, *dataBlob, sizeof( unsigned char ));
        *dataBlob +=  sizeof( unsigned char );

        if (_scalarDivisorFlagSet[3])
        {
            _lp_ctrl_tbl.scalars_set4 = new UINT16[_numChansSet[3]];                     
            _lp_ctrl_tbl.divisor_set4 = new UINT16[_numChansSet[3]];                     

            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.scalars_set4[index], *dataBlob, sizeof( UINT16 ));
                *dataBlob +=  sizeof( UINT16 );
            }
            for ( index = 0; index < _numChansSet[3]; index++ )
            {
                memcpy( (void *)&_lp_ctrl_tbl.divisor_set4[index], *dataBlob, sizeof( UINT16 ) );
                *dataBlob +=  sizeof( UINT16 ) ;
            }
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableSixTwo::printResult(  )
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
        dout << endl << "=======================  Std Table 62  ========================" << endl;
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

void CtiAnsiTableSixTwo::printLPSelSet(int set, int numChans)
{
    LP_SOURCE_SEL_RCD *tempSourceSelRcd;
    UINT8 tempIntSel;
    int x;

    switch (set+1)
    {
    case 1:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set1;
            tempIntSel = _lp_ctrl_tbl.int_fmt_cde1;
            break;
        }
    case 2:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set2;
            tempIntSel = _lp_ctrl_tbl.int_fmt_cde2;
            break;
        }
    case 3:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set3;
            tempIntSel = _lp_ctrl_tbl.int_fmt_cde3;
            break;
        }
    case 4:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set4;
            tempIntSel = _lp_ctrl_tbl.int_fmt_cde4;
            break;
        }
    default:
        break;

    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "            EndRdgFlg:  ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(bool)tempSourceSelRcd[x].chnl_flag.end_rdg_flag;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "            NoMultFlg: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(bool)tempSourceSelRcd[x].chnl_flag.no_multiplier_flag;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "            LPAlgorithm: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<(int)tempSourceSelRcd[x].chnl_flag.lp_algorithm;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "            IntSrcSel: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<tempSourceSelRcd[x].lp_source_sel;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "            EndBlkRdgSrcSel:";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<tempSourceSelRcd[x].end_blk_rdg_source_select;
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "            IntFmtCde: "<<tempIntSel<<endl;
    }

}
void CtiAnsiTableSixTwo::printScalarsDivisorSet(int set, int numChans)
{
    UINT16 *tempScalarsSet;
    UINT16 *tempDivisorSet;
    int x;

    switch (set+1)
    {
    case 1:
        {
            tempScalarsSet = _lp_ctrl_tbl.scalars_set1;
            tempDivisorSet = _lp_ctrl_tbl.divisor_set1;
            break;
        }
    case 2:
        {
            tempScalarsSet = _lp_ctrl_tbl.scalars_set1;
            tempDivisorSet = _lp_ctrl_tbl.divisor_set1;
            break;
        }
    case 3:
        {
            tempScalarsSet = _lp_ctrl_tbl.scalars_set1;
            tempDivisorSet = _lp_ctrl_tbl.divisor_set1;
            break;
        }
    case 4:
        {
            tempScalarsSet = _lp_ctrl_tbl.scalars_set1;
            tempDivisorSet = _lp_ctrl_tbl.divisor_set1;
            break;
        }
    default:
        break;

    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<"            Scalars Set: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<tempScalarsSet[x];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<<"            Divisor Set: ";
    }
    for (x = 0; x < numChans; x++)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  "<<tempDivisorSet[x];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl;
    }


}

bool  CtiAnsiTableSixTwo::getNoMultiplierFlag(int setNbr)
{
    bool retVal = false;
    switch (setNbr)
    {
        case 1:
            {
                retVal = (bool)_lp_ctrl_tbl.lp_sel_set1->chnl_flag.no_multiplier_flag;
                break;
            }
        case 2:
            {
                retVal = (bool)_lp_ctrl_tbl.lp_sel_set2->chnl_flag.no_multiplier_flag;
                break;
            }
        case 3:
            {
                retVal = (bool)_lp_ctrl_tbl.lp_sel_set3->chnl_flag.no_multiplier_flag;
                break;
            }
        case 4:
            {
                retVal = (bool)_lp_ctrl_tbl.lp_sel_set4->chnl_flag.no_multiplier_flag;
                break;
            }
        default:
            break;

    }

    return retVal;
}
UINT8 CtiAnsiTableSixTwo::getIntervalFmtCde(int setNbr)
{
    UINT8 retVal = 0;
    switch (setNbr)
    {
    case 1:
        {
            retVal = _lp_ctrl_tbl.int_fmt_cde1;
            break;
        }
    case 2:
        {
            retVal = _lp_ctrl_tbl.int_fmt_cde2;
            break;
        }
    case 3:
        {
            retVal = _lp_ctrl_tbl.int_fmt_cde3;
            break;
        }
    case 4:
        {
            retVal = _lp_ctrl_tbl.int_fmt_cde4;
            break;
        }
    default:
        break;

    }

    return retVal;
}

UINT8* CtiAnsiTableSixTwo::getLPDemandSelect(int setNbr)
{
    UINT8 *lpSrcSel = NULL;
    LP_SOURCE_SEL_RCD *tempSourceSelRcd;
    int x;

    switch (setNbr)
    {
    case 1:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set1; 
        }
        break;
    case 2:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set2; 
        }
        break;
    case 3:
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set3; 
        }
        break;
    case 4: 
        {
            tempSourceSelRcd = _lp_ctrl_tbl.lp_sel_set4; 
        }
        break;
    default:
        break;
    }

    lpSrcSel = new UINT8[_numChansSet[setNbr-1]]; //needs to be deallocated?
    for (x = 0; x < _numChansSet[setNbr-1]; x++)
    {
        //(void *)&lpSrcSel[x] = tempSourceSelRcd[x].lp_source_sel;
        memcpy(lpSrcSel + x, (void *)&(tempSourceSelRcd[x].lp_source_sel), sizeof (UINT8));
    }
    return lpSrcSel;
}
