#pragma once

#include "dlldefs.h"
#include "cticalls.h"

/* A-word function definitions */
#define AWORDLEN        4
#define A_RESTORE       0
#define A_SHED_A        1
#define A_SHED_B        2
#define A_SHED_C        3
#define A_SHED_D        4
#define A_LATCH_OPEN    5
#define A_LATCH_CLOSE   6
#define A_SCRAM         7

/* B-word function definitions */
#define BWORDLEN        7
#define CWORDLEN        7
#define DWORDLEN        7
#define EWORDLEN        7


/* Time definitions */
#define TIME_7_5        0
#define TIME_15         1
#define TIME_30         2
#define TIME_60         3


/*
 *  DLCROUTE is the route used to "exit" the CCU on the powerline,
 *  not to be confused by our other 72 uses of the word route
 */
struct DLCROUTE
{
  USHORT Amp;       // From Route and Transmitter Data.
  USHORT Bus;
  USHORT Stages;
  USHORT RepFixed;
  USHORT RepVar;
};

struct ASTRUCT
{
   USHORT   Port;
   USHORT   Remote;

   DLCROUTE DlcRoute;

   USHORT Group;     // Addressing must be zero through 63
   USHORT Time;      // From the request
   USHORT Function;  // Which relay or zero for a zero. From the group record etc..
};

struct BSTRUCT
{
   USHORT   Port;             // This is the port the remote transmitter device is connected to.
   USHORT   Remote;           // This is the DeviceID of the transmitter device (CCU etc)

   DLCROUTE DlcRoute;         // The data here is used by the word builder and comes from the route

   // DLC Identifications
   ULONG  Address;            // This is the DLC address of the DLC device

   USHORT Function;           // Indicates the desired operation on the DLC device
   USHORT Length;             // This is the byte count expected from the DLC device based upon the request.
   BYTE   Message[36];
   USHORT IO;                 // Input or Outout? In its basic form this is a 2 bit indicator of Cti::Protocol::Emetcon::IO_Write, ::IO_Read, ::IO_Function_Write, ::IO_Function_Read
                              //    At other times additional bits are attached and stuffed in B_Word (primarily ARM bits?)
};

struct DSTRUCT
{
   ULONG    Time;
   USHORT   Length;
   BYTE     Message[36];
   USHORT   RepVar;
   ULONG    Address;
   USHORT   Power;
   USHORT   Alarm;
   USHORT   TSync;
   USHORT   DSTFlag;
};

struct ESTRUCT
{
   unsigned repeater_variable :  3;
   unsigned echo_address      : 13;
   bool     power_fail;
   bool     alarm;

   struct
   {
       bool incoming_bch_error;
       bool incoming_no_response;
       bool listen_ahead_bch_error;
       bool listen_ahead_no_response;
       bool weak_signal;
       bool repeater_code_mismatch;

   } diagnostics;
};

struct JSTRUCT
{
   BYTE     Message[36];
   USHORT   RepVar;
   ULONG    Address;
   USHORT   Power;
   USHORT   Alarm;
};


int   IM_EX_CTIBASE A_Word  (PBYTE, const ASTRUCT &, BOOL Double = FALSE);
int   IM_EX_CTIBASE B_Word  (PBYTE, const BSTRUCT &, unsigned wordCount, BOOL Double = FALSE);
int   IM_EX_CTIBASE C_Word  (PBYTE, const PBYTE, USHORT);
int   IM_EX_CTIBASE C_Words (unsigned char *, const unsigned char *, unsigned short, unsigned int *cword_count = 0);
int   IM_EX_CTIBASE D1_Word (PBYTE, PBYTE, PUSHORT, PULONG, PUSHORT, PUSHORT);
int   IM_EX_CTIBASE D23_Word(PBYTE, PBYTE, PUSHORT, PUSHORT);
int   IM_EX_CTIBASE D_Words (PBYTE, USHORT, USHORT, DSTRUCT *);
int   IM_EX_CTIBASE E_Word  (PBYTE, ESTRUCT *);
int   IM_EX_CTIBASE BCHCheck(PBYTE);
int   IM_EX_CTIBASE PadTst  (PBYTE, USHORT, USHORT);
int   IM_EX_CTIBASE NackTst (BYTE, PUSHORT, USHORT);
int   IM_EX_CTIBASE APreamble (PBYTE, const ASTRUCT &);
int   IM_EX_CTIBASE BPreamble (PBYTE, const BSTRUCT &, INT wordsToFollow);
int   IM_EX_CTIBASE LPreamble (PBYTE, USHORT);
int   IM_EX_CTIBASE G_Word (PBYTE, const BSTRUCT &, INT dwordCount, BOOL Double = FALSE);
int   IM_EX_CTIBASE H_Word (PBYTE, PBYTE, USHORT);
int   IM_EX_CTIBASE I1_Word (PBYTE, PBYTE, PUSHORT, PULONG, PUSHORT, PUSHORT);
int   IM_EX_CTIBASE I23_Word (PBYTE, PBYTE, PUSHORT, PUSHORT);
int   IM_EX_CTIBASE I_Words (PBYTE, USHORT, USHORT, DSTRUCT *);
int   IM_EX_CTIBASE J_Word (PBYTE, JSTRUCT *);
int   IM_EX_CTIBASE I_BCHCheck (PBYTE);

