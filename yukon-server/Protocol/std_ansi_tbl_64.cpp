/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_64
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_64.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable64::CtiAnsiTable64( int numberBlocksSet, int numberChansSet,
                                bool closureStatusFlag, bool simpleIntervalStatusFlag,
                                int numberBlockIntervalsSet, bool blockEndReadFlag,
                                bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                                int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2, int timeFmt,
                                int meterHour, bool timeZoneApplied, bool lsbDataOrder, bool descBlockOrder, bool descIntervalOrder  ) :
    _nbrBlksSet1(numberBlocksSet),
    _nbrChnsSet1(numberChansSet),
    _closureStatusFlag(closureStatusFlag),
    _simpleIntStatusFlag(simpleIntervalStatusFlag),
    _nbrBlkIntsSet1(numberBlockIntervalsSet),
    _blkEndReadFlag(blockEndReadFlag),
    _blkEndPulseFlag(blockEndPulseFlag),
    _extendedIntStatusFlag(extendedIntervalStatusFlag),
    _maxIntvlTime(maxIntvlTime),
    _intFmtCde1(intervalFmtCde),
    _nbrValidInts(nbrValidInts),
    _niFmt1(niFmt1),
    _niFmt2(niFmt2),
    _timeFmt(timeFmt),
    _meterHour(meterHour),
    _timeZoneApplied(timeZoneApplied),
    _lsbDataOrder(lsbDataOrder),
    _descBlockOrder(descBlockOrder),
    _descIntervalOrder(descIntervalOrder)
{
    memset( &_lp_data_set1_tbl, 0, sizeof(LP_DATA_SET1_RCD) );
}

