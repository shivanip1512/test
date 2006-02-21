/*-----------------------------------------------------------------------------*
*
* File:   utility
*
* Date:   4/14/2000
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/utility.h-arc  $
* REVISION     :  $Revision: 1.32 $
* DATE         :  $Date: 2006/02/21 23:33:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __UTILITY_H__
#define __UTILITY_H__
#pragma warning( disable : 4786)


#include <string>
using std::string;

#include "ctitime.h"

#include "dsm2.h"
#include "dlldefs.h"
#include "sorted_vector.h"
using std::vector;


IM_EX_CTIBASE LONG GetMaxLMControl(long pao);
IM_EX_CTIBASE LONG LMControlHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG CommErrorHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG VerificationSequenceGen(bool force = false, int force_value = 0);
IM_EX_CTIBASE INT ChangeIdGen(bool force = false);
IM_EX_CTIBASE INT SystemLogIdGen();
IM_EX_CTIBASE INT CCEventLogIdGen();
IM_EX_CTIBASE INT CCEventSeqIdGen();
IM_EX_CTIBASE INT PAOIdGen();
IM_EX_CTIBASE INT VCUTime (CtiOutMessage *, PULONG);
IM_EX_CTIBASE BOOL isFileTooBig(const string& fileName, DWORD thisBig = 0x00500000);
IM_EX_CTIBASE BOOL InEchoToOut(const INMESS *In, CtiOutMessage *Out);
IM_EX_CTIBASE BOOL OutEchoToIN(const CtiOutMessage *Out, INMESS *In);
IM_EX_CTIBASE string convertVersacomAddressToHumanForm(INT address);
IM_EX_CTIBASE INT convertHumanFormAddressToVersacom(INT address);

IM_EX_CTIBASE bool pokeDigiPortserver(CHAR *server, INT port = 23);
IM_EX_CTIBASE string& traceBuffer(string &str, BYTE *Message, ULONG Length);
IM_EX_CTIBASE CtiTime nextScheduledTimeAlignedOnRate( const CtiTime &origin, LONG rate );
IM_EX_CTIBASE void autopsy(char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);

IM_EX_CTIBASE BOOL searchFuncForOutMessageDevID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageRteID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageUniqueID(void *pId, void* d);
IM_EX_CTIBASE void applyPortQueueOutMessageReport(void *ptr, void* d);
IM_EX_CTIBASE double limitValue(double input, double min, double max);

typedef struct
{
   char *proj;
   int major;
   int minor;
   int build;
   char *date;
} CTICOMPILEINFO;

typedef struct {
   char *fname;
   double rev;
   char *date;
} CTICOMPONENTINFO;

typedef struct {

// #define NOWAIT          0x0000
// #define NORESULT        0x0000
// #define WAIT            0x0001
// #define RESULT          0x0002
// #define QUEUED          0x0004
// #define ACTIN           0x0008
// #define AWORD           0x0010
// #define BWORD           0x0020
// #define DTRAN           0x0040
// #define RCONT           0x0080
// #define RIPPLE          0x0100
// #define STAGE           0x0200
// #define VERSACOM        0x0400
// #define TSYNC           0x0800
// #define REMS            0x1000   // This can never be used now.... CGP Corey.
// #define FISHERPIERCE    0x1000
// #define ENCODED         0x4000
// #define DECODED         0x4000
// #define COMMANDCODE     0x8000

    int priority_count[16];         // Count of elements at the respective priority 1-15.
    int metrics[256];               // Ok, these are 256 metrics I want to count!

} CtiQueueAnalysis_t;


IM_EX_CTIBASE string identifyProjectVersion(const CTICOMPILEINFO &Info);
IM_EX_CTIBASE void identifyProject(const CTICOMPILEINFO &Info);
IM_EX_CTIBASE void identifyProjectComponents(const CTICOMPONENTINFO *pInfo);


extern CTICOMPILEINFO CompileInfo;

extern CtiMutex gOutMessageMux;
extern ULONG gOutMessageCounter;

IM_EX_CTIBASE void incrementCount();
IM_EX_CTIBASE void decrementCount();
IM_EX_CTIBASE ULONG OutMessageCount();
IM_EX_CTIBASE bool isLCU(INT type);
IM_EX_CTIBASE bool isION(INT type);
IM_EX_CTIBASE int generateTransmissionID();
IM_EX_CTIBASE LONG GetPAOIdOfPoint(long pid);
IM_EX_CTIBASE INT GetPIDFromDeviceAndOffset(int device, int offset);
IM_EX_CTIBASE INT EstablishOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE INT OverrideOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE bool CheckSocketSubsystem();


IM_EX_CTIBASE ULONG    BCDtoBase10(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE ULONG    StrToUlong(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE INT      CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount);
IM_EX_CTIBASE USHORT   CCITT16CRC(INT Id, BYTE* buffer, LONG length, BOOL bAdd);
IM_EX_CTIBASE USHORT   ShortLittleEndian(USHORT *ShortEndianFloat);
IM_EX_CTIBASE FLOAT    FltLittleEndian(FLOAT  *BigEndianFloat);
IM_EX_CTIBASE DOUBLE   DblLittleEndian(DOUBLE *BigEndianDouble);
IM_EX_CTIBASE VOID     BDblLittleEndian(CHAR *BigEndianBDouble);
IM_EX_CTIBASE ULONG    MilliTime (PULONG);

IM_EX_CTIBASE LONG     ResetBreakAlloc();

IM_EX_CTIBASE bool findLPRequestEntries(void *om, void* d);
IM_EX_CTIBASE void cleanupOutMessages(void *unusedptr, void* d);
IM_EX_CTIBASE string explainTags(const unsigned tags);

IM_EX_CTIBASE int binaryStringToInt(const CHAR *buffer, int length);

IM_EX_CTIBASE unsigned char addBitToSA305CRC(unsigned char crc, unsigned char bit); // bit is 0 or 1
IM_EX_CTIBASE unsigned char addOctalCharToSA305CRC(unsigned char crc, unsigned char ch); // octal char
IM_EX_CTIBASE void testSA305CRC(char* testData);
IM_EX_CTIBASE LONG GetPAOIdOfEnergyPro(long devicesn);

//String Functions
inline void CtiToLower( std::string& str)
{
	std::transform(str.begin(),str.end(),str.begin(),::tolower);
}
inline void CtiToUpper( std::string& str)
{
	std::transform(str.begin(),str.end(),str.begin(),::toupper);
}

inline string trim_right ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( str.find_last_not_of ( t ) + 1 ) ;
}

inline string trim_left ( std::string & source ,std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( 0 , source.find_first_not_of ( t ) ) ;
}

inline string trim ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = trim_left ( trim_right ( str , t ) , t ) ;
}

inline int stringCompareIgnoreCase(const std::string& str1, const std::string& str2)
{
    std::string s1 = str1;
    std::string s2 = str2;
    std::transform(str1.begin(), str1.end(), s1.begin(), ::tolower);
    std::transform(str2.begin(), str2.end(), s2.begin(), ::tolower);
    return s1.compare(s2);

}

inline int stringContainsIgnoreCase(const std::string& str, const std::string& frag)
{
    std::string s1 = str;
    std::string s2 = frag;

    std::transform(str.begin(), str.end(), s1.begin(), ::tolower);
    std::transform(frag.begin(), frag.end(), s2.begin(), ::tolower);

	if (str.find(frag) == string::npos)
		return 0;
	else return 1;
}

inline string::size_type findStringIgnoreCase(std::string str, std::string sub)
{
    std::transform(str.begin(), str.end(), str.begin(), ::tolower);
    std::transform(sub.begin(), sub.end(), sub.begin(), ::tolower);
    return str.find(sub)!=std::string::npos;
}

inline string char2string(char c)
{
    string s;
    s = c;
    return s;
}
//This is for a vector of pointers. It will have compiler errors if used on a vector of none pointers
//It was written to replace  rwordered.clearAndDestroy
template < class T >
inline void delete_vector( std::vector<T> V )
{

   for (std::vector<T>::iterator itr = V.begin(); itr != V.end(); itr++) {
        delete *itr;
   }
}
//For Standard Vectors, this will go through and release the memory in a vector of pointers
template < class T >
inline void delete_vector( std::vector<T> *V )
{

   for (std::vector<T>::iterator itr = V->begin(); itr != V->end(); itr++) {
        delete *itr;
   }
}
//For Sorted Vectors
template<class K, bool bNoDuplicates,class Pr, class A >
inline void delete_vector( codeproject::sorted_vector<K,bNoDuplicates,Pr,A> V )
{

   for (codeproject::sorted_vector<K,bNoDuplicates,Pr,A>::iterator itr = V.begin(); 
        itr != V.end(); 
        itr++) 
   {
        delete *itr;
   }
}
template<class K, bool bNoDuplicates,class Pr, class A >
inline void delete_vector( codeproject::sorted_vector<K,bNoDuplicates,Pr,A> *V )
{

   for (codeproject::sorted_vector<K,bNoDuplicates,Pr,A>::iterator itr = V->begin(); 
        itr != V->end(); 
        itr++) 
   {
        delete *itr;
   }
}

#endif // #ifndef __UTILITY_H__
