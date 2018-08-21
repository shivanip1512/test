#include "precompiled.h"

#include "words.h"
#include "cti_asmc.h"
#include "yukon.h"
#include "logger.h"

using std::endl;


/* Routine A_Word generates an 'A' type message */
INT IM_EX_CTIBASE A_Word (PBYTE AWord, const ASTRUCT &ASt, BOOL Double)      /* A word structure */
{
   /* place info from A word structure into message string */
   if(Double)
      AWord[0] = 0x90 | ASt.DlcRoute.RepVar << 1 | ASt.DlcRoute.RepFixed >> 4;
   else
      AWord[0] = 0x80 | ASt.DlcRoute.RepVar << 1 | ASt.DlcRoute.RepFixed >> 4;
   AWord[1] = ASt.DlcRoute.RepFixed << 4 | ASt.Group >> 2;
   AWord[2] = (ASt.Group << 6 | ASt.Time << 4 | ASt.Function << 1) & 0xfe;

   /* calculate BCh */
   // AWord[3] = BCHCalc_C ((CHAR*)AWord, 24) & 0xfc;
   AWord[3] = BCHCalc_C (AWord, 24) & 0xfc;


   return ClientErrors::None;
}

/* Routine to create a "B" type word */

INT IM_EX_CTIBASE B_Word (PBYTE BWord, const BSTRUCT &BSt, unsigned wordCount, BOOL Double) /* B word structure */

{
   USHORT BCH;

   /* build b word into message string from info in structure */
   if(Double)
      BWord[0] = 0xb0 | BSt.DlcRoute.RepVar << 1 | BSt.DlcRoute.RepFixed >> 4;
   else
      BWord[0] = 0xa0 | BSt.DlcRoute.RepVar << 1 | BSt.DlcRoute.RepFixed >> 4;

   BWord[1]  = (UCHAR)(BSt.DlcRoute.RepFixed << 4 | BSt.Address >> 18);
   BWord[2]  = (UCHAR)(BSt.Address  >> 10);
   BWord[3]  = (UCHAR)(BSt.Address  >> 2);
   BWord[4]  = (UCHAR)(BSt.Address  << 6 | wordCount << 4 | (BSt.Function & 0xff) >> 4);
   BWord[5]  = (UCHAR)(BSt.Function << 4);
   BWord[5] |= (UCHAR)((BSt.IO) << 2);

   /* calculate BCH and place it in message */
   BWord[5] |= (BCH = BCHCalc_C (BWord, 46)) >> 6;
   BWord[6] = BCH << 2;

   return ClientErrors::None;
}


YukonError_t decodeBWord(const unsigned char *BWord, BSTRUCT *BSt)
{
   if( ! isBchValid(BWord) )
   {
      return ClientErrors::BadBch;
   }

   if( (BWord[0] & 0xe0) != 0xa0 )  //  allow both 0xA0 and 0xB0
   {
      return ClientErrors::BadWordType;
   }

   BSt->DlcRoute.RepVar   = (BWord[0] >> 1) & 0x07;
   BSt->DlcRoute.RepFixed = (BWord[0] << 4) & 0x10 |
                            (BWord[1] >> 4) & 0x0f;

   //  can't be determined from an inbound B word, so set them to FFFF
   BSt->DlcRoute.Amp = -1;
   BSt->DlcRoute.Bus = -1;
   BSt->DlcRoute.Stages = -1;

   BSt->Address  = (BWord[1] << 18) & 0x3c0000 |
                   (BWord[2] << 10) & 0x03fc00 |
                   (BWord[3] <<  2) & 0x0003fc |
                   (BWord[4] >>  6) & 0x000003;

   BSt->Length   = (BWord[4] >> 4) & 0x03;

   BSt->Function = (BWord[4] << 4) & 0xf0 |
                   (BWord[5] >> 4) & 0x0f;

   BSt->IO       = (BWord[5] >> 2) & 0x03;

   return ClientErrors::BWordReceived;
}


/* Routine to make single "C" word */

INT IM_EX_CTIBASE C_Word (PBYTE CWord,                        /* result */
                          const PBYTE Mess,                   /* message bytes */
                          USHORT Length)                      /* Length of message */
{
   INT i;
   USHORT BCH;

   /* clear out the message buffer */
   for(i = 1; i < 6; i++)
      CWord[i] = 0;

   /* c Word */
   CWord[0] = 0x00c0;

   /* load the data into the buffer */
   for(i = 0; i < Length; i++)
   {
      CWord[i] |= Mess[i] >> 4;
      CWord[i+1] = Mess[i] << 4;
   }

   /* for partial Word load count */
   if(Length < 5)
      CWord[5] = Length << 4 | 0x04;

   /* calculate and place BCh */
   CWord[5] |= (BCH = BCHCalc_C (CWord, 46)) >> 6;
   CWord[6] = BCH << 2;

   return ClientErrors::None;
}


