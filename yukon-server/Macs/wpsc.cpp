/*-----------------------------------------------------------------------------
    Filename:  cfdata.h

    Programmer:  Aaron Lauinger

    Description:    Source file for wisconsin public service company(corp)
                    file format decoders.

    Initial Date:  4/7/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "wpsc.h"
#include "numstr.h"

int gPagingConfigRouteID    = -1;
int gFMConfigRouteID        = -1;
int gFMConfigSerialLow[10]; //     = -1;
int gFMConfigSerialHigh[10];  //   = -1;

using std::iostream;
using std::string;
using std::endl;

/*-----------------------------------------------------------------------------
    DecodeCFDATAFile

    file is a cstring that represents the full path of the file to decode


    returns TRUE if successfull
    FALSE otherwise
-----------------------------------------------------------------------------*/
bool DecodeCFDATAFile(const string& file, std::vector<std::string*>* ordered)
{
    FILE* fptr;
    char l_buf[36];  //each line is 33 char's long + \r\n + add a byte for NULL

    if( ordered == NULL )
        return false;

    if( (fptr = fopen( file.c_str(), "r")) == NULL )
    {
        CTILOG_ERROR(dout, "Unable to open file");
        return false;
    }

    while( fgets( (char*) l_buf, 36, fptr) != NULL )
    {
        if( strlen( l_buf) != 34 )
            continue;

        std::string *decoded = new std::string();

        if( DecodeCFDATALine( l_buf, decoded ) == false)
        {
            fclose(fptr);
            delete decoded;
            break;
        }

        ordered->push_back(decoded);
    }

    fclose(fptr);

    if( ferror( fptr ) != 0 )
        return false;

    return true;
}


bool DecodeEOIFile(const string& file, std::vector<std::string*>* ordered)
{
    FILE* fptr;
    char l_buf[1000];

    if( ordered == NULL )
        return false;

    if( (fptr = fopen( file.c_str(), "r")) == NULL )
    {
        CTILOG_ERROR(dout, "Unable to open file");
        return false;
    }

    while( fgets( (char*) l_buf, 1000, fptr) != NULL )
    {
        if( DecodeEOILine( l_buf, ordered ) == false)
        {
            fclose(fptr);
            break;
        }
    }

    fclose(fptr);

    if( ferror( fptr ) != 0 )
        return false;

    return true;
}

bool DecodeWepcoFile(const string& file, std::vector<std::string*>* ordered)
{
    FILE* fptr;
    char l_buf[1000];

    if( ordered == NULL )
        return true;

    if( (fptr = fopen( file.c_str(), "r")) == NULL )
    {
        CTILOG_ERROR(dout, "Unable to open file");
        return false;
    }

    while( fgets( (char*) l_buf, 1000, fptr) != NULL )
    {
        if( DecodeWepcoLine( l_buf, ordered ) == false)
        {
            fclose(fptr);
            break;
        }
    }

    fclose(fptr);

    if( ferror( fptr ) != 0 )
        return false;

    return true;
}

bool DecodeWepcoFileService(const string& file, std::vector<std::string*>* results)
{
    FILE* fptr;
    char l_buf[1000];

    if ( results == NULL )
        return true;

    if ( (fptr = fopen( file.c_str(), "r")) == NULL )
    {
        CTILOG_ERROR(dout, "Unable to open file");
        return false;
    }

    while ( fgets( (char*) l_buf, 1000, fptr) != NULL )
    {
        if ( DecodeWepcoServiceLine( l_buf, results ) == false)
        {
            fclose(fptr);
            break;
        }
    }

    fclose(fptr);

    if ( ferror( fptr ) != 0 )
        return false;

    return true;

}


bool DecodeWepcoFileConfig(const string& file, std::vector<std::string*>* results)
{
    FILE* fptr;
    char l_buf[1000];

    if ( results == NULL )
        return false;

    if ( (fptr = fopen( file.c_str(), "r")) == NULL )
    {
        CTILOG_ERROR(dout, "Unable to open file");
        return false;
    }

    while ( fgets( (char*) l_buf, 1000, fptr) != NULL )
    {
        if ( DecodeWepcoConfigLine( l_buf, results ) == false)
        {
            fclose(fptr);
            break;
        }
    }

    fclose(fptr);

    if ( ferror( fptr ) != 0 )
        return false;

    return true;

}



