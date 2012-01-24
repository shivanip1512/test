#pragma once

#include "dlldefs.h"
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

class IM_EX_PROT CtiAnsiTable91 : public CtiAnsiTableBase
{
protected:

   TELEPHONE_RCD      _telephoneTbl;

private:


public:

   CtiAnsiTable91( BYTE *dataBlob );

   virtual ~CtiAnsiTable91();

   CtiAnsiTable91& operator=(const CtiAnsiTable91& aRef);
   void printResult( const std::string& deviceName);

   int getBitRate();
   int getNbrSetupStrings();
   int getSetupStringLength();

};
