/*-----------------------------------------------------------------------------*
*
* File:   PORTCONF
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTCONF.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#pragma title ( "Routines to handle VCONFIG.DAT" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        PORTCONF.c

    Purpose:
        Handles spewing out configuration commands for versacom receivers
        from information passed in the VCONFIG.DAT file

    The following procedures are contained in this module:
        VConfigThread           VSend
        VSend2

    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                    WRO
        6-7-95   Added multiple class and divisions                 WRO
        6-7-95   Added support for config names                     WRO
        6-24-95  Made less sensitive to \r \n and spaces            WRO
        6-30-95  Added code to print out configurations sent    WRO


   -------------------------------------------------------------------- */
#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <ctype.h>

#include "cparms.h"
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "master.h"
#include "group.h"
#include "elogger.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "dev_base.h"
#include "portdecl.h"
#include "hashkey.h"
#include "mgr_device.h"
#include "mgr_route.h"

#include "logger.h"
#include "guard.h"

extern CtiRouteManager RouteManager;

/* Prototype of function to get strings from file */
int GetString (FILE *, PCHAR, ULONG);


VOID VConfigThread (VOID *Arg)
{
   ULONG i;
   HANDLE HFile;
   FILE *File;
   ULONG Action;
   CHAR Route[100];
   CHAR Buffer[100];
   USHORT Function;
   VSTRUCT VSt;
   USHORT Service;
   USHORT Address, IAddress;
   CHAR Name[100];
   PSZ PName;
   VERSACONFIG VConfigRecord;
   PCHAR AddTok;
   ULONG ConfigsDone;
   ULONG ServicesDone;
   ULONG UtilIDsDone;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " VConfig Thread Starting as TID:  " << CurrentTID() << endl;
   }

   /* Figure out what the name is */
   if((gConfigParms.getValueAsString("PORTER_VCONFIGPATH")).isNull())
   {
      strcpy (Name, "CONFIG\\VCONFIG.DAT");
   }
   else
   {
      strcpy (Name, gConfigParms.getValueAsString("PORTER_VCONFIGPATH").data() );
      strcat (Name, "VCONFIG.DAT");
   }

   /* Give porter a chance */
   if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 10000L) )
      PorterQuit = TRUE;

   /* Open up the control config database */
   //if(InitVConfigDB ())
   if(0) // FIX FIX FIX CGP This must be repaired 082199
   {
      printf ("Unable to Open Versacom Configuration Database\n");
      CTIExit (EXIT_PROCESS, -1);
   }

   /* Do this forever */
   for(;!PorterQuit;)
   {
      ConfigsDone = 0;
      ServicesDone = 0;
      UtilIDsDone = 0;

      /* Take a crack at opening the file on an exclusive basis */
      if((i = CTIOpen (Name,
                       &HFile,
                       &Action,
                       0L,
                       FILE_READONLY,
                       FILE_OPEN,
                       OPEN_ACCESS_READONLY | OPEN_SHARE_DENYREADWRITE,
                       0L)) != NORMAL)
      {
         if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 1000L) )
            PorterQuit = TRUE;

         continue;
      }

      /* Ok it is there so close it and open it up in C */
      CTIClose (HFile);

      if((File = fopen (Name, "r")) == NULL) continue;

      SendTextToLogger ("Inf", "Processing Started: VCONFIG.DAT");

      /* Now loop through contents */
      for(;;)
      {
         GetString (File, Buffer, sizeof (Buffer));

         if(sscanf (Buffer, "%hd", &Function) != 1 || Function == 0)
         {
            fclose (File);
            CTIDelete (Name);
            break;
         }

         /* get the routing information */
         GetString (File, Buffer, sizeof (Buffer));

         /* Do not check for null on routes... it just means default routing */
         for(i = 0; i < STANDNAMLEN; i++)
         {
            if(Buffer[i] == '\0')
            {
               break;
            }

            Route[i] = toupper (Buffer[i]);
         }

         for(; i < STANDNAMLEN; i++)
         {
            Route[i] = ' ';
         }


         /* get the unique value */
         GetString (File, Buffer, sizeof (Buffer));

         if(sscanf (Buffer, "%ld", &VSt.Address) != 1)
         {
            fclose (File);
            CTIDelete (Name);
            break;
         }

         /* Check if we need to get other address info */
         if(VSt.Address == 0L)
         {
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &VSt.UtilityID) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            /* Check if we need to get utility ID */
            if(VSt.UtilityID == 0)
            {
               /* get the ID from the system */
               GetUtilID (&VSt.UtilityID);
            }

            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &VSt.Section) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &VSt.Class) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &VSt.Division) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }
         }

         if(Function == 1)
         {
            VSt.CommandType   =  VCONFIG;
            // VSt.Priority      = 0;

            /* get the CTIDBG_new auxilary ID */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd\n", &VSt.VConfig.Data[0]) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            if(!CheckUtilID (VSt.VConfig.Data[0]))
            {
               memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
               VSt.VConfig.ConfigType = VCONFIG_AUXID;
               VSend (&VSt, Route, TRUE);
            }
            else
         {
            printf ("Bad New Auxilary ID\n");
         }


            /* Do a CTIDBG_new Section address */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &VSt.VConfig.Data[0]) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }
            memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
            VSt.VConfig.ConfigType = VCONFIG_SECTION;
            VSend (&VSt, Route, TRUE);


            /* get the line containing the CTIDBG_new Class address(s) */
            GetString (File, Buffer, sizeof (Buffer));

            if(Buffer[0] == '\0')
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            /* Check for the method we will use */
            if(Buffer[0] == 'x' || Buffer[0] == 'X')
            {
               /* This is a straight mask */
               if(sscanf (Buffer + 1, "%hx", &Address) != 1)
               {
                  printf ("Invalid Class Hex Mask\n");
               }
               else
               {
                  IAddress = 0;

                  /* Invert The Address */
                  for(i = 0; i < 16; i++)
                  {
                     IAddress |= ((Address >> i) & 0x00000001) << (15 - i);
                  }

                  VSt.VConfig.Data[0] = HIBYTE (IAddress);
                  VSt.VConfig.Data[1] = LOBYTE (IAddress);
                  memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
                  VSt.VConfig.ConfigType = VCONFIG_CLASS;
                  VSend (&VSt, Route, TRUE);
               }
            }
            else
            {
               IAddress = 0;

               /* Tokenize the input string and set the appropriate masks */
               AddTok = strtok (Buffer, ",");

               while(AddTok != NULL)
               {
                  /* Check this token */
                  if(!(Address = atoi (AddTok)))
                  {
                     IAddress = 0;
                     break;
                  }

                  /* Check to be sure it is in the range */
                  if(Address > 16)
                  {
                     printf ("New Class out of Range\n");
                  }
                  else
                  {
                     IAddress |= (0x8000 >> (Address - 1));
                  }

                  /* get the next token */
                  AddTok = strtok (NULL, ",");
               }

               VSt.VConfig.Data[0] = HIBYTE (IAddress);
               VSt.VConfig.Data[1] = LOBYTE (IAddress);
               memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
               VSt.VConfig.ConfigType = VCONFIG_CLASS;
               VSend (&VSt, Route, TRUE);
            }

            /* get the line containing the CTIDBG_new Divison address(s) */
            GetString (File, Buffer, sizeof (Buffer));

            if(Buffer[0] == '\0')
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            /* Check for the method we will use */
            if(Buffer[0] == 'x' || Buffer[0] == 'X')
            {
               /* This is a straight mask */
               if(sscanf (Buffer + 1, "%hx", &Address) != 1)
               {
                  printf ("Invalid Divison Hex Mask\n");
               }
               else
               {
                  IAddress = 0;

                  /* Invert The Address */
                  for(i = 0; i < 16; i++)
                  {
                     IAddress |= ((Address >> i) & 0x00000001) << (15 - i);
                  }

                  VSt.VConfig.Data[0] = HIBYTE (IAddress);
                  VSt.VConfig.Data[1] = LOBYTE (IAddress);
                  memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
                  VSt.VConfig.ConfigType = VCONFIG_DIVISION;
                  VSend (&VSt,  Route, TRUE);
               }
            }
            else
            {
               IAddress = 0;

               /* Tokenize the input string and set the appropriate masks */
               AddTok = strtok (Buffer, ",");

               while(AddTok != NULL)
               {
                  /* Check this token */
                  if(!(Address = atoi (AddTok)))
                  {
                     IAddress = 0;
                     break;
                  }

                  /* Check to be sure it is in the range */
                  if(Address > 16)
                  {
                     printf ("New Division out of Range\n");
                  }
                  else
                  {
                     IAddress |= (0x8000 >> (Address - 1));
                  }

                  /* get the next token */
                  AddTok = strtok (NULL, ",");
               }

               VSt.VConfig.Data[0] = HIBYTE (IAddress);
               VSt.VConfig.Data[1] = LOBYTE (IAddress);
               memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
               VSt.VConfig.ConfigType = VCONFIG_DIVISION;
               VSend (&VSt, Route, TRUE);
            }

            ConfigsDone++;
         }

         if(Function ==  2)
         {
            GetString (File,
                       Buffer,
                       sizeof (Buffer));

            if(sscanf (Buffer, "%hd", &Service) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            VSt.CommandType = VSERVICE;
            // VSt.Priority = MAXPRIORITY - 1;

            if(Service)
               VSt.Service = VSERVICE_CONTIN | VSERVICE_TEMPIN;
            else
               VSt.Service = VSERVICE_CONTOUT | VSERVICE_TEMPIN;

            VSend (&VSt, Route, TRUE);

            ServicesDone++;
         }

         if(Function == 3)
         {
            VSt.CommandType =  VCONFIG;

            /* get the CTIDBG_new utility ID */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hd\n", &VSt.VConfig.Data[0]) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            if(!CheckUtilID (VSt.VConfig.Data[0]))
            {
               memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
               VSt.VConfig.ConfigType = VCONFIG_UTILID;
               VSend (&VSt, Route, TRUE);
               UtilIDsDone++;
            }
            else
            {
               printf ("Bad New Utility ID\n");
            }
         }

         if(Function == 4)
         {
            /* get the configuration Name */
            GetString (File, Buffer, sizeof (Buffer));

            if(Buffer[0] == '\0')
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }

            for(i = 0; i < STANDNAMLEN; i++)
            {
               if(Buffer[i] == '\0')
               {
                  break;
               }

               VConfigRecord.VConfigName[i] = toupper(Buffer[i]);
            }

            for(; i < STANDNAMLEN; i++)
            {
               VConfigRecord.VConfigName[i] = ' ';
            }

            /* Check for this record in the verscom configuration file */
            if(!(VConfigGetEqual (&VConfigRecord)))
            {
               /* Load 'er up */
               VSt.CommandType =  VCONFIG;

               /* Do the CTIDBG_new auxilary ID */
               VSt.VConfig.Data[0] = (UCHAR)VConfigRecord.UtilityID;
               memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
               VSt.VConfig.ConfigType = VCONFIG_AUXID;
               VSend (&VSt,
                      Route,
                      TRUE);

               /* Do a CTIDBG_new Section address */
               VSt.VConfig.Data[0] = (UCHAR)VConfigRecord.Section;
               memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
               VSt.VConfig.ConfigType = VCONFIG_SECTION;
               VSend (&VSt, Route, TRUE);


               /* Do a CTIDBG_new Class address */
               /* Invert The Address */
               IAddress = 0;
               for(i = 0; i < 16; i++)
               {
                  IAddress |= ((VConfigRecord.Class >> i) & 0x00000001) << (15 - i);
               }

               VSt.VConfig.Data[0] = HIBYTE (IAddress);
               VSt.VConfig.Data[1] = LOBYTE (IAddress);
               memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
               VSt.VConfig.ConfigType = VCONFIG_CLASS;
               VSend (&VSt, Route, TRUE);


               /* Do a CTIDBG_new Division address */
               /* Invert The Address */
               IAddress = 0;
               for(i = 0; i < 16; i++)
               {
                  IAddress |= ((VConfigRecord.Division >> i) & 0x00000001) << (15 - i);
               }

               VSt.VConfig.Data[0] = HIBYTE (IAddress);
               VSt.VConfig.Data[1] = LOBYTE (IAddress);
               memset (&VSt.VConfig.Data[2], 0, sizeof (VSt.VConfig.Data) - 2);
               VSt.VConfig.ConfigType = VCONFIG_DIVISION;
               VSend (&VSt, Route, TRUE);

               ConfigsDone++;

               /* Check if we are to send this one to the logger */
               if(VSt.Address != 0)
               {
                  sprintf (Buffer, "%012d        %20.20s",
                           VSt.Address,
                           VConfigRecord.VConfigName);

                  SendTextToLogger ("Cfg", Buffer);
               }


            }
            else
            {
               sprintf (Buffer, "Invalid Config      %20.20s", VConfigRecord.VConfigName);
               SendTextToLogger ("Inf", Buffer);
               printf ("Configuration not found\n");
            }
         }
      }

      /* Send Results to the logger */
      if(ConfigsDone)
      {
         sprintf (Buffer, "Configs Processed   %ld", ConfigsDone);
         SendTextToLogger ("Inf", Buffer);
      }

      if(ServicesDone)
      {
         sprintf (Buffer, "Services Processed  %ld", ServicesDone);
         SendTextToLogger ("Inf", Buffer);
      }

      if(UtilIDsDone)
      {
         sprintf (Buffer, "UtilityIDs Processed%ld", UtilIDsDone);
         SendTextToLogger ("Inf", Buffer);
      }

      SendTextToLogger ("Inf", "Processing Complete:VCONFIG.DAT");

      /* Wait a bit for next file */
      CTISleep (1000L);
   }
}


