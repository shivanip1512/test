#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_one_six
*
* Date:   9/19/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_one_six.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*    History: 
      $Log: std_ansi_tbl_one_six.cpp,v $
      Revision 1.6  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/09/30 21:37:18  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.4  2004/04/22 21:12:53  dsutton
      Last known revision DLS

      Revision 1.3  2003/04/25 15:09:53  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_one_six.h"

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix::CtiAnsiTableOneSix( int nbr_constants )
{
   _numberOfConstants = nbr_constants;
}

CtiAnsiTableOneSix::CtiAnsiTableOneSix( BYTE *dataBlob, int nbr_constants )
{
   int   index;
   _numberOfConstants = nbr_constants;

   _source_link = new SOURCE_LINK_BFLD[_numberOfConstants];

   for( index = 0; index < _numberOfConstants; index++ )
   {
      memcpy(( void *)&_source_link[index], dataBlob, sizeof( SOURCE_LINK_BFLD ));
      dataBlob += sizeof( SOURCE_LINK_BFLD );
   }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix::~CtiAnsiTableOneSix()
{
   delete []_source_link;
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableOneSix& CtiAnsiTableOneSix::operator=(const CtiAnsiTableOneSix& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
SOURCE_LINK_BFLD CtiAnsiTableOneSix::getSourceLink(int aOffset)
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

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneSix::generateResultPiece( BYTE **dataBlob )
{
    for( int index = 0; index < _numberOfConstants; index++ )
    {
        memcpy( *dataBlob, ( void *)&_source_link[index], sizeof( SOURCE_LINK_BFLD ));
        *dataBlob += sizeof( SOURCE_LINK_BFLD );
    }


}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneSix::decodeResultPiece( BYTE **dataBlob )
{
    _source_link = new SOURCE_LINK_BFLD[_numberOfConstants];

    for( int index = 0; index < _numberOfConstants; index++ )
    {
        memcpy(( void *)&_source_link[index], *dataBlob, sizeof( SOURCE_LINK_BFLD ));
        *dataBlob += sizeof( SOURCE_LINK_BFLD );
    }
}

bool CtiAnsiTableOneSix::getUOMEntryFlag( int index )
{
   return (bool)_source_link[index].uom_entry_flag;
}
bool CtiAnsiTableOneSix::getDemandCtrlFlag( int index )
{
   return (bool)_source_link[index].demand_ctrl_flag;
}
bool CtiAnsiTableOneSix::getDataCtrlFlag( int index )
{
   return (bool)_source_link[index].data_ctrl_flag;
}
bool CtiAnsiTableOneSix::getConstantsFlag( int index )
{
   return (bool)_source_link[index].constants_flag;
}
bool CtiAnsiTableOneSix::getPulseEngrFlag( int index )
{
   return (bool)_source_link[index].pulse_engr_flag;
} 
bool CtiAnsiTableOneSix::getConstToBeAppliedFlag( int index )
{
   return (bool)_source_link[index].constant_to_be_applied;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableOneSix::printResult(  )
{
    int integer;
    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 16 ========================" << endl;
    }
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " **SourceLink** "<<endl;
            dout << "idx uomEntryFlg dmndCtrlFlg dataCtrlFlg constantsFlg pulseEngrFlg constToBeApp"<<endl;
    }
    for( int index = 0; index < _numberOfConstants; index++ )
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout <<index<<"           "<<getUOMEntryFlag( index )<<"           ";
            dout <<getDemandCtrlFlag( index )<<"          "<<getDataCtrlFlag( index );
            dout <<"           "<<getConstantsFlag( index )<<"           ";
            dout <<getPulseEngrFlag( index )<<"           "<<getConstToBeAppliedFlag( index )<<endl;
        }
         
    }

}

