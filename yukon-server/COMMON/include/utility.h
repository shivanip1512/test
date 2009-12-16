/*-----------------------------------------------------------------------------*
*
* File:   utility
*
* Date:   4/14/2000
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/utility.h-arc  $
* REVISION     :  $Revision: 1.61.2.5 $
* DATE         :  $Date: 2008/11/20 16:49:26 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __UTILITY_H__
#define __UTILITY_H__
#pragma warning( disable : 4786)


#include <string>
#include <list>
using std::list;
using std::string;

#include "ctitime.h"

#include "queues.h"
#include "dlldefs.h"
#include "numstr.h"
#include "sorted_vector.h"
using std::vector;

class INMESS;
class CtiOutMessage;
class CtiMutex;


IM_EX_CTIBASE LONG GetMaxLMControl(long pao);

//  Functions for ID generation
IM_EX_CTIBASE LONG LMControlHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG CommErrorHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG VerificationSequenceGen(bool force = false, int force_value = 0);
IM_EX_CTIBASE INT  ChangeIdGen(bool force = false);
IM_EX_CTIBASE INT  SystemLogIdGen();
IM_EX_CTIBASE INT  CCEventActionIdGen(LONG capBankPointId);
IM_EX_CTIBASE INT  CCEventLogIdGen();
IM_EX_CTIBASE INT  CCEventSeqIdGen();
IM_EX_CTIBASE INT  PAOIdGen();
IM_EX_CTIBASE INT  SynchronizedIdGen(string name, int values_needed);

IM_EX_CTIBASE BOOL InEchoToOut(const INMESS *In, CtiOutMessage *Out);
IM_EX_CTIBASE BOOL OutEchoToIN(const CtiOutMessage *Out, INMESS *In);

IM_EX_CTIBASE string convertVersacomAddressToHumanForm(INT address);
IM_EX_CTIBASE INT    convertHumanFormAddressToVersacom(INT address);

IM_EX_CTIBASE string& traceBuffer(string &str, BYTE *Message, ULONG Length);

IM_EX_CTIBASE CtiTime nextScheduledTimeAlignedOnRate( const CtiTime &origin, LONG rate );

IM_EX_CTIBASE void autopsy(const char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);

IM_EX_CTIBASE BOOL searchFuncForOutMessageDevID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageRteID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageUniqueID(void *pId, void* d);

IM_EX_CTIBASE void applyPortQueueOutMessageReport(void *ptr, void* d);

IM_EX_CTIBASE double limitValue(double input, double min, double max);

struct compileinfo_t
{
   char *project;
   char *version;
   char *details;
   char *date;
};

extern compileinfo_t CompileInfo;

IM_EX_CTIBASE void identifyProject(const compileinfo_t &Info);
IM_EX_CTIBASE bool setConsoleTitle(const compileinfo_t &Info);

#ifndef BUILD_VERSION
#define BUILD_VERSION (untagged)
#endif

#ifndef BUILD_VERSION_DETAILS
#define BUILD_VERSION_DETAILS __TIMESTAMP__
#endif

// needed to turn a #define into a string
#define STRINGIZE( x ) #x

//  common info across all projects
#define SETCOMPILEINFO( x, y, z ) compileinfo_t CompileInfo = { x, STRINGIZE(y), STRINGIZE(z), __TIMESTAMP__ }

#define PROJECT_ID( x ) SETCOMPILEINFO( x, BUILD_VERSION, BUILD_VERSION_DETAILS )


struct CtiQueueAnalysis_t
{

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
};


class CtiHighPerfTimer
{
private:
    LARGE_INTEGER _perfFrequency;
    LARGE_INTEGER _start;
    LARGE_INTEGER _stop;
    UINT _gripe;

    string _name;

    string _file;
    UINT _line;

    CtiHighPerfTimer(  ) : _gripe(0)
    {
        QueryPerformanceFrequency(&_perfFrequency);
        QueryPerformanceCounter(&_start);
    }

    string getName() const
    {
        string str = _name;
        if( !_file.empty() ) str += string(" (") + _file + string(":") + string(CtiNumStr(_line)) + string(")");
        return str;
    }

    inline UINT PERF_TO_MS(LARGE_INTEGER b,LARGE_INTEGER a,LARGE_INTEGER p)
    {
        return ((UINT)((b.QuadPart - a.QuadPart) / (p.QuadPart / 1000L)));
    }

public:
    CtiHighPerfTimer( string name, UINT gripeDelta = 0, string file = string(), UINT line = 0 );

    ~CtiHighPerfTimer();
    CtiHighPerfTimer& reset();
    CtiHighPerfTimer& report(bool force = true);
    UINT delta();
    CtiHighPerfTimer& rename(string name, string file = string(""), UINT line = 0);
    CtiHighPerfTimer& relocate(string file, UINT line);
};



IM_EX_CTIBASE void SetThreadName( DWORD dwThreadID, LPCSTR szThreadName);

extern LONG gOutMessageCounter;

IM_EX_CTIBASE void  incrementCount();
IM_EX_CTIBASE void  decrementCount();
IM_EX_CTIBASE LONG  OutMessageCount();

IM_EX_CTIBASE bool  isLCU(INT type);
IM_EX_CTIBASE bool  isION(INT type);
IM_EX_CTIBASE bool  isMCT(INT type);
IM_EX_CTIBASE bool  isExpresscomGroup(INT Type);

IM_EX_CTIBASE int   generateTransmissionID();

IM_EX_CTIBASE LONG  GetPAOIdOfPoint(long pid);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndOffset(int device, int offset);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndControlOffset(int device, int offset);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndOffsetAndType(int device, int offset, string &type);
IM_EX_CTIBASE void  GetPseudoPointIDs(std::vector<unsigned long> &pointIDs);

IM_EX_CTIBASE INT   EstablishOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE INT   OverrideOutMessagePriority(CtiOutMessage *Out, INT priority);

IM_EX_CTIBASE ULONG   BCDtoBase10(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE ULONG   StrToUlong(UCHAR* buffer, ULONG len);

IM_EX_CTIBASE INT     CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount);
IM_EX_CTIBASE USHORT  CCITT16CRC(INT Id, BYTE* buffer, LONG length, BOOL bAdd);

IM_EX_CTIBASE USHORT  ShortLittleEndian(USHORT *ShortEndianFloat);
IM_EX_CTIBASE FLOAT   FltLittleEndian(FLOAT  *BigEndianFloat);
IM_EX_CTIBASE DOUBLE  DblLittleEndian(DOUBLE *BigEndianDouble);
IM_EX_CTIBASE VOID    BDblLittleEndian(CHAR *BigEndianBDouble);

IM_EX_CTIBASE ULONG   MilliTime (PULONG);

IM_EX_CTIBASE LONG    ResetBreakAlloc();

IM_EX_CTIBASE bool findLPRequestEntries(void *om, void *d);
IM_EX_CTIBASE bool findRequestIDMatch(void *rid, void *d);
IM_EX_CTIBASE bool findExpiredOutMessage(void *rid, void *d);
IM_EX_CTIBASE void cleanupOutMessages(void *unusedptr, void* d);

IM_EX_CTIBASE string explainTags(const unsigned tags);

IM_EX_CTIBASE int binaryStringToInt(const CHAR *buffer, int length);

IM_EX_CTIBASE unsigned char addBitToSA305CRC(unsigned char crc, unsigned char bit); // bit is 0 or 1
IM_EX_CTIBASE unsigned char addOctalCharToSA305CRC(unsigned char crc, unsigned char ch); // octal char

IM_EX_CTIBASE void testSA305CRC(char* testData);

IM_EX_CTIBASE LONG GetPAOIdOfEnergyPro(long devicesn);

IM_EX_CTIBASE std::vector<int> getPointIdsOnPao(long paoid);
IM_EX_CTIBASE std::vector< std::vector<string> > getLmXmlParametersByGroupId(long groupId);
IM_EX_CTIBASE string getEncodingTypeForPort(long portId);
IM_EX_CTIBASE string getEncodingKeyForPort(long portId);

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

inline string &in_place_trim(std::string &source, char trim_char = ' ')
{
    //  clever - the "+ 1" turns string::npos into 0 if nothing is found
    source.erase(source.find_last_not_of(trim_char) + 1);
    return source.erase(0, source.find_first_not_of(trim_char));
}

inline bool ci_equal(char ch1, char ch2)
{
    return ::tolower((unsigned char)ch1) == ::tolower((unsigned char)ch2);
}

inline int stringCompareIgnoreCase(const std::string& str1, const std::string& str2)
{
    if( str1.size() != str2.size() )
        return 1;
    else
        return !std::equal(str1.begin(), str1.end(), str2.begin(), ci_equal);
}

inline int stringContainsIgnoreCase(const std::string& str, const std::string& frag)
{
    if( std::search(str.begin(), str.end(), frag.begin(), frag.end(), ci_equal) == str.end() )
        return 0;
    else
        return 1;
}

inline string::size_type findStringIgnoreCase(const std::string &str, const std::string &sub)
{
    return stringContainsIgnoreCase(str, sub);
}

inline string char2string(char c)
{
    string s;
    s = c;
    return s;
}

//  This is for a container of pointers. It will have compiler errors if used on a container of non-pointer types.
template <class Container>
inline void delete_container( Container &C )
{
    for( Container::iterator itr = C.begin(); itr != C.end(); itr++)
    {
        delete *itr;
        *itr = NULL;
    }
}

template <class Container, class Element, class Argument>
void delete_container_if( Container &C, bool (*predicate)(const Element *, Argument), Argument arg)
{
    for( Container::iterator itr = C.begin(); itr != C.end(); itr++)
    {
        if( predicate(*itr, arg) )
        {
            delete *itr;
            *itr = NULL;
        }
    }
}


template <class AssocContainer>
inline void delete_assoc_container( AssocContainer &AC )
{
    for( AssocContainer::iterator itr = AC.begin(); itr != AC.end(); itr++)
    {
        if( (*itr).second != NULL )
        {
            delete (*itr).second;
            (*itr).second = NULL;
        }
    }
}


template <class T>
inline bool list_contains( const std::list<T> &V, T x )
{
    for( std::list<T>::const_iterator itr = V.begin(); itr != V.end(); itr++ )
    {
        if( **itr == *x )
        {
            return true;
        }
   }

   return false;
}


//  generates a comma-separated list of values, i.e. 1,2,3,4,5
//  usage is as an output iterator - e.g.
//  vector<long> source;
//  ostringstream output;
//  csv_output_iterator<long, ostringstream> csv_itr(output);  //  OR csv_itr<long, CtiLogger>(dout)>
//    ...
//  std::copy(source.begin(), source.end(), csv_itr);
template <class Numeric, class Stream>
struct csv_output_iterator : public std::_Outit
{
    Stream &s;
    bool first;
    Numeric num;

    csv_output_iterator(Stream &s_) : s(s_), first(true) {};

    csv_output_iterator &operator*()  {  return *this;  };
    csv_output_iterator &operator=(Numeric num_)  {  num = num_;  return *this;  };
    csv_output_iterator &operator++()  { (first?(first = false, s):(s << ",")) << num;  return *this;  };
};


#endif // #ifndef __UTILITY_H__