/* Routine to build up message to send out */
VSend (VSTRUCT *VSt,
       PCHAR   Route,
       USHORT  CheckTimed)

{
   ULONG      i;
   CtiRouteSPtr RouteRecord;
   ROUTEMACRO RouteMacroRecord;

   /* Make sure that we are blank padded out to STANDNAMLEN */
   for(i = 0; i < STANDNAMLEN; i++)
   {
      if(Route[i] == '\0' || Route[i] == '\n')
      {
         Route[i] = ' ';
         if((i + 1) < STANDNAMLEN)
         {
            Route[i + 1] = '\0';
         }
      }
   }

   /* Do this base on the route passed */
   switch(Route[0])
   {
   case ' ':
      {
#ifdef OLD_WAY
         if(!(RouteGetFirst (&RouteRecord)))
         {
            do
            {
               if(RouteRecord.Timed || !CheckTimed)
               {
                  VSend2 (VSt, &RouteRecord);
               }
            }  while(!(RoutegetGT (&RouteRecord)));
         }
#else

         {
            CtiRouteManager::LockGuard  guard(RouteManager.getMux());        // Protect our iteration!

            CtiRouteManager::spiterator   rte_itr;

            /* Now do the routes */
            for(rte_itr = RouteManager.begin() ; rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr) )
            {
               CtiRouteSPtr RouteRecord = rte_itr->second;

               // A timed route is the original nomenclature fro a default route. 072099 CGP
               if(RouteRecord->isValid() && (RouteRecord->isDefaultRoute() || !CheckTimed))
               {
                  VSend2 (VSt, RouteRecord);
               }
            }
         }
#endif
         break;
      }
      /* Walk through the routes picking all timed routes */
   case '@':
      /* get this route macro */
#ifdef OLD_WAY
      memcpy (RouteMacroRecord.RouteMacroName, Route, STANDNAMLEN);
      if(RouteMacrogetEqual (&RouteMacroRecord))
      {
         printf ("Unknown Route Macro\n");
         break;
      }

      /* Send it on each route of the macro */
      for(i = 0; i < 30; i++)
      {
         if(RouteMacroRecord.RouteName[i][0] != ' ')
         {
            VSend (VSt,
                   RouteMacroRecord.RouteName[i],
                   FALSE);
         }
      }
#else

      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Route Macros were properly broken on 072199 by CGP " << endl;
      }