CtiAnsiTable64::CtiAnsiTable64( BYTE *dataBlob, int numberBlocksSet, int numberChansSet,
                                          bool closureStatusFlag, bool simpleIntervalStatusFlag,
                                          int numberBlockIntervalsSet, bool blockEndReadFlag,
                                          bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                                          int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2, int timeFmt, int meterHour,
                                          bool timeZoneApplied, bool lsbDataOrder, bool descBlockOrder, bool descIntervalOrder )
{
    int index, i, j;
    int bytes = 0;

    _nbrBlksSet1 = numberBlocksSet;
    _nbrChnsSet1 = numberChansSet;
    _closureStatusFlag = closureStatusFlag;
    _simpleIntStatusFlag = simpleIntervalStatusFlag;
    _nbrBlkIntsSet1 = numberBlockIntervalsSet;
    _blkEndReadFlag = blockEndReadFlag;
    _blkEndPulseFlag = blockEndPulseFlag;
    _extendedIntStatusFlag = extendedIntervalStatusFlag;
    _maxIntvlTime = maxIntvlTime;
    _intFmtCde1 = intervalFmtCde;
    _nbrValidInts = nbrValidInts;
    _timeFmt = timeFmt;
    _niFmt1 = niFmt1;
    _niFmt2 = niFmt2;
    _meterHour = meterHour;
    _timeZoneApplied = timeZoneApplied;
    _lsbDataOrder = lsbDataOrder;
    _descBlockOrder = descBlockOrder;
    _descIntervalOrder = descIntervalOrder;

    _lp_data_set1_tbl.lp_data_sets1 = new LP_BLK1_DAT_RCD[_nbrBlksSet1];

    for (index = 0; index < _nbrBlksSet1; index++)
    {

        // Block End Time - STIME_DATE
        bytes = toUint32STime( dataBlob, _lp_data_set1_tbl.lp_data_sets1[index].blk_end_time, _timeFmt );
        dataBlob += bytes;

        _lp_data_set1_tbl.lp_data_sets1[index].end_readings = new READINGS_RCD[_nbrChnsSet1];
        for (i = 0; i < _nbrChnsSet1; i++)
        {
            if (_blkEndReadFlag)
            {
                // END READINGS - block end reading
                bytes = toDoubleParser( dataBlob, _lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_read, _niFmt1, _lsbDataOrder );
                dataBlob += bytes;
            }
            if (_blkEndPulseFlag)
            {
                // END READINGS - block end pulse
                memcpy( (void *)&_lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_pulse, dataBlob, sizeof( UINT32 ));
                dataBlob += sizeof( UINT32 );
            }
        }
        if (_closureStatusFlag)
        {
            _lp_data_set1_tbl.lp_data_sets1[index].closure_status = new CLOSURE_STATUS_BFLD[_nbrChnsSet1];
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                // CLOSURE STATUS - status, nbr_valid_interval
                memcpy( (void *)&_lp_data_set1_tbl.lp_data_sets1[index].closure_status[i], dataBlob, sizeof( unsigned short ));
                dataBlob += sizeof( unsigned short );
            }

        }
        if (_simpleIntStatusFlag)
        {
            _lp_data_set1_tbl.lp_data_sets1[index].set_simple_int_status = new unsigned char [_nbrBlkIntsSet1+7/8];
            // Simple Interval Status
            memcpy( (void *)&_lp_data_set1_tbl.lp_data_sets1[index].set_simple_int_status, dataBlob, (_nbrBlkIntsSet1+7/8));
            dataBlob += _nbrBlkIntsSet1+7/8;
        }


        _lp_data_set1_tbl.lp_data_sets1[index].lp_int = new INT_SET1_RCD[_nbrBlkIntsSet1];
        for (i = 0; i < _nbrBlkIntsSet1; i++)
        {
            if (_extendedIntStatusFlag)
            {
                _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status = new UINT8[(_nbrChnsSet1/2)+1];
                for (j = 0; j < (_nbrChnsSet1/2)+1; j++ )
                {
                    memcpy( (void *)&_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[j], dataBlob, sizeof (UINT8));
                    dataBlob += sizeof (UINT8);
                }

                if ((UINT8)_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[0] & 0x10)
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].dayLightSavingsFlag = true;
                else
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].dayLightSavingsFlag = false;
                if ((UINT8)_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[0] & 0x20)
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].powerFailFlag = true;
                else
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].powerFailFlag = false;
                if ((UINT8)_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[0] & 0x40)
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].clockResetForwardFlag = true;
                else
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].clockResetForwardFlag = false;
                if ((UINT8)_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[0] & 0x80)
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].clockResetBackwardsFlag = true;
                else
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].clockResetBackwardsFlag = false;

                _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extendedStatus = new int[_nbrChnsSet1];
                int mask = 0x0F;
                for (j = 0; j < _nbrChnsSet1; j++)
                {
                    if (j%2 == 0)
                        mask = 0x0F;
                    else
                        mask = 0xF0;
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extendedStatus[j] = _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[(j/2) + 1] & mask;
                }

            }
            _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data = new INT_FMT1_RCD[_nbrChnsSet1];
            for (j = 0; j < _nbrChnsSet1; j++ )
            {
                bytes = populateIntData( &_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data[j], dataBlob );
                dataBlob += bytes;
            }
        }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable64::~CtiAnsiTable64()
{
    int i, j;

    if (_lp_data_set1_tbl.lp_data_sets1 != NULL)
    {
       for (i = 0; i < _nbrBlksSet1; i++)
       {
           if (_lp_data_set1_tbl.lp_data_sets1[i].end_readings != NULL)
           {
               delete []_lp_data_set1_tbl.lp_data_sets1[i].end_readings;
               _lp_data_set1_tbl.lp_data_sets1[i].end_readings = NULL;
           }
           if (_closureStatusFlag)
           {
               if (_lp_data_set1_tbl.lp_data_sets1[i].closure_status != NULL)
               {
                   delete []_lp_data_set1_tbl.lp_data_sets1[i].closure_status;
                   _lp_data_set1_tbl.lp_data_sets1[i].closure_status = NULL;
               }
           }
           if (_simpleIntStatusFlag)
           {
               if (_lp_data_set1_tbl.lp_data_sets1[i].set_simple_int_status != NULL)
               {
                   delete _lp_data_set1_tbl.lp_data_sets1[i].set_simple_int_status;
                   _lp_data_set1_tbl.lp_data_sets1[i].set_simple_int_status = NULL;
               }
           }
           if (_lp_data_set1_tbl.lp_data_sets1[i].lp_int != NULL)
           {
               for (j = 0; j < _nbrBlkIntsSet1; j++)
               {
                   if (_extendedIntStatusFlag)
                   {
                       if (_lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extended_int_status != NULL)
                       {
                            delete []_lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extended_int_status;
                            _lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extended_int_status = NULL;
                       }
                       if (_lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extendedStatus != NULL)
                       {
                           delete _lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extendedStatus;
                           _lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].extendedStatus = NULL;
                       }

                   }
                   if (_lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].int_data != NULL)
                   {
                       delete []_lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].int_data;
                       _lp_data_set1_tbl.lp_data_sets1[i].lp_int[j].int_data = NULL;
                   }
               }
               delete []_lp_data_set1_tbl.lp_data_sets1[i].lp_int;
               _lp_data_set1_tbl.lp_data_sets1[i].lp_int = NULL;
           }
       }
       delete []_lp_data_set1_tbl.lp_data_sets1;
       _lp_data_set1_tbl.lp_data_sets1 = NULL;

   }
}
//=========================================================================================================================================
//=========================================================================================================================================
int CtiAnsiTable64::populateIntData(INT_FMT1_RCD *intData, BYTE *dataBlob)
{
    int retVal = 0;

    switch(_intFmtCde1)
    {
        case 1:
        {
            memcpy( (void *)&intData->u.s1.item, dataBlob, sizeof (UINT8));
            retVal += sizeof (UINT8);
            break;
        }
        case 2:
        {
            memcpy( (void *)&intData->u.s2.item, dataBlob, sizeof (UINT16));
            retVal += sizeof (UINT16);
            break;
        }

        case 4:
        {
            memcpy( (void *)&intData->u.s4.item, dataBlob, sizeof (UINT32));
            retVal += sizeof (UINT32);
            break;
        }

        case 8:
        {
            memcpy( (void *)&intData->u.s8.item, dataBlob, sizeof (INT8));
            retVal += sizeof (INT8);
            break;
        }

        case 16:
        {
            memcpy( (void *)&intData->u.s16.item, dataBlob, sizeof (INT16));
            retVal += sizeof (INT16);
            break;
        }

        case 32:
        {
            memcpy( (void *)&intData->u.s32.item, dataBlob, sizeof (INT32));
            retVal += sizeof (INT32);
            break;
        }
        case 64:
        {
            retVal += 8;
            break;
        }
        case 128:
        {
            retVal += 16;
            break;
        }
        default:
            break;
    }
    return retVal;
}


