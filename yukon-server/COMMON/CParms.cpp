#include "precompiled.h"

#include <string.h>
#include <stdio.h>
#include <iomanip>

#include "cparms.h"
#include "utility.h"
#include "shlwapi.h"
#include "encryption.h"

using namespace std;

CtiConfigParameters::CtiConfigParameters() :
RefreshRate(900),
LastRefresh(::time(NULL))
{
}

CtiConfigParameters::~CtiConfigParameters()
{
    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    for( mHash_itr2 itr = mHash.begin(); itr != mHash.end(); itr++ )
    {
        delete (*itr).second;
    }
    mHash.clear();
}

void CtiConfigParameters::setYukonBase(const string& strName)
{
    YukonBase = strName;

    char buf[MAX_PATH];

    PathCombine(buf, YukonBase.c_str(), "server\\config\\master.cfg");

    FileName = buf;
}

string CtiConfigParameters::getYukonBase() const
{
    return YukonBase;
}

int CtiConfigParameters::RefreshConfigParameters()
{
    const unsigned MAX_CONFIG_BUFFER = 1024;
    const unsigned MAX_CONFIG_KEY    = 256;
    const unsigned MAX_CONFIG_VALUE  = MAX_CONFIG_BUFFER - MAX_CONFIG_KEY;

    int   i;
    char  key;
    char  valstr[80];

    char        Buffer[MAX_CONFIG_BUFFER];
    char        chKey[MAX_CONFIG_KEY];
    char        chValue[MAX_CONFIG_VALUE];

    char        *pch;

    string   CurrentLine;

    FILE*       fp;


    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    for( mHash_itr2 itr = mHash.begin(); itr != mHash.end(); itr++ )
    {
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

                                std::pair< bool, std::string > processed = preprocessValue(chValue);

                                /*
                                    processed.first
                                        did we get an acceptable value from the input.
                                            true  : unencrypted cparm or a correctly decrypted encrypted cparm
                                            false : decryption error occured on an encrypted cparm
                                    processed.second
                                        if processed.first is true the we have the cparm value - properly decrypted
                                            if it was encrypted.
                                        if processed.first is false this value is the improperly decrypted cparm and is ignored
                                */

                                if ( processed.first )
                                {
                                    CtiConfigValue *Val = new CtiConfigValue( processed.second );
                                    mHash_pair2 p = mHash.insert( std::make_pair(string(chKey), Val) );
                                    if( !p.second )
                                    {
                                        cout << "CPARM " << chKey << " has already been inserted.. \n\tPlease check for duplicate entries in the master.cfg file " << endl;
                                        cout << "\t" << chKey << " : " << getValueAsString(string(chKey)) << endl;
                                        //delete Key;
                                        delete Val;
                                    }
                                }
                                else
                                {
                                    cout << "Error decrypting Value for Key: " << chKey << " - ignoring." << endl;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        fclose(fp);
    }

    static const string ConfKeyRefreshRate("CONFIG_REFRESHRATE");

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
    string Key;
    CtiConfigValue   *Value;

    checkForRefresh();

    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    if(mHash.size() > 0)
    {
        cout << endl << "Configuration Parameters:" << endl;
        for( mHash_itr2 iter = mHash.begin(); iter != mHash.end(); iter++ )
        {
            Key = (*iter).first;
            Value = (CtiConfigValue*)(*iter).second;

            cout << setiosflags(ios::left) << setw(30) << Key << " : " << setw(40) << Value->getValue() << endl;
        }
    }
    else
    {
        cout << "No configuration exists." << endl;
    }
}


// This checks if a master.cfg entry is encrypted and automatically decrypts it if found
std::pair< bool, std::string > CtiConfigParameters::preprocessValue( char * chValue ) const
{
    const std::string autoEncrypted( "(AUTO_ENCRYPTED)" );

    bool success = true;
    std::string retVal(chValue);

    if ( ! retVal.compare( 0, autoEncrypted.length(), autoEncrypted ) ) // starts with "(AUTO_ENCRYPTED)"
    {
        Cti::Encryption::Buffer encrypted,
                                decrypted;

        convertHexStringToBytes( retVal.substr( autoEncrypted.length() ), encrypted );

        try
        {
            decrypted = Cti::Encryption::decrypt( Cti::Encryption::MasterCfg, encrypted );
            retVal.assign( decrypted.begin(), decrypted.end() );
        }
        catch ( Cti::Encryption::Error e )
        {
            success = false;
            cout << "Decryption Exception thrown parsing master.cfg: " << e.what() << endl;
        }
    }

    return std::make_pair( success, retVal );
}


BOOL CtiConfigParameters::isOpt(const string& key)
{
    CtiConfigValue   *Value;

    checkForRefresh();

    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    mHash_itr2 itr = mHash.find(key);


    if( itr != mHash.end() )
        return TRUE;
    else
        return FALSE;
}

bool CtiConfigParameters::isOpt(const string& key, const string& isEqualThisValue)
{
    //CtiConfigKey     Key(key);
    CtiConfigValue   *Value;

    checkForRefresh();

    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    mHash_itr2 itr = mHash.find(key);

    if( (itr != mHash.end()) && ciStringEqual((*itr).second->getValue(),isEqualThisValue) )
        return true;
    else
        return false;
}

// If there is no entry in master.cfg this returns defaultval which defaults to false;
bool CtiConfigParameters::isTrue(const string &key, bool defaultval)
{
    bool retVal;
    static string truestr = "true";
    static string falsestr = "false";
    if( defaultval == true )
    {
        retVal = ciStringEqual( getValueAsString(key, truestr), truestr );
    }
    else
    {
        retVal = ciStringEqual( getValueAsString(key, falsestr), truestr );
    }
    return retVal;
}

string
CtiConfigParameters::getValueAsString(const string& key, const string& defaultval)
{
    BOOL           bRet = TRUE;
    //CtiConfigKey   Key(key);
    CtiConfigValue *Value;

    string retStr = defaultval;      // A Null string.

    checkForRefresh();

    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    mHash_itr2 itr = mHash.find(key);

    if( itr != mHash.end() )
    {
        Value = (CtiConfigValue*)(*itr).second;
        retStr = Value->getValue();
    }
    return retStr;
}

string
CtiConfigParameters::getValueAsPath(const string& key, const string& defaultval)
{
    BOOL           bRet = TRUE;
    //CtiConfigKey   Key(key);
    CtiConfigValue *Value;

    string retStr = defaultval;      // A Null string.

    checkForRefresh();

    CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

    mHash_itr2 itr = mHash.find(key);

    if( itr != mHash.end() )
    {
        Value = (CtiConfigValue*)(*itr).second;
        retStr = Value->getValue();
    }

    if( PathIsRelative(retStr.c_str()) )
    {
        char buf[MAX_PATH];

        PathCombine(buf, YukonBase.c_str(), retStr.c_str());

        retStr = buf;
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
        if (source[i] == '\0')
        {
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
        acquired = true;
        CtiParmLockGuard< CtiParmCriticalSection > cs_lock(crit_sctn);

        if(acquired && now - (ULONG)RefreshRate > LastRefresh)
        {
            LastRefresh = now;
            RefreshConfigParameters();
            bRet = true;
        }
    }

    return bRet;
}

