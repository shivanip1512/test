
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ctiid
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/ctiid.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:11 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------
        Filename:  ctiid.h

        Programmer:  Aaron Lauinger

        Description:    Header file containing id numbers for various
                        Cti classes that inherit from RWCollectable.

        Initial Date:  5/27/99

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999
---------------------------------------------------------------------------*/

/* Various message classes */

#define CTIPING_ID  140

#define CTIMCERROR_ID     150
#define CTIMCDONE_ID      151
#define CTIMCUPDATE_ID 153
#define CTIMCCOMMAND_ID 154
#define CTIMCINTERACTIVECOMMAND_ID 155
#define CTIMCSHUTDOWN_ID 156

#define CTIMCSCHEDULE_ID        250
#define CTIMCCOMMANDFILE_ID     251

#define CTIMCNOSTARTPOLICY_ID    350
#define CTIMCDATETIMESTARTPOLICY_ID     351
#define CTIMCABSOLUTETIMESTARTPOLICY_ID 352
#define CTIMCDELTATIMESTARTPOLICY_ID    353
#define CTIMCDAYOFMONTHSTARTPOLICY_ID   354
#define CTIMCWEEKDAYTIMESTARTPOLICY_ID  355

#define CTIMCNOSTOPPOLICY_ID    450
#define CTIMCABSOLUTETIMESTOPPOLICY_ID  451
#define CTIMCDURATIONSTOPPOLICY_ID 452

