/*-----------------------------------------------------------------------------*
*
* File:   PORTCONF
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/PORTCONF.cpp-arc  $
* REVISION     :  $Revision: 1.15.2.2 $
* DATE         :  $Date: 2008/11/21 16:14:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

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
#include "porter.h"
#include "master.h"
#include "elogger.h"

#include "portglob.h"
#include "c_port_interface.h"
#include "dev_base.h"
#include "portdecl.h"
#include "mgr_device.h"
#include "mgr_route.h"

#include "logger.h"
#include "guard.h"

extern CtiRouteManager RouteManager;

#define ROUTESINMACRO 30

using std::endl;

/* Type For Route Macro Database */
typedef struct _ROUTEMACRO {
    CHAR RouteMacroName[STANDNAMLEN];   // user given macro route name
    CHAR RouteName[ROUTESINMACRO][STANDNAMLEN];    // user given logical route name
} ROUTEMACRO;

/* Prototype of function to get strings from file */
int GetString (FILE *, PCHAR, ULONG);

/*  Definition for VERSACOM Configuration DB */
/* VersaCom Load Config Structure */
typedef struct _VERSALOAD {
    USHORT CycleCount;
    USHORT ScramTime;
    USHORT ColdLoadTime;
    USHORT EnableDiscrOffset;
} VERSALOAD;



/* VersaCom Configuration Structure */
typedef struct _VERSACONFIG {
    CHAR VConfigName[STANDNAMLEN];
    USHORT UtilityID;
    USHORT Section;
    USHORT Class;
    USHORT Division;
    USHORT CyclePeriod;
    USHORT PropDispTime;
    VERSALOAD LoadConfig[3];
} VERSACONFIG;


IM_EX_CTIBASE INT VConfigGetEqual (VERSACONFIG *);


INT CheckUtilID (USHORT a)
{
   std::cout <<" Progress: " << __FILE__ << " " << __LINE__ << std::endl;
   return 0;
}

INT GetUtilID (PUSHORT a)
{
   std::cout <<" Progress: " << __FILE__ << " " << __LINE__ << std::endl;
   return 0;
}


void VConfigThread()
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

   CTILOG_INFO(dout, "VConfig Thread Started");

   /* Figure out what the name is */
   if((gConfigParms.getValueAsString("PORTER_VCONFIGPATH")).empty())
   {
      strcpy (Name, "CONFIG\\VCONFIG.DAT");
   }
   else
   {
      strcpy (Name, gConfigParms.getValueAsString("PORTER_VCONFIGPATH").c_str() );
      strcat (Name, "VCONFIG.DAT");
   }

   /* Give porter a chance */
   if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 10000L) )
      PorterQuit = TRUE;

   /* Open up the control config database */
   //if(InitVConfigDB ())
   if(0) // FIX FIX FIX CGP This must be repaired 082199
   {
      CTILOG_FATAL(dout, "Unable to Open Versacom Configuration Database");
      exit(-1);
   }

   /* Do this forever */
   for(;!PorterQuit;)
   {
      ConfigsDone = 0;
      ServicesDone = 0;
      UtilIDsDone = 0;

      /* Take a crack at opening the file on an exclusive basis */
      if( i = CTIOpen (Name,
                       &HFile,
                       &Action,
                       0L,
                       FILE_READONLY,
                       FILE_OPEN,
                       OPEN_ACCESS_READONLY | OPEN_SHARE_DENYREADWRITE,
                       0L) )
      {
         if( WAIT_OBJECT_0 == WaitForSingleObject(hPorterEvents[P_QUIT_EVENT], 1000L) )
            PorterQuit = TRUE;

         continue;
      }

      /* Ok it is there so close it and open it up in C */
      CTIClose (HFile);

      if((File = fopen (Name, "r")) == NULL) continue;

      CTILOG_INFO(dout, "Processing Started: VCONFIG.DAT");

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

            /* get the new auxilary ID */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hhu\n", &VSt.VConfig.Data[0]) != 1)
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
                CTILOG_ERROR(dout, "Bad New Auxilary ID");
            }


            /* Do a new Section address */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hhu", &VSt.VConfig.Data[0]) != 1)
            {
               fclose (File);
               CTIDelete (Name);
               break;
            }
            memset (&VSt.VConfig.Data[1], 0, sizeof (VSt.VConfig.Data) - 1);
            VSt.VConfig.ConfigType = VCONFIG_SECTION;
            VSend (&VSt, Route, TRUE);


            /* get the line containing the new Class address(s) */
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
                   CTILOG_ERROR(dout, "Invalid Class Hex Mask");
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
                      CTILOG_ERROR(dout, "New Class out of Range");
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

            /* get the line containing the new Divison address(s) */
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
                   CTILOG_ERROR(dout, "Invalid Divison Hex Mask");
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
                      CTILOG_ERROR(dout, "New Division out of Range");
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

            /* get the new utility ID */
            GetString (File, Buffer, sizeof (Buffer));

            if(sscanf (Buffer, "%hhu\n", &VSt.VConfig.Data[0]) != 1)
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
                CTILOG_ERROR(dout, "Bad New Utility ID");
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
         }
      }

      /* Send Results to the logger */
      if(ConfigsDone)
      {
          CTILOG_INFO(dout, "Configs Processed : "<< ConfigsDone);
      }

      if(ServicesDone)
      {
          CTILOG_INFO(dout, "Services Processed : "<< ServicesDone);
      }

      if(UtilIDsDone)
      {
          CTILOG_INFO(dout, "UtilityIDs Processed : "<< UtilIDsDone);
      }

      CTILOG_INFO(dout, "Processing Complete: VCONFIG.DAT");

      /* Wait a bit for next file */
      CTISleep (1000L);
   }
}


