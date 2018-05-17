#include "precompiled.h"

/*****************************************************************************
*
*    Example input lines:

Function 1: configurations
    format:  1,serial #, group name
        1,50001,WH      sends addressing from Yukon group WH to switch 50001

Function 2:  permanent in or out of service
    format:  2,serial #, command (IN or OUT)
        2,50001,IN      sends an in service to switch 50001
        2,50001,OUT     sends an out of service to switch 50001

Function 3:
    format:  3,serial #,utility,section,class1,class2,..classN,division1,division2...divisionN
        3,50001,u2,s10,c1,c5,d1,d5      sends the address section 10, classes 1 and 5, division 1 and 5 to serial number 50001
                        (NOTE:  order of the section,class,division doesn't really matter)

Function #  Comment in the file, will get moved to the exported file if needed

*
****************************************************************************
*/
#include "decodeTextCmdFile.h"

#include "ctidate.h"
#include "dllbase.h"

#include "utility.h"
#include "logger.h"

#include <vector>

using std::iostream;
using std::string;
using std::vector;
using std::endl;

// we seem to go back and forth as to whether 0 is valid so this lets me make the change easily
#define XCOM_ADDRESS_START 0

int decodeTextCommandFile(const string& fileName,
                                int aCommandsToPerform,
                                int aProtocolFlag,
                                std::vector<std::string*>* commandList)
{
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    char command;
    string serialNum;
    string programming;
    int lineCnt=0, cmdCnt=0;
    int retVal=ClientErrors::None;

    if( commandList == NULL )
    {
        CTILOG_ERROR(dout, "Invalid use of command decodeTextCommandFile - Second parameter commandList cannot be NULL");

        retVal = TEXT_CMD_FILE_COMMAND_LIST_INVALID;
    }
    else
    {

        // open file
        if( (fptr = fopen( fileName.c_str(), "r")) == NULL )
        {
            retVal = TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE;
        }
        else
        {
            // might be overkill but I'd like to see if it works
            vector<string>     commandVector;
            vector<string>     logVector;

            // load list in the command vector
            while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
            {
                string entry (workBuffer);
                commandVector.push_back (entry);
            }

            fclose(fptr);
            if( ferror( fptr ) != 0 )
            {
                commandVector.erase(commandVector.begin(), commandVector.end());
                retVal =  TEXT_CMD_FILE_ERROR;
            }
            else
            {
                // if commands were set to 0, do entire file
                if (aCommandsToPerform == 0)
                {
                    aCommandsToPerform = INT_MAX;
                }

                // retrieve each line in order
                int     totalLines = commandVector.size();
                int     totalCommands=0;
                lineCnt = 0;

                Cti::StreamBuffer logDecodedLines;

                while ((lineCnt < totalLines ) && (totalCommands < aCommandsToPerform))
                {
                    // increment for each valid line
                    logVector.push_back (commandVector[lineCnt]);

                    /*****************
                    *  ASCII command string built from the text line
                    ******************
                    */
                    std::unique_ptr<std::string> decodedCommand(new std::string());

                    if( true == validateAndDecodeLine(commandVector[lineCnt], aProtocolFlag, decodedCommand.get(), fileName))
                    {
                        totalCommands++;

                        logDecodedLines << endl <<"-- "<< *decodedCommand;

                        commandList->push_back(decodedCommand.release());
                    }

                    lineCnt++;
                }

                CTILOG_INFO(dout, "Decoded lines: "<<
                        logDecodedLines);

                // log the commands
                if (!outputLogFile (logVector))
                {
                    retVal = TEXT_CMD_FILE_LOG_FAIL;
                }

                if (!outputCommandFile (fileName, lineCnt, commandVector))
                {
                    retVal = TEXT_CMD_FILE_UNABLE_TO_EDIT_ORIGINAL;
                }

                logVector.erase(logVector.begin(), logVector.end());
                commandVector.erase(commandVector.begin(), commandVector.end());
            }
        }
    }
    return retVal;
}

