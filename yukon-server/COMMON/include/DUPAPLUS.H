
#ifndef _DUPAPLUS_H_
#define _DUPAPLUS_H_


#ifndef _DIALUP_H_
  #include "dialup.h"
#endif

/* Alpha Protocol Field Definition */

/* Alpha Start of Text char.  Must be first byte of every message */
#define  STX                  0x02
#define  PAD                  0x00

/* (CB) Command Byte Masks ans Defines */
#define  AP_FORMAT_MASK       0x80        /* 1 indicates a short command format (0 long) */

#define  AP_SHORT_CMD         0x80        /* 1 indicates a short command format (0 long) */

#define  AP_SHORT_CMD_MASK    0x1F
#define  AP_FUNCTION_MASK     0x08        /* 1 indicates a function command */
#define  AP_READ_MASK         0x04        /* 1 indicates a read command */
#define  AP_CLASS_MASK        0x01        /* 1 indicates a class request command */

#define  AP_CMD_TERMINATE     0x80        /* Terminate Session */
#define  AP_CMD_CONTINUE_RD   0x81        /* Continue Read Operation */
#define  AP_CMD_RESEND        0x82        /* Resend Last Packet */
#define  AP_CMD_OKAY          0x83        /* Are you there/Okay? (from Alpha) */
#define  AP_CMD_TAKE_CONTROL  0x84        /* Take Control (from Alpha) */
#define  AP_CMD_BAUD_1200     0x90        /* set baud to 1200 (from Alpha) */
#define  AP_CMD_BAUD_9600     0x93        /* set baud to 9600 (from Alpha) */

#define  AP_CMD_CLASS_READ    0x05
#define  AP_CMD_NO_DATA       0x08        /* Function without Data (see FUNC below)   */
#define  AP_CMD_WITH_DATA     0x18        /* Functions with short data buffers */
#define  AP_CMD_PARTIAL_READ  0x85        /* Class Read CMD (portion) */
#define  AP_CMD_READ_ALL      0x9F        /* Class Read ALL CMD (portion) */


/*
 * (FUNC) Function Codes
 */


/*
 * Command Format  : STX 0x08 FUNC CRCH CRCL
 * Command Response: STX 0x08 ACK/NAK STAT CRCH CRCL
 */
#define  AP_FUNC_DEMAND_RESET 0x01
#define  AP_FUNC_CAO          0x0E        /* Clear all occurance and status bits & perform 0x0D */
#define  AP_FUNC_BR_COMPLETE  0x10
#define  AP_FUNC_AR_COMPLETE  0x11
#define  AP_FUNC_BAUD_9600    0x12
#define  AP_FUNC_BAUD_19200   0x13
#define  AP_FUNC_LPRESET      0x14
#define  AP_FUNC_ELRESET      0x15
#define  AP_FUNC_FREEZE       0x17        /* Copies class 11 data to class 12 */


/*
 * Command Format  : STX 0x18 FUNC PAD LEN DATA CRCH CRCL
 * Command Response: STX 0x18 ACK/NAK STAT CRCH CRCL
 */
#define  AP_FUNC_PASSWORD     0x01
#define  AP_FUNC_TIME_SET     0x02
#define  AP_FUNC_WHO_ARE_YOU  0x06
#define  AP_FUNC_BR_CALL_DATE 0x07
#define  AP_FUNC_CALL_BACK    0x08
#define  AP_FUNC_PACKET_SIZE  0x09
#define  AP_FUNC_CRIT_PEAK    0x0B
#define  AP_FUNC_TIME_SYNC    0x0C
#define  AP_FUNC_COMM_TO      0xF2

/*
 * (CLASS) Class Number
 * 0-5 and 19-23 hold configuration data
 *
 * Command Format  : STX 0x05 PAD LENH LENL OFSH OFSL CLASS CRCH CRCL
 * Command Response:
 *           ACK   : STX 0x05 ACK STAT LEN DATA CRCH CRCL
 *           NAK   : STX 0x05 NAK STAT CRCH CRCL
 */



/*
 * (OFSH/OFSL) Address offset high, and low bytes
 */

/*
 * (LEN) Byte legth of data field
 */
#define  AP_LAST_DATA_SET      0x80        /* 1 indicates last data set */
#define  AP_DATA_LENGTH_MASK   0x7F        /* Length in bytes of data xfer. (1-64) */

