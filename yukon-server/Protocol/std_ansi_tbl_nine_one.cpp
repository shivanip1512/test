
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_ninety_one
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_nine_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableNineOne::CtiAnsiTableNineOne(  )
{  
    
}




CtiAnsiTableNineOne::CtiAnsiTableNineOne( BYTE *dataBlob )
{

    memcpy( (void *)&_telephoneTbl, dataBlob, sizeof( TELEPHONE_RCD ));
    dataBlob += sizeof( TELEPHONE_RCD ); 
}

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableNineOne::~CtiAnsiTableNineOne()
{
    
}
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableNineOne& CtiAnsiTableNineOne::operator=(const CtiAnsiTableNineOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableNineOne::generateResultPiece( BYTE **dataBlob )
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableNineOne::decodeResultPiece( BYTE **dataBlob )
{
    
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableNineOne::printResult(  )
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
        dout << endl << "=======================  Std Table 91  ========================" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "  ** Actual Telephone Table ** : "<<endl;
        dout << "                  Answer Flag -"<< (bool) _telephoneTbl.telephoneFlags.answer_flag<<endl;
        dout << "                  S Anchor Date Flag -"<< (bool)_telephoneTbl.telephoneFlags.s_anchor_date_flag <<endl;
        dout << "                  OffHook Detect Flag -"<< (bool) _telephoneTbl.telephoneFlags.offhook_detect_flag<<endl;
        dout << "                  Bit Rate -"<< getBitRate()<<endl;
        dout << "                  ID in Purpose -"<< (bool) _telephoneTbl.telephoneFlags.id_in_purpose<<endl;
        dout << "                  No Lockout Parm  -"<< (bool) _telephoneTbl.telephoneFlags.no_lockout_parm<<endl;
    }

}

int CtiAnsiTableNineOne::getBitRate()
{
    return  (int) _telephoneTbl.telephoneFlags.bit_rate;
}

int CtiAnsiTableNineOne::getNbrSetupStrings()
{
    return  (int) _telephoneTbl.nbr_setup_strings;
}

int CtiAnsiTableNineOne::getSetupStringLength()
{
    return  (int) _telephoneTbl.setup_string_length;
}
