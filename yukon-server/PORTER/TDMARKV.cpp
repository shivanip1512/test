#include <windows.h>
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   TDMARKV
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/TDMARKV.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "portsup.h"
#include "tcpsup.h"
#include "perform.h"
//#include "tdmarkv.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "port_base.h"
#include "dev_base.h"

/* Do the TransData Shuffle */
TDMarkVHandShake (OUTMESS     *OutMessage,
                  INMESS      *InMessage,
                  CtiPort     *PortRecord,
                  CtiDeviceBase   *RemoteRecord)
{
    ULONG i, j;
    ULONG OutIndex = PREIDLEN;
    ULONG InIndex = 0;
    UCHAR Buffer[2048];
    ULONG BufferIndex;
    BYTE  SendBuffer[20];
    ULONG ReceiveLength;
    PULONG InCount;
    BOOL NeedNumberRecords = FALSE;
    BOOL StartYModem = FALSE;
    BYTE YModemRecord;
    struct tm  TMTime;


    /* Assume for now that responses from messages will not exceed length of buffer... */
    i = NORMAL;

    while (!i && OutMessage->Buffer.OutMessage[OutIndex] != '\0') {
        /* If this is not the first time through wait a bit */
        if (OutIndex != PREIDLEN) {
            CTISleep (200L);
        }

        if (PortRecord->isTCPIPPort())
        {
            i = OutMess (&OutMessage->Buffer.OutMessage[OutIndex],
                         strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]),
                         PortRecord,
                         RemoteRecord);
        }
        else {
            for (j = 0; j < strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]); j++) {
                i = OutMess (&OutMessage->Buffer.OutMessage[OutIndex + j],
                             1,
                             PortRecord,
                             RemoteRecord);

                if (PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY)) {
                    CTISleep ((ULONG) PortRecord->getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));
                }
            }
        }

        /* Based on the command figure out what we need to do */
        if (!i) {
            if (OutMessage->Buffer.OutMessage[OutIndex] == '\r') {
                /* Do Nothing */
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "OS", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "DC", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "ID", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "DR", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "MI", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "GT", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "IS", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CM", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CA", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CI", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CO", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CS", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "SO", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "RI", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "DS", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "GC", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "PI", 2))) {
                /* Copy the command to the result */
                memcpy (&InMessage->Buffer.InMessage[InIndex],
                        &OutMessage->Buffer.OutMessage[OutIndex],
                        strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1);

                /* Move the InIndex */
                InIndex += strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1;

                /* Blow away characters till we have nailed the first \n */
                do {
                    i = InMess (Buffer,
                                1,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut + 2,
                                &ReceiveLength,
                                0,
                                TRUE);
                } while (!i && Buffer[0] != '\n');


                /* Read this in until we get a ? then back up two... */
                BufferIndex = 0;
                do {
                    i = InMess (&Buffer[BufferIndex],
                                1,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut + 2,
                                &ReceiveLength,
                                0,
                                TRUE);
                } while (!i && Buffer[BufferIndex++] != '?');

                /* Move it into the InMess buffer without the OK<CR><LF>? */
                if (BufferIndex >= 5) {
                    memcpy (&InMessage->Buffer.InMessage[InIndex],
                            Buffer,
                            BufferIndex - 5);

                    InIndex += BufferIndex - 5;

                    InMessage->Buffer.InMessage[InIndex++] = '\0';
                }
                else {
                    i = FRAMEERR;
                }

                /* Check if this is a ID command and if so that they match */
                if (!i) {
                    if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "ID", 2))) {
                        if (RemoteRecord->getAddress() != 0) {
                            if (atol ((const char *)(Buffer + 22)) != RemoteRecord->getAddress()) {
                                i = ADDRESSERROR;
                                OutMessage->Retry = 0;
                            }
                        }
                    }
                }
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "CC", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "SC", 2)) ||
                    !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "HC", 2))) {
                /* Copy the command to the result */
                memcpy (&InMessage->Buffer.InMessage[InIndex],
                        &OutMessage->Buffer.OutMessage[OutIndex],
                        strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1);

                /* Move the InIndex */
                InIndex += strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1;

                /* This will produce an OK ? combo with nothing important */
                do {
                    i = InMess (Buffer,
                                1,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut + 2,
                                &ReceiveLength,
                                0,
                                TRUE);
                } while (!i && Buffer[0] != '?');
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "BU", 2))) {
                /* Message type will already be in input buffer */
                /* This will lead to start the protocol */
                StartYModem = TRUE;
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "RC", 2)) ||
                !(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "RV", 2))) {
                /* Copy the command to the result */
                memcpy (&InMessage->Buffer.InMessage[InIndex],
                        &OutMessage->Buffer.OutMessage[OutIndex],
                        strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1);

                /* Move the InIndex */
                InIndex += strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1;

                /* get rid of any leading \n \r type characters */
                do {
                    i = InMess (Buffer,
                                1,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut,
                                &ReceiveLength,
                                0,
                                TRUE);
                } while (!i && Buffer[0] != 'n');

                if (!i) {
                    /* Wait for the request for number of records */
                    i = InMess (&Buffer[1],
                                43,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut,
                                &ReceiveLength,
                                0,
                                TRUE);
                }

                if (!i) {
                    /* Make sure the response is correct */
                    if (strnicmp ((const char *)Buffer, "nter number of records dumped. format XXXXX", 42)) {
                        i = FRAMEERR;
                    }
                    else {
                        /* We are being asked number of records */
                        NeedNumberRecords = TRUE;
                    }
                }
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "TI", 2))) {
                /* This is a time sync so wait till we have a \n */
                do {
                    i = InMess (Buffer,
                                1,
                                PortRecord,
                                RemoteRecord,
                                OutMessage->TimeOut + 2,
                                &ReceiveLength,
                                0,
                                TRUE);
                } while (!i && Buffer[0] != '\n');

                if (!i) {
                    /* Build up the time in Buffer */
                    memcpy (&TMTime, UCTLocalTime ((time_t)(LongTime () + ((20 * PortRecord->getDelay(RTS_TO_DATA_OUT_DELAY)) / 1000)),
                                                   (USHORT)DSTFlag ()),
                                                   sizeof (TMTime));

                    sprintf ((CHAR*)Buffer,
                             "%02d\r%02d\r%02d\r%02d\r%02d\r%02d\r%1d\r",
                             TMTime.tm_sec,
                             TMTime.tm_min,
                             TMTime.tm_hour,
                             TMTime.tm_mday,
                             TMTime.tm_mon + 1,
                             TMTime.tm_year,
                             TMTime.tm_wday);

                    /* Output it */
                    i = OutMess (Buffer,
                                 strlen ((CHAR*)Buffer),
                                 PortRecord,
                                 RemoteRecord);

                    /* Now wait for a ? */
                    if (!i) {
                        do {
                            i = InMess (Buffer,
                                        1,
                                        PortRecord,
                                        RemoteRecord,
                                        OutMessage->TimeOut + 2,
                                        &ReceiveLength,
                                        0,
                                        TRUE);
                        } while (!i && Buffer[0] != '?');
                    }
                }
            }
            else if (!(strnicmp ((const char *)&OutMessage->Buffer.OutMessage[OutIndex], "LO", 2))) {
                /* Copy the command to the result */
                memcpy (&InMessage->Buffer.InMessage[InIndex],
                        &OutMessage->Buffer.OutMessage[OutIndex],
                        strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1);

                /* Move the InIndex */
                InIndex += strlen ((const char *)&OutMessage->Buffer.OutMessage[OutIndex]) + 1;

                /* Nothing important will happen with this one */
                InMessage->Buffer.InMessage[InIndex++] = '\0';
            }
            else if (atoi ((const char *)&OutMessage->Buffer.OutMessage[OutIndex])) {
                /* This is either a password or a request for number of survey records */
                if (NeedNumberRecords) {
                    NeedNumberRecords = FALSE;

                    /* YMODEM in the results */
                    StartYModem = TRUE;
                }
                else {
                    /* This must be a password or somehthing like it so just wait for ? */
                    do {
                        i = InMess (Buffer,
                                    1,
                                    PortRecord,
                                    RemoteRecord,
                                    OutMessage->TimeOut + 2,
                                    &ReceiveLength,
                                    0,
                                    TRUE);
                    } while (!i && Buffer[0] != '?');
                }
            }
            else {
                /* Something is FUBARED */
                i = FRAMEERR;
            }
        }

        /* Move to the next frame */
        OutIndex += strlen ((char*)&OutMessage->Buffer.OutMessage[OutIndex]) + 1;

        /* Check if it is YMODEM time... */
        if (StartYModem) {
            StartYModem = FALSE;

            InCount = (PULONG) &InMessage->Buffer.InMessage[InIndex];
            InIndex += 4;
            *InCount = 0;

            /* get rid of any leading \n \r type characters */
            do {
                i = InMess (Buffer,
                            1,
                            PortRecord,
                            RemoteRecord,
                            OutMessage->TimeOut,
                            &ReceiveLength,
                            0,
                            TRUE);
            } while (!i && Buffer[0] != 't');

            if (!i) {
                /* Wait for the Start Protocol prompt */
                i = InMess (&Buffer[1],
                            19,
                            PortRecord,
                            RemoteRecord,
                            OutMessage->TimeOut,
                            &ReceiveLength,
                            0,
                            TRUE);
            }

            if (!i) {
                /* Make sure the response is correct */
                if (strnicmp ((CHAR*)Buffer, "tart the protocol", 17)) {
                    i = FRAMEERR;
                }
            }

            if (!i) {
                /* Y-Modem */

                /* Send the start character */
                SendBuffer[0] = 'C';

                i = OutMess (SendBuffer,
                             1,
                             PortRecord,
                             RemoteRecord);

                if (!i) {
                    YModemRecord = 1;
                    BufferIndex = 0;

                    do {
                        /* get the first character and decide how long we will be */
                        i = InMess (Buffer,
                                    1,
                                    PortRecord,
                                    RemoteRecord,
                                    OutMessage->TimeOut,
                                    &ReceiveLength,
                                    0,
                                    TRUE);
                        if (i) {
                            break;
                        }

                        /* Decide how many more (if any) to get */
                        switch (Buffer[0]) {
                            case 0x01:                              // SOH ==> 128 bytes data
                                i = InMess (Buffer + 1,
                                            132,
                                            PortRecord,
                                            RemoteRecord,
                                            OutMessage->TimeOut,
                                            &ReceiveLength,
                                            0,
                                            TRUE);

                                break;

                            case 0x02:                              // STX ==> 1024 bytes data
                                i = InMess (Buffer + 1,
                                            1028,
                                            PortRecord,
                                            RemoteRecord,
                                            OutMessage->TimeOut,
                                            &ReceiveLength,
                                            0,
                                            TRUE);

                                break;

                            case 0x04:                              // EOT ==> end of data
                                /* This means we are done so send and ACK */
                                SendBuffer[0] = 0x06;

                                i = OutMess (SendBuffer,
                                             1,
                                             PortRecord,
                                             RemoteRecord);

                                InMessage->Buffer.InMessage[InIndex++] = '\0';

                                /* Wait for ? */
                                do {
                                    i = InMess (SendBuffer,
                                                1,
                                                PortRecord,
                                                RemoteRecord,
                                                OutMessage->TimeOut + 2,
                                                &ReceiveLength,
                                                0,
                                                TRUE);
                                } while (!i && SendBuffer[0] != '?');

                                continue;

                            default:
                                /* Something is wrong so send a nack */
                                SendBuffer[0] = 0x15;

                                i = OutMess (SendBuffer,
                                             1,
                                             PortRecord,
                                             RemoteRecord);
                                continue;
                        }

                        /* At this point we have a response,,, check it for proper frame CRC */
                        if (Buffer[1] != (255 - Buffer[2])) {
                            /* Ask for the packet again */
                            /* Something is wrong so send a nack */
                            SendBuffer[0] = 0x15;

                            i = OutMess (SendBuffer,
                                         1,
                                         PortRecord,
                                         RemoteRecord);
                            continue;
                        }

                        if (Buffer[1] != YModemRecord) {
                            /* We have a frame error so send a CAN */
                            SendBuffer[0] = 0x18;

                            i = OutMess (SendBuffer,
                                         1,
                                         PortRecord,
                                         RemoteRecord);

                            if (!i) {
                                i = FRAMEERR;
                            }

                            /* Wait for ? */
                            do {
                                j = InMess (Buffer,
                                            1,
                                            PortRecord,
                                            RemoteRecord,
                                            OutMessage->TimeOut + 2,
                                            &ReceiveLength,
                                            0,
                                            TRUE);
                            } while (!j && Buffer[0] != '?');

                            continue;
                        }

                        /* Check the CRC and if OK copy it over and ACK */
                        switch (Buffer[0]) {
                            case 0x01:
                                if (CrcCalc_C (&Buffer[3], 128) !=
                                        (MAKEUSHORT (Buffer[132],
                                                     Buffer[131]))) {
                                    /* Send a NACK */
                                    SendBuffer[0] = 0x15;

                                    i = OutMess (SendBuffer,
                                                 1,
                                                 PortRecord,
                                                 RemoteRecord);

                                    continue;
                                }

                                /* Send an ACK */
                                SendBuffer[0] = 0x06;

                                i = OutMess (SendBuffer,
                                             1,
                                             PortRecord,
                                             RemoteRecord);

                                /* This is good data */
                                for (j = 3; j < 131; j++) {
                                    InMessage->Buffer.InMessage[InIndex++] = Buffer[j];
                                }

                                /* Increase the byte Count */
                                *InCount += 128;

                                YModemRecord++;

                                continue;

                            case 0x02:
                                if (CrcCalc_C (&Buffer[3], 1024) !=
                                        (MAKEUSHORT (Buffer[1028],
                                                     Buffer[1027]))) {
                                    /* Send a NACK */
                                    SendBuffer[0] = 0x15;

                                    i = OutMess (SendBuffer,
                                                 1,
                                                 PortRecord,
                                                 RemoteRecord);

                                    continue;
                                }

                                /* Send an ACK */
                                SendBuffer[0] = 0x06;

                                i = OutMess (SendBuffer,
                                             1,
                                             PortRecord,
                                             RemoteRecord);

                                /* This is good data */
                                for (j = 3; j < 1027; j++) {
                                    InMessage->Buffer.InMessage[InIndex++] = Buffer[j];
                                }

                                /* Increase the byte Count */
                                *InCount += 1024;

                                YModemRecord++;

                                continue;

                            default:
                                /* Should not happen, but just in case */
                                SendBuffer[0] = 0x15;

                                i = OutMess (SendBuffer,
                                             1,
                                             PortRecord,
                                             RemoteRecord);
                                continue;
                        }
                    } while (!i && Buffer[0] != 0x04);
                }
            }
        }
    }

    InMessage->Buffer.InMessage[InIndex++] = '\0';
    InMessage->InLength = InIndex;

    return (i);
}