bool outputLogFile (vector<string> &aLog)
{
    HANDLE logFileHandle,importFileHandle;
    ULONG fileSize,bytesRead,bytesWritten;
    CHAR *workBuffer;
    CHAR newFileName[100];
    bool copyFailed = true;
    bool retVal = true;
    int cnt=0;

    if (aLog.size())
    {
        sprintf (newFileName,"..\\export\\sent-%02d-%02d-%04d.txt",
                 CtiDate().month(),
                 CtiDate().dayOfMonth(),
                 CtiDate().year());

        // create or open file of the day
        logFileHandle = CreateFile (newFileName,
                                     GENERIC_READ | GENERIC_WRITE,
                                     0,
                                     NULL,
                                     OPEN_ALWAYS,
                                     FILE_ATTRIBUTE_NORMAL,
                                     NULL);
        // loop around until we get exclusive access to this guy
        while (logFileHandle == INVALID_HANDLE_VALUE && cnt < 30)
        {
            if (GetLastError() == ERROR_SHARING_VIOLATION || GetLastError() == ERROR_LOCK_VIOLATION)
            {
                logFileHandle = CreateFile (newFileName,
                                             GENERIC_READ | GENERIC_WRITE,
                                             0,
                                             NULL,
                                             OPEN_ALWAYS,
                                             FILE_ATTRIBUTE_NORMAL,
                                             NULL);

                CTILOG_ERROR(dout, "Log file "<< newFileName <<" is locked");

                cnt++;
                Sleep (1000);
            }
            else
                break;
        }

        // if we tried for 30 seconds, log that we failed to log the file
        if (cnt >= 30)
        {
            retVal = false;
        }
        else
        {
            // loop the vector and append to the file
            int     totalLines = aLog.size();
            int     lineCnt=0;
            int     retCode=0;
            char    workString[500];

            while (lineCnt < totalLines)
            {
                // move to end of file and write
                retCode=SetFilePointer(logFileHandle,0,NULL,FILE_END);
                retCode=SetEndOfFile (logFileHandle);
                memset (workString, '\0',500);
                strcpy (workString,aLog[lineCnt].c_str());
                if (workString[aLog[lineCnt].length()-1] == '\n')
                {
                    workString[aLog[lineCnt].length()-1] = '\r';
                    workString[aLog[lineCnt].length()] = '\n';
                    retCode=WriteFile (logFileHandle,workString,aLog[lineCnt].length()+1,&bytesWritten,NULL);
                }
                else
                {
                    workString[aLog[lineCnt].length()] = '\r';
                    workString[aLog[lineCnt].length()+1] = '\n';
                    retCode=WriteFile (logFileHandle,workString,aLog[lineCnt].length()+2,&bytesWritten,NULL);
                }

                lineCnt++;
            }
            CloseHandle (logFileHandle);
        }
    }
    return retVal;
}
bool outputCommandFile (const string &aFileName, int aLineCnt, vector<string> &aCmdVector)
{
    HANDLE tmpFileHandle,importFileHandle;
    ULONG fileSize,bytesRead,bytesWritten;
    CHAR *workBuffer;
    CHAR newFileName[100];
    bool copyFailed = true;
    bool retVal = true;
    int cnt=0;

    if (aCmdVector.size())
    {
        CtiDate date;

        sprintf (newFileName,"..\\export\\ctitmp%02d%02d%04d.txt",
                 date.month(),
                 date.dayOfMonth(),
                 date.year());

        // create or open file of the day
        tmpFileHandle = CreateFile (newFileName,
                                     GENERIC_READ | GENERIC_WRITE,
                                     0,
                                     NULL,
                                     CREATE_ALWAYS,
                                     FILE_ATTRIBUTE_NORMAL,
                                     NULL);
        // loop around until we get exclusive access to this guy
        while (tmpFileHandle == INVALID_HANDLE_VALUE && cnt < 30)
        {
            if (GetLastError() == ERROR_SHARING_VIOLATION || GetLastError() == ERROR_LOCK_VIOLATION)
            {
                tmpFileHandle = CreateFile (newFileName,
                                             GENERIC_READ | GENERIC_WRITE,
                                             0,
                                             NULL,
                                             CREATE_ALWAYS,
                                             FILE_ATTRIBUTE_NORMAL,
                                             NULL);

                CTILOG_ERROR(dout, "Temporary file "<< newFileName <<" is locked");

                cnt++;
                Sleep (1000);
            }
            else
                break;
        }

        // if we tried for 30 seconds, log that we failed to log the file
        if (cnt >= 30)
        {
            // since we couldn't create the tmp file, we won't delete the current one
            retVal = false;
        }
        else
        {
            // loop the vector and append to the file
            int     totalLines = aCmdVector.size();
            int     lineCnt=aLineCnt;
            int     retCode=0;
            char    workString[500];

            while (lineCnt < totalLines)
            {
                // move to end of file and write
                retCode=SetFilePointer(tmpFileHandle,0,NULL,FILE_END);
                retCode=SetEndOfFile (tmpFileHandle);
                memset (workString, '\0',500);
                strcpy (workString,aCmdVector[lineCnt].c_str());

                if (workString[aCmdVector[lineCnt].length()-1] == '\n')
                {
                    workString[aCmdVector[lineCnt].length()-1] = '\r';
                    workString[aCmdVector[lineCnt].length()] = '\n';
                    retCode=WriteFile (tmpFileHandle,workString,aCmdVector[lineCnt].length()+1,&bytesWritten,NULL);
                }
                else
                {
                    workString[aCmdVector[lineCnt].length()] = '\r';
                    workString[aCmdVector[lineCnt].length()+1] = '\n';
                    retCode=WriteFile (tmpFileHandle,workString,aCmdVector[lineCnt].length()+2,&bytesWritten,NULL);
                }
                lineCnt++;
            }
            CloseHandle (tmpFileHandle);

            // loop on the copy
            CopyFile(newFileName, aFileName.c_str(), false);
            cnt =0;
            while ( (GetLastError() == ERROR_SHARING_VIOLATION || GetLastError() == ERROR_LOCK_VIOLATION) && cnt < 30)
            {
                CopyFile (newFileName, aFileName.c_str(), false);

                CTILOG_ERROR(dout, "Original file "<< aFileName <<" is locked");

                cnt++;
                Sleep (1000);
            }

            if (cnt >= 30)
            {
                // since we couldn't create the tmp file, we won't delete the current one
                retVal = false;
            }

            // now copy the tmp to the old and delete the tmp
            DeleteFile (newFileName);
        }
    }
    else
    {
        DeleteFile (aFileName.c_str());
    }
    return retVal;
}