/* Routine to make multiple "C" words from Message */

INT IM_EX_CTIBASE C_Words (unsigned char * CWords,             /* results */
                           const unsigned char * Message,      /* Message to be converted */
                           unsigned short Length,            /* Length of Message */
                           unsigned int *cword_count)    /* number of c words generated */
{
   unsigned i;

   for(i = 0; i < Length / 5; ++i)
      C_Word ((unsigned char *) (CWords + i * 7), (const PBYTE)( Message + i * 5), (USHORT ) 5 );

   /* if partial word left go ahead and build it */
   if(i * 5 < Length)
   {
      C_Word ((unsigned char *) (CWords + i * 7), (const PBYTE) (Message + i * 5), (USHORT ) (Length - i * 5));
      i++;
   }

   if( cword_count )  *cword_count = i;

   return ClientErrors::None;
}


YukonError_t validateDWord(const unsigned char firstByte)
{
   switch( firstByte & 0xf0 )
   {
      case 0xa0:
      case 0xb0:  return ClientErrors::BWordReceived;
      case 0xd0:  return ClientErrors::None;
      case 0xe0:  return ClientErrors::EWordReceived;
      default:    return ClientErrors::BadWordType;
   }
}


/* Routine to decode "D1" type Message */

YukonError_t D1_Word (const unsigned char *DWord,  /* D word to be decoded */
                           PBYTE Mess,            /* result */
                           PUSHORT RepVar,        /* returned repeater variable bits */
                           PULONG Address,        /* returned lsb 13 bits of address */
                           PUSHORT Power,         /* power fail flag */
                           PUSHORT Alarm)         /* Alarm flag */
{
   if( ! isBchValid(DWord) )
   {
      return ClientErrors::BadBch;
   }

   /* check if we actually have a DWord */
   if( const auto error = validateDWord(DWord[0]) )
   {
      return error;
   }

   /* decode the overhead stuff */
   *RepVar = DWord[0] >> 1 & 0x07;
   *Address = (DWord[0] & 0x01) << 12 | DWord[1] << 4 | DWord[2] >> 4;
   *Power = DWord[5] >> 3 & 0x01;
   *Alarm = DWord[5] >> 2 & 0x01;

   /* get the data out and copy it into the Message string */
   for(int i = 0; i < 3; i++)
   {
      Mess[i] = DWord[i+2] << 4 | DWord[i+3] >> 4;
   }

   return ClientErrors::None;
}


/* Routine to decode a "D2" or "D3" type word */

YukonError_t D23_Word(
        const unsigned char *DWord,  /* d word to be decoded */
        PBYTE Mess,            /* resulting message bytes */
        PUSHORT S1,            /* flag spare1 */
        PUSHORT S2)            /* flag spare2 */
{
   if( ! isBchValid(DWord) )
   {
      return ClientErrors::BadBch;
   }

   /* make sure that we received a d word */
   if( const auto error = validateDWord(DWord[0]) )
   {
      return error;
   }

   /* decode the status bits */
   *S1 = DWord[5] >> 3 & 0x01;
   *S2 = DWord[5] >> 2 & 0x01;

   /* copy the data into the result string */
   for(int i = 0; i < 5; i++)
   {
      Mess[i] = DWord[i] << 4 | DWord[i+1] >> 4;
   }

   return ClientErrors::None;
}


/* Routine to decode a group of "D" words */

