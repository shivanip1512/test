/* CGP 08/05/98 Added for sanity sake */

#ifndef _DUPREQ_H_
#define _DUPREQ_H_

#define ALPHA_READ_SCOM_STATUS         1
#define ALPHA_READ_SCAN                2
#define ALPHA_READ_PASSWORD            3


#define ALPHA_READ_RATE_A_KWKWH        16
#define ALPHA_READ_RATE_B_KWKWH        13
#define ALPHA_READ_RATE_C_KWKWH        14
#define ALPHA_READ_RATE_D_KWKWH        15
#define ALPHA_READ_TOTAL_KWKWH         17

#define ALPHA_READ_RATE_A_KVARKVARH    26
#define ALPHA_READ_RATE_B_KVARKVARH    23
#define ALPHA_READ_RATE_C_KVARKVARH    24
#define ALPHA_READ_RATE_D_KVARKVARH    25
#define ALPHA_READ_TOTAL_KVARKVARH     27

#define ALPHA_READ_RATE_A_KVAKVAH      36
#define ALPHA_READ_RATE_B_KVAKVAH      33
#define ALPHA_READ_RATE_C_KVAKVAH      34
#define ALPHA_READ_RATE_D_KVAKVAH      35
#define ALPHA_READ_TOTAL_KVAKVAH       37

#define ALPHA_READ_DATE                50
#define ALPHA_READ_DEMAND              51

#define ALPHA_READ_RC_TO_BUFFER        60
#define ALPHA_READ_PWRF                61

/* These is a Request Word 1 values */
#define ALHPA_READREQ_STATUS           0x0001
#define ALHPA_READREQ_SCAN             0x0002
#define ALHPA_READREQ_PWRF             0x0004
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

#define ALPHA_WRITREQ_RESET            0x0001
#define ALPHA_WRITREQ_PASSWORD         0x0002
#define ALPHA_WRITREQ_SCAN             0x0004
#define ALPHA_WRITREQ_CLASS            0x0008
#define ALPHA_WRITREQ_COMMAND          0x0010


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


/* These are Request word 5 values.. they are NOT bit mark values, but command set ids */

#define ALPHA_READREQ                  0x0000
#define ALPHA_WRITEREQ                 0x0001
#define ALPHA_COMMAND                  0x0002
#define ALPHA_NO_COMM_COMMAND          0x8000
          
// I think this is the only stuff used in here                   
//#define IDENT_ALPHAPPLUS               0x0001
#define IDENT_ALPHA_PPLUS              0x0001
#define IDENT_ALPHA_A1                 0x0002
#define IDENT_LANDISGYRS4              0x0003
#define IDENT_FULCRUM				   0x0004
#define IDENT_VECTRON				   0x0005
#define IDENT_DR87  				   0x0006
#define IDENT_QUANTUM                  0x0007
                   
#endif //_DUPREQ_H_