bool validateAndDecodeLine( string &input, int aProtocolFlag, string* programming, string aFileName)
{
    string serialNum;
    string tempString1;
    bool retCode = true;

    std::transform(input.begin(), input.end(), input.begin(), tolower);

    boost::char_separator<char> sep(",\r\n");
    Boost_char_tokenizer cmdLine(input, sep);

    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    if( tok_iter != cmdLine.end() )
    {
        tempString1 = *tok_iter;                // Will receive each token
    }

    if (!tempString1.empty())
    {
        // check for a comment
        if (tempString1[0] != '#')
        {
            // just a comment, don't do anything
            switch (atoi (tempString1.c_str()))
            {
                case 1:
                    {
                        /****************************
                        * line is configuration command specifying group name
                        * format: 1,serial #,group name
                        *
                        * this function works for both expresscom and versacom
                        * porter handles which is which based on the group
                        *****************************
                        */
                        if (++tok_iter != cmdLine.end())
                        {
                            serialNum = *tok_iter;

                            if (++tok_iter != cmdLine.end())
                            {
                                /*************************
                                * output must look like this
                                *
                                * putconfig serial 12345 template "group name"
                                * You MUST quote the group name.
                                **************************
                                */

                                // do a select on the group before adding the serial number
                                *programming = "set MessagePriority 5 ; PutConfig serial ";
                                *programming += serialNum.c_str();
                                *programming += " template '";
                                *programming += tok_iter->c_str();
                                *programming += "'";
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }
                case 2:
                    {
                        /****************************
                        * line is a service cmd to a serial number
                        * format:  2,serial #,in|out[,hours:#][,load:]
                        *****************************
                        */
                        if (++tok_iter != cmdLine.end())
                        {
                            serialNum = *tok_iter;

                            if (++tok_iter != cmdLine.end())
                            {
                                tempString1 = *tok_iter;
                                if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL)
                                {
                                    *programming = "set MessagePriority 7 ; PutConfig serial ";
                                }
                                else if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM)
                                {
                                    *programming = "set MessagePriority 7 ; PutConfig xcom serial ";
                                }
                                else if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_VERSACOM)
                                {
                                    *programming =  "set MessagePriority 7 ; PutConfig versacom serial ";
                                }
                                else
                                {
                                    *programming =  ("set MessagePriority 7 ; PutConfig serial ");
                                }

                                if (tempString1.find("in")!=string::npos)
                                {
                                    *programming += serialNum.c_str();
                                    *programming += " service in";
                                }
                                else if (tempString1.find("out")!=string::npos)
                                {
                                    *programming += serialNum.c_str();
                                    *programming += " service out";
                                }
                                else
                                {
                                    retCode = false;
                                }

                                // everything from now on is optional
                                if (++tok_iter != cmdLine.end())
                                {
                                    tempString1 = *tok_iter;
                                    // check for a timeout
                                    if (tempString1.find("offhours:")!=string::npos)
                                    {
                                        int colon = tempString1.find_first_of(':');

                                        if( colon !=string::npos )
                                        {
                                            tempString1.replace(colon,1," ");
                                            *programming += " temp ";
                                            *programming += tempString1.c_str();
                                        }
                                    }
                                    else if (tempString1.find("temp")!=string::npos)
                                    {
                                        *programming += " temp ";

                                        // see if offhours was after this
                                        if (++tok_iter != cmdLine.end())
                                        {
                                            tempString1 = *tok_iter;
                                            if (tempString1.find("offhours:")!=string::npos)
                                            {
                                                int colon = tempString1.find_first_of(':');

                                                if( colon !=string::npos )
                                                {
                                                    tempString1.replace(colon,1," ");
                                                    *programming += tempString1.c_str();
                                                }
                                            }
                                            else
                                            {
                                                CTILOG_ERROR(dout, "Invalid parameter -"<< tempString1 <<"- in line ("<< input <<")");
                                                retCode = false;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        CTILOG_ERROR(dout, "Invalid parameter -"<< tempString1 <<"- in line ("<< input <<")");
                                        retCode = false;
                                    }
                                }
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }
                case 3:
                    {
                        /****************************
                        * line is a configuration command specifing section,class,division
                        * format:  3,serial #,s1,.. sn, c1,c2..cn, d1,d2,... dn
                        *
                        * function is only valid for versacom so it works with only
                        * versacom or no protocol specified flags
                        *****************************
                        */
                        if ((aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL) ||
                            (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_VERSACOM))
                        {
                            string utilityAddr;
                            string sectionAddr;
                            string classAddr("class ");
                            string divisionAddr("division ");
                            bool haveUtility=false, haveSection=false,firstClass=true,firstDivision=true;

                            if (++tok_iter != cmdLine.end())
                            {
                                serialNum = *tok_iter;

                                if (++tok_iter != cmdLine.end())
                                {
                                    tempString1 = *tok_iter;
                                    bool continueFlag = true;
                                    while (continueFlag)
                                    {
                                        if (tempString1.find("u")!=string::npos)
                                        {
                                            haveUtility = true;
                                            tempString1 = trim_left(tempString1,"u");
                                            utilityAddr = string ("utility ");
                                            utilityAddr += tempString1;
                                        }
                                        else if (tempString1.find("s")!=string::npos)
                                        {
                                            haveSection = true;
                                            tempString1 = trim_left(tempString1,"s");
                                            sectionAddr = string ("section ");
                                            sectionAddr += tempString1;
                                        }
                                        else if (tempString1.find ("c")!=string::npos)
                                        {
                                            // we have a class address
                                            if (firstClass)
                                            {
                                                // first time thru, we won't need a comma
                                                firstClass = false;
                                            }
                                            else
                                            {
                                                classAddr += string (",");
                                            }
                                            tempString1 = trim_left(tempString1,"c");
                                            classAddr += tempString1;

                                        }
                                        else if (tempString1.find ("d")!=string::npos)
                                        {
                                            // we have a division address
                                            if (firstDivision)
                                            {
                                                // first time thru, we won't need a comma
                                                firstDivision = false;
                                            }
                                            else
                                            {
                                                divisionAddr += string (",");
                                            }
                                            tempString1 = trim_left(tempString1,"d");
                                            divisionAddr += tempString1;
                                        }

                                        if (++tok_iter != cmdLine.end())
                                        {
                                            tempString1 = *tok_iter;
                                        }
                                        else
                                        {
                                            continueFlag = false;
                                        }
                                    }

                                    // make sure we found something
                                    if ((!firstDivision) || (!firstClass) || (haveSection) || (haveUtility))
                                    {
                                        *programming = "set MessagePriority 5 ; PutConfig versacom serial ";
                                        *programming +=serialNum.c_str();

                                        if (haveUtility)
                                        {
                                            *programming += " ";
                                            *programming += utilityAddr.c_str();
                                        }

                                        if (haveSection)
                                        {
                                            *programming += " ";
                                            *programming += sectionAddr.c_str();
                                        }

                                        if (!firstClass)
                                        {
                                            *programming += " ";
                                            *programming += classAddr.c_str();
                                        }

                                        if (!firstDivision)
                                        {
                                            *programming += " ";
                                            *programming += divisionAddr.c_str();
                                        }
                                    }
                                    else
                                    {
                                        retCode = false;
                                    }
                                }
                                else
                                {
                                    retCode = false;
                                }
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }
                case 4:
                    {
                        /****************************
                        * line is a configuration command specifing section,class,division
                        * format:  4,serial #,Spid #, Geo #, Sub #, Feeder #, Zip #, User #, Prog #, Splinter #, Load #
                        *
                        * function is only valid for expresscom so it works with only
                        * expresscom or no protocol specified flags
                        *
                        * the function, the serial number and at least one address is needed
                        * everything else is optional.  NOTE:  If you use program and splinter
                        * then a load number must be included
                        *****************************
                        */
                        string currentCmd;
                        USHORT feeder=0, splinterCnt=0, programCnt=0, loadCnt=0;

                        if ((aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL) ||
                            (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM))
                        {
                            currentCmd = string("set MessagePriority 5 ; PutConfig xcom assign serial ");
                            bool haveSomething=false;
                            bool haveFeeder=false;
                            bool haveLoad=false;
                            bool haveProgram=false;
                            bool haveSplinter=false;
                            bool invalidSPID=false, invalidGEO=false, invalidSub=false,invalidFeeder=false,invalidZip=false;
                            bool invalidUser=false, invalidProgram=false, invalidSplinter=false;
                            string load,program,splinter;
                            CHAR buffer[20];
                            int lastLoad=0;
                            ULONG tmpAddress;

                            memset (&buffer, '\0', 20);

                            if (++tok_iter != cmdLine.end())
                            {
                                currentCmd += *tok_iter;
                                serialNum = *tok_iter;

                                if (++tok_iter != cmdLine.end())
                                {
                                    tempString1 = *tok_iter;
                                    bool continueFlag = true;
                                    while (continueFlag)
                                    {
                                        tempString1 = trim(tempString1);

                                        if (tempString1.find("spid")!=string::npos)
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.erase (0,5);
                                            tmpAddress = atoi(tempString1.c_str());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidSPID = true;
                                            }
                                            else
                                            {
                                                currentCmd += " s " + tempString1;
                                            }
                                        }
                                        else if (tempString1.find("geo")!=string::npos)
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.erase (0,4);
                                            tmpAddress = atoi(tempString1.c_str());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidGEO = true;
                                            }
                                            else
                                            {
                                                currentCmd += " g " + tempString1;
                                            }

                                        }
                                        else if (tempString1.find("sub")!=string::npos)
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.erase (0,4);
                                            tmpAddress = atoi(tempString1.c_str());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidSub = true;
                                            }
                                            else
                                            {
                                                currentCmd += " b " + tempString1;
                                            }

                                        }
                                        else if (tempString1.find("feeder")!=string::npos)
                                        {
                                            haveSomething = true;
                                            haveFeeder = true;
                                            tempString1 = tempString1.erase (0,6);
                                            if (atoi (tempString1.c_str()) != 0)
                                            {
                                                tmpAddress = atoi (tempString1.c_str());
                                                if ((tmpAddress < 1) || (tmpAddress > 16))
                                                {
                                                    invalidFeeder = true;
                                                }
                                                else
                                                {
                                                    feeder |= (0x0001 << ((atoi (tempString1.c_str())-1)));
                                                }
                                            }

                                            //feeder |= setExpresscomFeederBit (atoi (tempString1.c_str()));
                                        }
                                        else if (tempString1.find("zip")!=string::npos)
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.erase (0,4);
                                            tmpAddress = atoi(tempString1.c_str());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 16777214)
                                            {
                                                invalidZip = true;
                                            }
                                            else
                                            {
                                                currentCmd += " z " + tempString1;
                                            }
                                        }
                                        else if (tempString1.find("user")!=string::npos)
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.erase (0,5);
                                            tmpAddress = atoi(tempString1.c_str());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidUser = true;
                                            }
                                            else
                                            {
                                                currentCmd += " u " + tempString1;
                                            }
                                        }
                                        else if (tempString1.find("load")!=string::npos)
                                        {
                                            haveSomething = true;
                                            string workString;

                                            boost::char_separator<char> space_sep(" ");
                                            Boost_char_tokenizer subCmd(tempString1, space_sep);
                                            Boost_char_tokenizer::iterator sub_tok_iter = subCmd.begin();

                                            while(sub_tok_iter != subCmd.end())
                                            {
                                                workString = *sub_tok_iter++;
                                                if (workString.find("load")!=string::npos)
                                                {
                                                    loadCnt++;
                                                    if (!haveLoad)
                                                    {
                                                        haveLoad = true;
                                                        load = " load ";
                                                    }
                                                    else
                                                    {
                                                        load += ",";
                                                    }



                                                    if (sub_tok_iter != subCmd.end())
                                                    {
                                                        workString = *sub_tok_iter++;
                                                        if (lastLoad < atoi(workString.c_str()))
                                                        {
                                                            load += workString;
                                                            lastLoad = atoi(workString.c_str());

                                                        }
                                                        else
                                                        {
                                                            CTILOG_ERROR(dout, "Invalid configuration line in "<< aFileName <<" for serial number "<< serialNum <<
                                                                    endl <<"--- Loads must be addressed in numerical order. "<<
                                                                    endl <<"---"<< input);

                                                            loadCnt--;
                                                        }
                                                    }
                                                }
                                                else if (workString.find("p")!=string::npos)
                                                {
                                                    programCnt++;
                                                    if (!haveProgram)
                                                    {
                                                        haveProgram = true;
                                                        program = " p ";
                                                    }
                                                    else
                                                    {
                                                        program += ",";
                                                    }

                                                    if (workString.length() < 2)
                                                    {
                                                        if (sub_tok_iter != subCmd.end())
                                                        {
                                                            workString = *sub_tok_iter++;
                                                            tmpAddress = atoi(workString.c_str());
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidProgram=true;
                                                            }
                                                            else
                                                            {
                                                                program += workString;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if (workString.length() > 1)
                                                        {
                                                            memcpy (&buffer, &workString[1],workString.length()-1);
                                                            tmpAddress = atoi(buffer);
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidProgram=true;
                                                            }
                                                            else
                                                            {
                                                                program += string(buffer);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            // we've got a problem here of some sort
                                                            haveProgram = false;
                                                            programCnt--;
                                                        }
                                                    }
                                                }
                                                else if (workString.find("s")!=string::npos)
                                                {
                                                    splinterCnt++;
                                                    if (!haveSplinter)
                                                    {
                                                        haveSplinter = true;
                                                        splinter = " r ";
                                                    }
                                                    else
                                                    {
                                                        splinter += ",";
                                                    }

                                                    if (workString.length() < 2)
                                                    {
                                                        if (sub_tok_iter != subCmd.end())
                                                        {
                                                            workString = *sub_tok_iter++;
                                                            tmpAddress = atoi(workString.c_str());
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidSplinter=true;
                                                            }
                                                            else
                                                            {
                                                                splinter+=workString;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if (workString.length() > 1)
                                                        {
                                                            memcpy (&buffer, &workString[1],workString.length()-1);

                                                            tmpAddress = atoi(buffer);
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidSplinter=true;
                                                            }
                                                            else
                                                            {
                                                                splinter += string(buffer);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            // we've got a problem here of some sort
                                                            haveSplinter = false;
                                                            splinterCnt--;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (++tok_iter != cmdLine.end())
                                        {
                                            tempString1 = *tok_iter;
                                        }
                                        else
                                        {
                                            continueFlag = false;
                                        }
                                    }

                                    if (!(invalidSPID || invalidGEO || invalidSub || invalidFeeder ||invalidZip ||
                                        invalidUser || invalidProgram || invalidSplinter))
                                    {
                                        if (haveFeeder)
                                        {
                                            currentCmd += " f ";
                                            currentCmd += string (ltoa(feeder,buffer,10));
                                        }
                                        if (haveLoad)
                                        {
                                            if (haveProgram)
                                            {
                                                currentCmd += program;
                                            }
                                            if (haveSplinter)
                                            {
                                                currentCmd += splinter;
                                            }
                                            currentCmd += load;
                                        }

                                        // make sure we found something
                                        if (haveSomething)
                                        {
                                            bool programMatches=false;
                                            bool splinterMatches=false;
                                            bool sendCmd=false;

                                            if (haveLoad && haveProgram)
                                            {
                                                if (loadCnt == programCnt)
                                                {
                                                    programMatches = true;
                                                }
                                            }

                                            if (haveLoad && haveSplinter)
                                            {
                                                if (loadCnt == splinterCnt)
                                                {
                                                    splinterMatches = true;
                                                }
                                            }


                                            if (haveProgram && haveSplinter)
                                            {
                                                if (programMatches && splinterMatches)
                                                {
                                                    sendCmd=true;
                                                }
                                            }
                                            else if (haveProgram)
                                            {
                                                if (programMatches)
                                                {
                                                    sendCmd=true;
                                                }
                                            }
                                            else if (haveSplinter)
                                            {
                                                if (splinterMatches)
                                                {
                                                    sendCmd=true;
                                                }
                                            }
                                            else if ((programCnt==0) &&(splinterCnt==0) && (loadCnt==0))
                                            {
                                                // we have some addressing to do here, it just doesn't have load specific addressing
                                                sendCmd=true;
                                            }

                                            if  (sendCmd)
                                            {
                                                *programming = currentCmd.c_str();
                                            }
                                            else
                                            {
                                                CTILOG_ERROR(dout, "Invalid configuration line in "<< aFileName <<" for serial number "<< serialNum <<
                                                        endl <<"--- Number of addressed loads/splinters/programs must be equal"<<
                                                        endl <<"--- "<< input);

                                                retCode = false;
                                            }
                                        }
                                        else
                                        {
                                            retCode = false;
                                        }
                                    }
                                    else
                                    {
                                        string address;

                                        if (invalidSPID)
                                            address += string (" Spid ");
                                        if (invalidGEO)
                                            address += string (" Geo ");
                                        if (invalidSub)
                                            address += string (" Substation ");
                                        if (invalidFeeder)
                                            address += string (" Feeder ");
                                        if (invalidZip)
                                            address += string (" Zip ");
                                        if (invalidUser)
                                            address += string (" User ");
                                        if (invalidProgram)
                                            address += string (" Program ");
                                        if (invalidSplinter)
                                            address += string (" Splinter ");

                                        CTILOG_ERROR(dout, "Invalid configuration line in "<< aFileName <<" for serial number "<< serialNum <<
                                                endl <<"--- Address is out of range : "<< address <<
                                                endl <<"--- "<< input);

                                        retCode = false;
                                    }
                                }
                                else
                                {
                                    retCode = false;
                                }
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }
                    case 5:
                    {
                        /****************************
                        * line is a configuration command specifing target amps
                        * format:  5,serial #,Amps (decimal A)
                        *
                        * function is only valid for expresscom so it works with only
                        * expresscom or no protocol specified flags
                        *****************************
                        */
                        if ((aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL) ||
                            (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM))
                        {
                            string amps;
                            if (++tok_iter != cmdLine.end())
                            {
                                serialNum = *tok_iter;

                                if (++tok_iter != cmdLine.end())
                                {
                                    amps = *tok_iter;

                                    *programming = "set MessagePriority 5 ; PutConfig xcom serial ";
                                    *programming += serialNum.c_str();
                                    *programming += " targetloadamps ";
                                    *programming += amps.c_str();
                                }
                                else
                                {
                                    retCode = false;
                                }
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }
                    case 6:
                    {
                        /****************************
                        * Format: 6,TARGET,spid #,geo #,sub #,feeder #,zip #,uda #,user #,program #,splinter #,serial #,ASSIGN, spid #,geo #,sub #,feeder #,zip #,uda #,user #,program #,splinter #, relay #
                        *
                        * function is only valid for expresscom so it works with only
                        * expresscom or no protocol specified flags
                        *
                        * This function is very similar to function 4.
                        * However function 6 can assign addressing to any other addressing level, not just serial.
                        * This function only allows for a single relay/program/splinter combination.
                        * Addressing may be in any order, but TARGET must come before ASSIGN.
                        * Relay may only be sent if either program or splinter are also sent.
                        * Example: 6,TARGET,spid 2,geo 3,sub 4,ASSIGN,spid 3,geo 2,sub 5,program 3,relay 3
                        *****************************
                        */
                        string currentCmd;

                        if ((aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL) ||
                            (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM))
                        {
                            currentCmd = string("set MessagePriority 5 ; PutConfig xcom target");
                            bool fail = false, foundTarget = false, foundAssign = false;
                            USHORT feeder = 0; //Feeder is special
                            CHAR buffer[20];
                            int lastLoad=0;
                            ULONG tmpAddress;

                            memset (&buffer, '\0', 20);

                            if (++tok_iter != cmdLine.end())
                            {
                                tempString1 = *tok_iter;
                                bool continueFlag = true;
                                while (continueFlag)
                                {
                                    tempString1 = trim(tempString1);

                                    if (tempString1.find("target")!=string::npos)
                                    {
                                        foundTarget = true;
                                    }
                                    else if (tempString1.find("assign")!=string::npos)
                                    {
                                        if (feeder != 0)
                                        {
                                            currentCmd += " feeder " + CtiNumStr(feeder);
                                        }
                                        feeder = 0;
                                        foundAssign = true;
                                        currentCmd += " assign";
                                    }
                                    else if (tempString1.find("spid")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,5);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 65534)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " spid " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("geo")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,4);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 65534)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " geo " + tempString1;
                                        }

                                    }
                                    else if (tempString1.find("sub")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,4);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 65534)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " sub " + tempString1;
                                        }

                                    }
                                    else if (tempString1.find("feeder")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,7);
                                        tmpAddress = atoi (tempString1.c_str());
                                        if ((tmpAddress < 1) || (tmpAddress > 16))
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            feeder |= (0x0001 << ((atoi (tempString1.c_str())-1)));
                                        }

                                    }
                                    else if (tempString1.find("zip")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,4);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 16777214)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " zip " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("user")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,5);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 65534)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " user " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("uda")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,4);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress <= XCOM_ADDRESS_START || tmpAddress > 65534)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " uda " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("program")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,8);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress < 1 || tmpAddress > 254)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " program " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("splinter")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,9);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress < 1 || tmpAddress > 254)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " splinter " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("relay")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,6);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (tmpAddress < 0 || tmpAddress > 15 || !foundAssign)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " relay " + tempString1;
                                        }
                                    }
                                    else if (tempString1.find("serial")!=string::npos)
                                    {
                                        tempString1 = tempString1.erase (0,7);
                                        tmpAddress = atoi(tempString1.c_str());
                                        if (foundAssign)
                                        {
                                            fail = true;
                                            continueFlag = false;
                                        }
                                        else
                                        {
                                            currentCmd += " serial " + tempString1;
                                        }
                                    }
                                    else
                                    {
                                        fail = true;

                                        CTILOG_ERROR(dout, "Invalid configuration line in "<< aFileName <<" for serial number "<< serialNum <<
                                                endl <<"--- Unable to decode parameter: "<< tempString1);
                                    }

                                    if (++tok_iter != cmdLine.end())
                                    {
                                        tempString1 = *tok_iter;
                                    }
                                    else
                                    {
                                        continueFlag = false;

                                        if (feeder != 0)
                                        {
                                            currentCmd += " feeder " + CtiNumStr(feeder);
                                        }
                                        feeder = 0;
                                    }
                                }

                                if (!fail && foundTarget && foundAssign)
                                {
                                    *programming = currentCmd.c_str();
                                }
                                else
                                {
                                    CTILOG_ERROR(dout, "Invalid configuration line in "<< aFileName <<
                                            endl <<"--- "<< input);

                                    retCode = false;
                                }
                            }
                            else
                            {
                                retCode = false;
                            }
                        }
                        else
                        {
                            retCode = false;
                        }
                        break;
                    }

                default:
                    {
                        retCode = false;
                    }
            }
        }
        else
        {
            retCode = false;
        }
    }
    else
    {
        retCode = false;
    }

    return retCode;
}

