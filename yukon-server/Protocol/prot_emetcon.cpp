

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_emetcon
*
* Date:   2/22/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_emetcon.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/06/11 16:50:31 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw\rwtime.h>

#include "cmdparse.h"
#include "devicetypes.h"
#include "dllbase.h"
#include "porter.h"
#include "prot_emetcon.h"
#include "yukon.h"
#include "logger.h"


CtiProtocolEmetcon& CtiProtocolEmetcon::setDouble(BOOL dbl)
{
   _double = dbl;
   return *this;
}

BOOL CtiProtocolEmetcon::getDouble() const
{
   return _double;
}


INT CtiProtocolEmetcon::primeOut(const OUTMESS &OutTemplate)
{
   INT      status = NORMAL;

   OUTMESS  *Out = new OUTMESS(OutTemplate);

   if(Out != NULL)
   {
      _out.insert( Out );
      _last = _out.entries() - 1;
   }
   else
   {
      status = MEMORY;
   }

   return status;
}

INT CtiProtocolEmetcon::advanceAndPrime(const OUTMESS &OutTemp)
{
   return primeOut(OutTemp);
}

INT CtiProtocolEmetcon::parseRequest(CtiCommandParser  &parse, OUTMESS &aOutTemplate)
{
    INT            status = NORMAL;

    switch( parse.getCommand() )
    {
        case LoopbackRequest:
        case ScanRequest:
        case GetValueRequest:
        case ControlRequest:
        case GetStatusRequest:
        case GetConfigRequest:
        case PutValueRequest:
        case PutConfigRequest:
        case PutStatusRequest:
        {
            assembleCommand(parse, aOutTemplate);
            break;
        }

        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand() << endl;
            }

            status = NoMethod;

            break;
        }
    }

    return status;
}


INT CtiProtocolEmetcon::assembleCommand(CtiCommandParser  &parse, OUTMESS &aOutTemplate)
{
   INT   status = NORMAL;

   status = primeOut(aOutTemplate);                   // Copy and add to the list.

   if(status == NORMAL)
   {
      _out[_last]->EventCode |= BWORD;          // Make sure we know the basics

      status = buildMessages(parse, aOutTemplate);
   }

   return status;
}

INT CtiProtocolEmetcon::buildMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   OUTMESS *curOutMessage= _out[_last];                 // Grab a pointer to the current OUTMESS

   if(curOutMessage->EventCode & AWORD)
   {
      curOutMessage->EventCode &= ~BWORD;  //  maybe we just shouldn't set it above in assembleCommand... ?
      status = buildAWordMessages(parse, aOutTemplate);
   }
   else if(curOutMessage->EventCode & BWORD)
   {
      status = buildBWordMessages(parse, aOutTemplate);
   }

   return status;
}

INT CtiProtocolEmetcon::buildAWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   OUTMESS *curOutMessage= _out[_last];                 // Grab a pointer to the current OUTMESS

   // Use the template's ASt since Buffer.OutMessage & Buffer.ASt are members of a union.
   APreamble(curOutMessage->Buffer.OutMessage + PREIDLEN, aOutTemplate.Buffer.ASt);
   A_Word(curOutMessage->Buffer.OutMessage + PREIDLEN + 3, aOutTemplate.Buffer.ASt);

   /* calculate an approximate timeout */
   curOutMessage->TimeOut = TIMEOUT + aOutTemplate.Buffer.ASt.DlcRoute.Stages;

   /* Calculate the message lengths */
   curOutMessage->OutLength = AWORDLEN + PREAMLEN + 3;
   curOutMessage->InLength = 2;

   return status;
}

INT CtiProtocolEmetcon::buildBWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   INT wordCount = 0;

   OUTMESS *curOutMessage= _out[_last];                 // Grab a pointer to the current OUTMESS

   if( aOutTemplate.Buffer.BSt.IO & IO_READ )
   {
      /* build preamble message.  At this point wordCount represents the number of outbound cwords (it is zero for a read!) */

      BPreamble (curOutMessage->Buffer.OutMessage + PREIDLEN, aOutTemplate.Buffer.BSt, wordCount);

      // We need to determine the Number of Words (DWORDS) we expect from the DLC device.
      // That is done based upon the BSt.Length, and is needed by the B_Word call.

      wordCount = determineDWordCount(curOutMessage->Buffer.BSt.Length);
      curOutMessage->InLength = 3 + wordCount * (DWORDLEN + 1);                     // InLength is based upon the read request/function requested.
      curOutMessage->OutLength = PREAMLEN + BWORDLEN + 3;                           // OutLength is fixed (only the B read request)
      // 20020611 CGP Hep the repeaters? // curOutMessage->TimeOut = TIMEOUT + aOutTemplate.Buffer.BSt.DlcRoute.Stages;   // Therefore trx time is too
      curOutMessage->TimeOut = TIMEOUT + aOutTemplate.Buffer.BSt.DlcRoute.Stages * (wordCount + 1);   // Therefore trx time is too

      /*  build the b word
       *
       *  At this point wordCount represents the number of dwords expected.
       */

      B_Word (curOutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, aOutTemplate.Buffer.BSt, wordCount, getDouble());
   }
   else
   {
      if(aOutTemplate.Buffer.BSt.Length == 0)   /*  IO_FCT_WRITE - function write */
      {
         // Function writes are fully qualified already, however, they may require an ARM message.
         // To preceed them to the device.
         if( generateARMMessage(parse, aOutTemplate) )
         {
            curOutMessage = _out[_last];              // reset it since generate changed _out[_last]!
         }
      }
      else           /* IO_WRITE */
      {
         // Nail the CWORDS into the correct location in the current message.
         C_Words (curOutMessage->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN,
                  (unsigned char *)aOutTemplate.Buffer.BSt.Message,
                  aOutTemplate.Buffer.BSt.Length,
                  &wordCount);
      }

      /* build preamble message
       *
       *  At this point wordCount represents the number of outbound cwords (it is zero for a function write!)
       */

      BPreamble (curOutMessage->Buffer.OutMessage + PREIDLEN, aOutTemplate.Buffer.BSt, wordCount);
      curOutMessage->TimeOut = TIMEOUT + aOutTemplate.Buffer.BSt.DlcRoute.Stages * (wordCount + 1);

      /* calculate message lengths */
      curOutMessage->OutLength = PREAMLEN + BWORDLEN + wordCount * CWORDLEN + 3;
      curOutMessage->InLength = 2;

      /*  build the b word
       *
       *  At this point wordCount represents:
       *   IO_READ: the number of dwords expected.
       *   IO_WRITE / IO_FCT_WRITE: the number of cwords being sent out to the field.
       */

      B_Word (curOutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, aOutTemplate.Buffer.BSt, wordCount, getDouble());
   }

   /* load the IDLC specific stuff for DTRAN */
   curOutMessage->Source                = 0;
   curOutMessage->Destination           = DEST_DLC;
   curOutMessage->Command               = CMND_DTRAN;
   curOutMessage->Buffer.OutMessage[6]  = (UCHAR)aOutTemplate.InLength;
   curOutMessage->EventCode             &= ~RCONT;

   return status;
}


