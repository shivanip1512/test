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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/12/20 17:19:55 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "cmdparse.h"
#include "devicetypes.h"
#include "dllbase.h"
#include "porter.h"
#include "prot_emetcon.h"
#include "logger.h"


namespace Cti      {
namespace Protocol {


Emetcon::Emetcon(INT devType, INT transmitterType) :
    _ied(0),
    _last(0),
    _deviceType(devType),
    _transmitterType(transmitterType),
    _double(FALSE)
{
}


Emetcon::Emetcon(const Emetcon& aRef)  :
    _ied(0),
    _last(0),
    _double(FALSE)
{
    _out.clearAndDestroy();
    *this = aRef;
}


Emetcon::~Emetcon()
{
    _out.clearAndDestroy();
}


INT Emetcon::entries() const
{
    return _out.entries();
}


Emetcon& Emetcon::operator=(const Emetcon& aRef)
{
    if(this != &aRef)
    {
        _out.clearAndDestroy();

        for( int i = 0; i < aRef.entries(); i++ )
        {
            _ied = getIED();

            OUTMESS *Out = CTIDBG_new OUTMESS(aRef.getOutMessage(i));
            if(Out != NULL)
            {
                _out.insert( Out );
            }
        }

        _sspec = aRef.getSSpec();
        _transmitterType  = aRef.getTransmitterType();
    }
    return *this;
}


Emetcon& Emetcon::setDouble(BOOL dbl)
{
   _double = dbl;
   return *this;
}


BOOL Emetcon::getDouble() const
{
   return _double;
}


INT Emetcon::primeOut(const OUTMESS &OutTemplate)
{
   INT status = NORMAL;

   OUTMESS *Out = CTIDBG_new OUTMESS(OutTemplate);

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


INT Emetcon::advanceAndPrime(const OUTMESS &OutTemp)
{
   return primeOut(OutTemp);
}


INT Emetcon::parseRequest(CtiCommandParser  &parse, OUTMESS &aOutTemplate)
{
    INT status = NORMAL;

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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand() << endl;
            }

            status = NoMethod;

            break;
        }
    }

    return status;
}


INT Emetcon::assembleCommand(CtiCommandParser  &parse, OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   status = primeOut(aOutTemplate);                   // Copy and add to the list.

   if(status == NORMAL)
   {
      _out[_last]->EventCode |= BWORD;          // Make sure we know the basics

      status = buildMessages(parse, aOutTemplate);
   }

   return status;
}

INT Emetcon::buildMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   OUTMESS *curOutMessage= _out[_last];                 // Grab a pointer to the current OUTMESS

   if(curOutMessage->EventCode & AWORD)
   {
      // Add these two items to the list for control accounting!
      parse.setValue("control_interval", getControlInterval(parse));
      parse.setValue("control_reduction", 100 );

      curOutMessage->EventCode &= ~BWORD;  //  maybe we just shouldn't set it above in assembleCommand... ?
      status = buildAWordMessages(parse, aOutTemplate);
   }
   else if(curOutMessage->EventCode & BWORD)
   {
      status = buildBWordMessages(parse, aOutTemplate);
   }

   return status;
}

INT Emetcon::buildAWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
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

INT Emetcon::buildBWordMessages(CtiCommandParser  &parse, const OUTMESS &aOutTemplate)
{
   INT status = NORMAL;

   INT wordCount = 0;

   OUTMESS *curOutMessage= _out[_last];                 // Grab a pointer to the current OUTMESS

   if( aOutTemplate.Buffer.BSt.IO & Emetcon::IO_Read )
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
      if(aOutTemplate.Buffer.BSt.Length > 0)
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
       *   IO_Read  / IO_Function_Read:  the number of dwords expected.
       *   IO_Write / IO_Function_Write: the number of cwords being sent out to the field.
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


INT Emetcon::determineDWordCount(INT Length)
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

OUTMESS Emetcon::getOutMessage(INT pos) const
{
   return *_out[pos];
}
OUTMESS& Emetcon::getOutMessage(INT pos)
{
   return *_out[pos];
}

OUTMESS* Emetcon::popOutMessage()
{
   OUTMESS *ptr = _out.removeFirst();
   _last = _out.entries() - 1;

   return ptr;
}


Emetcon& Emetcon::setTransmitterType(INT type)
{
   _transmitterType = type;
   return *this;
}
INT Emetcon::getTransmitterType() const
{
   return _transmitterType;
}

Emetcon& Emetcon::setSSpec(INT spec)
{
   _sspec = spec;
   return *this;
}
INT Emetcon::getSSpec() const
{
   return _sspec;
}

Emetcon& Emetcon::setIED(INT ied)
{
   _ied = ied;
   return *this;
}
INT Emetcon::getIED() const
{
   return _ied;
}

Emetcon& Emetcon::setDeviceType(INT type)
{
   _deviceType = type;
   return *this;
}
INT Emetcon::getDeviceType() const
{
   return _deviceType;
}

int Emetcon::getControlInterval(CtiCommandParser &parse) const
{
    int nRet = 0;
    int Seconds = parse.getiValue("shed", 0);

    if(Seconds < 5)
    {
        // This must be a restore
        nRet = 0;
    }
    else if(Seconds < 451)
    {
        nRet = 450;
    }
    else if(Seconds < 901)
    {
        nRet = 900;
    }
    else if(Seconds < 1801)
    {
        nRet = 1800;
    }
    else if(Seconds < 3601)
    {
        nRet = 3600;
    }
    else
    {
        nRet = 3600;
    }

    return nRet;
}



};
};

