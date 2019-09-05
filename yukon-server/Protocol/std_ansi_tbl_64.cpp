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

#include <boost/lexical_cast.hpp>
#include "logger.h"
#include "ctitime.h"
#include "std_ansi_tbl_64.h"


using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable64::CtiAnsiTable64( BYTE *dataBlob, int numberBlocksSet, int numberChansSet,
                                          bool closureStatusFlag, bool simpleIntervalStatusFlag,
                                          int numberBlockIntervalsSet, bool blockEndReadFlag,
                                          bool blockEndPulseFlag, bool extendedIntervalStatusFlag, int maxIntvlTime,
                                          int intervalFmtCde, int nbrValidInts, int niFmt1, int niFmt2, int timeFmt, int meterHour,
                                          bool timeZoneApplied, DataOrder dataOrder, bool descBlockOrder, bool descIntervalOrder )
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
    _dataOrder = dataOrder;
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
                bytes = toDoubleParser( dataBlob, _lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_read, _niFmt1, _dataOrder );
                dataBlob += bytes;
            }
            if (_blkEndPulseFlag)
            {
                // END READINGS - block end pulse
                dataBlob += toAnsiIntParser(dataBlob, &_lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_pulse, sizeof( UINT32 ), _dataOrder);
            }
        }
        if (_closureStatusFlag)
        {
            _lp_data_set1_tbl.lp_data_sets1[index].closure_status = new CLOSURE_STATUS_BFLD[_nbrChnsSet1];
            for (i = 0; i < _nbrChnsSet1; i++)
            {
                // CLOSURE STATUS - status, nbr_valid_interval
                dataBlob += toAnsiIntParser(dataBlob, &_lp_data_set1_tbl.lp_data_sets1[index].closure_status[i], sizeof( unsigned short ), _dataOrder);
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
                    dataBlob += toAnsiIntParser(dataBlob, &_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[j], sizeof (UINT8));
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
                    _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extendedStatus[j] = _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[(j+1)/2] & mask;
                }

            }
            _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data = new INT_FMT1_RCD[_nbrChnsSet1];
            for (j = 0; j < _nbrChnsSet1; j++ )
            {
                dataBlob += populateIntData( &_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data[j], dataBlob );
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
            retVal = toAnsiIntParser(dataBlob, &intData->u.s1.item, sizeof (UINT8));
            break;
        }
        case 2:
        {
            retVal = toAnsiIntParser(dataBlob, &intData->u.s2.item, sizeof (UINT16), _dataOrder);
            break;
        }

        case 4:
        {
            retVal = toAnsiIntParser(dataBlob, &intData->u.s4.item, sizeof (UINT32), _dataOrder);
            break;
        }

        case 8:
        {
            retVal = toAnsiIntParser(dataBlob, &intData->u.s8.item, sizeof (INT8));
            break;
        }

        case 16:
        {
            retVal = toAnsiIntParser(dataBlob, &intData->u.s16.item, sizeof (INT16), _dataOrder);
            break;
        }

        case 32:
        {
            retVal = toAnsiIntParser(dataBlob, &intData->u.s32.item, sizeof (INT32), _dataOrder);
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
void CtiAnsiTable64::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    for (int index = 0; index < _nbrBlksSet1; index++)
    {
        int startInt = 0, nbrBlkInts;
        if( (!_descBlockOrder && index == (_nbrBlksSet1-1)) || (_descBlockOrder && index == 0) )
        {
            if(_descIntervalOrder)
            {
                startInt = _nbrBlkIntsSet1 - _nbrValidInts;
                nbrBlkInts = _nbrBlkIntsSet1;
            }
            else
            {
                nbrBlkInts = _nbrValidInts;
            }
        }
        else
        {
            nbrBlkInts = _nbrBlkIntsSet1;
        }

        itemList <<"B*L*O*C*K "<< index;

        const CtiTime::DisplayOffset displayOffset = _timeZoneApplied
                ? CtiTime::Gmt
                : CtiTime::Local;

        itemList.add("Block End Time") << CtiTime(_lp_data_set1_tbl.lp_data_sets1[index].blk_end_time).asString(displayOffset, CtiTime::OmitTimezone);

        if( _blkEndReadFlag )
        {
            Cti::StreamBufferSink& block_end_read = itemList.add("Block End Reads");
            for (int i = 0; i < _nbrChnsSet1; i++)
            {
                block_end_read << _lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_read <<"  ";
            }
        }

        if( _blkEndPulseFlag )
        {
            Cti::StreamBufferSink& block_end_pulse = itemList.add("Block End Pulse");
            for (int i = 0; i < _nbrChnsSet1; i++)
            {
                block_end_pulse << _lp_data_set1_tbl.lp_data_sets1[index].end_readings[i].block_end_pulse <<"  ";
            }
        }

        if( _closureStatusFlag )
        {
            itemList <<"Closure Status BitField";

            Cti::StreamBufferSink& status = itemList.add("Status");
            for (int i = 0; i < _nbrChnsSet1; i++)
            {
                status << _lp_data_set1_tbl.lp_data_sets1[index].closure_status[i].status <<"  ";
            }

            Cti::StreamBufferSink& nbr_valid_interval = itemList.add("Nbr Valid Interval");
            for (int i = 0; i < _nbrChnsSet1; i++)
            {
                nbr_valid_interval << _lp_data_set1_tbl.lp_data_sets1[index].closure_status[i].nbr_valid_interval <<"  ";
            }
        }

        if( _simpleIntStatusFlag )
        {
            itemList.add("Simple Interval Status") << _lp_data_set1_tbl.lp_data_sets1[index].set_simple_int_status;
        }

        for( int i = startInt; i < nbrBlkInts; i++ )
        {
            itemList <<"BLOCK INTERVAL "<< i+1;

            if( _extendedIntStatusFlag )
            {
                Cti::StreamBufferSink& extended_int_status = itemList.add("Extended Interval Status");
                for (int j = 0; j < (_nbrChnsSet1/2)+1; j++ )
                {
                    extended_int_status << _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].extended_int_status[j] <<".DSTFlag("
                                        << _lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].dayLightSavingsFlag <<")  ";
                }
            }

            Cti::StreamBufferSink& int_data = itemList.add("Interval Data");
            for( int j = 0; j < _nbrChnsSet1; j++ )
            {
                if( const boost::optional<int> intervalFmtRecord = resolveIntervalFmtRecord(_lp_data_set1_tbl.lp_data_sets1[index].lp_int[i].int_data[j]) )
                {
                    int_data << *intervalFmtRecord <<"  ";
                }
            }
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 64") <<
            itemList
            );
}