/*
 * (ACK/NAK) -  Acknowledge/Negative Acknowledge byte
 */

#define  ACK_MASK             0x00
#define  NAK_MASK             0x0F

#define  NAK_BAD_CRC          0x01
#define  NAK_COMM_LOCK        0x02
#define  NAK_SYNTAX           0x03
#define  NAK_FRAMING          0x04
#define  NAK_TIMEOUT          0x05
#define  NAK_PASSWORD         0x06
#define  NAK_REC_NAK          0x07
#define  NAK_REQ_IN_PROGRESS  0x0C
#define  NAK_TOO_BUSY         0x0D
#define  NAK_RULES_CLASS      0x0F

/*
 * (STAT) Status Codes
 */

#define  AP_AUTOREAD_OCCURED  0x80
#define  AP_SEASON_CHANGE     0x40
#define  AP_POWER_FAIL        0x20
#define  AP_GENERAL_ALARM     0x10
#define  AP_WRITE_PROTECT     0x08
#define  AP_FUTURE_CONFIG     0x04
#define  AP_DEMAND_RESET      0x02
#define  AP_TIME_CHANGE       0x01


#define KEY_AP_CLASS 1
#define KEY_AP_DESC  2
#define KEY_AP_FUNC  3

// State values for handshake function
#define HANDSHAKE_INIT_STATE     0x0001
#define HANDSHAKE_IDENTIFY_STATE 0x0002
#define HANDSHAKE_WHO_STATE      0x0004
#define HANDSHAKE_PASSWORD_STATE 0x0008

#define HANDSHAKE_COMMAND_STATE  0x0010
#define HANDSHAKE_ERROR_STATE    0x0020

#define HANDSHAKE_COMPLETE_STATE 0x8000

/*
 * Alpha states for class reads
 */

#define ALPHA_INIT_STATE         0x0001
#define ALPHA_SENDCLASS_STATE    0x0002
#define ALPHA_RECKNOWN_STATE     0x0004
#define ALPHA_RECUNKNOWN_STATE   0x0008
#define ALPHA_CONTINUERD_STATE   0x0010
#define ALPHA_COMPLETE_STATE     0x0020
#define ALPHA_INIT_RCLASSRD2     0x0040


/*
 *  DEBUG bit fields.
 *
 */
#define  DBG_PRINT_RECEIPTS      0x8000
#define  DBG_PRINT_SENDS         0x4000
#define  DBG_PRINT_CHARBUF       0x2000
#define  DBG_PRINT_DESCRIPTIONS  0x1000



/* These is a Request Word 1 values */
#define ALHPA_READREQ_DATE             0x0010
#define ALHPA_READREQ_DEMAND           0x0020
#define ALHPA_READREQ_TKW              0x0040
#define ALHPA_READREQ_TKWH             0x0080
#define ALHPA_READREQ_AKW              0x0100
#define ALHPA_READREQ_BKW              0x0200
#define ALHPA_READREQ_CKW              0x0400
#define ALHPA_READREQ_DKW              0x0800
#define ALHPA_READREQ_AKWH             0x1000
#define ALHPA_READREQ_BKWH             0x2000
#define ALHPA_READREQ_CKWH             0x4000
#define ALHPA_READREQ_DKWH             0x8000

/* These is a Request Word 2 values */
#define ALHPA_READREQ_TKVAR            0x0040
#define ALHPA_READREQ_TKVARH           0x0080
#define ALHPA_READREQ_AKVAR            0x0100
#define ALHPA_READREQ_BKVAR            0x0200
#define ALHPA_READREQ_CKVAR            0x0400
#define ALHPA_READREQ_DKVAR            0x0800
#define ALHPA_READREQ_AKVARH           0x1000
#define ALHPA_READREQ_BKVARH           0x2000
#define ALHPA_READREQ_CKVARH           0x4000
#define ALHPA_READREQ_DKVARH           0x8000

/* These is a Request Word 3 values */
#define ALHPA_READREQ_TKVA             0x0040
#define ALHPA_READREQ_TKVAH            0x0080
#define ALHPA_READREQ_AKVA             0x0100
#define ALHPA_READREQ_BKVA             0x0200
#define ALHPA_READREQ_CKVA             0x0400
#define ALHPA_READREQ_DKVA             0x0800
#define ALHPA_READREQ_AKVAH            0x1000
#define ALHPA_READREQ_BKVAH            0x2000
#define ALHPA_READREQ_CKVAH            0x4000
#define ALHPA_READREQ_DKVAH            0x8000