//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable64& CtiAnsiTable64::operator=(const CtiAnsiTable64& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable64::generateResultPiece( BYTE **dataBlob )
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable64::decodeResultPiece( BYTE **dataBlob )
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable64::printResult( const string& deviceName )
{
    int index, i, j;
    int nbrBlkInts;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "================== "<<deviceName<<"  Std Table 64  ========================" << endl;
    }

    for (index = 0; index < _nbrBlksSet1; index++)
    {
        if ( ( !_descBlockOrder && index == (_nbrBlksSet1-1))
              || (  _descBlockOrder && index == 0 )
              )
        {
            nbrBlkInts = _nbrValidInts;
        }
        else
        {
            nbrBlkInts = _nbrBlkIntsSet1;
        }

        {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  **B*L*O*C*K** : "<<index<<endl;
                if (_timeZoneApplied)
                {
                    dout << "  **Block End Time: "<<CtiTime(_lp_data_set1_tbl.lp_data_sets1[index].blk_end_time).asGMTString()<<endl;
                }
                else
                {
                    dout << "  **Block End Time: "<<CtiTime(_lp_data_set1_tbl.lp_data_sets1[index].blk_end_time)<<endl;
                }
        }
        if (_blkEndReadFlag)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  **Block End Reads: ";
            }
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  "<<_lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_read;
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << endl;
            }
        }
        if (_blkEndPulseFlag)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  **Block End Pulse: ";
            }
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  "<< _lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_pulse;
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << endl;
            }
        }
        if (_closureStatusFlag)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  **Closure Status BitField: ";
                dout << "             Status -  ";
            }
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  "<<_lp_data_set1_tbl.lp_data_sets1[index].closure_status[i].status;
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout <<endl<< "             Nbr Valid Interval -  ";
            }
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  "<<_lp_data_set1_tbl.lp_data_sets1[index].closure_status[i].nbr_valid_interval;
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout <<endl;
            }
            nbrBlkInts = _lp_data_set1_tbl.lp_data_sets1[index].closure_status[0].nbr_valid_interval;
        }
        if (_simpleIntStatusFlag)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "  **Simple Interval Status: "<<_lp_data_set1_tbl.lp_data_sets1[index].set_simple_int_status<<endl;
            }
        }
        for (i = 0; i < nbrBlkInts; i++)
        {
            {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << "  **BLOCK INTERVAL: "<<i+1<<endl;
            }
            if (_extendedIntStatusFlag)
            {
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << "    **Extended Interval Status: ";
                }
                for (j = 0; j < (_nbrChnsSet1/2)+1; j++ )
                {
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << "  "<<(int)_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[j];
                    }
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " DSTFlag( "<<_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].dayLightSavingsFlag<<" )";
                    }
                }
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout <<endl;
                }
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << "    **Interval Data: ";
            }
            for (j = 0; j < _nbrChnsSet1; j++ )
            {
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << "  ";
                }
                printIntervalFmtRecord(_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data[j]);
            }
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << endl;
            }
        }
    }
}
void CtiAnsiTable64::printIntervalFmtRecord(INT_FMT1_RCD intData)
{
    switch(_intFmtCde1)
    {
        case 1:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s1.item;
            }
            break;
        }
        case 2:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s2.item;
            }
            break;
        }

        case 4:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s4.item;
            }
            break;
        }

        case 8:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s8.item;
            }
            break;
        }

        case 16:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s16.item;
            }
            break;
        }

        case 32:
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << (int)intData.u.s32.item;
            }
            break;
        }
        case 64:
        {
            break;
        }
        case 128:
        {
            break;
        }
        default:
            break;
    }
}


