/*-----------------------------------------------------------------------------*
*
* File:   prot_welco
*
* Class:
* Date:   6/20/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_welco.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/05/20 22:39:25 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_WELCO_H__
#define __PROT_WELCO_H__
#pragma warning( disable : 4786)


#define IDLC_FREEZE             0x0a
#define IDLC_ACCUMDUMP          0x0b
#define IDLC_DEMANDDUMP         0x09
#define IDLC_DEMANDRATE         0x08
#define IDLC_TIMESYNC           0x15
#define IDLC_STATUSDUMP         0x06
#define IDLC_ANALOGDUMP         0x01
#define IDLC_SBOSELECT          0x0c
#define IDLC_SBOEXECUTE         0x0d
#define IDLC_CONTINUE           0x1a
#define IDLC_POLL               0x1f
#define IDLC_STATUSEXCEPTION    0x05
#define IDLC_ANALOGEXCEPTION    0x00
#define IDLC_DEADBANDS          0x17
#define IDLC_RESET              0x13
#define IDLC_CONFIGURATION      0x14
#define IDLC_DIAGNOSTICS        0x3f

#define IDLC_ERR_BIT            0x04

/* Added these for 32-bit accums */
#define IDLC_ACCUM32DUMP        0x4b
#define IDLC_NUL_HDR            0x80


#define EW_PWR_BIT              0x80
#define EW_FMW_BIT              0x40
#define EW_HDW_BIT              0x20
#define EW_SYN_BIT              0x10

#define EW_TRIP_MASK            0x40
#define EW_CLOSE_MASK           0x80

#define EW_DIAG_EX_ACCUM32      0x01           /* RTU supports 32 bit accums   */

/* E003 v102 Byte 5 extensions */
#define EW_DIAG_EX_WD1OFFLINE   0x10           /* WESDAC_1 points are offline  */
#define EW_DIAG_EX_WD2OFFLINE   0x20           /* WESDAC_2 points are offline  */
#define EW_DIAG_EX_WD3OFFLINE   0x40           /* WESDAC_3 points are offline  */
#define EW_DIAG_EX_WD4OFFLINE   0x80           /* WESDAC_4 points are offline  */

#define EW_DIAG_EX_OFFLINE    ( EW_DIAG_EX_WD1OFFLINE | \
                                EW_DIAG_EX_WD2OFFLINE | \
                                EW_DIAG_EX_WD3OFFLINE | \
                                EW_DIAG_EX_WD4OFFLINE)


#define EW_ERR_BIT_MASK         (EW_PWR_BIT | EW_FMW_BIT | EW_HDW_BIT | EW_SYN_BIT )


#endif // #ifndef __PROT_WELCO_H__