INT CtiProtocolEmetcon::determineDWordCount(INT Length)
{
   INT cnt = 0;

   /* calculate number of d words to ask for */
   if(Length > 0 && Length <= 3)
      cnt = 1;
   else if(Length > 3 && Length <= 8)
      cnt = 2;
   else if(Length > 8 && Length <= 13)
      cnt = 3;

   return cnt;
}

bool CtiProtocolEmetcon::generateARMMessage(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   bool status = false;

   OUTMESS *armOutMessage = _out[_last];                 // Grab a pointer to the current OUTMESS

   switch(armOutMessage->Buffer.BSt.Function)
   {
   case 0x41:
   case 0x42:
   case 0x51:
   case 0x52:
      {
         status = true;    // We must ARM

         OUTMESS PeekMessage = *armOutMessage;

         /* We need to generate the appropriate arm command */
         advanceAndPrime(*armOutMessage);    // This copied the OUTMESS at the end of the list.
         OUTMESS *curOutMessage = _out[_last];

         /* Make sure we decode it on the return */
         armOutMessage->EventCode |= DTRAN;

         /* Load up an Arm */
         PeekMessage.Buffer.BSt.IO = 0;
         PeekMessage.Buffer.BSt.Length = 0;

         switch(curOutMessage->Buffer.BSt.Function)
         {
         case 0x41:
         case 0x42:
            PeekMessage.Sequence = Control_ARML;
            PeekMessage.Buffer.BSt.Function = ARML;
            break;

         case 0x51:
         case 0x52:
            PeekMessage.Sequence = Control_ARMS;
            PeekMessage.Buffer.BSt.Function = ARMS;
            break;
         }

         armOutMessage->InLength = 2;

         /* build the b word */
         BPreamble(armOutMessage->Buffer.OutMessage + PREIDLEN, PeekMessage.Buffer.BSt, 0);
         B_Word(armOutMessage->Buffer.OutMessage + PREIDLEN + PREAMLEN, PeekMessage.Buffer.BSt, getDouble());

         armOutMessage->Sequence = PeekMessage.Sequence;

         armOutMessage->TimeOut = TIMEOUT + PeekMessage.Buffer.BSt.DlcRoute.Stages;
         armOutMessage->OutLength = PREAMLEN + BWORDLEN + 3;

         /* load the IDLC specific stuff for DTRAN */
         armOutMessage->Source = 0;
         armOutMessage->Destination = DEST_DLC;
         armOutMessage->Command = CMND_DTRAN;
         armOutMessage->Buffer.OutMessage[6] = (UCHAR)armOutMessage->InLength;
         armOutMessage->EventCode &= ~RCONT;
      }
   }

   return status;
}

OUTMESS CtiProtocolEmetcon::getOutMessage(INT pos) const
{
   return *_out[pos];
}
OUTMESS& CtiProtocolEmetcon::getOutMessage(INT pos)
{
   return *_out[pos];
}

OUTMESS* CtiProtocolEmetcon::popOutMessage()
{
   OUTMESS *ptr = _out.removeFirst();
   _last = _out.entries() - 1;

   return ptr;
}


CtiProtocolEmetcon& CtiProtocolEmetcon::setTransmitterType(INT type)
{
   _transmitterType = type;
   return *this;
}
INT CtiProtocolEmetcon::getTransmitterType() const
{
   return _transmitterType;
}

CtiProtocolEmetcon& CtiProtocolEmetcon::setSSpec(INT spec)
{
   _sspec = spec;
   return *this;
}
INT CtiProtocolEmetcon::getSSpec() const
{
   return _sspec;
}

CtiProtocolEmetcon& CtiProtocolEmetcon::setIED(INT ied)
{
   _ied = ied;
   return *this;
}
INT CtiProtocolEmetcon::getIED() const
{
   return _ied;
}

CtiProtocolEmetcon& CtiProtocolEmetcon::setDeviceType(INT type)
{
   _deviceType = type;
   return *this;
}
INT CtiProtocolEmetcon::getDeviceType() const
{
   return _deviceType;
}



