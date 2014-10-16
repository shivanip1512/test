#include "precompiled.h"

#include "ctidate.h"
#include "ctitime.h"
#include "std_ansi_tbl_23.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable23::CtiAnsiTable23( BYTE *dataBlob, int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag, int format1, int format2, int timefmat, int tableNbr,  DataOrder dataOrder )
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
   _dataOrder = dataOrder;

   _tablePrintNumber = tableNbr;


   _totSize = 0;
   //don't forget to make the pointer point!
   //don't forget to use the reset when allocating mem

   //part 1
   if( _reset == true )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_nbr_demand_resets, sizeof( unsigned char ));
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
       bytes = toDoubleParser( dataBlob, data_block->summations[index], _format1, _dataOrder );
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
         bytes = toDoubleParser( dataBlob, data_block->demands[index].cum_demand, _format1, _dataOrder );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cum_demand = 0;

      if( _cumcont == true )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].cont_cum_demand, _format1, _dataOrder );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cont_cum_demand = 0;

      data_block->demands[index].demand = new double[_ocNums];
      for( cnt = 0; cnt < _ocNums; cnt++ )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].demand[cnt], _format2, _dataOrder );
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
          bytes = toDoubleParser( dataBlob, data_block->coincidents[index].coincident_values[cnt], _format2, _dataOrder );
          dataBlob += bytes;
          offset += bytes;
       }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::printResult( const string& deviceName )
{
    Cti::StreamBuffer outLog;

    if( _tablePrintNumber == 23 )
    {
        outLog << endl << formatTableName(deviceName +" Std Table 23");
    }

    Cti::FormattedList itemList;
    appendResult(itemList);
    outLog << itemList;

    CTILOG_INFO(dout, outLog);
}


void CtiAnsiTable23::appendResult( Cti::FormattedList &itemList )
{
    itemList <<"** Register Data Record **";

    if ( _reset == true )
    {
        itemList.add("Number Demand Resets") << _nbr_demand_resets;
    }

    itemList <<"Total Data Block";

    ////////  SUMMATIONS  //////////
    appendSummations(_tot_data_block, itemList);

    ////////  DEMANDS  //////////
    appendDemands(_tot_data_block, itemList);

    ////////  COINCIDENTS  //////////
    appendCoincidents(_tot_data_block, itemList);

    for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++)
    {
        itemList<<"Tier Data Block "<< tierIndex+1;
        appendSummations (_tier_data_block[tierIndex], itemList);
        appendDemands    (_tier_data_block[tierIndex], itemList);
        appendCoincidents(_tier_data_block[tierIndex], itemList);
    }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::appendSummations( DATA_BLK_RCD data_block, Cti::FormattedList &itemList )
{
    itemList <<"  Summations";

    Cti::StreamBufferSink& values = itemList <<"  ";
    for (int index = 0; index < _sumNums; index++ )
    {
        values << data_block.summations[index] <<"  ";
    }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::appendDemands( DATA_BLK_RCD data_block, Cti::FormattedList &itemList )
{
    itemList <<"  Demands";

    for( int index = 0; index < _demandNums; index++ )
    {
        if( _time == true )
        {
            Cti::StreamBufferSink& event_time = itemList.add("    Event Time");
            for( int cnt = 0; cnt < _ocNums; cnt++ )
            {
                event_time << ((data_block.demands[index].event_time[cnt] != 0)
                        ? CtiTime( data_block.demands[index].event_time[cnt])
                        : CtiTime(CtiDate(1,1,1970))) <<"  ";
            }
        }

        if( _cumd == true )
        {
            itemList.add("    Cum Demand") << data_block.demands[index].cum_demand;
        }

        if( _cumcont == true )
        {
            itemList.add("    Cont Cum Demand") << data_block.demands[index].cont_cum_demand;
        }

        Cti::StreamBufferSink& demands = itemList.add("    Peak Demand");
        for( int cnt = 0; cnt < _ocNums; cnt++ )
        {
            demands << data_block.demands[index].demand[cnt] <<"  ";
        }
    }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable23::appendCoincidents( DATA_BLK_RCD data_block, Cti::FormattedList &itemList )
{
    itemList <<"  Coincidents";

    for( int index = 0; index < _coinNums; index++ )
    {
        Cti::StreamBufferSink& values = itemList.add("    Coincident Values");
        for( int cnt = 0; cnt < _ocNums; cnt++ )
        {
            values << data_block.coincidents[index].coincident_values[cnt] <<"  ";
        }
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