bool DecodeCFDATALine( char* line, string* decoded )
{
    char delim = ' ';
    char* pos = 0;
    char buf[80];

    int func;
    int temp;

    long serial_num = -1;

    unsigned short class_id = 0;
    unsigned short div_id = 0;

    *decoded = "set MessagePriority 6 ; putconfig versacom serial ";

    //DLC Function (2bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 3;

    func = atoi(pos);

    //Serial number of switch (6 bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 7;

    serial_num = atol(pos);

    *decoded += pos;

    if( func == 4 )
    {
        *decoded += " service in ";
        *decoded += GetSelectCustomRouteID(serial_num);
        return true;
    }
    else
    if( func == 5 )
    {
        *decoded += " service out ";
        *decoded += GetSelectCustomRouteID(serial_num);
        return true;
    }

    //Utility ID (3 bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 4;

    if( func != 2 )
    {
        temp = atoi(pos);
        sprintf( buf, "%d", temp );

        *decoded += " utility ";
        *decoded += buf;
    }

    //Division Code - aux (3 bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 4;

    temp = atoi(pos);
    sprintf(buf, "%d", temp);

    *decoded += " aux ";
    *decoded += buf;

    //Operating Disctrict - section (3 bytes )
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 4;

    temp = atoi(pos);
    sprintf(buf, "%d", temp);

    *decoded += " section ";
    *decoded += buf;

    //Class #1 (2bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 3;

    temp = atoi(pos);

    if( temp > 0 )
        class_id |= (1 << (temp-1));

    //Class #2 (2bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 3;

    temp = atoi(pos);

    if( temp > 0 )
        class_id |= (1 << (temp-1));

    //Class #3 (2bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 3;

    temp = atoi(pos);

    if( temp > 0 )
        class_id |= (1 << (temp-1));

    sprintf(buf, "0x%04x", class_id);

    *decoded += " class ";
    *decoded += buf;

    //DLC Division (2 bytes)
    if( (pos = strtok( line, &delim)) == NULL )
        return false;

    line += 3;

    temp = atoi(pos);

    if( temp > 0 )
           div_id |= (1 << (temp-1));

    sprintf(buf, "0x%04x", div_id);

    *decoded += " division ";
    *decoded += buf;

    string route_select = GetSelectCustomRouteID(serial_num);
    if( route_select.length() > 0 ) {
        *decoded += " ";
        *decoded += route_select;
    }

    return true;
}

/*
    Decodes a line from an EOI command file.
    $VSERV=<groupname>,VN<serial num>,CONTRACT=(IN|OUT),TEMP=(IN|OUT)
*/

bool DecodeEOILine( char* line, std::vector<std::string*>* results)
{
    char* group = NULL;
    char* serial = NULL;
    char* contract = NULL;
    char* temp = NULL;

    char* token;
    char* delim = "=";

    // strip command "$VSERV"
    token = strtok(line, delim);

    if( token == NULL || strcmp(token, "$VSERV") != 0 )
        return false;

    delim = ",";
    token = strtok(NULL,delim);
    while( token != NULL ) {

        if( strncmp(token, "VN", 2) == 0) {
            serial = token;
        }
        else
        if( strncmp(token, "CONTRACT=", 9) == 0 ) {
            contract = token;
        }
        else
        if( strncmp(token, "TEMP=", 5) == 0 ) {
            temp = token;
        }
        else {
            group = token;
        }

        token = strtok(NULL, delim);
    }

    if( serial != NULL ) {
        serial += 2;
    }

    delim = "=\r\n";
    if( contract != NULL ) {
        if( (token = strtok(contract,delim)) != NULL ) {
            if( (token = strtok(NULL,delim)) != NULL ) {
                contract = token;
            }
        }
    }

    if( temp != NULL ) {
        if( (token = strtok(temp,delim)) != NULL ) {
            if( (token = strtok(NULL,delim)) != NULL ) {
                temp = token;
            }
        }
    }


    //Either use the serial number or the group, not both
    string serial_str;
    string select_str(" select name \"System Device\"");

    if( serial != NULL ) {

        serial_str += "serial ";
        serial_str += serial;

        long serial_num = atol(serial);

        string route_select = GetSelectCustomRouteID(serial_num);
        if( route_select.length() > 0 ) {
            select_str = " ";
            select_str += route_select;
        }
    }
    else
    if( group != NULL ) {
        select_str = " select name \"";
        select_str += group;
        select_str += "\"";
    }
    else {
        return false;
    }

    if( contract != NULL ) {
        std::string* decoded = new std::string();
        *decoded = "set MessagePriority 6 ; putconfig versacom ";
        *decoded += serial_str.c_str();
        *decoded += " service ";
        *decoded += contract;
        *decoded += select_str.c_str();

        results->push_back(decoded);
    }

    if( temp != NULL ) {
        std::string* decoded = new std::string();
        *decoded = "set MessagePriority 6 ; putconfig versacom ";
        *decoded += serial_str.c_str();
        *decoded += " service ";
        *decoded += temp;
        *decoded += " t";
        *decoded += select_str.c_str();

        results->push_back(decoded);
    }

    return true;

}

