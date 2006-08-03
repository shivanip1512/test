#include "yukon.h"
#include <windows.h>
#include <string.h>
#include <stdio.h>
#include <iomanip>


#include "cparms.h"
#include "utility.h"
using namespace std;

CtiConfigParameters::CtiConfigParameters(const string& strName) :
FileName(strName),
RefreshRate(900),
LastRefresh(::time(NULL))
{
    if(!FileName.empty())
    {
        RefreshConfigParameters();
    }
}

CtiConfigParameters::~CtiConfigParameters()
{
    #ifndef USE_RECURSIVE_MUX
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif

    for( mHash_itr itr = mHash.begin(); itr != mHash.end(); itr++ )
    {
        delete (*itr).first;
        delete (*itr).second;
    }
    mHash.clear();
}

CtiConfigParameters& CtiConfigParameters::setConfigFile(const string& strName)
{
    FileName = strName;
    return *this;
}

string CtiConfigParameters::getYukonBaseDir() const
{
    char buf[2048] = "c:\\yukon";
    char* pos;

    //Assume the master.cfg is in the normal place
    //x:\\yukon\\server\\config\\master.cfg
    int n = GetFullPathName(FileName.c_str(), 2048, buf, &pos);
    if(n != 0 && n < 1000)
    {
        for(int i = 0; i < 3; i++)
        {
            if((pos = strrchr(buf, '\\')) != NULL)
                *pos = NULL;
            else
                break;
        }
    }

    return string(buf);
}

int CtiConfigParameters::RefreshConfigParameters()
{

    int   i;
    char  key;
    char  valstr[80];

    char        Buffer[MAX_CONFIG_BUFFER];
    char        chKey[MAX_CONFIG_KEY];
    char        chValue[MAX_CONFIG_VALUE];

    char        *pch;

    string   CurrentLine;

    FILE*       fp;


    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif

    for( mHash_itr itr = mHash.begin(); itr != mHash.end(); itr++ )
    {
        delete (*itr).first;
        delete (*itr).second;
    }
    mHash.clear();

    if( FileName.empty() )
    {
        return -1;
    }

    if((fp = fopen(FileName.c_str(), "r")) != NULL)
    {
        while(fgets(Buffer, MAX_CONFIG_BUFFER, fp))
        {
            // cout << Buffer;

            for(i = 0; i < MAX_CONFIG_BUFFER; i++)
            {
                if(Buffer[i] != ' ')
                {
                    if( Buffer[i] != '#' && Buffer[i] != ';' )
                    {
                        if((pch = strtok(&Buffer[i], ":")) != NULL)
                        {
                            HeadAndTail(pch, chKey, MAX_CONFIG_KEY);
                            if(pch = strtok(NULL, "\0\n\r"))
                            {
                                HeadAndTail(pch, chValue, MAX_CONFIG_VALUE);

                                CtiConfigKey   *Key = new CtiConfigKey(string(chKey));
                                CtiConfigValue *Val = new CtiConfigValue(string(chValue));
                                mHash_pair p = mHash.insert( std::make_pair(Key, Val) );
                                if( !p.second )
                                {
                                    cout << "CPARM " << chKey << " has already been inserted.. \n\tPlease check for duplicate entries in the master.cfg file " << endl;
                                    cout << "\t" << chKey << " : " << getValueAsString(string(chKey)) << endl;
                                    delete Key;
                                    delete Val;
                                }
#ifdef DEBUGLEVEL100
                                else
                                {
                                    cout << "Key/Value = " << Key->getKey() << " : " << Val->getValue() << endl;
                                }
#endif
                            }
                        }
                    }
                    break;
                }
            }
        }

        fclose(fp);
    }

    if(isOpt(ConfKeyRefreshRate))
    {
        RefreshRate = atoi(getValueAsString(ConfKeyRefreshRate).c_str());
        // cout << "1. Expiring every " << RefreshRate << " seconds" << endl;
    }
    else
    {
        // cout << "2. Expiring every " << RefreshRate << " seconds" << endl;
    }

    return 0;
}

void
CtiConfigParameters::Dump()
{
    CtiConfigKey     *Key;
    CtiConfigValue   *Value;

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif

    if(mHash.size() > 0)
    {
        cout << endl << "Configuration Parameters:" << endl;
        for( mHash_itr iter = mHash.begin(); iter != mHash.end(); iter++ )
        {
            Key = (CtiConfigKey*)(*iter).first;
            Value = (CtiConfigValue*)(*iter).second;

            cout << setiosflags(ios::left) << setw(30) << Key->getKey() << " : " << setw(40) << Value->getValue() << endl;
        }
    }
    else
    {
        cout << "No configuration exists." << endl;
    }
}

