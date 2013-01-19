#include "precompiled.h"


#include "prot_emetcon.h"
#include "logger.h"
#include "utility.h"
#include "porter.h"

using std::endl;

namespace Cti {
namespace Protocols {
namespace EmetconProtocol {

void buildAWordMessage(OUTMESS *&out_result)
{
   const OUTMESS out_template = *out_result;

   // Use the template's ASt since Buffer.OutMessage & Buffer.ASt are members of a union.
   APreamble(out_result->Buffer.OutMessage + PREIDLEN,     out_template.Buffer.ASt);
   A_Word   (out_result->Buffer.OutMessage + PREIDLEN + 3, out_template.Buffer.ASt);

   /* calculate an approximate timeout */
   out_result->TimeOut = TIMEOUT + out_template.Buffer.ASt.DlcRoute.Stages;

   /* Calculate the message lengths */
   out_result->OutLength = AWORDLEN + PREAMLEN + 3;
   out_result->InLength = 2;
}

void buildBWordMessage(OUTMESS *&out_result, bool double_message)
{
   const OUTMESS out_template = *out_result;

   //  this is both the IO_Read and IO_Function_Read case
   if( out_template.Buffer.BSt.IO & IO_Read )
   {
       //  build preamble message.  Note that wordCount is zero for a read!
       BPreamble(out_result->Buffer.OutMessage + PREIDLEN, out_template.Buffer.BSt, 0);

       unsigned wordsExpected = determineDWordCount(out_result->Buffer.BSt.Length);

       out_result->InLength = 3 + wordsExpected * (DWORDLEN + 1);  // InLength is based upon the read request/function requested.
       out_result->OutLength = PREAMLEN + BWORDLEN + 3;            // OutLength is fixed (only the B read request)

       //  The timeout must include time for the repeaters to transmit the result words
       out_result->TimeOut = TIMEOUT + out_template.Buffer.BSt.DlcRoute.Stages * (wordsExpected + 1);

       B_Word(out_result->Buffer.OutMessage + PREIDLEN + PREAMLEN, out_template.Buffer.BSt, wordsExpected, double_message);
   }
   else
   {
       unsigned wordsToWrite = 0;

       if(out_template.Buffer.BSt.Length > 0)
       {
           // Nail the CWORDS into the correct location in the current message.
           C_Words((unsigned char *) (out_result->Buffer.OutMessage+PREIDLEN+PREAMLEN+BWORDLEN),
                   (unsigned char *)  out_template.Buffer.BSt.Message,
                   (unsigned short)   out_template.Buffer.BSt.Length,
                   (unsigned int *)  &wordsToWrite);
       }

       //  build preamble message - wordsToWrite represents the number of outbound cwords
       BPreamble(out_result->Buffer.OutMessage + PREIDLEN, out_template.Buffer.BSt, wordsToWrite);

       out_result->TimeOut = TIMEOUT + out_template.Buffer.BSt.DlcRoute.Stages * (wordsToWrite + 1);

       //  calculate message lengths
       out_result->OutLength = PREAMLEN + BWORDLEN + wordsToWrite * CWORDLEN + 3;
       out_result->InLength = 2;

       //  build the b word
       B_Word(out_result->Buffer.OutMessage + PREIDLEN + PREAMLEN, out_template.Buffer.BSt, wordsToWrite, double_message);
    }

    /* load the IDLC specific stuff for DTRAN */
    out_result->Source                = 0;
    out_result->Destination           = DEST_DLC;
    out_result->Command               = CMND_DTRAN;
    out_result->Buffer.OutMessage[6]  = (UCHAR)out_template.InLength;
    out_result->EventCode             &= ~RCONT;
}


unsigned determineDWordCount(unsigned length)
{
    unsigned count = 0;

    if( length && length <= 13 )
    {
        //  1 to  3 bytes = 1 word needed
        //  4 to  8 bytes = 2 words needed
        //  9 to 13 bytes = 3 words needed
        count = (length + 6) / 5;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - invalid length (" << length << ") in Protocols::EmetconProtocol::determineDWordCount() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return count;
}

int calculateControlInterval(int interval)
{
    int nRet = 0;

    if     (interval <    5)    nRet =    0;   // This must be a restore
    else if(interval <  451)    nRet =  450;
    else if(interval <  901)    nRet =  900;
    else if(interval < 1801)    nRet = 1800;
    else if(interval < 3601)    nRet = 3600;
    else                        nRet = 3600;

    return nRet;
}


}
}
}