boost::optional<int> CtiAnsiTable64::resolveIntervalFmtRecord(INT_FMT1_RCD intData)
{
    switch(_intFmtCde1)
    {
        case 1:
        {
            return (int)intData.u.s1.item;
        }
        case 2:
        {
            return (int)intData.u.s2.item;
        }
        case 4:
        {
            return (int)intData.u.s4.item;
        }
        case 8:
        {
            return (int)intData.u.s8.item;
        }
        case 16:
        {
            return (int)intData.u.s16.item;
        }
        case 32:
        {
            return (int)intData.u.s32.item;
        }
        case 64:
        case 128:
        default:
            return boost::none;
    }
}


void CtiAnsiTable64::getBlkIntvlTime(int blkSet, int blkIntvl, ULONG &blkIntvlTime)
{
    ULONG blkEndTime = 0;
    if (_descIntervalOrder)
    {
        blkIntvl = _nbrBlkIntsSet1 - (blkIntvl + 1);
    }

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
            if ((blkSet == (_nbrBlksSet1 -1) && blkIntvl < _nbrValidInts) || (_descIntervalOrder && blkSet == 0))
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

ULONG CtiAnsiTable64::getLPDemandTime (int blkSet, int blkIntvl)
{
    ULONG blkEndTime = 0;

    getBlkIntvlTime(blkSet, blkIntvl, blkEndTime);
    return blkEndTime;
}

double CtiAnsiTable64::getLPDemandValue ( int channel, int blkSet, int blkIntvl )
{
    double retVal = 0;

    switch(_intFmtCde1)
    {
        case 1:
        {
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s1.item;
            break;
        }
        case 2:
        {
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s2.item;
            break;
        }

        case 4:
        {
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s4.item;
            break;
        }

        case 8:
        {
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s8.item;
            break;
        }

        case 16:
        {
            retVal = _lp_data_set1_tbl.lp_data_sets1[blkSet].lp_int[blkIntvl].int_data[channel].u.s16.item;
            break;
        }

        case 32:
        {
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


