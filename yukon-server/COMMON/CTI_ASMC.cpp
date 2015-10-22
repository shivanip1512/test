#include "precompiled.h"

#include "cti_asmc.h"


USHORT  IM_EX_CTIBASE CrcCalc_C(const BYTE* pStr, ULONG cnt)
{

   int      i;
   int      rpoly   = 0x01021;
   USHORT  accum = 0;
   BYTE    ch;

   while(cnt--)
   {
      ch = *pStr++;     // get char increment pointer

      accum ^= ((USHORT)ch << 8);
      for(i=0; i < 8; i++)
      {
         if(accum & 0x8000)
         {
            accum <<= 1;      // Shift by one and..
            accum ^= rpoly;
         }
         else
         {
            accum <<= 1;      // Shift by one and..
         }
      }

   }
   return(accum);
}

USHORT  IM_EX_CTIBASE NCrcCalc_C(const BYTE* pStr, ULONG cnt)
{

   //int    i;
   int     index;
   USHORT  accum = 0xFFFF;
   USHORT  t1=0, t2=0;
   BYTE ch;
   static  USHORT table[] = {   0x0000,
      0x1081,
      0x2102,
      0x3183,
      0x4204,
      0x5285,
      0x6306,
      0x7387,
      0x8408,
      0x9489,
      0xA50A,
      0xB58B,
      0xC60C,
      0xD68D,
      0xE70E,
      0xF78F
   };

   while(cnt--)
   {
      ch = *pStr++;     // get char increment pointer
      accum ^= (0x00ff & ((USHORT)ch));
      index = (accum & 0x000f);
      accum >>= 4;
      accum ^= table[index];
      index = (accum & 0x000f);
      accum >>= 4;
      accum ^= table[index];
   }

   accum = ~accum;

   t1 = 0x00FF & (accum >> 8);
   t2 = 0xFF00 & (accum << 8);
   accum = t1 | t2;

   return(accum);
}

USHORT  IM_EX_CTIBASE  SCrcCalc_C(BYTE* pStr, ULONG cnt)
{

   int     i;
   int     rpoly = 0xA001;
   USHORT  accum = 0;
   BYTE    ch;


   while(cnt--)
   {
      ch = *pStr++;     // get char increment pointer

      accum ^= ((USHORT)ch);
      for(i=0; i < 8; i++)
      {
         if(accum & 0x0001)
         {
            accum >>= 1;      // Shift by one and..
            accum ^= rpoly;
         }
         else
         {
            accum >>= 1;      // Shift by one and..
         }
      }
   }

   return(accum);
}

USHORT IM_EX_CTIBASE  BCHCalc_C(BYTE* pStr, ULONG bits)
{

   int     bitcnt;
   USHORT  axreg = ((USHORT)*pStr++) << 8;     // In ah reg.

   while(bits)
   {
      bitcnt = 8;
      if(bits > 8)   axreg |= ((USHORT)*pStr++);      // In al reg.

      while(bitcnt && bits)
      {
         if(axreg & 0x8000)
         {
            axreg ^= 0x8600;
         }

         axreg <<= 1;
         bitcnt--;
         bits--;
      }
   }

   axreg >>= 8;        // Return in the lsb left justified
   return(axreg);
}

char  IM_EX_CTIBASE Parity_C(char ch)
{
   int i;
   int parity = 0x00;         // Even parity by default
   char temp;

   temp = ch &= 0x7f;

   for(i=0 ; i < 8; i++)
   {
      if(temp & 0x01) parity = ~parity;
      temp >>= 1;
   }

   if(parity) ch |= 0x80;

   return ch;
}
