/*-----------------------------------------------------------------------------*
*
* File:   port_dialable
*
* Date:   12/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/17 19:02:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "port_dialable.h"


INT CtiPortDialable::disconnect(CtiDeviceSPtr Device, INT trace)
{
    INT status = NORMAL;

    if(_superPort->isViable())          // Makes most dialup modems drop carrier!
    {
        _superPort->lowerDTR();
        _superPort->lowerRTS();

        Sleep(2000);                        /* In order for the +++ sequence to work, */
        _modem.sendStringNoWait( "+++", -1 );   /* there generally has to be at least one */
        Sleep(2000);                        /* second of idle line time before and aft*/
                                            /* it.  This is to prevent false hang-ups.*/
                                            /* We accomodate that here by putting 2   */
                                            /* seconds of idle time before and after  */
                                            /* the sequence.                          */

        _modem.setHookSwitch( 0 );          /* Then for good measure, give it the on  */
                                            /* hook command.                          */
    }

    return status;
}

RWCString CtiPortDialable::getDialedUpNumber() const
{
    return _dialedUpNumber;
}

RWCString& CtiPortDialable::getDialedUpNumber()
{
    return _dialedUpNumber;
}

CtiPortDialable& CtiPortDialable::setDialedUpNumber(const RWCString &str)
{
    _dialedUpNumber = str;
    return *this;
}


/* Routine to wait for a response or a timeout */
INT CtiPortDialable::waitForResponse(PULONG ResponseSize, PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    ULONG   i , j;
    ULONG   BytesRead;
    INT     status = READTIMEOUT;

    i = 0;        // i represents the count of bytes in response.
    j = 0;

    if(!_superPort->isViable())
    {
        return(NOTNORMAL);
    }

    /* Set the timeout on read to 1 second */
    // 012201 CGP This clears the buffer on WIN32.//  SetReadTimeOut (PortHandle, 1);

    while(j <= Timeout)
    {
        if(_superPort->readPort(&Response[i], 1, 1, &BytesRead) || BytesRead == 0) // Reads one byte into the buffer
        {
            j++;        // Fails once per second if no bytes read
            continue;
        }

        if(i == 0)                                            // Special case for the zeroeth byte
        {
            if(Response[i] == 0x0a || Response[i] == 0x0d || Response[i] == 0x00)    // Make sure it is not a CR or LF or null
            {
                continue;
            }
        }

        /* Check what this is */
        if(Response[i] == '\r')
        {
            // null out any CR LF
            Response[i] = '\0';

            if(ExpectedResponse != NULL)
            {
                // handle non-verbal OK
                if(!(strcmp(ExpectedResponse, "OK")) && !(strcmp(Response, "0")))
                {
                    // 0 is the same as OK for non-verbal
                    strcpy(Response,"OK");
                }
            }

            // check for expected return
            if(ExpectedResponse == NULL || strstr(Response, ExpectedResponse) != NULL)
            {
                // it was the response we wanted or we did not specify any response
                *ResponseSize = i;
                status = NORMAL;
                break; // the while
            }
            else if(_modem.validModemResponse(Response))
            {
                // this is valid, though unexpected, response or we did not specify.
                *ResponseSize = strlen(Response);
                status = NORMAL;
                break; // the while
            }

            // look for a new message if there is still time!
            Response[0] = '\0';
            i = 0;
        }
        else
        {
            i++;

            if(i + 2 > *ResponseSize)     // are we still within the size limit.
            {
                status = NOTNORMAL;
                break; // the while
            }
        }
    }

    return status;
}

void CtiPortDialable::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    CtiTablePortDialup::getSQL(db, keyTable, selector);
}

void CtiPortDialable::DecodeDatabaseReader(RWDBReader &rdr)
{
    _tblPortDialup.DecodeDatabaseReader(rdr);       // get the base class handled
}

CtiTablePortDialup CtiPortDialable::getTablePortDialup() const
{
    return _tblPortDialup;
}

CtiTablePortDialup& CtiPortDialable::getTablePortDialup()
{
    return _tblPortDialup;
}

CtiPortDialable& CtiPortDialable::setTablePortDialup(const CtiTablePortDialup& aRef)
{
    _tblPortDialup = aRef;
    return *this;
}

