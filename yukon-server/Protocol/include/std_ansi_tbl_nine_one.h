


#pragma warning( disable : 4786)
#ifndef __STD_ANSI_TBL_NINE_ONE_H__
#define __STD_ANSI_TBL_NINE_ONE_H__

/*---------------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_nine_one
*
* Class:
* Date:   10/24/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/std_ansi_tbl_nine_one.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/03/14 21:45:15 $
*    History: 
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/


#include "dlldefs.h"
#include "dsm2.h"
#include "ctitypes.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define INT16             __int16
#define INT8               __int8
#define UINT16             __int16
#define UINT8               __int8

#pragma pack( push, 1)

struct TELEPHONE_FLAGS_BFLD
{
    unsigned char answer_flag:1;
    unsigned char s_anchor_data_flag:1;
    unsigned char offhook_detect_flag:1;
    unsigned char bit_rate:2;
    unsigned char id_in_purpose:1;
    unsigned char no_lockout_parm:1;
    unsigned char filler:1;
};
struct TELEPHONE_RCD
{
    TELEPHONE_FLAGS_BFLD  telephoneFlags;
    UINT8 nbr_originate_windows;
    UINT8 nbr_setup_strings;
    UINT8 setup_string_length;
    UINT8 prefix_length;
    UINT8 nbr_originate_numbers;
    UINT8 phone_number_length;
    UINT8 nbr_recurring_dates;
    UINT8 nbr_non_recurring_dates;
    UINT8 nbr_events;
    UINT8 nbr_weekly_schedules;
    UINT8 nbr_answer_windows;
    UINT8 nbr_caller_ids;
    UINT8 caller_id_length;
};


#pragma pack( pop )

class IM_EX_PROT CtiAnsiTableNineOne : public CtiAnsiTableBase
{
protected:

   TELEPHONE_RCD      _telephoneTbl;

private:


public:

   CtiAnsiTableNineOne(  );
   CtiAnsiTableNineOne( BYTE *dataBlob );
   
   virtual ~CtiAnsiTableNineOne();

   CtiAnsiTableNineOne& operator=(const CtiAnsiTableNineOne& aRef);
   void generateResultPiece( BYTE **dataBlob );
   void printResult();
   void decodeResultPiece( BYTE **dataBlob );

   int getBitRate();
   int getNbrSetupStrings();
   int getSetupStringLength();

};
#endif // #ifndef __STD_ANSI_TBL_NINE_ONE_H__
