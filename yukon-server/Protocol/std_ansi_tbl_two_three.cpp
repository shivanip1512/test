
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_three
*
* Date:   9/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_two_three.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/09/30 21:37:18 $
*    History: 
      $Log: std_ansi_tbl_two_three.cpp,v $
      Revision 1.4  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_two_three.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoThree::CtiAnsiTableTwoThree( int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag, int format1, int format2, int timefmat )
{
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

   
    //timefmat;
}

CtiAnsiTableTwoThree::CtiAnsiTableTwoThree( BYTE *dataBlob, int occur, int summations, int demands, int coinValues, int tier, bool reset_flag,
                                            bool time_flag, bool cum_demand_flag, bool cum_cont_flag, int format1, int format2, int timefmat )
{
   int      index;
   int      cnt;
   int      track;
   BYTE     *ptr;
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

   printSummations( _tot_data_block);

   _tot_data_block.demands = new DEMANDS_RCD[_demandNums];
   populateDemandsRecord(dataBlob, &_tot_data_block, offset);
   ptr = dataBlob;
   {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< " DEBUG PTR";
   }
   for (int xy = 0; xy < 20; xy++) 
   {
       {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout << "   "<<(int)*ptr;
       }
       ptr++;
   }
   {
              CtiLockGuard< CtiLogger > doubt_guard( dout );
              dout <<endl<< " END DEBUG PTR";
   }
   dataBlob += offset;
   _totSize += offset;
   printDemands(_tot_data_block);
   
   
   _tot_data_block.coincidents = new COINCIDENTS_RCD[_coinNums];
   populateCoincidentsRecord(dataBlob, &_tot_data_block, offset);
  dataBlob += offset;
   _totSize += offset;
   
   _tier_data_block = new DATA_BLK_RCD[_tierNums];

   for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++) 
   {
       
       _tier_data_block[tierIndex].summations = new double[_sumNums];

       populateSummations(dataBlob, &_tier_data_block[tierIndex], _totSize);
       dataBlob += offset;

           _tier_data_block[tierIndex].demands = new DEMANDS_RCD[_demandNums];
           _tier_data_block[tierIndex].coincidents = new COINCIDENTS_RCD[_coinNums];

           //summations are not variable

           //demands record
           populateDemandsRecord(dataBlob, &_tier_data_block[tierIndex], _totSize);
           dataBlob += offset;
             
           populateCoincidentsRecord(dataBlob, &_tier_data_block[tierIndex], _totSize);
           dataBlob += offset;
   }      
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableTwoThree::~CtiAnsiTableTwoThree()
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

