/* Base address of DAS-08 */
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   DAS08
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/INCLUDE/DAS08.H-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define DAS08BASE       0x390

/* DAS-08 Read Functions */
#define ADLOWDATA       0
#define ADHIDATA        1
#define ADSTATUS        2
#define COUNTREAD0      4
#define COUNTREAD1      5
#define COUNTREAD2      6
#define PORTREADA       8
#define PORTREADB       9
#define PORTREADC       10


/* DAS-08 Write Functions */
#define ADSTART8        0
#define ADSTART12       1
#define ADCONTROL       2
#define COUNTLOAD0      4
#define COUNTLOAD1      5
#define COUNTLOAD2      6
#define COUNTCONTROL    7
#define PORTOUTA        8
#define PORTOUTB        9
#define PORTOUTC        10
#define PORTCONTROL     11

#define NUMANALOGS      8

/* Define the various DIO24 values */
#define DIO24OFFSET     0x0f
#define DIO24PORTA      0
#define DIO24PORTB      1
#define DIO24PORTC      2
#define DIO24CONTROL    3

/* Prototypes from DAS08.C */
IM_EX_PORT VOID DAS08ScanThread (PVOID);
IM_EX_PORT INT  DAS08Scan (DEVICE *, struct timeb *);



