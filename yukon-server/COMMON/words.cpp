#include "yukon.h"
#pragma title ( "Routines to Build and Decode DLC words" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        William R. Ockert

    FileName:
        WORDS.C

    Purpose:
        Build and decode power line carrier messages

    The following procedures are contained in this module:
        A_Word                  B_Word
        C_Word                  C_Words
        D1_Word                 D23_Word
        D_Words                 E_Word
        BCHCheck                PadTst
        NackTst                 APreamble
        BPreamble               LPreamble
        G_Word                  H_Word
        H_Words                 I_BCHCheck
        I1_Word                 I23_Word
        I_Words                 J_Word


    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-7-93   Converted to 32 bit                                WRO


   -------------------------------------------------------------------- */

#include <windows.h>
#include <process.h>
#include "os2_2w32.h"
#include "cticalls.h"
#include "cti_asmc.h"

#include <stdlib.h>
#include "queues.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "routes.h"
#include "porter.h"
#include "logger.h"


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

INT IM_EX_CTIBASE B_Word (PBYTE BWord, const BSTRUCT &BSt, INT wordCount, BOOL Double) /* B word structure */

{
   USHORT BCH;

   /* build b word into message string from info in structure */
   if(Double)
      BWord[0] = 0xb0 | BSt.DlcRoute.RepVar << 1 | BSt.DlcRoute.RepFixed >> 4;
   else
      BWord[0] = 0xa0 | BSt.DlcRoute.RepVar << 1 | BSt.DlcRoute.RepFixed >> 4;

   BWord[1] = (UCHAR)(BSt.DlcRoute.RepFixed << 4 | BSt.Address >> 18);
   BWord[2] = (UCHAR)(BSt.Address >> 10);
   BWord[3] = (UCHAR)(BSt.Address >> 2);
   BWord[4] = (UCHAR)(BSt.Address << 6 | wordCount << 4 | BSt.Function >> 4);
   BWord[5] = (UCHAR)(BSt.Function << 4);
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

INT IM_EX_CTIBASE C_Words (PBYTE CWords,             /* results */
                           const PBYTE Message,      /* Message to be converted */
                           USHORT Length,            /* Length of Message */
                           INT *Num)                 /* number of c words generated */
{
   INT i;

   /* loop building full size c words till not enough data */
   for(i = 0; i < Length / 5; ++i)
      C_Word (CWords + i * 7, Message + i * 5, 5);

   /* if partial word left go ahead and build it */
   if(i * 5 < Length)
   {
      C_Word (CWords + i * 7, Message + i * 5, (USHORT)(Length - i * 5));
      i++;
   }

   *Num = i;

   return(NORMAL);
}


/* Routine to decode "D1" type Message */

INT IM_EX_CTIBASE D1_Word (PBYTE DWord,           /* D word to be decoded */
                           PBYTE Mess,            /* result */
                           PUSHORT RepVar,        /* returned repeater variable bits */
                           PULONG Address,        /* returned lsb 13 bits of address */
                           PUSHORT Power,         /* power fail flag */
                           PUSHORT Alarm)         /* Alarm flag */
{
   USHORT i;

   /* check if BCh is correct */
   if(BCHCheck (DWord))
      return(BADBCH);

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
   for(i = 0; i < 3; i++)
      Mess[i] = DWord[i+2] << 4 | DWord[i+3] >> 4;

   return(NORMAL);
}


/* Routine to decode a "D2" or "D3" type word */

INT IM_EX_CTIBASE D23_Word(PBYTE DWord,           /* d word to be decoded */
                           PBYTE Mess,            /* resulting message bytes */
                           PUSHORT S1,            /* flag spare1 */
                           PUSHORT S2)            /* flag spare2 */
{
   USHORT i;

   /* make sure that the BCh is correct */
   if(BCHCheck (DWord))
      return(BADBCH);

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
   for(i = 0; i < 5; i++)
      Mess[i] = DWord[i] << 4 | DWord[i+1] >> 4;

   return(NORMAL);
}


/* Routine to decode a group of "D" words */

INT IM_EX_CTIBASE D_Words (PBYTE DWords,     /* D words to decode */
                           USHORT Num,       /* DWord count */
                           USHORT CCU,       /* CCU number */
                           DSTRUCT *DSt)     /* D word structure */
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

   /* check for a nacked */
   if((Code = NackTst (DWords[7], &Nack, CCU)) != NORMAL)
   {
      return(Code);
   }

   /* if it is nacked is it padded? */
   if(Nack)
   {
      if(PadTst (DWords, 3, CCU))
      {
         return(NACKPAD1);
      }
      else
         return(NACK1);
   }

   /* decode the first word and get the data from it */
   if((Code = D1_Word (DWords, DSt->Message, &DSt->RepVar, &DSt->Address, &DSt->Power, &DSt->Alarm)) != NORMAL)
      return(Code);

   /* repeat the process for each DWord in the message */
   if(Num > 1)
   {
      if((Code = NackTst (DWords[15], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(PadTst (DWords + 8, 5, CCU))
            return(NACKPAD2);
         else
            return(NACK2);
      }

      if((Code = D23_Word (DWords+8, DSt->Message + 3, &DSt->TSync, &Dummy)) != NORMAL)
          return(Code);
   }

   if(Num > 2)
   {

      if((Code = NackTst (DWords[23], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(PadTst (DWords+16, 5, CCU))
            return(NACKPAD3);
         else
            return(NACK3);
      }

      if((Code = D23_Word (DWords+16, DSt->Message + 8, &Dummy, &Dummy)) != NORMAL)
         return(Code);
   }

   /* compute the Length of the returned data */
   DSt->Length = (Num - 1) * 5 + 3;

   return(NORMAL);
}


/* Routine to decode "E" word */

INT IM_EX_CTIBASE E_Word (PBYTE EWord,            /* E word to be decoded */
                          ESTRUCT *ESt)        /* E word structure */
{
   USHORT i;

   /* check for the proper BCh */
   if(BCHCheck (EWord))
      return(BADBCH);

   /* Check to be sure type is right */
   if(EWord[0] & 0xf0 != 0xe0)
      return(BADTYPE);

   /* decode the information and place it in the e word structure */
   ESt->RepVar = EWord[0] >> 1 & 0x07;
   ESt->Address = EWord[0] << 12 | EWord[1] << 4 | EWord[2] >> 4;
   ESt->Power = (USHORT)(EWord[5] & 0x04 >> 3);
   ESt->Alarm = (USHORT)(EWord[5] & 0x03 >> 2);

   for(i = 0; i < 3; i++)
      ESt->Message[i] = EWord[i+2] << 4 | EWord[i+3] >> 4;

   return(NORMAL);
}


/* Routine checks the BCh in a recieved "D" word. */
/* Returns 0 if ok, error code if not. */

INT IM_EX_CTIBASE BCHCheck (PBYTE DWord)

{
   SHORT BCH;
   BYTE Save;

   /* Save the old BCh */
   Save = DWord[5];
   DWord[5] &= 0xfc;

   /* calculate what it should have been */
   BCH = BCHCalc_C (DWord, 46) >> 2;

   /* restore the old */
   DWord[5] = Save;

   /* test if it the received BCh was correct */
   if(BCH != ((DWord[5] & 0x03) << 4 | DWord[6] >> 4))
      return(BADBCH);

   return(NORMAL);
}


#define PADDED       1
#define NOTPADDED    0

/* Routine to check for padding in a decoded return message */
/* returns: 0 = not padded, !0 == padded */

INT IM_EX_CTIBASE PadTst (PBYTE Message,                 /* Message to check */
                          USHORT Length,                 /* number of bytes to check */
                          USHORT CCU)                    /* number of the CCU invovled */
{
   USHORT Count, Nack, Code;

   /* test each character for nack... if any are not it is not padded */
   for(Count = 0; Count < Length; Count++)
   {
      Code = NackTst (Message[Count], &Nack, CCU);

      if(Code == BADPARITY || (!Nack))
         return(NOTPADDED);
   }

   return(PADDED);
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
    if(ASt.DlcRoute.Feeder < 8)
        Pre[0] |= 0x04 | ASt.DlcRoute.Feeder << 3;
    else
        Pre[0] |= (ASt.DlcRoute.Feeder - 8) << 3;

    //  load the amp (either 0 or 1)
    if(ASt.DlcRoute.Amp)
        Pre[1] |= 0x40;

    /* load number of repeater stages */
    Pre[1] |= ASt.DlcRoute.Stages;

#if 0
    /* load extended amp info (actually only used by smart CCU) with data count */
    if(ASt.DlcRoute.Amp > 1)
        Pre[2] = AWORDLEN | 0x40;
    else
        Pre[2] = AWORDLEN;
#else
    //  always pay attention to amp card specified above (no 0x40 bit)
    Pre[2] = AWORDLEN;
#endif

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
    if(BSt.DlcRoute.Feeder < 8)
        Pre[0] |= 0x04 | BSt.DlcRoute.Feeder << 3;
    else
        Pre[0] |= (BSt.DlcRoute.Feeder - 8) << 3;

    //  load the amp (either 0 or 1)
    if(BSt.DlcRoute.Amp)
        Pre[1] |= 0x40;

    //  load number of repeater stages
    Pre[1] |= BSt.DlcRoute.Stages;

#if 0
    //  load extended amp info (actually only used by smart CCU) with data count */
    if(BSt.DlcRoute.Amp > 0)
        Pre[2] = (BWORDLEN * (wordsToFollow + 1)) | 0x40;
    else
        Pre[2] = BWORDLEN * (wordsToFollow + 1);
#else
    //  always pay attention to the amp card specified above (no 0x40 bit)
    Pre[2] = BWORDLEN * (wordsToFollow + 1);
#endif

    /* calculate the parity on all three bytes */
    for(i = 0; i < 3; i++)
        Pre[i] = Parity_C (Pre[i]);

    return(NORMAL);
}


/* Routine to do a loopback preamble for a 700/710 */
INT IM_EX_CTIBASE LPreamble (PBYTE Pre, USHORT Remote)             /* A word structure */

{
   USHORT i;

   /* load the CCU address */
   Pre[0] = Remote & 0x03;

   if(Remote > 3)
   {
      Pre[0] |= 0x40;
      Pre[1] = ((Remote & 0x1c) << 1) | 0x45;
   }
   else
      Pre[1] = 0x55;

   Pre[0] |= 2 << 3;

   Pre[2] = 0x55;

   /* calculate the parity on all three bytes */
   for(i = 0; i < 3; i++)
      Pre[i] = Parity_C (Pre[i]);

   return(NORMAL);
}



/* G_Word is Routine to create a "G" type word */

INT IM_EX_CTIBASE G_Word (PBYTE GWord, const BSTRUCT &GSt, CtiDeviceBase* Dev, INT dwordCount, BOOL Double)

{
   // extern USHORT Double;

   if(Double)
      GWord[0] = 0x30 | GSt.DlcRoute.RepVar << 1 | GSt.DlcRoute.RepFixed >> 4;
   else
      GWord[0] = 0x20 | GSt.DlcRoute.RepVar << 1 | GSt.DlcRoute.RepFixed >> 4;
   GWord[1] = (UCHAR)(GSt.DlcRoute.RepFixed << 4 | GSt.Address >> 18);
   GWord[2] = (UCHAR)(GSt.Address >> 10);
   GWord[3] = (UCHAR)(GSt.Address >> 2);
   GWord[4] = (UCHAR)(GSt.Address << 6 | dwordCount << 4 | GSt.Function >> 12);
   GWord[5] = (UCHAR)(GSt.Function >> 4);
   GWord[6] = (UCHAR)((GSt.Function << 4 & 0xf0) | GSt.IO >> 1);
   GWord[7] = (UCHAR)(GSt.IO << 7);
   GWord[7] |= (UCHAR)(BCHCalc_C (GWord, 57) << 1);

   return(NORMAL);

}


/* Routine to make single "H" word */

INT IM_EX_CTIBASE H_Word (PBYTE HWord,                 /* result */
                          PBYTE Mess,                  /* bytes to place Message */
                          USHORT Len)                  /* Length of Message */
{
   USHORT i;

   /* clear out the message buffer */
   for(i = 1; i < 8; i++)
      HWord[i] = 0;

   HWord[0] = 0x40;

   /* load the data into the buffer */
   for(i = 0; i < Len; i++)
   {
      HWord[i] |= Mess[i] >> 4;
      HWord[i+1] = Mess[i] << 4;
   }

   /* for partial Word load count */
   if(Len < 6)
   {
      HWord[6] = Len << 4;
      HWord[7] = 0x80;
   }

   /* calculate BCh */
   HWord[7] |= BCHCalc_C (HWord, 57) << 1;

   return(NORMAL);
}


/* Routine to make multiple "H" words from Message */

INT IM_EX_CTIBASE H_Words (PBYTE HWords,               /* result */
                           PBYTE Message,              /* Message to be converted */
                           USHORT Length,              /* Length of Message */
                           USHORT *Num)                /* number of H words generated */
{
   INT i;

   /* loop building full size h words till not enough data */
   for(i = 0; i < Length / 6; ++i)
      H_Word (HWords + i * 8, Message + i * 6, 6);

   /* if partial word left go ahead and build it */
   if(i * 6 < Length)
   {
      H_Word (HWords + i * 8, Message + i * 6, (USHORT)(Length - i * 6));
      i++;
   }

   *Num = i;

   return(NORMAL);
}


/* Routine to decode "I1" type Message */

INT IM_EX_CTIBASE I1_Word (PBYTE IWord,         /* I word to be decoded */
                           PBYTE Mess,          /* result */
                           PUSHORT RepVar,      /* returned repeater variable bits */
                           PULONG Address,      /* returned lsb 13 bits of address */
                           PUSHORT Power,       /* power fail flag */
                           PUSHORT Alarm)       /* alarm flag */
{
   USHORT i;

   /* check if BCh is correct */
   if(I_BCHCheck (IWord))
      return(BADBCH);

   /* check if we actually have an IWord */
   if((IWord[0] & 0xf0) != 0x50)
   {
      /* well if it isnt is it a J word? */
      if((IWord[0] & 0xf0) != 0x60)
         return(BADTYPE);
      else
         return(JWORDRCV);
   }

   /* decode the overhead stuff */
   *RepVar = IWord[0] >> 1 & 0x07;
   *Address = (IWord[0] & 0x01) << 12 | IWord[1] << 4 | IWord[2] >> 4;
   *Power = IWord[6] & 0x01;
   *Alarm = IWord[7] >> 7 & 0x01;

   /* get the data out and copy it into the Message string */
   for(i = 0; i < 4; i++)
      Mess[i] = IWord[i+2] << 4 | IWord[i+3] >> 4;

   return(NORMAL);
}


/* Routine to decode a "I2" or "I3" type word */
INT IM_EX_CTIBASE I23_Word (PBYTE IWord,         /* I word to be decoded */
                            PBYTE Mess,          /* resulting Message bytes */
                            PUSHORT S1,            /* flag spare1 */
                            PUSHORT S2)            /* flag spare2 */
{
   USHORT i;

   /* check if we actually have an IWord */
   if((IWord[0] & 0xf0) != 0x50)
   {
      /* well if it isnt is it a J word? */
      if((IWord[0] & 0xf0) != 0x60)
         return(BADTYPE);
      else
         return(JWORDRCV);
   }

   if(I_BCHCheck (IWord))
      return(BADBCH);

   /* decode the status bits */
   *S1 = IWord[6] & 0x01;
   *S2 = IWord[7] >> 7 & 0x1;

   /* copy the data into the result string */
   for(i = 0; i < 6; i++)
      Mess[i] = IWord[i] << 4 | IWord[i+1] >> 4;

   return(NORMAL);
}


/* Routine to decode a group of "I" words */
INT IM_EX_CTIBASE I_Words (PBYTE IWords,      /* IWords to decode */
                           USHORT Num,        /* IWord count */
                           USHORT CCU,        /* CCU number */
                           DSTRUCT *ISt)      /* I word structure */
{
   USHORT Code, Dummy, Nack;

   ISt->Length = 0;
   if(Num == 0 || Num > 3)
      return(BADLENGTH);

   /* check for a nacked */
   if((Code = NackTst (IWords[8], &Nack, CCU)) != NORMAL)
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
      return(Code);
   }

   /* if it is nacked is it padded? */
   if(Nack)
   {
      if(PadTst (IWords, 4, CCU))
         return(NACKPAD1);
      else
         return(NACK1);
   }

   /* decode the first word and get the data from it */
   if((Code = I1_Word (IWords,
                       ISt->Message,
                       &ISt->RepVar,
                       &ISt->Address,
                       &ISt->Power,
                       &ISt->Alarm)) != NORMAL)
      return(Code);

   /* repeat the process for each IWord in the message */
   if(Num > 1)
   {
      if((Code = NackTst (IWords[17], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(PadTst (IWords+9, 6, CCU))
            return(NACKPAD2);
         else
            return(NACK2);
      }

      if((Code = I23_Word (IWords+9, ISt->Message + 4, &ISt->TSync, &Dummy)) != NORMAL)
         return(Code);
   }

   if(Num > 2)
   {

      if((Code = NackTst (IWords[26], &Nack, CCU)) != NORMAL)
      {
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
         return(Code);
      }

      if(Nack)
      {
         if(PadTst (IWords + 18, 6, CCU))
            return(NACKPAD3);
         else
            return(NACK3);
      }

      if((Code = I23_Word (IWords + 18, ISt->Message + 10, &Dummy, &Dummy)) != NORMAL)
         return(Code);
   }

   /* compute the Length of the returned data */
   ISt->Length = (Num - 1) * 6 + 4;

   return(NORMAL);
}


/* Routine to decode "J" Word */

INT IM_EX_CTIBASE J_Word (PBYTE JWord,             /* J Word to be decoded */
                          ESTRUCT *JSt)            /* J word structure */
{
   SHORT i;

   /* check for the proper BCh */
   if(I_BCHCheck (JWord))
      return(BADBCH);

   /* Check to be sure type is right */
   if(JWord[0] & 0xf0 != 0x60)
      return(BADTYPE);

   /* decode the information and place it in the j word structure */
   JSt->RepVar = JWord[0] >> 1 & 0x07;
   JSt->Address = JWord[0] << 12 | JWord[1] << 4 | JWord[2] >> 4;
   JSt->Power = JWord[6] & 0x01;
   JSt->Alarm = JWord[7] >> 7 & 0x01;

   for(i = 0; i < 4; i++)
      JSt->Message[i] = JWord[i+2] << 4 | JWord[i+3] >> 4;


   return(NORMAL);
}


/* This routine checks the BCH in a recieved "I" word */
/* returns:  0 if ok, error code if not */

INT IM_EX_CTIBASE I_BCHCheck (PBYTE IWord)                     /* I word to be checked */

{
   USHORT BCH;
   BYTE Save;

   Save = IWord[7];
   IWord[7] &= 0x80;

   BCH = BCHCalc_C (IWord, 57);

   IWord[7] = Save;
   if(BCH != ((IWord[7] & 0x7f) >> 1))
      return(BADBCH);
   return(NORMAL);
}
