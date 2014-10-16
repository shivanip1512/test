#pragma once

#include <string>
#include <list>
#include <iterator> // for std::iterator and std::output_iterator_tag

#include "ctitime.h"
#include "queues.h"
#include "dlldefs.h"
#include "numstr.h"

#include <algorithm>
#include <vector>

class INMESS;
class CtiOutMessage;
class CtiMutex;



IM_EX_CTIBASE LONG GetMaxLMControl(long pao);

//  Functions for ID generation
IM_EX_CTIBASE LONG LMControlHistoryIdGen(bool force = false);
IM_EX_CTIBASE LONG VerificationSequenceGen(bool force = false, int force_value = 0);
IM_EX_CTIBASE int  DynamicPaoStatisticsIdGen();
IM_EX_CTIBASE __int64 ChangeIdGen(bool force = false);
IM_EX_CTIBASE INT  SystemLogIdGen();
IM_EX_CTIBASE INT  CCEventActionIdGen(LONG capBankPointId);
IM_EX_CTIBASE INT  CCEventLogIdGen();
IM_EX_CTIBASE INT  CCEventSeqIdGen();
IM_EX_CTIBASE INT  PAOIdGen();
IM_EX_CTIBASE INT  SynchronizedIdGen(std::string name, int values_needed);

IM_EX_CTIBASE void InEchoToOut(const INMESS &In, CtiOutMessage &Out);
IM_EX_CTIBASE void OutEchoToIN(const CtiOutMessage &Out, INMESS &In);

IM_EX_CTIBASE std::string convertVersacomAddressToHumanForm(INT address);
IM_EX_CTIBASE INT    convertHumanFormAddressToVersacom(INT address);

IM_EX_CTIBASE std::string& traceBuffer(std::string &str, BYTE *Message, ULONG Length);

IM_EX_CTIBASE CtiTime nextScheduledTimeAlignedOnRate( const CtiTime &origin, LONG rate );

IM_EX_CTIBASE void autopsy(const char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);

IM_EX_CTIBASE BOOL searchFuncForOutMessageDevID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageRteID(void *pId, void* d);
IM_EX_CTIBASE BOOL searchFuncForOutMessageUniqueID(void *pId, void* d);

IM_EX_CTIBASE void applyPortQueueOutMessageReport(void *ptr, void* d);

IM_EX_CTIBASE double limitValue(double input, double min, double max);


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


IM_EX_CTIBASE void SetThreadName( DWORD dwThreadID, LPCSTR szThreadName);

IM_EX_CTIBASE void CreateMiniDump(const std::string &dumpfilePrefix);

extern LONG gOutMessageCounter;

IM_EX_CTIBASE void  incrementCount();
IM_EX_CTIBASE void  decrementCount();
IM_EX_CTIBASE LONG  OutMessageCount();

IM_EX_CTIBASE bool  isLCU(INT type);
IM_EX_CTIBASE bool  isExpresscomGroup(INT Type);
IM_EX_CTIBASE bool  isRepeater(INT Type);

IM_EX_CTIBASE int   generateTransmissionID();

IM_EX_CTIBASE LONG  GetPAOIdOfPoint(long pid);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndOffset(int device, int offset);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndControlOffset(int device, int offset);
IM_EX_CTIBASE INT   GetPIDFromDeviceAndOffsetAndType(int device, int offset, std::string &type);
IM_EX_CTIBASE std::vector<unsigned long>  GetPseudoPointIDs();

IM_EX_CTIBASE INT   EstablishOutMessagePriority(CtiOutMessage *Out, INT priority);
IM_EX_CTIBASE INT   OverrideOutMessagePriority(CtiOutMessage *Out, INT priority);

IM_EX_CTIBASE ULONG   BCDtoBase10(const UCHAR* buffer, ULONG len);
IM_EX_CTIBASE ULONG   StrToUlong(UCHAR* buffer, ULONG len);
IM_EX_CTIBASE void  convertHexStringToBytes( std::string & stringInput, std::vector< unsigned char > & result );

IM_EX_CTIBASE INT     CheckCCITT16CRC(INT Id,BYTE *InBuffer,ULONG InCount);
IM_EX_CTIBASE USHORT  CCITT16CRC(INT Id, BYTE* buffer, LONG length, BOOL bAdd);

IM_EX_CTIBASE USHORT  ShortLittleEndian(USHORT *ShortEndianFloat);
IM_EX_CTIBASE FLOAT   FltLittleEndian(FLOAT *BigEndianFloat);

IM_EX_CTIBASE ULONG   MilliTime (PULONG);

