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


   return(NORMAL);
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

   return(NORMAL);
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

   return(NORMAL);
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

   return(NORMAL);
}


/* Routine to decode "D1" type Message */

INT IM_EX_CTIBASE D1_Word (const unsigned char *DWord,  /* D word to be decoded */
                           PBYTE Mess,            /* result */
                           PUSHORT RepVar,        /* returned repeater variable bits */
                           PULONG Address,        /* returned lsb 13 bits of address */
                           PUSHORT Power,         /* power fail flag */
                           PUSHORT Alarm)         /* Alarm flag */
{
   if( ! isBchValid(DWord) )
   {
      return BADBCH;
   }

   /* check if we actually have a DWord */
   if((DWord[0] & 0xf0) != 0xd0)
   {
      /* well if it isnt is it an e word? */
      if((DWord[0] & 0xf0) != 0xe0)
         return(BADTYPE);
      else
         return(EWORDRCV);
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

   return(NORMAL);
}


/* Routine to decode a "D2" or "D3" type word */

INT IM_EX_CTIBASE D23_Word(const unsigned char *DWord,  /* d word to be decoded */
                           PBYTE Mess,            /* resulting message bytes */
                           PUSHORT S1,            /* flag spare1 */
                           PUSHORT S2)            /* flag spare2 */
{
   if( ! isBchValid(DWord) )
   {
      return BADBCH;
   }

   /* make sure that we received a d word */
   if((DWord[0] & 0xf0) != 0xd0)
   {
      /* if it is not a d word is it an e word? */
      if((DWord[0] & 0xf0) != 0xe0)
         return(BADTYPE);
      else
         return(EWORDRCV);
   }

   /* decode the status bits */
   *S1 = DWord[5] >> 3 & 0x01;
   *S2 = DWord[5] >> 2 & 0x01;

   /* copy the data into the result string */
   for(int i = 0; i < 5; i++)
   {
      Mess[i] = DWord[i] << 4 | DWord[i+1] >> 4;
   }

   return(NORMAL);
}


/* Routine to decode a group of "D" words */

INT IM_EX_CTIBASE D_Words (const unsigned char *DWords, /* D words to decode */
                           USHORT Num,         /* DWord count */
                           USHORT CCU,         /* CCU number */
                           DSTRUCT *DSt,       /* D word structure */
                           ESTRUCT *ESt)       /* E word structure in case one is found */
{
   USHORT Code, Dummy, Nack;

   DSt->Length = 0;
   DSt->Alarm = 0;
   DSt->Power = 0;
   DSt->TSync = 0;

   if(Num == 0 || Num > 3)
   {
      return(DLENGTH);
   }

   /* if it's not a D word, and it's not a NACK, we got garbage */
   if( ((DWords[0] & 0xf0) != 0xd0) &&
       ((DWords[0] & 0xf0) != 0xe0) &&
       !isNackPadded(DWords, 1, CCU) )
   {
       return BADTYPE;
   }

   /* check for a nacked */
   if((Code = NackTst (DWords[7], &Nack, CCU)) != NORMAL)
   {
      return(Code);
   }

   /* if it is nacked is it padded? */
   if(Nack)
   {
       //  This is only checking the first 3 bytes of the first D word... ?
       if(isNackPadded (DWords, 3, CCU))
       {
           return(NACKPAD1);
       }
       else
       {
           return(NACK1);
       }
   }

   /* decode the first word and get the data from it */
   if((Code = D1_Word (DWords, DSt->Message, &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm)) != NORMAL)
   {
      if(Code == EWORDRCV)
      {
         /* try to decode the E word we just found */
         return E_Word(DWords, ESt);
      }

      return(Code);
   }

   /* repeat the process for each DWord in the message */
   if(Num > 1)
   {
      if((Code = NackTst (DWords[15], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(isNackPadded (DWords + 8, 5, CCU))
            return(NACKPAD2);
         else
            return(NACK2);
      }

      if((Code = D23_Word (DWords+8, DSt->Message + 3, &DSt->TSync, &Dummy)) != NORMAL)
      {
         if(Code == EWORDRCV)
         {
            /* try to decode the E word we just found */
            return E_Word(DWords+8, ESt);
         }

         return(Code);
      }
   }

   if(Num > 2)
   {

      if((Code = NackTst (DWords[23], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(isNackPadded (DWords+16, 5, CCU))
            return(NACKPAD3);
         else
            return(NACK3);
      }

      if((Code = D23_Word (DWords+16, DSt->Message + 8, &Dummy, &Dummy)) != NORMAL)
      {
         if(Code == EWORDRCV)
         {
            /* try to decode the E word we just found */
            return E_Word(DWords+16, ESt);
         }

         return(Code);
      }
   }

   /* compute the Length of the returned data */
   DSt->Length = (Num - 1) * 5 + 3;

   return(NORMAL);
}


/* Routine to decode "E" word */
int IM_EX_CTIBASE E_Word (const unsigned char *EWord,   /* E word to be decoded */
                          ESTRUCT *ESt)        /* E word structure */
{
   if( ! isBchValid(EWord) )
   {
      return BADBCH;
   }

   /* Check to be sure type is right */
   if((EWord[0] & 0xf0) != 0xe0)
   {
      return BADTYPE;
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

   return EWORDRCV;
}


/* Routine checks the BCh in a recieved "D" word. */
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

      if(Code == BADPARITY || (!Nack))
      {
         return false;
      }
   }

   return true;
}


/* Routine checks if character is proper ACK or NACK */

INT IM_EX_CTIBASE NackTst (BYTE Reply,           /* character to be tested */
                           PUSHORT NAck,         /* result: 0 == ACK, 1 == NACK */
                           USHORT CCU)           /* CCU number to use in test */
{
   /* check for proper parity */
   if(Reply != (BYTE)Parity_C(Reply))
   {
      return(BADPARITY);
   }

   /* calculate out the ack character for this CCU and compare */
   if((Reply & 0x7c) == 0x40)
      *NAck = 0;
   else if((Reply & 0x7c) == 0x30)
      *NAck = 1;

   if((Reply & 0x03) != (CCU & 0x03))
      return(BADCCU);

   return(NORMAL);
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

   return(NORMAL);
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

    return(NORMAL);
}

