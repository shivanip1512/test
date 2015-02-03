#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_22.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22::CtiAnsiTable22( BYTE *dataBlob, int num_sums, int num_demands, int num_coins )
{
    int index;

    _numSums = num_sums;
    _numDemands = num_demands;
    _numCoins = num_coins;
    _totalTableSize = 0;

    _summation_select = new unsigned char[_numSums];
    _totalTableSize += sizeof( unsigned char ) * _numSums;

   for( index = 0; index < _numSums; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_summation_select[index], sizeof( unsigned char ));
      _totalTableSize += sizeof( unsigned char );
   }

   _demand_select = new unsigned char[_numDemands];
   _totalTableSize += sizeof( unsigned char ) * _numDemands;

   for( index = 0; index < _numDemands; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_demand_select[index], sizeof( unsigned char ));
      _totalTableSize += sizeof( unsigned char );
   }

   _demandSelectSize = sizeof( unsigned char ) * _numDemands;

   _set = new unsigned char[( _numDemands +7)/8];
   memcpy ( _set, dataBlob, (( _numDemands +7)/8));
   dataBlob += (( _numDemands +7)/8);
   _totalTableSize += ((_numDemands +7)/8);

   _coincident_select = new unsigned char[_numCoins];
   _totalTableSize += sizeof( unsigned char ) * _numCoins;

   for( index = 0; index < _numCoins; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_coincident_select[index], sizeof( unsigned char ));
      _totalTableSize += sizeof( unsigned char );
   }

   _coin_demand_assoc = new unsigned char[_numCoins];
   _totalTableSize += sizeof( unsigned char ) * _numCoins;

   for( index = 0; index < _numCoins; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_coin_demand_assoc[index], sizeof( unsigned char ));
      _totalTableSize += sizeof( unsigned char );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22::~CtiAnsiTable22()
{
    if (_summation_select != NULL)
    {
        delete []_summation_select;
        _summation_select = NULL;
    }
    if (_demand_select != NULL)
    {
        delete []_demand_select;
        _demand_select = NULL;
    }
    if (_set != NULL)
    {
        delete []_set;
        _set = NULL;
    }
    if (_coincident_select != NULL)
    {
        delete []_coincident_select;
        _coincident_select = NULL;
    }
    if (_coin_demand_assoc)
    {
        delete []_coin_demand_assoc;
        _coin_demand_assoc = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable22& CtiAnsiTable22::operator=(const CtiAnsiTable22& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::copyDemandSelect( BYTE *ptr )
{
   memcpy( ptr, _demand_select, _demandSelectSize );
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::getDemandSelectSize( void )
{
   return _demandSelectSize;
}

//=========================================================================================================================================
//=========================================================================================================================================

unsigned char* CtiAnsiTable22::getDemandSelect( void )
{
   return _demand_select;
}

//=========================================================================================================================================
//=========================================================================================================================================
unsigned char* CtiAnsiTable22::getSummationSelect( void )
{
   return _summation_select;
}


//=========================================================================================================================================
//=========================================================================================================================================

int CtiAnsiTable22::getTotalTableSize( void )
{
   return _totalTableSize;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable22::printResult( const string& deviceName )
{
    Cti::StreamBuffer outLog;

    outLog << endl << formatTableName(deviceName +" Std Table 22");

    outLog << endl <<"** Summation_Select **"<< endl;
    for( int index = 0; index < _numSums; index++ )
    {
        outLog << _summation_select[index] <<" ";
    }

    outLog << endl <<"** Demand_Select **"<< endl;
    for( int index = 0; index < _numDemands; index++ )
    {
        outLog << _demand_select[index] <<" ";
    }

    outLog << endl <<"** Min or Max Flags **"<< endl;
    for( int index = 0; index < ((_numDemands + 7) / 8); index++ )
    {
        outLog << _set[index] <<" ";
    }

    outLog << endl <<"** Coincident_Select **"<< endl;
    for( int index = 0; index < _numCoins; index++ )
    {
        outLog << _coincident_select[index] <<" ";
    }

    outLog << endl <<"** Coin_Demand_Assoc **"<< endl;
    for( int index = 0; index < _numCoins; index++ )
    {
        outLog << _coin_demand_assoc[index] <<" ";
    }

    CTILOG_INFO(dout, outLog);
}