bool decodeDsm2Lines( string &aFunction,
                      string &aRoute,
                      string &aSerialNum,
                      string &aCmd,
                      string* programming)
{
    bool retCode = true;
    string route,function,serialNum,cmd;

    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer tFunction(aFunction, sep);
    Boost_char_tokenizer tRoute(aRoute, sep);
    Boost_char_tokenizer tSN(aSerialNum, sep);
    Boost_char_tokenizer tCmd(aCmd, sep);

    if( tFunction.begin() != tFunction.end() )
    {
        function = trim(string(*tFunction.begin()));
    }

    if( tRoute.begin() != tRoute.end() )
    {
        route = trim(string(*tRoute.begin()));
    }

    if( tSN.begin() != tSN.end() )
    {
        serialNum = trim(string(*tSN.begin()));
    }

    if( tCmd.begin() != tCmd.end() )
    {
        cmd = trim(string(*tCmd.begin()));
    }

    switch (atoi (function.c_str()))
    {
        case 4:
            {
                /****************************
                * line is configuration command specifying a config name from dsm2
                * we are assuming there will now be a corresponding group name on the
                * yukon side (we are adding config: to the front of each dsm2 group name
                * to avoid confusion
                * 4
                *
                * serial num
                * config name
                *****************************
                */
                /*************************
                * output must look like this
                *
                * putconfig serial 12345 template 'group name'
                * You MUST quote the group name.
                **************************
                */

                // do a select on the group before adding the serial number
                *programming = "set MessagePriority 5 ; PutConfig serial ";
                *programming += serialNum.c_str();
                *programming +=  (" template '");
                *programming +=  ("config: ");
                *programming += cmd.c_str();
                *programming +=  ("'");

                if ((!route.empty()) && (route[0] != ' '))
                {
                    *programming +=  (" select routename '");
                    *programming += route.c_str();
                    *programming +=  ("'");
                }
                break;
            }
        case 2:
            {
                /****************************
                * line is a service cmd to a serial number
                * 2
                * <route name>
                * serial number
                * 0 = out, 1 = in
                *****************************
                */
                *programming =  ("set MessagePriority 7 ; PutConfig versacom serial ");
                *programming += serialNum.c_str();
                if (atoi (cmd.c_str()) ==  0)
                {
                    *programming += " service out";
                }
                else
                {
                    *programming += " service in";
                }

                if ((!route.empty()) && (route[0] != ' '))
                {
                    *programming +=  (" select route name '");
                    *programming += route.c_str();
                    *programming +=  ("'");
                }

                break;
            }
        case 3:
            {
                /****************************
                * line is a utility id command and sent on every config so no decoding
                *****************************
                */
                retCode = false;
                break;
            }
        default:
            {
                retCode = false;
            }
    }
    return retCode;
}


