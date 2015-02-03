#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_15.h"

using std::endl;
using std::string;
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15::CtiAnsiTable15( BYTE *dataBlob, int selector, int constants_entries, bool noOffset, bool useSet1, bool useSet2,
                                          int format1, int format2, DataOrder dataOrder)
{
   int      index;
   int      bytes;

    _RawConstantsSelector = selector;
    _NumberConstantsEntries = constants_entries;
    _NoOffsetFlag = noOffset;
    _SetOnePresentFlag = useSet1;
    _SetTwoPresentFlag = useSet2;
    _NIFormat1 = format1;
    _NIFormat2 = format2;
    _dataOrder = dataOrder;

   _constants_table = new CONSTANTS_SELECT[_NumberConstantsEntries];

   switch( _RawConstantsSelector )
   {
   case 0:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 1:
      {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
      }
      break;

   case 2:
      {
         for( index = 0; index < _NumberConstantsEntries; index++ )
         {
            //if( _constants_table != NULL )
           // {
               bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.multiplier, _NIFormat1, _dataOrder );
               dataBlob += bytes;

               if( !_NoOffsetFlag )
               {
                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.offset, _NIFormat1, _dataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.offset = 0;
               }

               if( _SetOnePresentFlag )
               {
                  dataBlob += toAnsiIntParser(dataBlob, &_constants_table[index].electric_constants.set1_constants.set_flags, sizeof( unsigned char ));

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_f1, _NIFormat1, _dataOrder );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set1_constants.ratio_p1, _NIFormat1, _dataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set1_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set1_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set1_constants.set_flags.filler = 0;
               }

               if( _SetTwoPresentFlag )
               {
                  dataBlob += toAnsiIntParser(dataBlob, &_constants_table[index].electric_constants.set2_constants.set_flags, sizeof( unsigned char ));

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_f1, _NIFormat1, _dataOrder );
                  dataBlob += bytes;

                  bytes = toDoubleParser( dataBlob, _constants_table[index].electric_constants.set2_constants.ratio_p1, _NIFormat1, _dataOrder );
                  dataBlob += bytes;
               }
               else
               {
                  _constants_table[index].electric_constants.set2_constants.ratio_f1 = 0;
                  _constants_table[index].electric_constants.set2_constants.ratio_p1 = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag = 0;
                  _constants_table[index].electric_constants.set2_constants.set_flags.filler = 0;
               }
            //}
         }
      }
      break;

   default:
      {
         //3-255 are reserved
      }
      break;
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15::~CtiAnsiTable15()
{
    if (_constants_table != NULL)
    {
        delete[] _constants_table;
        _constants_table = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable15& CtiAnsiTable15::operator=(const CtiAnsiTable15& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getElecMultiplier( int index )
{
   return _constants_table[index].electric_constants.multiplier;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getElecOffset( int index )
{
   return _constants_table[index].electric_constants.offset;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTable15::getElecSetOneConstants( void )
{
   return _constants_table->electric_constants.set1_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

SET_APPLIED CtiAnsiTable15::getElecSetTwoConstants( void )
{
   return _constants_table->electric_constants.set2_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable15::getUseControl( void )
{
   if( 1 )
      return _constants_table->electric_constants.set1_constants.set_flags.set_applied_flag;
   else
      return _constants_table->electric_constants.set2_constants.set_flags.set_applied_flag;
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getRatioF1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable15::getRatioP1( void )
{
   return(1);
}

//=========================================================================================================================================
//=========================================================================================================================================

ELECTRIC_CONSTANTS CtiAnsiTable15::getElecConstants( void )
{
   return _constants_table->electric_constants;
}

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiAnsiTable15::getSet1AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set1_constants.set_flags.set_applied_flag);
}
bool CtiAnsiTable15::getSet2AppliedFlag(int index )
{
   return ((bool)_constants_table[index].electric_constants.set2_constants.set_flags.set_applied_flag);
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable15::printResult( const string& deviceName )
{
    Cti::StreamBuffer outLog;

    outLog << endl << formatTableName(deviceName +" Std Table 15");

    switch( _RawConstantsSelector )
    {
    case 0:
        {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
        }
        break;

    case 1:
        {
//         memcpy( _constants_table, dataBlob, sizeof( _constants_table ));
        }
        break;

    case 2:
        {
            Cti::FormattedTable table;

            unsigned column = 0;
            table.setCell(0, column++) << "ELTRC CONSTS RCD";
            table.setCell(0, column++) << "Multiplier";
            table.setCell(0, column++) << "Offset";

            if( _SetOnePresentFlag )
            {
                table.setCell(0, column++) << "set1_applied_flag";
                table.setCell(0, column++) << "ratio1_f1";
                table.setCell(0, column++) << "ratio1_p1";
            }

            if( _SetTwoPresentFlag )
            {
                table.setCell(0, column++) << "set2_applied_flag";
                table.setCell(0, column++) << "ratio2_f1";
                table.setCell(0, column++) << "ratio2_p1";
            }

            for( int index = 0; index < _NumberConstantsEntries; index++ )
            {
                column = 0;
                const unsigned row=index+1;

                table.setCell(row, column++) << index;
                table.setCell(row, column++) << _constants_table[index].electric_constants.multiplier;

                if( _NoOffsetFlag )
                {
                    table.setCell(row, column++) << "no offset";
                }
                else
                {
                    table.setCell(row, column++) << _constants_table[index].electric_constants.offset;
                }

                if( _SetOnePresentFlag )
                {
                    table.setCell(row, column++) << getSet1AppliedFlag(index);
                    table.setCell(row, column++) << _constants_table[index].electric_constants.set1_constants.ratio_f1;
                    table.setCell(row, column++) << _constants_table[index].electric_constants.set1_constants.ratio_p1;
                }

                if( _SetTwoPresentFlag )
                {
                    table.setCell(row, column++) << getSet2AppliedFlag(index);
                    table.setCell(row, column++) << _constants_table[index].electric_constants.set2_constants.ratio_f1;
                    table.setCell(row, column++) << _constants_table[index].electric_constants.set2_constants.ratio_p1;
                }
            }

            outLog << table;
        }
        break;

    default:
        {
         //3-255 are reserved
        }
    }

    CTILOG_INFO(dout, outLog);
}