/* Routine to build up message to send out */
INT VSend (VSTRUCT *VSt,
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

        CtiRouteManager::coll_type::reader_lock_guard_t guard(RouteManager.getLock());

        CtiRouteManager::spiterator   rte_itr;

        /* Now do the routes */
        for(rte_itr = RouteManager.begin() ; rte_itr != RouteManager.end(); CtiRouteManager::nextPos(rte_itr) )
        {
           CtiRouteSPtr aRouteRecord = rte_itr->second;

           // A timed route is the original nomenclature fro a default route. 072099 CGP
           if(aRouteRecord->isValid() && (aRouteRecord->isDefaultRoute() || !CheckTimed))
           {
              VSend2 (VSt, aRouteRecord);
           }
        }
      }
      break;
      /* Walk through the routes picking all timed routes */
   case '@':
      /* get this route macro */

      CTILOG_INFO(dout, "Route Macros were properly broken on 072199 by CGP");

      break;

   default:
      /* get the route */
      RouteRecord = RouteManager.getRouteById(atol(Route));

      if(RouteRecord)
      {
         VSend2 (VSt, RouteRecord);
      }
      break;
   }

   return ClientErrors::None;
}


/* Routine to actually send the message */
INT VSend2 (VSTRUCT *VSt, CtiRouteSPtr RouteRecord)

{
   ULONG i;
   OUTMESS OutMessage;
   CtiDeviceSPtr RemoteRecord;

   /* get the remote record for this beast */
   RemoteRecord = DeviceManager.getDeviceByID(RouteRecord->getTrxDeviceID());

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
      CTILOG_WARN(dout, "Functional code missing");

      return ClientErrors::None; //   return(VersacomSend (&OutMessage));
   }

   return ClientErrors::Abnormal;
}


INT GetString (FILE *File, PCHAR Buffer, ULONG Length)
{
   ULONG i;
   CHAR Save;

   for(i = 0; i < Length;  i++)
   {
      Buffer[i] = fgetc (File);

      if(Buffer[i] == (BYTE) EOF)
      {
         Buffer[i] = '\0';
         return ClientErrors::None;
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
               return ClientErrors::None;
            }

            continue;
         }

         Buffer[i] = '\0';
         return ClientErrors::None;
      }
   }

   /* Not enough buffer space so get to start of next record or end */
   Buffer[Length - 1] = '\0';

   do
   {
      Save = fgetc (File);
   } while(Save != '\r' && Save != '\n' && Save != (BYTE) EOF);

   return(!(ClientErrors::None));
}


