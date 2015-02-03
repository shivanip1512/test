#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_16.h"

using std::endl;
using std::string;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16::CtiAnsiTable16( BYTE *dataBlob, int nbr_constants )
{
   int   index;
   _numberOfConstants = nbr_constants;

   _source_link = new SOURCE_LINK_BFLD[_numberOfConstants];

   for( index = 0; index < _numberOfConstants; index++ )
   {
      dataBlob += toAnsiIntParser(dataBlob, &_source_link[index], sizeof( SOURCE_LINK_BFLD ));
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16::~CtiAnsiTable16()
{
   delete []_source_link;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable16& CtiAnsiTable16::operator=(const CtiAnsiTable16& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
SOURCE_LINK_BFLD CtiAnsiTable16::getSourceLink(int aOffset)
{
    SOURCE_LINK_BFLD ret;

    ret.constant_to_be_applied =0;
    ret.constants_flag = 0;
    ret.data_ctrl_flag =0;
    ret.demand_ctrl_flag=0;
    ret.filler=0;
    ret.pulse_engr_flag=0;
    ret.uom_entry_flag=0;

    if (aOffset < _numberOfConstants)
    {
        ret = _source_link[aOffset];
    }

    return (ret);
}

bool CtiAnsiTable16::getUOMEntryFlag( int index )
{
   return (bool)_source_link[index].uom_entry_flag;
}
bool CtiAnsiTable16::getDemandCtrlFlag( int index )
{
   return (bool)_source_link[index].demand_ctrl_flag;
}
bool CtiAnsiTable16::getDataCtrlFlag( int index )
{
   return (bool)_source_link[index].data_ctrl_flag;
}
bool CtiAnsiTable16::getConstantsFlag( int index )
{
   return (bool)_source_link[index].constants_flag;
}
bool CtiAnsiTable16::getPulseEngrFlag( int index )
{
   return (bool)_source_link[index].pulse_engr_flag;
}
bool CtiAnsiTable16::getConstToBeAppliedFlag( int index )
{
   return (bool)_source_link[index].constant_to_be_applied;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable16::printResult( const string& deviceName )
{
    Cti::FormattedTable table;

    table.setCell(0, 0) << "idx";
    table.setCell(0, 1) << "uomEntryFlg";
    table.setCell(0, 2) << "dmndCtrlFlg";
    table.setCell(0, 3) << "dataCtrlFlg";
    table.setCell(0, 4) << "constantsFlg";
    table.setCell(0, 5) << "pulseEngrFlg";
    table.setCell(0, 6) << "constToBeApp";

    for( int index = 0; index < _numberOfConstants; index++ )
    {
        const unsigned row = index + 1;

        table.setCell(row, 0) << index;
        table.setCell(row, 1) << getUOMEntryFlag(index);
        table.setCell(row, 2) << getDemandCtrlFlag(index);
        table.setCell(row, 3) << getDataCtrlFlag(index);
        table.setCell(row, 4) << getConstantsFlag(index);
        table.setCell(row, 5) << getPulseEngrFlag(index);
        table.setCell(row, 6) << getConstToBeAppliedFlag(index);
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 16") <<
            endl <<"** SourceLink **"<<
            table
            );
}

