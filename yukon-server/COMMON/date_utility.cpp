#include "precompiled.h"

#include "date_utility.h"
#include "ctitokenizer.h"

namespace Cti {

CtiDate parseDateValue(std::string date_str)
{
    CtiTokenizer date_tokenizer(date_str);

    int month = atoi(date_tokenizer("-/").data());
    int day   = atoi(date_tokenizer("-/").data());
    int year  = atoi(date_tokenizer("-/").data());

    if( !year || !month || !day )
    {
        return CtiDate::neg_infin;
    }

    if( year < 100 )
    {
        year += 2000;  //  this will need to change in 2100
    }

    //  naive date construction - no range checking, so we count
    //    on CtiDate() resetting itself to 1/1/1970
    return CtiDate(day, month, year);
}

}