typedef struct
{
   UINT  Class;
   INT   Length;
   char  *Description;
} CTI_ap_class;

typedef struct
{
   UINT  Function;
   INT   DataBytes;
   char  *Description;
} CTI_ap_func;

typedef struct
{
   UCHAR Year;
   UCHAR Month;
   UCHAR Day;
   UCHAR Hour;
   UCHAR Minute;
} APDATETIME;

typedef struct _AlphaClass0Parts
{
   struct {
      UCHAR    UKH[3];
      UCHAR    UPR;
      UCHAR    UKE[5];
      UCHAR    INTNORM;
      UCHAR    INTTEST;
      UCHAR    DPLOCE;
      UCHAR    DPLOCD;
      UCHAR    NUMSBI;
      UCHAR    VTRATIO[3];
      UCHAR    CTRATIO[3];
      UCHAR    XFACTOR[4];
      UCHAR    SPARES[15];
      UCHAR    CLOCKS;
   } Byte;

   struct {
      FLOAT    UKH;
      FLOAT    UPR;
      FLOAT    UKE;
      FLOAT    INTNORM;
      FLOAT    INTTEST;
      FLOAT    DPLOCE;
      FLOAT    DPLOCD;
      FLOAT    NUMSBI;
      FLOAT    VTRATIO;
      FLOAT    CTRATIO;
      FLOAT    XFACTOR;
      FLOAT    SPARES;
      FLOAT    CLOCKS;
   } Real;

   BOOL     bDataIsReal;

} AlphaClass0Parts;                /* 40 bytes total, 13 elements, 52 FLOAT bytes */

typedef struct _AlphaClass1Parts
{
   struct {
      UCHAR    PSWORD[4];
      UCHAR    CL1CKS;
   } Byte;

   struct {
      FLOAT    PSWORD;
      FLOAT    CL1CKS;
   } Real;

   BOOL     bDataIsReal;
} AlphaClass1Parts;                /* 5 bytes total, 2 elements, 8 FLOAT bytes */

typedef struct _AlphaClass2Parts
{
   struct {
            UCHAR    UMTRSN[5];
            UCHAR    ACCTID[14];
            UCHAR    USER[11];
            UCHAR    DFOVAL;
            UCHAR    DFGVAL;
            UCHAR    DRLVAL;
            UCHAR    KWOVRL[3];
            UCHAR    KWTHRSA[3];
            UCHAR    KWTHRSB[3];
            UCHAR    KWTHRSC[3];
            UCHAR    KWTHRSD[3];
            UCHAR    E2KYZDV;
            UCHAR    EMETFLG[2];
            UCHAR    EATRVAL;
            UCHAR    EBLKCF1;
            UCHAR    EBLKCF2;
            UCHAR    SPARE[9];
            UCHAR    CL2CKS;
   } Byte;

   struct {
            FLOAT    UMTRSN;
            FLOAT    ACCTID;
            FLOAT    USER;
            FLOAT    DFOVAL;
            FLOAT    DFGVAL;
            FLOAT    DRLVAL;
            FLOAT    KWOVRL;
            FLOAT    KWTHRSA;
            FLOAT    KWTHRSB;
            FLOAT    KWTHRSC;
            FLOAT    KWTHRSD;
            FLOAT    E2KYZDV;
            FLOAT    EMETFLG;
            FLOAT    EATRVAL;
            FLOAT    EBLKCF1;
            FLOAT    EBLKCF2;
            FLOAT    SPARE;
            FLOAT    CL2CKS;
   } Real;

   BOOL     bDataIsReal;
} AlphaClass2Parts;                /* 64 bytes total, 18 elements, 72 FLOAT bytes*/

typedef struct _AlphaClass3Parts
{
   UCHAR    EDHDTIM;
   UCHAR    DATFMT;
   UCHAR    DISFLG;
   UCHAR    D1STAB[192];
   UCHAR    CL3CKS;
} AlphaClass3Parts;                /* 196 bytes total */