YukonError_t D_Words (
        const unsigned char *DWords, /* D words to decode */
        size_t len,         /* data length */
        USHORT CCU,         /* CCU number */
        DSTRUCT *DSt,       /* D word structure */
        ESTRUCT *ESt,       /* E word structure in case one is found */
        BSTRUCT *BSt )      /* B word structure in case one is found */
{
   const size_t Num = len / (DWORDLEN + 1);

   YukonError_t Code;
   USHORT Dummy, Nack;

   DSt->Length = 0;
   DSt->Alarm = 0;
   DSt->Power = 0;
   DSt->TSync = 0;

   if(Num == 0 || Num > 3)
   {
      return ClientErrors::DLength;
   }

   /* early check to rule out garbage */
   if( ((DWords[0] & 0xf0) != 0xa0) &&
       ((DWords[0] & 0xf0) != 0xb0) &&
       ((DWords[0] & 0xf0) != 0xd0) &&
       ((DWords[0] & 0xf0) != 0xe0) &&
       !isNackPadded(DWords, 1, CCU) )
   {
       return ClientErrors::BadWordType;
   }

   /* check for a nacked */
   if( Code = NackTst (DWords[7], &Nack, CCU) )
   {
      return(Code);
   }

   /* if it is nacked is it padded? */
   if(Nack)
   {
       //  This is only checking the first 3 bytes of the first D word... ?
       if(isNackPadded (DWords, 3, CCU))
       {
           return ClientErrors::Word1NackPadded;
       }
       else
       {
           return ClientErrors::Word1Nack;
       }
   }

   /* decode the first word and get the data from it */
   if( Code = D1_Word (DWords, DSt->Message, &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm) )
   {
      if(Code == ClientErrors::EWordReceived)
      {
         /* try to decode the E word we just found */
         return E_Word(DWords, ESt);
      }
      if(Code == ClientErrors::BWordReceived)
      {
         return decodeBWord(DWords, BSt);
      }

      return(Code);
   }

   /* repeat the process for each DWord in the message */
   if(Num > 1)
   {
      if( Code = NackTst (DWords[15], &Nack, CCU) )
      {
          CTILOG_ERROR(dout, "NackTst failed with error code ("<< Code <<")");
          return(Code);
      }

      if(Nack)
      {
         if(isNackPadded (DWords + 8, 5, CCU))
         {
            return ClientErrors::Word2NackPadded;
         }
         else
         {
            return ClientErrors::Word2Nack;
         }
      }

      if( Code = D23_Word (DWords+8, DSt->Message + 3, &DSt->TSync, &Dummy) )
      {
         if(Code == ClientErrors::EWordReceived)
         {
            /* try to decode the E word we just found */
            return E_Word(DWords+8, ESt);
         }
         if(Code == ClientErrors::BWordReceived)
         {
            return decodeBWord(DWords+8, BSt);
         }

         return(Code);
      }
   }

   if(Num > 2)
   {
      if( Code = NackTst (DWords[23], &Nack, CCU) )
      {
          CTILOG_ERROR(dout, "NackTst failed with error code ("<< Code <<")");
          return(Code);
      }

      if(Nack)
      {
         if(isNackPadded (DWords+16, 5, CCU))
         {
            return ClientErrors::Word3NackPadded;
         }
         else
         {
            return ClientErrors::Word3Nack;
         }
      }

      if( Code = D23_Word (DWords+16, DSt->Message + 8, &Dummy, &Dummy) )
      {
         if(Code == ClientErrors::EWordReceived)
         {
            /* try to decode the E word we just found */
            return E_Word(DWords+16, ESt);
         }
         if(Code == ClientErrors::BWordReceived)
         {
            return decodeBWord(DWords+16, BSt);
         }

         return(Code);
      }
   }

   /* compute the Length of the returned data */
   DSt->Length = (Num - 1) * 5 + 3;

   return ClientErrors::None;
}


/* Routine to decode "E" word */
YukonError_t E_Word (
        const unsigned char *EWord,   /* E word to be decoded */
        ESTRUCT *ESt)        /* E word structure */
{
   if( ! isBchValid(EWord) )
   {
      return ClientErrors::BadBch;
   }

   /* Check to be sure type is right */
   if((EWord[0] & 0xf0) != 0xe0)
   {
      return ClientErrors::BadWordType;
   }

   /* decode the information and place it in the e word structure */
   ESt->repeater_variable = (EWord[0] & 0x0e) >> 1;
   ESt->echo_address      = (EWord[0] & 0x01) << 12 |
                             EWord[1]         <<  4 |
                            (EWord[2] & 0xf0) >>  4;
   ESt->power_fail        =  EWord[5] & 0x04;
   ESt->alarm             =  EWord[5] & 0x02;

   //  Technically defined as bits 20-43 (see Emetcon spec, page 2-7)
   unsigned char diagnostic_data = EWord[2] << 4 | EWord[3] >> 4;

   ESt->diagnostics.incoming_bch_error       = diagnostic_data & 0x01;
   ESt->diagnostics.incoming_no_response     = diagnostic_data & 0x02;
   ESt->diagnostics.listen_ahead_bch_error   = diagnostic_data & 0x04;
   ESt->diagnostics.listen_ahead_no_response = diagnostic_data & 0x08;
   ESt->diagnostics.repeater_code_mismatch   = diagnostic_data & 0x10;
   ESt->diagnostics.weak_signal              = diagnostic_data & 0x20;

   return ClientErrors::EWordReceived;
}


