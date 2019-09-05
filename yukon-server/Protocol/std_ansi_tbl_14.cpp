#include "precompiled.h"

#include <boost/lexical_cast.hpp>
#include "logger.h"
#include "std_ansi_tbl_14.h"

using std::string;
using std::endl;
//=========================================================================================================================================
//wow... what a mess this is
//allocate however many data_records we have (from table 11)
//then, for each one, allocate howevermany source id's (again from table 11)
//=========================================================================================================================================

CtiAnsiTable14::CtiAnsiTable14( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries )
{
   int   index;
   int   cnt;

   //we're saving these so we can delete the memory later
   _controlLength = dataCtrlLen;
   _controlEntries = numDataCtrlEntries;

   if( _controlEntries != 0 )
   {
      _data_control_record.data_rcd = new DATA_RCD[_controlEntries];

      for( index = 0; index < _controlEntries; index++ )
      {
         if( _controlLength != 0 )
         {
            _data_control_record.data_rcd[index].source_id = new unsigned char[_controlLength];

            for( cnt = 0; cnt < _controlLength; cnt++ )
            {
               dataBlob += toAnsiIntParser(dataBlob, &_data_control_record.data_rcd[index].source_id[cnt], sizeof( unsigned char ));
            }
         }
      }
   }
}

//=========================================================================================================================================
//the most massive memory management problem ... ick!
//hahaha... I really thought that when I wrote it, then I did table 23!
//=========================================================================================================================================

CtiAnsiTable14::~CtiAnsiTable14()
{
   int index;
   //int count;
   if (_data_control_record.data_rcd != NULL)
   {
       for( index = 0; index < _controlEntries; index++ )
       {
           if (_data_control_record.data_rcd[index].source_id != NULL)
           {
               delete[] _data_control_record.data_rcd[index].source_id;
               _data_control_record.data_rcd[index].source_id = NULL;
           }
       }
       delete[] _data_control_record.data_rcd;
       _data_control_record.data_rcd = NULL;
   }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable14& CtiAnsiTable14::operator=(const CtiAnsiTable14& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable14::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    for( int index = 0; index < _controlEntries; index++ )
    {
        Cti::StreamBufferSink& values = itemList.add("DATA_RCD "+ boost::lexical_cast<string>(index));
        for(int cnt = 0; cnt < _controlLength; cnt++)
        {
            values << _data_control_record.data_rcd[index].source_id[cnt] <<" ";
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 14") <<
            itemList
            );
}