#endif

      break;

   default:
      /* get the route */
      RouteRecord = RouteManager.getEqual(atol(Route));

      if(RouteRecord)
      {
         VSend2 (VSt, RouteRecord);
      }
      break;
   }

   return(NORMAL);
}


/* Routine to actually send the message */
VSend2 (VSTRUCT *VSt, CtiRouteSPtr RouteRecord)

{
   ULONG i;
   OUTMESS OutMessage;
   CtiDeviceSPtr RemoteRecord;

   /* get the remote record for this beast */
   RemoteRecord = DeviceManager.getEqual(RouteRecord->getTrxDeviceID());

   if(RemoteRecord)
   {
      /* Finsish loading up versacom structure */

      /* Load the peices into outmessage */
      OutMessage.DeviceID    = RemoteRecord->getID();
      OutMessage.Port        = RemoteRecord->getPortID();
      OutMessage.Remote      = RemoteRecord->getAddress();
      OutMessage.Priority    = 0;
      OutMessage.Retry       = 2;
      OutMessage.EventCode   = VERSACOM | NOWAIT | NORESULT;
      OutMessage.Sequence    = 0;
      OutMessage.ReturnNexus = NULL;
      OutMessage.SaveNexus   = NULL;

      OutMessage.Buffer.VSt = *VSt;

      /* Build and send message */
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << "**** ADD CODE HERE Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
      return NORMAL; //   return(VersacomSend (&OutMessage));
   }

   return !NORMAL;
}


GetString (FILE *File, PCHAR Buffer, ULONG Length)
{
   ULONG i;
   CHAR Save;

   for(i = 0; i < Length;  i++)
   {
      Buffer[i] = fgetc (File);

      if(Buffer[i] == (BYTE) EOF)
      {
         Buffer[i] = '\0';
         return(NORMAL);
      }

      if(Buffer[i] == '\r' || Buffer[i] == '\n')
      {
         if(i == 0)
         {
            do
            {
               Buffer[i] = fgetc (File);
            } while(Buffer[i] == '\r' || Buffer[i] == '\n');

            if(Buffer[i] == (BYTE) EOF)
            {
               Buffer[i] = '\0';
               return(NORMAL);
            }

            continue;
         }

         Buffer[i] = '\0';
         return(NORMAL);
      }
   }

   /* Not enough buffer space so get to start of next record or end */
   Buffer[Length - 1] = '\0';

   do
   {
      Save = fgetc (File);
   } while(Save != '\r' && Save != '\n' && Save != (BYTE) EOF);

   return(!(NORMAL));
}


