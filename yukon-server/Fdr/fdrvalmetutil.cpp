#include "precompiled.h"
#include "ctitime.h"
#include "ctidate.h"
#include "fdrvalmetutil.h"
#include "cparms.h"

using std::string;
namespace Fdr {
namespace Valmet {

USHORT ForeignToYukonQuality (USHORT aQuality)
{
    USHORT Quality = NormalQuality;
    USHORT HostQuality;

    HostQuality = ntohs (aQuality);

    /* Test for the various Valmet Qualities and translate to CTI */
    if (HostQuality & VALMET_PLUGGED)
        Quality = NonUpdatedQuality;
    else if (HostQuality & VALMET_MANUALENTRY)
        Quality = ManualQuality;
    else if (HostQuality & VALMET_DATAINVALID)
        Quality = InvalidQuality;
    else if (HostQuality & VALMET_UNREASONABLE)
        Quality = AbnormalQuality;
    else if (HostQuality & VALMET_OUTOFSCAN)
        Quality = AbnormalQuality;

    return(Quality);
}

USHORT YukonToForeignQuality (const CtiFDRPoint &p)
{
    const unsigned YukonQuality = p.getQuality();
    USHORT Quality = VALMET_NORMAL;

    if(p.isUnsolicited() && gConfigParms.isTrue("FDR_VALMET_SEND_UNSOLICITED_QUALITY", true))
        Quality = VALMET_UNSOLICITED;
    else if (YukonQuality == NonUpdatedQuality)
        Quality = VALMET_PLUGGED;
    else if (YukonQuality == InvalidQuality)
        Quality = VALMET_PLUGGED;
    else if (YukonQuality == ManualQuality)
        Quality = VALMET_MANUALENTRY;
    else if (YukonQuality == AbnormalQuality)
        Quality = VALMET_PLUGGED;
    else if (YukonQuality == UnintializedQuality)
        Quality = VALMET_PLUGGED;

    return htons (Quality);
}

 string ForeignQualityToString(USHORT quality)
 {
     string retString;
     quality = ntohs(quality);
     if (quality == VALMET_NORMAL)
        retString = "VALMET_NORMAL";
     else if (quality == VALMET_PLUGGED)
        retString = "VALMET_PLUGGED";
     else if (quality == VALMET_MANUALENTRY)
         retString = "VALMET_MANUALENTRY";
     else if (quality == VALMET_DATAINVALID)
         retString = "VALMET_DATAINVALID";
     else if (quality == VALMET_UNREASONABLE)
         retString = "VALMET_UNREASONABLE";
     else if (quality == VALMET_OUTOFSCAN)
         retString = "VALMET_OUTOFSCAN";
     else
     {
         retString = "UNDEFINED - 0x";
         CHAR buf[20];
         retString += string (itoa (quality,buf,16));
     }
     return retString;
 }


// Convert Valmet status to CTI Status
int ForeignToYukonStatus (USHORT aStatus)
{
    int tmpstatus=INVALID;

    switch (ntohs (aStatus))
    {
        case Valmet_Open:
            tmpstatus = OPENED;
            break;
        case Valmet_Closed:
            tmpstatus = CLOSED;
            break;
        case Valmet_Indeterminate:
            tmpstatus = INDETERMINATE;
            break;

    }
    return(tmpstatus);
}

USHORT YukonToForeignStatus (int aStatus)
{
    USHORT tmpstatus=Valmet_Invalid;

    switch (aStatus)
    {
        case OPENED:
            tmpstatus = Valmet_Open;
            break;
        case CLOSED:
            tmpstatus = Valmet_Closed;
            break;
        case INDETERMINATE:
            tmpstatus = Valmet_Indeterminate;
            break;
    }
    return(htons (tmpstatus));
}


CtiTime ForeignToYukonTime (PCHAR aTime, int timeStampReasonability, bool aTimeSyncFlag)
{
    struct tm ts;
    CtiTime retVal;

    if (sscanf (aTime,
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        retVal = PASTDATE;
    }

    ts.tm_year -= 1900;
    ts.tm_mon--;

    /*********************
    * valmet doesn't fill this in apparently so
    * use whatever we think daylight savings is
    *********************
    */
    ts.tm_isdst = CtiTime().isDST();

    CtiTime returnTime(&ts);

    if (aTimeSyncFlag)
    {
        // just check for validy
        if (!returnTime.isValid())
        {
            retVal = PASTDATE;
        }
        else
        {
            retVal = returnTime;
        }
    }
    else
    {
        // if CtiTime can't make a time or we are outside the window
        if ((returnTime.seconds() > (CtiTime::now().seconds() + timeStampReasonability)) ||
            (returnTime.seconds() < (CtiTime::now().seconds() - timeStampReasonability)) ||
            (!returnTime.isValid()))
    //    if ((returnTime.seconds() > (CtiTime().seconds() + getTimestampReasonabilityWindow())) || (!returnTime.isValid()))
        {
            retVal = PASTDATE;
        }
        else
        {
            retVal = returnTime;
        }
    }

    return retVal;
}

string YukonToForeignTime (CtiTime aTimeStamp)
{
    CHAR      tmp[30];

    /*******************************
    * if the timestamp is less than 01-01-2000 (completely arbitrary number)
    * then set it to now because its probably an error or its uninitialized
    * note: uninitialized points come across as 11-10-1990
    ********************************
    */
    if (aTimeStamp < CtiTime(CtiDate(1,1,2001)))
    {
        aTimeStamp = CtiTime();
    }

    CtiDate tmpDate (aTimeStamp);

    // Place it into the Valmet structure */
    _snprintf (tmp,
             30,
             "%4ld%02ld%02ld%02ld%02ld%02ldCST",
             tmpDate.year(),
             tmpDate.month(),
             tmpDate.dayOfMonth(),
             aTimeStamp.hour(),
             aTimeStamp.minute(),
             aTimeStamp.second());

    if (aTimeStamp.isDST())
    {
        tmp[15] = 'D';
    }

    return(string (tmp));
}

} //Valmet namespace
} //Fdr namespace