typedef struct _AlphaClass4Parts
{
   UCHAR    SMSEAS;
   UCHAR    SEASFLG;
   UCHAR    ENDRSP;
   UCHAR    ENDCDT;
   UCHAR    ENDSDT;
   UCHAR    RATET[156];
   UCHAR    RATEYR;
   UCHAR    SPARE;
   UCHAR    DTYPE[12];
   UCHAR    CL4CKS;
} AlphaClass4Parts;                /* 176 bytes total */

typedef struct _AlphaClass6Parts
{

   UCHAR    XREV;
   UCHAR    XPGMREV;
   UCHAR    XUOM[2];
   UCHAR    RESERVED1[3];
   UCHAR    DSPFUNC;
   UCHAR    XUOM1;
   UCHAR    XUOM2;
   UCHAR    RESERVED2[3];
   UCHAR    XPGMREV2[2];
   UCHAR    RESERVED3[272];
   UCHAR    CL6CKS;
} AlphaClass6Parts;                /* 288 bytes total */

typedef struct _AlphaClass7Parts
{
   UCHAR    XMTRSN[5];
   UCHAR    XKH[3];
   UCHAR    XPR1;
   UCHAR    XKE1[5];
   UCHAR    XKHDIV;
   UCHAR    XKE2[5];
   UCHAR    RESERVED1[8];
   UCHAR    XPGMREV2[2];
   UCHAR    XPGMREV;
   UCHAR    RESERVED2[272];
   UCHAR    CL7CKS;
} AlphaClass7Parts;                /* 304 bytes total */

typedef struct _AlphaClass8Parts
{
   UCHAR    MKTPROD;
   UCHAR    SSPEC1[3];
   UCHAR    GROUP1;
   UCHAR    REVNO1;
   UCHAR    PCODE1;
   UCHAR    PSERIES1;
   UCHAR    FFENA1[3];
   UCHAR    SSPEC2[3];
   UCHAR    GROUP2;
   UCHAR    REVNO2;
   UCHAR    PCODE2;
   UCHAR    PSERIES2;
   UCHAR    FFENA2[3];
   UCHAR    SSPEC3[3];
   UCHAR    GROUP3;
   UCHAR    REVNO3;
   UCHAR    XUOM[2];
   UCHAR    SCRATCH[30];
   UCHAR    LPLMEM[2];
   UCHAR    SPARES[4];
} AlphaClass8Parts;                /* 64 bytes */

typedef struct _AlphaClass9Parts
{
   UCHAR    XUOM;
   UCHAR    SYSERR[3];
   UCHAR    SYSWARN;
   UCHAR    SYSSTAT;
   UCHAR    CUMDDR;
   UCHAR    CUMDPUL;
   UCHAR    PWRLOG[4];
   UCHAR    PSTART[6];
   UCHAR    PEND[6];
   UCHAR    DOY[2];
   UCHAR    TD[6];
   UCHAR    TRI[2];
   UCHAR    DATATR[3];
   UCHAR    DATAREP[3];
   UCHAR    DATMOD[3];
   UCHAR    CUMDR;
   UCHAR    CUMCOMM;
   UCHAR    CUMOUT[2];
} AlphaClass9Parts;                /* 48 bytes */

typedef struct __AlphaClass10Parts
{
   UCHAR    KH[3];
   UCHAR    PR;
   UCHAR    PULDEF;
   UCHAR    MTRSN[5];
   UCHAR    KEADJ[5];
   UCHAR    KDADJ[5];
   UCHAR    ENEWCON[3];
   UCHAR    ENEWACT;

} AlphaClass10Parts;


typedef struct _AlphaClass14Parts
{
   struct 
   {
      UCHAR    Spare1[3];
      UCHAR    RLPSCAL;       // load profile pulse scaling factor
      UCHAR    LPLEN;         // load profile interval length
      UCHAR    DASIZE[2];     // load profile day record size
      UCHAR    LPMEM;         // amount of load profile memory in days
      UCHAR    CHANS;         // number channels
      UCHAR    IO01FLG;       // channel 1 selection
      UCHAR    IO02FLG;       // channel 2 selection
      UCHAR    IO03FLG;       // channel 3 selection
      UCHAR    IO04FLG;       // channel 4 selection
      UCHAR    Spare2[28];
      UCHAR    CL14CKS;       // class checksum
   } Byte;

   struct 
   {
      FLOAT    RLPSCAL;       // load profile pulse scaling factor
      FLOAT    LPLEN;         // load profile interval length
      FLOAT    DASIZE;     // load profile day record size
      FLOAT    LPMEM;         // amount of load profile memory in days
      FLOAT    CHANS;         // number channels
      FLOAT    IO01FLG;       // channel 1 selection
      FLOAT    IO02FLG;       // channel 2 selection
      FLOAT    IO03FLG;       // channel 3 selection
      FLOAT    IO04FLG;       // channel 4 selection
      FLOAT    CL14CKS;       // class checksum
   } Real;

   BOOL     bDataIsReal;

} AlphaClass14Parts;  // total size 84 bytes