/* Routine checks the BCh in a received "D" word. */
/* Returns 0 if ok, error code if not. */

bool isBchValid (const unsigned char *DWord)
{
   BYTE DWordCopy[DWORDLEN];

   std::copy(DWord, DWord + DWORDLEN, DWordCopy);

   /* Blank out the last two bits so we calculate the BCH correctly */
   DWordCopy[5] &= 0xfc;

   /* calculate what it should have been */
   SHORT BCH = BCHCalc_C (DWordCopy, 46) >> 2;

   /* test if it the received BCh was correct */
   return BCH == ((DWord[5] & 0x03) << 4 | DWord[6] >> 4);
}


/* Routine to check for padding in a decoded return message */

bool isNackPadded (const unsigned char *Message,  /* Message to check */
                   USHORT Length,                 /* number of bytes to check */
                   USHORT CCU)                    /* number of the CCU invovled */
{
   USHORT Count, Nack, Code;

   /* test each character for nack... if any are not it is not padded */
   for(Count = 0; Count < Length; Count++)
   {
      Code = NackTst (Message[Count], &Nack, CCU);

      if(Code == ClientErrors::BadParity || (!Nack))
      {
         return false;
      }
   }

   return true;
}


/* Routine checks if character is proper ACK or NACK */

YukonError_t NackTst (
        BYTE Reply,           /* character to be tested */
        PUSHORT NAck,         /* result: 0 == ACK, 1 == NACK */
        USHORT CCU)           /* CCU number to use in test */
{
   /* check for proper parity */
   if(Reply != (BYTE)Parity_C(Reply))
   {
      return ClientErrors::BadParity;
   }

   /* calculate out the ack character for this CCU and compare */
   if((Reply & 0x7c) == 0x40)
      *NAck = 0;
   else if((Reply & 0x7c) == 0x30)  //  not sufficient... ?  Only checking for NAK-S (signal dropout)...  see Emetcon protocol spec 3-3
      *NAck = 1;

   if((Reply & 0x03) != (CCU & 0x03))
   {
      return ClientErrors::BadCcu;
   }

   return ClientErrors::None;
}


/* Routines to build preamble for CCU 700/710 operations. */

INT IM_EX_CTIBASE APreamble (PBYTE Pre, const ASTRUCT &ASt)             /* A word structure */
{
    USHORT i;

    /* load the CCU address */
    Pre[1] = 0;
    Pre[0] = ASt.Remote & 0x03;
    if(ASt.Remote > 3)
    {
        Pre[0] |= 0x40;
        Pre[1] = (ASt.Remote & 0x1c) << 1;
    }

    /* load the feeder/funtion */
    if(ASt.DlcRoute.Bus < 8)
        Pre[0] |= 0x04 | ASt.DlcRoute.Bus << 3;
    else
        Pre[0] |= (ASt.DlcRoute.Bus - 8) << 3;

    //  load the amp (either 0 or 1)
    if(ASt.DlcRoute.Amp)
        Pre[1] |= 0x40;

    /* load number of repeater stages */
    Pre[1] |= ASt.DlcRoute.Stages;

    //  always pay attention to amp card specified above (no 0x40 bit)
    Pre[2] = AWORDLEN;

    /* calculate the parity on all three bytes */
    for(i = 0; i < 3; i++)
        Pre[i] = Parity_C (Pre[i]);

   return ClientErrors::None;
}


INT IM_EX_CTIBASE BPreamble (PBYTE Pre, const BSTRUCT &BSt, INT wordsToFollow)     /* B Word structure */
{
    USHORT i;

    //  load the CCU address
    Pre[1] = 0;
    Pre[0] = BSt.Remote & 0x03;
    if(BSt.Remote > 3)
    {
        Pre[0] |= 0x40;
        Pre[1] = (BSt.Remote & 0x1c) << 1;
    }

    //  load the feeder/funtion
    if(BSt.DlcRoute.Bus < 8)
        Pre[0] |= 0x04 | BSt.DlcRoute.Bus << 3;
    else
        Pre[0] |= (BSt.DlcRoute.Bus - 8) << 3;

    //  load the amp (either 0 or 1)
    if(BSt.DlcRoute.Amp)
        Pre[1] |= 0x40;

    //  load number of repeater stages
    Pre[1] |= BSt.DlcRoute.Stages;

    //  always pay attention to the amp card specified above (no 0x40 bit)
    Pre[2] = BWORDLEN * (wordsToFollow + 1);

    /* calculate the parity on all three bytes */
    for(i = 0; i < 3; i++)
        Pre[i] = Parity_C (Pre[i]);

    return ClientErrors::None;
}