BOOL CtiConfigParameters::isOpt(const string& key)
{
    CtiConfigKey     Key(key);
    CtiConfigValue   *Value;

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    mHash_itr itr = mHash.find(&Key);
    

    if( itr != mHash.end() )
        return TRUE;
    else
        return FALSE;
}

bool CtiConfigParameters::isOpt(const string& key, const string& isEqualThisValue)
{
    CtiConfigKey     Key(key);
    CtiConfigValue   *Value;

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    mHash_itr itr = mHash.find(&Key);

    if( (itr != mHash.end()) && !stringCompareIgnoreCase((*itr).second->getValue(),isEqualThisValue) )
        return true;
    else
        return false;
}

bool CtiConfigParameters::isTrue(string key)
{
    return !stringCompareIgnoreCase( getValueAsString(key, "false"), "true" );
}

string
CtiConfigParameters::getValueAsString(const string& key, const string& defaultval)
{
    BOOL           bRet = TRUE;
    CtiConfigKey   Key(key);
    CtiConfigValue *Value;

    string retStr = defaultval;      // A Null string.

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif

    mHash_itr itr = mHash.find(&Key);

    if( itr != mHash.end() )
    {   
        Value = (CtiConfigValue*)(*itr).second;
        retStr = Value->getValue();
    }
    return retStr;
}

int CtiConfigParameters::getValueAsInt(const string& key, int defaultval)
{
    int ret = defaultval;

    if(isOpt(key))
    {
        ret = atoi(getValueAsString(key).c_str());
    }

    return ret;
}

ULONG CtiConfigParameters::getValueAsULong(const string& key, ULONG defaultval, int base)
{
    char *ch;
    ULONG ret = defaultval;

    if(isOpt(key))
    {
        ret = strtoul(getValueAsString(key).c_str(), &ch, base);
    }

    return ret;
}

double CtiConfigParameters::getValueAsDouble(const string& key, double defaultval)
{
    double ret = defaultval;

    if(isOpt(key))
    {
        ret = atof(getValueAsString(key).c_str());
    }

    return ret;
}

#ifdef TESTOBJECT

void Usage(void)
{
    cout << "You must be daft" << endl;
}

int main(int argc, char **argv)
{

    if(argc > 0)
    {
        CtiConfigParameters Opts(argc, argv);

        Opts.Dump();

        if(Opts.isOpt('?'))
        {
            Usage();
            exit(0);
        }

        cout << "d is set to " << Opts.ReturnIntOpt('d') << endl;
        if(Opts.isError()) cout << "Error " << Opts.isError() << endl;
        cout << "e is set to " << Opts.ReturnDoubleOpt('e') << endl;
        if(Opts.isError()) cout << "Error " << Opts.isError() << endl;
    }
    else
    {
        Usage();
    }



    return 0;
}
#endif

void CtiConfigParameters::HeadAndTail(char *source, char *dest, size_t len)
{
    int      i, j;
    char     *pch;

    dest[0] = '\0';

    for(i = 0; i < len; i++)
    {
        if(isgraph(source[i]))                // Leading Spaces and other unprintables are Gone Gone Gone...
        {
            if( source[i] != '#' && source[i] != ';' )
            {

                /*
                 *  This loop copies all bytes up to len into the dest buffer
                 *  It stops on the occurance of any of the chars listed in the if
                 */
                for(j = 0; j < len - i; j++)
                {
                    dest[j] = source[i+j];

                    if(
                      dest[j] == '#'  ||
                      dest[j] == ';'  ||
                      dest[j] == '\0' ||
                      dest[j] == '\r' ||
                      dest[j] == '\n'
                      )
                    {
                        dest[j] = '\0';
                        break;                  // inner for
                    }
                }

                /*
                 *  Starting at the end and cruising backwards, this loop searches for the first non-whitespace
                 *  character in the string.
                 */
                for(i = strlen(dest) - 1; i > 0; i--)
                {
                    if(dest[i] == ' ' || dest[i] == '\t')
                    {
                        dest[i] = '\0';
                    }
                    else
                    {
                        break; // the for
                    }

                }
            }
            break;
        }
    }
}

bool CtiConfigParameters::checkForRefresh()
{
    bool bRet = false;
    bool acquired = false;

    time_t now = ::time(NULL);

    if(now - (ULONG)RefreshRate > LastRefresh)
    {
        #ifdef USE_RECURSIVE_MUX
        RWRecursiveLock<RWMutexLock>::TryLockGuard gaurd(mutex);           // Do it this way to reduce the need for locking to only once in a while, and no deadlocks.
        acquired = gaurd.isAcquired();
        #else
        acquired = true;
        CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
        #endif

        if(acquired && now - (ULONG)RefreshRate > LastRefresh)
        {
            LastRefresh = now;
            RefreshConfigParameters();
            bRet = true;
        }
    }

    return bRet;
}