/**********************
* used to retrieve comma separated data
* strtok wouldn't work because of multiple
* tokens in a row ,,,,
***********************
*/
bool getToken (char **InBuffer,
                 string &outBuffer)
{
    bool retVal = true;
    char *ptr;

        // find comma if one exists
    if (InBuffer == NULL)
    {
        retVal = false;
    }
    else if ((ptr = strchr (*InBuffer, ',')) != NULL)
    {

            // found one
        *ptr = '\0';
        outBuffer = *InBuffer;
        *InBuffer += outBuffer.length() + 1;

    }
    else if ((ptr = strchr (*InBuffer, '\0')) != NULL)
    {
            *ptr = '\0';
            outBuffer = *InBuffer;
            *InBuffer = '\0';
    }
    else
    {
         retVal = false;
    }
        // return current buffer
    return retVal;
}


int decodeDSM2VconfigFile(const string& fileName, std::vector<std::string *>* commandList)
{
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    char command;
    string serialNum;
    string programming;
    int lineCnt=0, cmdCnt=0;
    int retVal=ClientErrors::None;

    if( commandList == NULL )
    {
        CTILOG_ERROR(dout, "Invalid use of command decodeDSM2VconfigFile. Second parameter commandList cannot be NULL");

        retVal = TEXT_CMD_FILE_COMMAND_LIST_INVALID;
    }
    else
    {

        // open file
        if( (fptr = fopen( fileName.c_str(), "r")) == NULL )
        {
            retVal = TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE;
        }
        else
        {
            vector<string>     commandVector;

            // load list in the command vector
            while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
            {
                string entry (workBuffer);
                commandVector.push_back (entry);
            }

            fclose(fptr);
            if( ferror( fptr ) != 0 )
            {
                commandVector.erase(commandVector.begin(), commandVector.end());
                retVal =  TEXT_CMD_FILE_ERROR;
            }
            else
            {
                // retrieve each line in order
                int     totalLines = commandVector.size();
                int     totalCommands=0;
                lineCnt = 0;

                Cti::StreamBuffer logDecodedDsm2Lines;

                while ((lineCnt+4) <= totalLines )
                {
                    /*****************
                    *  ASCII command string built from the text line
                    ******************
                    */

                    std::unique_ptr<std::string> decodedCommand(new std::string());

                    if( true == decodeDsm2Lines( commandVector[lineCnt],
                                                 commandVector[lineCnt+1],
                                                 commandVector[lineCnt+2],
                                                 commandVector[lineCnt+3],
                                                 decodedCommand.get() ))
                    {
                        logDecodedDsm2Lines << endl <<"-- "<< *decodedCommand;

                        commandList->push_back(decodedCommand.release());
                    }

                    lineCnt+=4;
                }

                CTILOG_INFO(dout, "Decoded DSM2 lines: "<<
                        logDecodedDsm2Lines);

                commandVector.erase(commandVector.begin(), commandVector.end());
            }
        }
    }
    if (retVal == ClientErrors::None)
    {
        DeleteFile (fileName.c_str());
    }

    return retVal;
}