void CtiAnsiTable64::getBlkIntvlTime(int blkSet, int blkIntvl, ULONG &blkIntvlTime, bool blockOrderDecreasing)
{
    ULONG blkEndTime = 0;
    if (getBlkEndTime(blkSet,blkEndTime))
    {
        if (_closureStatusFlag)
        {
            int totIntvls = _lp_data_set1_tbl.lp_data_sets1[blkSet].closure_status[0].nbr_valid_interval;
            if (blkIntvl <= totIntvls)
            {

                blkIntvlTime = blkEndTime - ((totIntvls - (blkIntvl+1)) * _maxIntvlTime * 60); //likely need to change to time, then convert to seconds.
            }
            else
            {
                blkIntvlTime = 0;
            }
        }
        else
        {
            //if (blkSet == (_nbrBlksSet1-1) && blkIntvl < _nbrValidInts)
            if (blkSet == (_nbrBlksSet1 -1) && blkIntvl < _nbrValidInts)
            {
               blkIntvlTime = blkEndTime - ((_nbrValidInts - (blkIntvl+1)) * _maxIntvlTime * 60);
            }
            else
            {
                if (blkIntvl < _nbrBlkIntsSet1)
                {
                    blkIntvlTime = blkEndTime - ((_nbrBlkIntsSet1 - (blkIntvl+1)) * _maxIntvlTime * 60);
                }
                else
                {
                    blkIntvlTime = 0;
                }
            }
        }
    }
    return;
}

bool CtiAnsiTable64::getBlkEndTime(int blkSet, ULONG &blkEndTime)
{
    bool retVal = false;
    if (blkSet <= _nbrBlksSet1)
    {
        blkEndTime = _lp_data_set1_tbl.lp_data_sets1[blkSet].blk_end_time;
        retVal = true;
    }
    else
    {
        blkEndTime = 0;
    }
    return retVal;
}

ULONG CtiAnsiTable64::getLPDemandTime (int blkSet, int blkIntvl, bool decreasingBlockOrder)
{
    ULONG blkEndTime = 0;
    getBlkIntvlTime(blkSet, blkIntvl, blkEndTime, decreasingBlockOrder);
    return blkEndTime;
}
double CtiAnsiTable64::getLPDemandValue ( int channel, int blkSet, int blkIntvl )
{
    double retVal = 0;
    switch(_intFmtCde1)
    {
        case 1:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s1.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s1.item;
            break;
        }
        case 2:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s2.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s2.item;
            break;
        }

        case 4:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s4.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s4.item;
            break;
        }

        case 8:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s8.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s8.item;
            break;
        }

        case 16:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s16.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s16.item;
            break;
        }

        case 32:
        {
            //retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet - 1].lp_int[blkIntvl - 1].int_data[channel - 1].u.s32.item;
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s32.item;
            break;
        }
        case 64:
        {
            break;
        }
        case 128:
        {
            break;
        }
        default:
            break;
    }
   /* {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " return val  "<<retVal<<endl;
    } */

    return retVal;
}

int CtiAnsiTable64::getExtendedIntervalStatus(int channel, int blkSet, int blkIntvl)
{
    if (_extendedIntStatusFlag)
    {
        return _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].extendedStatus[channel];
    }
    else
        return 0;
}

bool CtiAnsiTable64::getDayLightSavingsFlag(int blkSet, int blkIntvl) const
{
    if (_extendedIntStatusFlag)
        return _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].dayLightSavingsFlag;
    else
        return false;
}
bool CtiAnsiTable64::getPowerFailFlag(int blkSet, int blkIntvl) const
{
    if (_extendedIntStatusFlag)
        return _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].powerFailFlag;
    else
        return false;
}

bool CtiAnsiTable64::getClockResetForwardFlag(int blkSet, int blkIntvl) const
{
    if (_extendedIntStatusFlag)
        return _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].clockResetForwardFlag;
    else
        return false;
}

bool CtiAnsiTable64::getClockResetBackwardsFlag(int blkSet, int blkIntvl) const
{
    if (_extendedIntStatusFlag)
        return _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].clockResetBackwardsFlag;
    else
        return false;
}


