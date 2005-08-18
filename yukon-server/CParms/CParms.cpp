#include "yukon.h"
#include <windows.h>
#include <string.h>
#include <stdio.h>
#include <iomanip>
using namespace std;

#include "cparms.h"
#include "configkey.h"
#include "configval.h"

CtiConfigParameters::CtiConfigParameters(RWCString strName) :
FileName(strName),
RefreshRate(900)
{
    if(!FileName.isNull())
    {
        RefreshConfigParameters();
    }
}

CtiConfigParameters::~CtiConfigParameters()
{
    #ifndef USE_RECURSIVE_MUX
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    mHash.clearAndDestroy();
}

CtiConfigParameters& CtiConfigParameters::setConfigFile(RWCString strName)
{
    FileName = strName;
    return *this;
}

RWCString CtiConfigParameters::getYukonBaseDir() const
{
    char buf[2048] = "c:\\yukon";
    char* pos;

    //Assume the master.cfg is in the normal place
    //x:\\yukon\\server\\config\\master.cfg
    int n = GetFullPathName(FileName, 2048, buf, &pos);
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

    return RWCString(buf);
}

int CtiConfigParameters::RefreshConfigParameters()
{

    int   i;
    char  key;

    char        Buffer[MAX_CONFIG_BUFFER];
    char        chKey[MAX_CONFIG_KEY];
    char        chValue[MAX_CONFIG_VALUE];

    char        *pch;

    RWCString   CurrentLine;

    FILE*       fp;


    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif

    mHash.clearAndDestroy();

    if( FileName.isNull() )
    {
        return -1;
    }

    if((fp = fopen(FileName, "r")) != NULL)
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

                                CtiConfigKey   *Key = new CtiConfigKey(RWCString(chKey));
                                CtiConfigValue *Val = new CtiConfigValue(RWCString(chValue));

                                if(!mHash.insertKeyAndValue(Key, Val))
                                {
                                    cout << "CPARM " << chKey << " has already been inserted.. \n\tPlease check for duplicate entries in the master.cfg file " << endl;
                                    cout << "\t" << chKey << " : " << getValueAsString(RWCString(chKey)) << endl;
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
        RefreshRate = atoi(getValueAsString(ConfKeyRefreshRate));
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

    if(mHash.entries() > 0)
    {
        RWHashDictionaryIterator iter(mHash);

        cout << endl << "Configuration Parameters:" << endl;
        while(iter())
        {
            Key = (CtiConfigKey*)iter.key();
            Value = (CtiConfigValue*)iter.value();

            cout << setiosflags(ios::left) << setw(30) << Key->getKey() << " : " << setw(40) << Value->getValue() << endl;

        }
    }
    else
    {
        cout << "No configuration exists." << endl;
    }
}

BOOL CtiConfigParameters::isOpt(RWCString key)
{
    CtiConfigKey     Key(key);
    CtiConfigValue   *Value;

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    Value = (CtiConfigValue*)mHash.findValue(&Key);

    if(Value)
        return TRUE;
    else
        return FALSE;
}

bool CtiConfigParameters::isOpt(RWCString key, RWCString isEqualThisValue)
{
    CtiConfigKey     Key(key);
    CtiConfigValue   *Value;

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    Value = (CtiConfigValue*)mHash.findValue(&Key);

    if(Value && !Value->getValue().compareTo(isEqualThisValue, RWCString::ignoreCase) )
        return true;
    else
        return false;
}


RWCString
CtiConfigParameters::getValueAsString(RWCString key, RWCString defaultval)
{
    BOOL           bRet = TRUE;
    CtiConfigKey   Key(key);
    CtiConfigValue *Value;

    RWCString retStr = defaultval;      // A Null string.

    checkForRefresh();

    #ifdef USE_RECURSIVE_MUX
    RWRecursiveLock<RWMutexLock>::LockGuard gaurd(mutex);
    #else
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
    #endif
    Value = (CtiConfigValue*)mHash.findValue(&Key);

    if(Value != NULL)
    {
        retStr = Value->getValue();
    }
    return retStr;
}

int CtiConfigParameters::getValueAsInt(RWCString key, int defaultval)
{
    int ret = defaultval;

    if(isOpt(key))
    {
        ret = atoi(getValueAsString(key));
    }

    return ret;
}

ULONG CtiConfigParameters::getValueAsULong(RWCString key, ULONG defaultval, int base)
{
    char *ch;
    ULONG ret = defaultval;

    if(isOpt(key))
    {
        ret = strtoul(getValueAsString(key).data(), &ch, base);
    }

    return ret;
}

double CtiConfigParameters::getValueAsDouble(RWCString key, double defaultval)
{
    double ret = defaultval;

    if(isOpt(key))
    {
        ret = atof(getValueAsString(key));
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
        if(source[i] != ' ')                // Leading Spaces are Gone Gone Gone...
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

    RWTime now;

    if(now.seconds() - (ULONG)RefreshRate > LastRefresh.seconds())
    {
        #ifdef USE_RECURSIVE_MUX
        RWRecursiveLock<RWMutexLock>::TryLockGuard gaurd(mutex);           // Do it this way to reduce the need for locking to only once in a while, and no deadlocks.
        acquired = gaurd.isAcquired();
        #else
        acquired = true;
        CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);
        #endif

        if(acquired && now.seconds() - (ULONG)RefreshRate > LastRefresh.seconds())
        {
            LastRefresh = now;
            RefreshConfigParameters();
            bRet = true;
        }
    }

    return bRet;
}