CtiAnsiTableTwoThree& CtiAnsiTableTwoThree::operator=(const CtiAnsiTableTwoThree& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

DATA_BLK_RCD CtiAnsiTableTwoThree::getTotDataBlock( void )
{
   return _tot_data_block;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::copyTotDataBlock( BYTE *ptr )
{
   memcpy( ptr, &_tot_data_block, _totSize );

   return _totSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getOccurs( void )
{
   return _ocNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getSums( void )
{
   return _sumNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getDemands( void )
{
   return _demandNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getCoins( void )
{
   return _coinNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getTier( void )
{
   return _tierNums;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getReset( void )
{
   return _reset;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getTime( void )
{
   return _time;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getCumd( void )
{
   return _cumd;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTableTwoThree::getCumcont( void )
{
   return _cumcont;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTableTwoThree::getTotSize( void )
{
   return _totSize;
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::generateResultPiece( BYTE **dataBlob )
{
    int dummy = 0;

     //part 1
    if( _reset == true )
    {
       memcpy(  *dataBlob, (void *)&_nbr_demand_resets, sizeof( unsigned char ));
       *dataBlob += sizeof( unsigned char);
       dummy += 1;
    }
    //part 2
    retrieveSummations(*dataBlob, _tot_data_block, dummy);
    *dataBlob += dummy;

    retrieveDemandsRecord(*dataBlob, _tot_data_block, dummy);
    *dataBlob += dummy;

    retrieveCoincidentsRecord(*dataBlob, _tot_data_block, dummy);
    *dataBlob += dummy;
    
    for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++) 
    {
        retrieveSummations(*dataBlob, _tier_data_block[tierIndex], dummy);
        *dataBlob += dummy;

        retrieveDemandsRecord(*dataBlob, _tier_data_block[tierIndex], dummy);
        *dataBlob += dummy;

        retrieveCoincidentsRecord(*dataBlob, _tier_data_block[tierIndex], dummy);
        *dataBlob += dummy;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::decodeResultPiece( BYTE **dataBlob )
{
   int dummy = 0;

    //part 1
   if( _reset == true )
   {
      memcpy( (void *)&_nbr_demand_resets, *dataBlob, sizeof( unsigned char ));
      *dataBlob += sizeof( unsigned char);
      dummy += 1;
   }
   else
      _nbr_demand_resets = NULL;

   //part 2
   _tot_data_block.summations = new double[_sumNums];
   populateSummations(*dataBlob, &_tot_data_block, dummy);
   *dataBlob += dummy;


   _tot_data_block.demands = new DEMANDS_RCD[_demandNums];
   _tot_data_block.coincidents = new COINCIDENTS_RCD[_coinNums];

   populateDemandsRecord(*dataBlob, &_tot_data_block, dummy);
   *dataBlob += dummy;

   populateCoincidentsRecord(*dataBlob, &_tot_data_block, dummy);
   *dataBlob += dummy;

   _tier_data_block = new DATA_BLK_RCD[_tierNums];
   for (int tierIndex = 0; tierIndex < _tierNums; tierIndex++) 
   {
       _tier_data_block[tierIndex].summations = new double[_sumNums];
       populateSummations(*dataBlob, &_tier_data_block[tierIndex], dummy);
       *dataBlob += dummy;

       _tier_data_block[tierIndex].demands = new DEMANDS_RCD[_demandNums];
       _tier_data_block[tierIndex].coincidents = new COINCIDENTS_RCD[_coinNums];
       populateDemandsRecord(*dataBlob, &_tier_data_block[tierIndex], dummy);
       *dataBlob += dummy;

       populateCoincidentsRecord(*dataBlob, &_tier_data_block[tierIndex], dummy);
       *dataBlob += dummy;
   }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::populateSummations( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
{
    int index, bytes;
    offset = 0;
    
    for( index = 0; index < _sumNums; index++ )
    {
       bytes = toDoubleParser( dataBlob, data_block->summations[index], _format1 );
       dataBlob += bytes;
       offset += bytes;
    }
    
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::populateDemandsRecord( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
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
         //_tot_data_block.demands[index].cum_demand = new double;

         bytes = toDoubleParser( dataBlob, data_block->demands[index].cum_demand, _format1 );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cum_demand = 0;
      
      if( _cumcont == true )
      {
         //_tot_data_block.demands[index].cont_cum_demand = new double;

         bytes = toDoubleParser( dataBlob, data_block->demands[index].cont_cum_demand, _format1 );
         dataBlob += bytes;
         offset += bytes;
      }
      else
         data_block->demands[index].cont_cum_demand = 0;

      data_block->demands[index].demand = new double[_ocNums];
      for( cnt = 0; cnt < _ocNums; cnt++ )
      {
         bytes = toDoubleParser( dataBlob, data_block->demands[index].demand[cnt], _format2 );
         dataBlob += bytes;
         offset += bytes;
      }   
   }    

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::populateCoincidentsRecord( BYTE *dataBlob, DATA_BLK_RCD *data_block, int &offset )
{
    int index, cnt, bytes;
    offset = 0;

    for( index = 0; index < _coinNums; index++ )
    {
       data_block->coincidents[index].coincident_values = new double[_ocNums];

       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
          bytes = toDoubleParser( dataBlob, data_block->coincidents[index].coincident_values[cnt], _format2 );
          dataBlob += bytes;
          offset += bytes;
       }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::retrieveSummations( BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset )
{
    int index, bytes;
    offset = 0;
    
    for( index = 0; index < _sumNums; index++ )
    {
       bytes = fromDoubleParser( data_block.summations[index], dataBlob, _format1 );
       dataBlob += bytes;
       offset += bytes;
    }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::retrieveDemandsRecord( BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset )
{
    int index, cnt, bytes;
    offset = 0;

    for( index = 0; index < _demandNums; index++ )
    {
       if( _time == true )
       {
          for( cnt = 0; cnt < _ocNums; cnt++ )
          {
            // bytes = toUint32STime( dataBlob, data_block.demands[index].event_time[cnt], _timefmt );
             //dataBlob += bytes;
            // offset += bytes;
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << "  *****JULIE **** Uh Oh...  " << endl;
              }
          }
       }
       
       if( _cumd == true )
       {
          //_tot_data_block.demands[index].cum_demand = new double;

          bytes = fromDoubleParser( data_block.demands[index].cum_demand, dataBlob, _format1 );
          dataBlob += bytes;
          offset += bytes;
       }
       
       if( _cumcont == true )
       {
          //_tot_data_block.demands[index].cont_cum_demand = new double;

          bytes = fromDoubleParser( data_block.demands[index].cont_cum_demand, dataBlob, _format1 );
          dataBlob += bytes;
          offset += bytes;
       }
       
       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
          bytes = fromDoubleParser( data_block.demands[index].demand[cnt], dataBlob, _format2 );
          dataBlob += bytes;
          offset += bytes;
       }
    }

}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::retrieveCoincidentsRecord( BYTE *dataBlob, DATA_BLK_RCD data_block, int &offset )
{
    int index, cnt, bytes;
    offset = 0;

    for( index = 0; index < _coinNums; index++ )
    {
       for( cnt = 0; cnt < _ocNums; cnt++ )
       {
          bytes = fromDoubleParser( data_block.coincidents[index].coincident_values[cnt], dataBlob, _format2 );
          dataBlob += bytes;
          offset += bytes;
       }
    }
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableTwoThree::printResult(  )
{
    int index, cnt;
    RWCString string1,string2;
    double double1;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 23 ========================" << endl;
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
void CtiAnsiTableTwoThree::printSummations( DATA_BLK_RCD data_block )
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
void CtiAnsiTableTwoThree::printDemands( DATA_BLK_RCD data_block )
{
    int index, cnt;
    RWCString timeString;
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
             timeString = RWTime( _tot_data_block.demands[index].event_time[cnt]).asString();
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
              dout <<endl<< "          Demand: " ;
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
void CtiAnsiTableTwoThree::printCoincidents( DATA_BLK_RCD data_block )
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


double CtiAnsiTableTwoThree::getDemandValue ( int index )
{
    return _tot_data_block.demands->demand[index];
}
double CtiAnsiTableTwoThree::getSummationsValue ( int index )
{
    return _tot_data_block.summations[index];
}