typedef struct _BillingDataParts
{
   struct {
      UCHAR          AKWH1[7];
      UCHAR          AKW1[3];
      APDATETIME     ATD1;
      UCHAR          AKWCUM1[3];
      UCHAR          AKWC1[3];
      UCHAR          BKWH1[7];
      UCHAR          BKW1[3];
      APDATETIME     BTD1;
      UCHAR          BKWCUM1[3];
      UCHAR          BKWC1[3];
      UCHAR          CKWH1[7];
      UCHAR          CKW1[3];
      APDATETIME     CTD1;
      UCHAR          CKWCUM1[3];
      UCHAR          CKWC1[3];
      UCHAR          DKWH1[7];
      UCHAR          DKW1[3];
      APDATETIME     DTD1;
      UCHAR          DKWCUM1[3];
      UCHAR          DKWC1[3];
      UCHAR          AKWH2[7];
      UCHAR          AKW2[3];
      APDATETIME     ATD2;
      UCHAR          AKWCUM2[3];
      UCHAR          AKWC2[3];
      UCHAR          BKWH2[7];
      UCHAR          BKW2[3];
      APDATETIME     BTD2;
      UCHAR          BKWCUM2[3];
      UCHAR          BKWC2[3];
      UCHAR          CKWH2[7];
      UCHAR          CKW2[3];
      APDATETIME     CTD2;
      UCHAR          CKWCUM2[3];
      UCHAR          CKWC2[3];
      UCHAR          DKWH2[7];
      UCHAR          DKW2[3];
      APDATETIME     DTD2;
      UCHAR          DKWCUM2[3];
      UCHAR          DKWC2[3];
      UCHAR          EKVARH4[7];
      UCHAR          EKVARH3[7];
      UCHAR          EKVARH2[7];
      UCHAR          EKVARH1[7];
      UCHAR          ETKWH1[7];
      UCHAR          ETKWH2[7];
      UCHAR          EAVGPF[2];
   } Byte;

   struct {
      FLOAT          AKWH1;
      FLOAT          AKW1;
      APDATETIME     ATD1;
      FLOAT          AKWCUM1;
      FLOAT          AKWC1;
      FLOAT          BKWH1;
      FLOAT          BKW1;
      APDATETIME     BTD1;
      FLOAT          BKWCUM1;
      FLOAT          BKWC1;
      FLOAT          CKWH1;
      FLOAT          CKW1;
      APDATETIME     CTD1;
      FLOAT          CKWCUM1;
      FLOAT          CKWC1;
      FLOAT          DKWH1;
      FLOAT          DKW1;
      APDATETIME     DTD1;
      FLOAT          DKWCUM1;
      FLOAT          DKWC1;
      FLOAT          AKWH2;
      FLOAT          AKW2;
      APDATETIME     ATD2;
      FLOAT          AKWCUM2;
      FLOAT          AKWC2;
      FLOAT          BKWH2;
      FLOAT          BKW2;
      APDATETIME     BTD2;
      FLOAT          BKWCUM2;
      FLOAT          BKWC2;
      FLOAT          CKWH2;
      FLOAT          CKW2;
      APDATETIME     CTD2;
      FLOAT          CKWCUM2;
      FLOAT          CKWC2;
      FLOAT          DKWH2;
      FLOAT          DKW2;
      APDATETIME     DTD2;
      FLOAT          DKWCUM2;
      FLOAT          DKWC2;
      FLOAT          EKVARH4;
      FLOAT          EKVARH3;
      FLOAT          EKVARH2;
      FLOAT          EKVARH1;
      FLOAT          ETKWH1;
      FLOAT          ETKWH2;
      FLOAT          EAVGPF;
   } Real;

   BOOL     bDataIsReal;

} BillingDataParts;                /* 212 bytes total, 46 elements, 184 FLOAT bytes */