IM_EX_CTIBASE bool findLPRequestEntries(void *om, void *d);
IM_EX_CTIBASE bool findRequestIDMatch(void *rid, void *d);
IM_EX_CTIBASE bool findExpiredOutMessage(void *rid, void *d);
IM_EX_CTIBASE void cleanupOutMessages(void *unusedptr, void* d);

IM_EX_CTIBASE std::string explainTags(const unsigned tags);

IM_EX_CTIBASE int binaryStringToInt(const CHAR *buffer, int length);

IM_EX_CTIBASE std::vector<int> getPointIdsOnPao(long paoid);
IM_EX_CTIBASE std::string getEncodingTypeForPort(long portId);
IM_EX_CTIBASE std::string getEncodingKeyForPort(long portId);

IM_EX_CTIBASE bool canConnectToDatabase();

//String Functions
inline void CtiToLower( std::string& str)
{
    std::transform(str.begin(),str.end(),str.begin(),::tolower);
}
inline void CtiToUpper( std::string& str)
{
    std::transform(str.begin(),str.end(),str.begin(),::toupper);
}

inline std::string trim_right ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( str.find_last_not_of ( t ) + 1 ) ;
}

inline std::string trim_left ( std::string & source ,std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( 0 , source.find_first_not_of ( t ) ) ;
}

inline std::string trim ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = trim_left ( trim_right ( str , t ) , t ) ;
}

inline std::string &in_place_trim(std::string &source, char trim_char = ' ')
{
    //  clever - the "+ 1" turns string::npos into 0 if nothing is found
    source.erase(source.find_last_not_of(trim_char) + 1);
    return source.erase(0, source.find_first_not_of(trim_char));
}

inline bool ci_equal(char ch1, char ch2)
{
    return ::tolower((unsigned char)ch1) == ::tolower((unsigned char)ch2);
}

/**
 * Case Insensitive comparison
 */
inline bool ciStringEqual(const std::string& str1, const std::string& str2)
{
    if( str1.size() != str2.size() )
        return false;
    else
        return std::equal(str1.begin(), str1.end(), str2.begin(), ci_equal);
}

/**
 * Case Sensitive comparison
 */
inline bool StringEqual(const std::string& str1, const std::string& str2)
{
    if( str1.size() != str2.size() )
        return false;
    else
        return std::equal(str1.begin(), str1.end(), str2.begin());
}

inline int stringContainsIgnoreCase(const std::string& str, const std::string& frag)
{
    if( std::search(str.begin(), str.end(), frag.begin(), frag.end(), ci_equal) == str.end() )
        return 0;
    else
        return 1;
}

inline std::string::size_type findStringIgnoreCase(const std::string &str, const std::string &sub)
{
    return stringContainsIgnoreCase(str, sub);
}

inline std::string char2string(char c)
{
    std::string s;
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
struct csv_output_iterator : public std::iterator<std::output_iterator_tag, void, void, void, void>
{
    Stream &s;
    bool first;
    Numeric num;

    csv_output_iterator(Stream &s_) : s(s_), first(true) {}

    csv_output_iterator &operator*()  {  return *this;  }
    csv_output_iterator &operator=(Numeric num_)  {  num = num_;  return *this;  }
    csv_output_iterator &operator++()  { (first?(first = false, s):(s << ",")) << num;  return *this;  }
};


//  usage is as an output iterator - e.g.
//  vector<char> source;
//    ...
//  std::copy(source.begin(), source.end(), padded_output_iterator<int>(dout, '0', 2));
template <class Numeric, class Stream>
struct padded_output_iterator : public std::iterator<std::output_iterator_tag, void, void, void, void>
{
    Stream &s;
    bool first;
    Numeric num;
    char old_fill;
    unsigned width;

    padded_output_iterator(Stream &s_, char fill_, unsigned width_) : s(s_), width(width_), first(true), old_fill(s.fill(fill_)) {}
    ~padded_output_iterator() {  s.fill(old_fill);  }

    padded_output_iterator &operator*()  {  return *this;  }
    padded_output_iterator &operator=(Numeric num_)  {  num = num_;  return *this;  }
    padded_output_iterator &operator++()
    {
        if( first )
        {
            first = false;
        }
        else
        {
            s << " ";
        }

        s << std::setw(width) << num;

        return *this;
    }
};

template <class T>
struct priority_sort : public std::binary_function<T, T, bool>
{
    bool operator()(const T &lhs, const T &rhs) const
    {
        return lhs.Priority > rhs.Priority;
    }
};
