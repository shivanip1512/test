/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_23
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_23.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_two_three.cpp,v $
      Revision 1.13  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.12  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.11  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.10  2005/09/29 21:18:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.9  2005/06/16 19:17:59  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.8  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.7  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.6  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.5  2004/12/10 21:58:41  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "ctidate.h"
#include "std_ansi_tbl_23.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable23::CtiAnsiTable23( BYTE *dataBlob, int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag, int format1, int format2, int timefmat, int tableNbr,  bool lsbDataOrder )
{
   int      index;
   int      cnt;
   int      track;
   int      bytes = 0;
   int      offset = 0;

   //keep our numbers for later
   _ocNums = occur;
   _sumNums = summations;
   _demandNums = demands;
   _coinNums = coinValues;
   _tierNums = tier;
   _reset = reset_flag;
   _time = time_flag;
   _cumd = cum_demand_flag;
   _cumcont = cum_cont_flag;
   _format1 = format1;
   _format2 = format2;
   _timefmt = timefmat;
   _lsbDataOrder = lsbDataOrder;

   _tablePrintNumber = tableNbr;


   _totSize = 0;
   //don't forget to make the pointer point!
   //don't forget to use the reset when allocating mem

   //part 1
   if( _reset == true )
   {
      memcpy( (void *)&_nbr_demand_resets, dataBlob, sizeof( unsigned char ));
      dataBlob += sizeof( unsigned char);
      _totSize += 1;
   }
   else
      _nbr_demand_resets = 0;

   //part 2
   _tot_data_block.summations = new double[_sumNums];
   populateSummations(dataBlob, &_tot_data_block, offset);
   dataBlob += offset;
   _totSize += offset;

   _tot_data_block.demands = new DEMANDS_RCD[_demandNums];
   populateDemandsRecord(dataBlob, &_tot_data_block, offset);
   dataBlob += offset;
   _totSize += offset;


   _tot_data_block.coincidents = new COINCIDENTS_RCD[_coinNums];
   populateCoincidentsRecord(dataBlob, &_tot_data_block, offset);
   dataBlob += offset;
   _totSize += offset;

   _tier_data_block = new DATA_BLK_RCD[_tierNums];

   for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++)
   {

       _tier_data_block[tierIndex].summations = new double[_sumNums];

       populateSummations(dataBlob, &_tier_data_block[tierIndex], offset);
       dataBlob += offset;
       _totSize += offset;

           _tier_data_block[tierIndex].demands = new DEMANDS_RCD[_demandNums];
           _tier_data_block[tierIndex].coincidents = new COINCIDENTS_RCD[_coinNums];

           //summations are not variable

           //demands record
           populateDemandsRecord(dataBlob, &_tier_data_block[tierIndex], offset);
           dataBlob += offset;
           _totSize += offset;


           populateCoincidentsRecord(dataBlob, &_tier_data_block[tierIndex], offset);
           dataBlob += offset;
           _totSize += offset;

   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable23::~CtiAnsiTable23()
{
   int index;
   //if( getReset() == true )
   //   delete _nbr_demand_resets;

   //part 2
   if (_tot_data_block.summations != NULL)
   {
       delete []_tot_data_block.summations;
       _tot_data_block.summations = NULL;
   }
   //demands record
   if (_tot_data_block.demands != NULL)
   {
       for( index = 0; index < getDemands(); index++ )
       {
          if( _tot_data_block.demands[index].event_time != NULL )
          {
              delete []_tot_data_block.demands[index].event_time;
              _tot_data_block.demands[index].event_time = NULL;
          }
          if (_tot_data_block.demands[index].demand != NULL)
          {
              delete []_tot_data_block.demands[index].demand;
              _tot_data_block.demands[index].demand = NULL;
          }
       }
       delete []_tot_data_block.demands;
       _tot_data_block.demands = NULL;
   }

   if (_tot_data_block.coincidents != NULL)
   {
       for( index = 0; index < getCoins(); index++ )
       {
           if (_tot_data_block.coincidents[index].coincident_values != NULL)
           {
               delete []_tot_data_block.coincidents[index].coincident_values;
               _tot_data_block.coincidents[index].coincident_values = NULL;
           }
       }
      delete []_tot_data_block.coincidents;
       _tot_data_block.coincidents = NULL;
   }
      //part 3
   if (_tier_data_block != NULL)
   {
       for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++)
       {
           if (_tier_data_block[tierIndex].summations != NULL)
           {
               delete []_tier_data_block[tierIndex].summations;
               _tier_data_block[tierIndex].summations = NULL;
           }
           if (_tier_data_block[tierIndex].demands != NULL)
           {
               for( index = 0; index < getDemands(); index++ )
               {
                  if( _tier_data_block[tierIndex].demands[index].event_time != NULL )
                  {
                      delete []_tier_data_block[tierIndex].demands[index].event_time;
                      _tier_data_block[tierIndex].demands[index].event_time = NULL;
                  }

                  if (_tier_data_block[tierIndex].demands[index].demand != NULL)
                  {
                      delete []_tier_data_block[tierIndex].demands[index].demand;
                      _tier_data_block[tierIndex].demands[index].demand = NULL;
                  }

               }
               delete []_tier_data_block[tierIndex].demands;
               _tier_data_block[tierIndex].demands = NULL;
           }

           if (_tier_data_block[tierIndex].coincidents != NULL)
           {
               for( index = 0; index < getCoins(); index++ )
               {
                   if (_tier_data_block[tierIndex].coincidents[index].coincident_values != NULL)
                   {
                       delete []_tier_data_block[tierIndex].coincidents[index].coincident_values;
                       _tier_data_block[tierIndex].coincidents[index].coincident_values = NULL;
                   }

               }
               delete []_tier_data_block[tierIndex].coincidents;
               _tier_data_block[tierIndex].coincidents = NULL;
           }

       }

       delete []_tier_data_block;
       _tier_data_block = NULL;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable23& CtiAnsiTable23::operator=(const CtiAnsiTable23& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

DATA_BLK_RCD CtiAnsiTable23::getTotDataBlock( void )
{
   return _tot_data_block;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::copyTotDataBlock( BYTE *ptr )
{
   memcpy( ptr, &_tot_data_block, _totSize );

   return _totSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getOccurs( void )
{
   return _ocNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getSums( void )
{
   return _sumNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getDemands( void )
{
   return _demandNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getCoins( void )
{
   return _coinNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getTier( void )
{
   return _tierNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable23::getReset( void )
{
   return _reset;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable23::getTime( void )
{
   return _time;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable23::getCumd( void )
{
   return _cumd;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable23::getCumcont( void )
{
   return _cumcont;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable23::getTotSize( void )
{
   return _totSize;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::populateSummations( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
{
    int index, bytes;
    offset = 0;

    for( index = 0; index < _sumNums; index++ )
    {
       bytes = toDoubleParser( dataBlob, data_block->summations[index], _format1, _lsbDataOrder );
       dataBlob += bytes;
       offset += bytes;
    }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::populateDemandsRecord( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
{
    int index, cnt, bytes;
    offset = 0;

    for( index = 0; index < _demandNums; index++ )
    {
      if( _time == true )
      {
         data_block->demands[index].event_time = new ULONG[_ocNums];

         for( cnt = 0; cnt < _ocNums; cnt++ )
         {
            bytes = toUint32STime( dataBlob, data_block->demands[index].event_time[cnt], _timefmt );
            dataBlob += bytes;
            offset += bytes;
         }
      }
      else
         data_block->demands[index].event_time = 0;

      if( _cumd == true )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].cum_demand, _format1, _lsbDataOrder );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cum_demand = 0;

      if( _cumcont == true )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].cont_cum_demand, _format1, _lsbDataOrder );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cont_cum_demand = 0;

      data_block->demands[index].demand = new double[_ocNums];
      for( cnt = 0; cnt < _ocNums; cnt++ )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].demand[cnt], _format2, _lsbDataOrder );
         dataBlob += bytes;
         offset += bytes;
      }
   }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::populateCoincidentsRecord( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
{
    int index, cnt, bytes;
    offset = 0;

    for( index = 0; index < _coinNums; index++ )
    {
       data_block->coincidents[index].coincident_values = new double[_ocNums];

       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
          bytes = toDoubleParser( dataBlob, data_block->coincidents[index].coincident_values[cnt], _format2, _lsbDataOrder );
          dataBlob += bytes;
          offset += bytes;
       }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::printResult( const string& deviceName )
{
    int index, cnt;
    string string1,string2;
    double double1;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    if (_tablePrintNumber == 23)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "==================== "<<deviceName<<"  Std Table 23 ========================" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "**Register Data Record**   " ;
    }
    if ( _reset == true )
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "    Number Demand Resets : " <<(int)_nbr_demand_resets;
    }
    ////////  SUMMATIONS  //////////
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl<< "    Total Data Block : " << endl;
    //    dout << "       ***Summations: " ;
    }
    printSummations(_tot_data_block);
    ////////  DEMANDS  //////////
    printDemands(_tot_data_block);
    ////////  COINCIDENTS  //////////
    printCoincidents(_tot_data_block);
    for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++)
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "    Tier Data Block "<<tierIndex +1<<" : " << endl;
        }
        printSummations(_tier_data_block[tierIndex]);
        printDemands(_tier_data_block[tierIndex]);
        printCoincidents( _tier_data_block[tierIndex] );
    }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::printSummations( DATA_BLK_RCD data_block )
{
    int index;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        //dout <<endl<< "    Total Data Block : " << endl;
        dout << "       ***Summations: " ;
    }
    for (index = 0; index < _sumNums; index++ )
    {
       CtiLockGuard< CtiLogger > doubt_guard( dout );
       dout <<"  "<<data_block.summations[index];
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout <<endl;
    }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::printDemands( DATA_BLK_RCD data_block )
{
    int index, cnt;
    string timeString;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "       ***Demands: " ;
    }
    for( index = 0; index < _demandNums; index++ )
    {
       if( _time == true )
       {
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< "          Event Time: " ;
          }
          for( cnt = 0; cnt < _ocNums; cnt++ )
          {
              if (data_block.demands[index].event_time[cnt] != 0)
              {
                  timeString = CtiTime( data_block.demands[index].event_time[cnt]).asString();
              }
              else
                  timeString = CtiTime(CtiDate(1,1,1970)).asString();
              {
                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                   dout << "  "<<timeString;
              }
          }
       }

       if( _cumd == true )
       {
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< "          Cum Demand: " << data_block.demands[index].cum_demand;
          }
       }

       if( _cumcont == true )
       {
          {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< "          Cont Cum Demand: " << data_block.demands[index].cont_cum_demand;
          }
       }
       {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< "          Peak Demand: " ;
       }
       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "  " << data_block.demands[index].demand[cnt];
           }
       }
   }
   {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl;
   }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::printCoincidents( DATA_BLK_RCD data_block )
{
    int index, cnt;

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "       ***Coincidents: " ;
    }
    for( index = 0; index < _coinNums; index++ )
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout <<endl<< "           Coincident Values: " ;
        }
       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
           {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "  " <<data_block.coincidents[index].coincident_values[cnt];
           }
        }
    }
    {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl;
    }

}


double CtiAnsiTable23::getDemandValue ( int index, int dataBlock )
{
    switch (dataBlock)
    {
        case 1:
        case 2:
        case 3:
        case 4:
        {
            if (_tier_data_block[dataBlock -1].demands[index].demand != NULL)
            {
                return  *(_tier_data_block[dataBlock -1].demands[index].demand);
            }
            break;
        }
        case 0:
        default:
        {
            if (_tot_data_block.demands[index].demand != NULL)
            {
                return  *(_tot_data_block.demands[index].demand);
            }
            break;
        }
    }
    return *(_tot_data_block.demands[0].demand);
}
double CtiAnsiTable23::getSummationsValue ( int index, int dataBlock )
{

    switch (dataBlock)
    {
        case 0:
        {
            return  _tot_data_block.summations[index];
            break;
        }
        case 1:
        case 2:
        case 3:
        case 4:
        {
            return  _tier_data_block[dataBlock -1].summations[index];
            break;
        }
        default:
            return  _tot_data_block.summations[index];
            break;
    }

    return _tot_data_block.summations[index];
}

double CtiAnsiTable23::getDemandEventTime( int index, int dataBlock )
{
    if( _time == true )
    {
        switch (dataBlock)
        {
            case 0:
            {
                return (double)_tot_data_block.demands[index].event_time[0];
                break;
            }
            case 1:
            case 2:
            case 3:
            case 4:
            {
                return  (double)_tier_data_block[dataBlock -1].demands[index].event_time[0];
                break;
            }
        default:
            return (double)_tot_data_block.demands[index].event_time[0];
            break;

        }
    }
    return CtiTime().seconds();
}