typedef struct __AlphaClass72Parts
{
   struct {
      /*
       *  Offset 1 & 2 data
       */

      UCHAR    BLK1[3];    // KW Demand last interval
      UCHAR    BLK2[6];    // Last interval KVAR - Last three bytes are filler
      UCHAR    CUMOUT[2];  // Power Outage Count
      UCHAR    SYSTAT[2];  // Phase's Present - Last byte is filler

      UCHAR       AKW1[3];
      APDATETIME  ATD1;
      UCHAR       AKWH1[5];

      UCHAR       BKW1[3];
      APDATETIME  BTD1;
      UCHAR       BKWH1[5];

      UCHAR       CKW1[3];
      APDATETIME  CTD1;
      UCHAR       CKWH1[5];

      UCHAR       DKW1[3];
      APDATETIME  DTD1;
      UCHAR       DKWH1[5];

      UCHAR       ETKWH1[7];
      UCHAR       ETKWH2[6];

      UCHAR       AKW2[3];
      APDATETIME  ATD2;
      UCHAR       AKWH2[5];

      UCHAR       BKW2[3];
      APDATETIME  BTD2;
      UCHAR       BKWH2[5];

      UCHAR       CKW2[3];
      APDATETIME  CTD2;
      UCHAR       CKWH2[5];

      APDATETIME  TD;         // Bizarre, but true.
      UCHAR       SECONDS[1];    // NOTE SECONDS ARE NOT IN THE STANDARD STRUCTURE....

      UCHAR    CUMDR[1];
      UCHAR    SEARAT[1];

      /*
       *  Offset 3 & 4 data follows here!
       */
      UCHAR    BLK1_EX[3];    // KW Demand last interval
      UCHAR    BLK2_EX[6];    // Last interval KVAR - Last three bytes are filler
      UCHAR    CUMOUT_EX[2];  // Power Outage Count
      UCHAR    SYSTAT_EX[2];  // Phase's Present - Last byte is filler

      UCHAR    AKWCUM1[3];
      UCHAR    AKWC1[3];
      UCHAR    BKWCUM1[3]; // 1 filler byte
      UCHAR    BKWC1[4];

      UCHAR    CKWCUM1[3];
      UCHAR    CKWC1[3];
      UCHAR    DKWCUM1[3]; // 1 filler byte
      UCHAR    DKWC1[4];
      UCHAR    AKWCUM2[3];
      UCHAR    AKWC2[3];
      UCHAR    BKWCUM2[3]; // 1 filler byte
      UCHAR    BKWC2[4];
      UCHAR    CKWCUM2[3];
      UCHAR    CKWC2[3];
      UCHAR    DKWCUM2[3]; // 1 filler byte
      UCHAR    DKWC2[4];

      UCHAR    EKVARH4[7];
      UCHAR    EKVARH3[6];
      UCHAR    EKVARH2[7];
      UCHAR    EKVARH1[6];

      UCHAR       DKW2[3];
      APDATETIME  DTD2;
      UCHAR       DKWH2[5];

      UCHAR    EAVGPF[2];

   } Byte;

   struct {
      FLOAT    BLK1;
      FLOAT    BLK2;
      INT      CUMOUT;
      INT      SYSTAT;

      FLOAT       AKW1;
      APDATETIME  ATD1;
      FLOAT       AKWH1;

      FLOAT       BKW1;
      APDATETIME  BTD1;
      FLOAT       BKWH1;

      FLOAT       CKW1;
      APDATETIME  CTD1;
      FLOAT       CKWH1;

      FLOAT       DKW1;
      APDATETIME  DTD1;
      FLOAT       DKWH1;

      FLOAT       ETKWH1;
      FLOAT       ETKWH2;

      FLOAT       AKW2;
      APDATETIME  ATD2;
      FLOAT       AKWH2;

      FLOAT       BKW2;
      APDATETIME  BTD2;
      FLOAT       BKWH2;

      FLOAT       CKW2;
      APDATETIME  CTD2;
      FLOAT       CKWH2;

      APDATETIME  TD;
      UCHAR       SECONDS;

      UINT     CUMDR;
      UINT     SEARAT;

      FLOAT AKWCUM1;
      FLOAT AKWC1;
      FLOAT BKWCUM1; // 1 filler byte
      FLOAT BKWC1;
      FLOAT CKWCUM1;
      FLOAT CKWC1;
      FLOAT DKWCUM1; // 1 filler byte
      FLOAT DKWC1;

      FLOAT AKWCUM2;
      FLOAT AKWC2;
      FLOAT BKWCUM2; // 1 filler byte
      FLOAT BKWC2;
      FLOAT CKWCUM2;
      FLOAT CKWC2;
      FLOAT DKWCUM2;    // 1 filler byte
      FLOAT DKWC2;

      FLOAT EKVARH4;
      FLOAT EKVARH3;
      FLOAT EKVARH2;
      FLOAT EKVARH1;

      FLOAT       DKW2;
      APDATETIME  DTD2;
      FLOAT       DKWH2;

      FLOAT    EAVGPF;
   } Real;

   BOOL     bDataIsReal;

} AlphaClass72Parts;

