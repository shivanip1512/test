#ifndef MASTER_H
#define MASTER_H

class REMOTE;
class DEVICE;

/* Include file for various mastercomm devices */

/* MasterComm Commands */
#define MASTERLOOPBACK  0x0000
#define MASTERSCANINT   0x0001
#define MASTERSCANEXT   0x0004
#define MASTERCONTROL   0x0006
#define MASTERSCANALL   0x0044
#define MASTERSCANINT   0x0001
#define MASTERFREEZE    0x0051
#define MASTERSCANVCU   0x0054
#define MASTERRESET     0x0071
#define MASTERSEND      0x0003
#define MASTERSENDREPLY 0x0043
#define MASTERSTAGE     0x0023
#define MASTERLOCKOUTSET 0x001e
#define MASTERLOCKOUTRESET 0x000e

#define MASTERGLOBAL    127
#define LCUGLOBAL       126

/* Misc definitions */
#define MASTERHEADER    5
#define MASTERLENGTH    4
#define TIMETOSTAGE     10L
#define STAGETIME       60L * 30L

/* Masks */
#define LCURESET            0x00000080
#define LCUTRANSMITSENT     0x00000001
#define LCUWASTRANSMITTING  0x00000002
#define LCUNEVERRETRY       0x00000010

#define LCUBUSYTRANSMITTING 0x04
#define LCUCHECKBITALARM    0x02
#define LCUMAJORALARM       0x02
#define LCUMINORALARM       0x04
#define LCULOCKEDOUT        0x08

// LS Status byte  [4]
#define MnA_TESTMODE       0x01
#define MnA_ANYALARM       0x02
#define MnA_BUSY           0x04
#define MnA_STANDALONE     0x08
#define MnA_RESET          0x10
#define MnA_NA             0xe0

// MS Status byte [5]
#define MnA_BITERROR       0x01
#define MnA_INJDISABLED    0x02
#define MnA_INJSTAGED      0x04
#define MnA_COUPLING_AL    0x08
#define MnA_PRECHARGE_AL   0x10
#define MnA_TRANSMIT_AL    0x20
#define MnA_OVERCURR_AL    0x40
#define MnA_PHASELOSS_AL   0x80


/* Versacom command types */
#define EXDATA          3
#define VCONTROL        4
#define VINITIATOR      5
#define VCONFIG         6
#define VCOUNTRESET     8
#define VSERVICE        9
#define VFULLADDRESS    0x0a
#define VFILLER         11
#define VPROPOGATION    12
#define VDATA           13
#define VECONTROL       14
#define VSCRAM          15
#define VCUOVERQUE      0x04

/* Versacom Configuration Commands */
#define VCONFIG_SECTION     0x2a
#define VCONFIG_CLASS       0x2b
#define VCONFIG_DIVISION    0x2c
#define VCONFIG_AUXID       0x30
#define VCONFIG_UTILID      0x29

#define VCONFIG_PHYSICALRELAY1 0x43
#define VCONFIG_PHYSICALRELAY2 0x44
#define VCONFIG_PHYSICALRELAY3 0x45

#define VCONFIG_SCD 0x63

#define VCONFIG_RELAYOUT 0x00
#define VCONFIG_RELAY1IN 0x01
#define VCONFIG_RELAY2IN 0x02
#define VCONFIG_RELAY3IN 0x03


    /* cold load pickup */
#define VCONFIG_RELAY1COLDLOAD 0x20
#define VCONFIG_RELAY2COLDLOAD 0x21
#define VCONFIG_RELAY3COLDLOAD 0x22

/* Versacom Service Masks */
#define VSERVICE_CONTIN     0x08
#define VSERVICE_CONTOUT    0x04
#define VSERVICE_TEMPIN     0x02
#define VSERVICE_TEMPOUT    0x01

/* Prototypes from LCU.C */
IM_EX_CTIBASE INT LCUScanAll (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT LCUFreeze (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT LCUReset (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT LCUDecode (INMESS *, DEVICE *, REMOTE *, struct timeb *);

/* Prototypes from TCU.C */
IM_EX_CTIBASE INT TCUScanAll (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT TCUDecode (INMESS *, DEVICE *, REMOTE *, struct timeb *);

/* Prototypes from VCU.C */
IM_EX_CTIBASE INT VCUScanAll (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT VCUScanInt (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT VCUScanExt (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT VCUDecode (INMESS *, DEVICE *, REMOTE *, struct timeb *);

/* Prototypes from MASTER.C */
IM_EX_CTIBASE INT MasterHeader (PBYTE, USHORT, USHORT, USHORT);
IM_EX_CTIBASE INT PreMaster (PBYTE, USHORT);
IM_EX_CTIBASE INT PostMaster (PBYTE, USHORT, PULONG);
IM_EX_CTIBASE INT MasterReply (PBYTE, USHORT);
#endif


