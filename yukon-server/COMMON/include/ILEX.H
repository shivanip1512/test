/* ILEX Message definitions */
#define ILEXGLOBAL   0


/* ILEX command definitions */
#define ILEXNODATA           0x00
#define ILEXSCAN          0x01
#define ILEXFREEZE           0x02
#define ILEXSBOSELECT       0x02
#define ILEXSBOEXECUTE      0x03
#define ILEXSCANPARTIAL     0x05
#define FORCED_SCAN          0X01
#define EXCEPTION_SCAN      0x00
#define ILEXTIMESYNC        0x06
#define TIMESYNC1           0x01
#define TIMESYNC2           0x00
#define ILEXGETTIME         0x01
#define ILEXSETTIME         0x02
#define ILEXCORRECTTIME     0x03


/* Lengths */
#define ILEXHEADERLEN        2
#define ILEXTIMELENGTH      10

/* Prototypes from ILEX.C */
IM_EX_CTIBASE INT ILEXForceScan (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT ILEXExceptionScan (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT ILEXFreezeRTU (REMOTE *, DEVICE *, USHORT, USHORT);
IM_EX_CTIBASE INT ILEXDecode (INMESS *, DEVICE *, REMOTE *, struct timeb *);

/* Prototypes from ILEXPROT.C */
IM_EX_CTIBASE INT ILEXHeader (PBYTE, USHORT, USHORT, USHORT, USHORT);