typedef struct __AlphaClass82Parts
{
   struct 
   {
      /*
       *  Demand data -- assumes we use an Offset of 9 for class 82
       */
      UCHAR    Filler1[9];    // some filler
      UCHAR    BLK1[3];       // KW Demand last interval
      UCHAR    Filler2[51];   // some filler
      UCHAR    BLK2[3];       // Last interval KVAR
      UCHAR    Filler3[7];    // more Filler ?? returns 117 bytes of data
   } Byte;

   struct 
   {
      FLOAT    BLK1Demand;
      FLOAT    BLK2Demand;
   } Real;

   BOOL     bDataIsReal;

} AlphaClass82Parts;

typedef union
{
   UCHAR             Buffer[40];
   AlphaClass0Parts  Parts;
} AlphaClass0;

typedef union
{
   UCHAR             Buffer[5];
   AlphaClass1Parts  Parts;
} AlphaClass1;

typedef union
{
   UCHAR             Buffer[64];
   AlphaClass2Parts  Parts;
} AlphaClass2;

typedef union
{
   UCHAR             Buffer[196];
   AlphaClass3Parts  Parts;
} AlphaClass3;

typedef union
{
   UCHAR             Buffer[176];
   AlphaClass4Parts  Parts;
} AlphaClass4;

typedef union
{
   UCHAR             Buffer[288];
   AlphaClass6Parts  Parts;
} AlphaClass6;

typedef union
{
   UCHAR             Buffer[304];
   AlphaClass7Parts  Parts;
} AlphaClass7;

typedef union
{
   UCHAR             Buffer[64];
   AlphaClass8Parts  Parts;
} AlphaClass8;

typedef union
{
   UCHAR             Buffer[48];
   AlphaClass9Parts  Parts;
} AlphaClass9;

typedef union
{
   UCHAR             Buffer[24];
   AlphaClass10Parts Parts;
} AlphaClass10;

typedef union
{
   UCHAR             Buffer[212];
   BillingDataParts  Parts;
} AlphaClass11;

typedef union
{
   UCHAR             Buffer[212];
   BillingDataParts  Parts;
} AlphaClass12;

typedef union
{
   UCHAR             Buffer[212];
   BillingDataParts  Parts;
} AlphaClass13;

typedef union
{
   UCHAR             Buffer[125];
   AlphaClass72Parts Parts;
} AlphaClass72;

// Flags defnied for Flags member of the above structure
#define  XFER_ADD_CRC      0x0001
#define  XFER_VERIFY_CRC   0x0002

/*   DEBUG DLS  moved to dev_aplus.h
UCHAR    DecodeAckNak(UCHAR AckNak);
ULONG    APlusCrypt(ULONG Key, ULONG PWord);
INT      GetAPClassOffset(UINT Key, void *ptr);
INT      GetAPFuncOffset(UINT Key, VOID *ptr);

INT      DoCommandNoData (UINT Function, REQUESTRECORD *ReqRec);
INT      DoCommandWithData (UINT Function, REQUESTRECORD *ReqRec);
INT      DoClassRead(BYTE Class, USHORT Offset, USHORT Length, REQUESTRECORD *ReqRec);
INT      DoReadAll( REQUESTRECORD *ReqRec );
BOOL     DoClassDecode( REQUESTRECORD *ReqRec );
UCHAR    PowerPlusStatus(UCHAR Status, BOOL bClear, REQUESTRECORD *ReqRecord);

INT      DoGetClassZero(REQUESTRECORD* ReqRec);
*/
#endif // _APLUSDUP_H_