bool DecodeWepcoLine( char* line, std::vector<std::string*>* results)
{
    char* token;
    char* delim = ",";

    int func;
    long serial_num;
    unsigned short class_id;
    unsigned short div_id;

    char buf[80];
    int temp;

    string serviceTempCmd("set MessagePriority 6 ; putconfig versacom serial ");
    string serviceCmd("set MessagePriority 5 ; putconfig versacom serial ");
    string configCmd("set MessagePriority 4 ; putconfig versacom serial ");

    // function
    if( (token = strtok(line,delim)) == NULL )
        return false;

    func = atoi(token);

    //should catch the last line which is xx,xx,xx....
    if( func == 0 )
        return false;

    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    serial_num = atol(token);

    serviceTempCmd += token;
    serviceCmd += token;
    configCmd += token;

    if( func == 3 )
    {
        serviceTempCmd += " service in temp";
        serviceTempCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceTempCmd));

        serviceCmd += " service in ";
        serviceCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceCmd));
    }
    else
    if( func == 5 )
    {
        serviceCmd += " service out ";
        serviceCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceCmd));
        return true;
    }

    //Utility ID
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " aux ";
    configCmd += token;

    //Section
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " section ";
    configCmd += token;

    //Class
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " class ";
    configCmd += token;

    //Division
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " division ";
    configCmd += token;

    string route_select = GetSelectCustomRouteID(serial_num);
    if( route_select.length() > 0 ) {
        configCmd += " ";
        configCmd += route_select;
    }

    results->push_back(new std::string(configCmd));
    return true;
}


/*
    Returns a select route id command
*/
string GetSelectCustomRouteID(long serial_num) {
    string cmd;

    if( gFMConfigRouteID != -1 )
    {
        int i = 0;
        while( gFMConfigSerialLow[i] != -1 &&
               gFMConfigSerialHigh[i] != -1 )
        {
            if ( serial_num >= gFMConfigSerialLow[i] &&
                 serial_num <= gFMConfigSerialHigh[i] )
            {
                cmd += " select route id ";
                cmd += CtiNumStr(gFMConfigRouteID);
                return cmd;
            }

            i++;
        }
    }

    if( gPagingConfigRouteID != -1 )
    {
        cmd = " select route id ";
        cmd += CtiNumStr(gPagingConfigRouteID);
    }

    return cmd;
}

bool DecodeWepcoServiceLine( char* line, std::vector<std::string*>* results )
{
    char* token;
    char* delim = ",";

    int func;
    long serial_num;
    unsigned short class_id;
    unsigned short div_id;

    char buf[80];
    int temp;

    string serviceTempCmd("set MessagePriority 6 ; putconfig versacom serial ");
    string serviceCmd("set MessagePriority 5 ; putconfig versacom serial ");

    // function
    if( (token = strtok(line,delim)) == NULL )
        return false;

    func = atoi(token);

    //should catch the last line which is xx,xx,xx....
    if( func == 0 )
        return false;

    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    serial_num = atol(token);

    serviceTempCmd += token;
    serviceCmd += token;

    if( func == 3 )
    {
        serviceTempCmd += " service in temp";
        serviceTempCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceTempCmd));

        serviceCmd += " service in ";
        serviceCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceCmd));
    }
    else
    if( func == 5 )
    {
        serviceCmd += " service out ";
        serviceCmd += GetSelectCustomRouteID(serial_num);
        results->push_back(new std::string(serviceCmd));
    }
    else
    {
        return false;
    }

    return true;
}


bool DecodeWepcoConfigLine( char* line, std::vector<std::string*>* results )
{
    char* token;
    char* delim = ",";

    int func;
    long serial_num;
    unsigned short class_id;
    unsigned short div_id;

    char buf[80];
    int temp;

    string configCmd("set MessagePriority 4 ; putconfig versacom serial ");

    // function
    if( (token = strtok(line,delim)) == NULL )
        return false;

    func = atoi(token);

    //should catch the last line which is xx,xx,xx....
    if( func == 0 )
        return false;

    // nothing to do on a service out command
    if( func == 5 )
        return true;

    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    serial_num = atol(token);

    configCmd += token;

    //Utility ID
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " aux ";
    configCmd += token;

    //Section
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " section ";
    configCmd += token;

    //Class
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " class ";
    configCmd += token;

    //Division
    if( (token = strtok(NULL,delim)) == NULL )
        return false;

    configCmd += " division ";
    configCmd += token;

    string route_select = GetSelectCustomRouteID(serial_num);
    if( route_select.length() > 0 ) {
        configCmd += " ";
        configCmd += route_select;
    }

    results->push_back(new std::string(configCmd));
    return true;
}

