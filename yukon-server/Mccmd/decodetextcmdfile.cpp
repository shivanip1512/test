#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: decodeTextCmdFile.cpp
*
*    DATE: 05/06/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Operations performed through a batch file
*
*    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
*
*
* 	 Example input lines:

Function 1: configurations
	format:  1,serial #, group name
		1,50001,WH		sends addressing from Yukon group WH to switch 50001

Function 2:  permanent in or out of service
	format:  2,serial #, command (IN or OUT)
		2,50001,IN		sends an in service to switch 50001
		2,50001,OUT		sends an out of service to switch 50001

Function 3:  
	format:  3,serial #,utility,section,class1,class2,..classN,division1,division2...divisionN
		3,50001,u2,s10,c1,c5,d1,d5		sends the address section 10, classes 1 and 5, division 1 and 5 to serial number 50001
						(NOTE:  order of the section,class,division doesn't really matter)
                        
Function #  Comment in the file, will get moved to the exported file if needed                        

*
****************************************************************************
*/
#include <rw/ctoken.h>
#include <rw/rwdate.h>

#include <vector>
#include "decodeTextCmdFile.h"

/***************************
*
*  this function reads in the file and 
*  decides which function type we are talking about
*
****************************
*/

// we seem to go back and forth as to whether 0 is valid so this lets me make the change easily
#define XCOM_ADDRESS_START 0

bool validateAndDecodeLine( RWCString & line, int aProtocolFlag, RWCollectableString* programming, RWCString aFileName);

int decodeTextCommandFile(const RWCString& fileName, 
                                int aCommandsToPerform,
                                int aProtocolFlag, 
                                RWOrdered* commandList)
{
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
	char command;
	RWCString serialNum;
	RWCString programming;
    int lineCnt=0, cmdCnt=0;
    int retVal=NORMAL;

    if( commandList == NULL )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Invalid use of command decodeTextCommandFile"<< endl;
        dout << "             second parameter commandList cannot be NULL " << endl;
        retVal = TEXT_CMD_FILE_COMMAND_LIST_INVALID;
    }
    else
    {
                                   
        // open file               
        if( (fptr = fopen( fileName.data(), "r")) == NULL )
        {
            retVal = TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE;
        }
        else
        {
            // might be overkill but I'd like to see if it works
            vector<RWCString>     commandVector;
            vector<RWCString>     logVector;

            // load list in the command vector
            while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
            {
                RWCString entry (workBuffer);
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

                while ((lineCnt < totalLines ) && (totalCommands < aCommandsToPerform))
                {
                    // increment for each valid line
                    logVector.push_back (commandVector[lineCnt]);

                    /*****************
                    *  ASCII command string built from the text line
                    ******************
                    */
                    RWCollectableString* decodedCommand = new RWCollectableString();

                    if( true == validateAndDecodeLine( commandVector[lineCnt], aProtocolFlag, decodedCommand, fileName ))
                    {
                        totalCommands++;
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << "-- " << *decodedCommand << endl;
                        }
                        commandList->insert(decodedCommand);
                        // set to null for safety sakes
                        decodedCommand = NULL;

                    }
                    else
                    {
                        delete decodedCommand;
                    }

                    lineCnt++;
                }

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

bool outputLogFile (vector<RWCString> &aLog)
{
    HANDLE logFileHandle,importFileHandle;
    ULONG fileSize,bytesRead,bytesWritten;
    CHAR *workBuffer;
    CHAR newFileName[50];
    bool copyFailed = true;
    bool retVal = true;
    int cnt=0;

    if (aLog.size())
    {
        sprintf (newFileName,"..\\export\\sent-%02d-%02d.txt",
                 RWDate().month(),
                 RWDate().dayOfMonth());

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
                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << RWTime() << " - Log file " << RWCString (newFileName) << " is locked "<< endl;
                }
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
                strcpy (workString,aLog[lineCnt].data());
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
bool outputCommandFile (const RWCString &aFileName, int aLineCnt, vector<RWCString> &aCmdVector)
{
    HANDLE tmpFileHandle,importFileHandle;
    ULONG fileSize,bytesRead,bytesWritten;
    CHAR *workBuffer;
    CHAR newFileName[50];
    bool copyFailed = true;
    bool retVal = true;
    int cnt=0;

    if (aCmdVector.size())
    {
        sprintf (newFileName,"..\\export\\ctitmp%02d%02d.txt",
                 RWDate().month(),
                 RWDate().dayOfMonth());

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
                {
                    CtiLockGuard< CtiLogger > guard(dout);
                    dout << RWTime() << " - tmp file " << RWCString (newFileName) << " is locked "<< endl;
                }
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
            char    workString[50];

            while (lineCnt < totalLines)
            {
                // move to end of file and write
                retCode=SetFilePointer(tmpFileHandle,0,NULL,FILE_END);
                retCode=SetEndOfFile (tmpFileHandle);
                strcpy (workString,aCmdVector[lineCnt].data());
                workString[aCmdVector[lineCnt].length()-1] = '\r';
                workString[aCmdVector[lineCnt].length()] = '\n';
                retCode=WriteFile (tmpFileHandle,workString,aCmdVector[lineCnt].length()+1,&bytesWritten,NULL);
                lineCnt++;
            }
            CloseHandle (tmpFileHandle);

            // loop on the copy
            CopyFile (newFileName, aFileName, false);
            cnt =0;
            while ( (GetLastError() == ERROR_SHARING_VIOLATION || GetLastError() == ERROR_LOCK_VIOLATION) && cnt < 30)
            {
                CopyFile (newFileName, aFileName, false);
                    {
                        CtiLockGuard< CtiLogger > guard(dout);
                        dout << RWTime() << " - original file " << RWCString (aFileName) << " is locked "<< endl;
                    }
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
        DeleteFile (aFileName);
    }
    return retVal;
}



bool validateAndDecodeLine( RWCString &input, int aProtocolFlag, RWCollectableString* programming, RWCString aFileName)
{
	RWCString serialNum;
	bool retCode = true;
    
    input.toLower();

    RWCTokenizer cmdLine(input);           // Tokenize the string a
    RWCString tempString1;                // Will receive each token

    if (!(tempString1 = cmdLine(",\r\n")).isNull())
    {
        // check for a comment
        if (tempString1.data()[0] != '#')
        {
            // just a comment, don't do anything
            switch (atoi (tempString1))
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
                        if (!(tempString1 = cmdLine(",\r\n")).isNull())
                        {
                            serialNum = tempString1;

                            if (!(tempString1 = cmdLine(",\r\n")).isNull())
                            {
                                /*************************
                                * output must look like this
                                *
                                * putconfig serial 12345 template "group name"
                                * You MUST quote the group name.
                                **************************
                                */

                                // do a select on the group before adding the serial number
                                *programming = RWCString ("set MessagePriority 5 ; PutConfig serial ");
                                *programming += serialNum;
                                *programming += RWCString (" template '");
                                *programming += RWCString (tempString1);
                                *programming += RWCString ("'");
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
                        if (!(tempString1 = cmdLine(",\r\n")).isNull())
                        {
                            serialNum = tempString1;

                            if (!(tempString1 = cmdLine(",\r\n")).isNull())
                            {
                                if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL)
                                {
                                    *programming = RWCString ("set MessagePriority 7 ; PutConfig serial ");
                                }
                                else if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM)
                                {
                                    *programming = RWCString ("set MessagePriority 7 ; PutConfig xcom serial ");
                                }
                                else if (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_VERSACOM)
                                {
                                    *programming = RWCString ("set MessagePriority 7 ; PutConfig versacom serial ");
                                }
                                else
                                {
                                    *programming = RWCString ("set MessagePriority 7 ; PutConfig serial ");
                                }

                                if (tempString1.contains("in"))
                                {
                                    *programming += serialNum;
                                    *programming += " service in";
                                }
                                else if (tempString1.contains("out"))
                                {
                                    *programming += serialNum;
                                    *programming += " service out";
                                }
                                else
                                {
                                    retCode = false;
                                }

                                // everything from now on is optional
                                if (!(tempString1 = cmdLine(",\r\n")).isNull())
                                {
                                    // check for a timeout
                                    if (tempString1.contains("offhours:"))
                                    {
                                        int colon = tempString1.first(':');

                                        if( colon !=RW_NPOS )
                                        {
                                            tempString1.replace(colon,1,' ');
                                            *programming += " temp ";
                                            *programming += tempString1;
                                        }
                                    }
                                    else if (tempString1.contains("temp"))
                                    {
                                        *programming += " temp ";

                                        // see if offhours was after this
                                        if (!(tempString1 = cmdLine(",\r\n")).isNull())
                                        {
                                            if (tempString1.contains("offhours:"))
                                            {
                                                int colon = tempString1.first(':');

                                                if( colon !=RW_NPOS )
                                                {
                                                    tempString1.replace(colon,1,' ');
                                                    *programming += tempString1;
                                                }
                                            }
                                            else
                                            {
                                                CtiLockGuard< CtiLogger > guard(dout);
                                                dout << RWTime() << " Invalid parameter -" << tempString1 << "- in line (" << input << ") " << endl;
                                                retCode = false;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        CtiLockGuard< CtiLogger > guard(dout);
                                        dout << RWTime() << " Invalid parameter -" << tempString1 << "- in line (" << input << ") " << endl;
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
                            RWCString utilityAddr;
                            RWCString sectionAddr;
                            RWCString classAddr("class ");
                            RWCString divisionAddr("division ");
                            bool haveUtility=false, haveSection=false,firstClass=true,firstDivision=true;
    
                            if (!(tempString1 = cmdLine(",\r\n")).isNull())
                            {
                                serialNum = tempString1;
    
                                if (!(tempString1 = cmdLine(",\r\n")).isNull())
                                {
                                    bool continueFlag = true;
                                    while (continueFlag)
                                    {
                                        if (tempString1.contains("u"))
                                        {
                                            haveUtility = true;
                                            tempString1 = tempString1.strip (RWCString::leading,'u');
                                            utilityAddr = RWCString ("utility ");
                                            utilityAddr += tempString1;
                                        }
                                        else if (tempString1.contains("s"))
                                        {
                                            haveSection = true;
                                            tempString1 = tempString1.strip (RWCString::leading,'s');
                                            sectionAddr = RWCString ("section ");
                                            sectionAddr += tempString1;
                                        }
                                        else if (tempString1.contains ("c"))
                                        {
                                            // we have a class address
                                            if (firstClass)
                                            {   
                                                // first time thru, we won't need a comma
                                                firstClass = false;
                                            }
                                            else
                                            {
                                                classAddr += RWCString (",");
                                            }
                                            tempString1 = tempString1.strip (RWCString::leading,'c');
                                            classAddr += tempString1;
    
                                        }
                                        else if (tempString1.contains ("d"))
                                        {
                                            // we have a division address
                                            if (firstDivision)
                                            {   
                                                // first time thru, we won't need a comma
                                                firstDivision = false;
                                            }
                                            else
                                            {
                                                divisionAddr += RWCString (",");
                                            }
                                            tempString1 = tempString1.strip (RWCString::leading,'d');
                                            divisionAddr += tempString1;
                                        }
    
                                        if ((tempString1 = cmdLine(",\r\n")).isNull())
                                            continueFlag = false;
                                    }
    
                                    // make sure we found something
                                    if ((!firstDivision) || (!firstClass) || (haveSection) || (haveUtility))
                                    {
                                        *programming = "set MessagePriority 5 ; PutConfig versacom serial ";
                                        *programming +=serialNum;
    
                                        if (haveUtility)
                                        {
                                            *programming += " ";
                                            *programming += utilityAddr;
                                        }
    
                                        if (haveSection)
                                        {
                                            *programming += " ";
                                            *programming += sectionAddr;
                                        }
    
                                        if (!firstClass)
                                        {
                                            *programming += " ";
                                            *programming += classAddr;
                                        }
    
                                        if (!firstDivision)
                                        {
                                            *programming += " ";
                                            *programming += divisionAddr;
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
                        RWCString currentCmd;
                        USHORT feeder=0, splinterCnt=0, programCnt=0, loadCnt=0;

                        if ((aProtocolFlag == TEXT_CMD_FILE_SPECIFY_NO_PROTOCOL) ||
                            (aProtocolFlag == TEXT_CMD_FILE_SPECIFY_EXPRESSCOM))
                        {
                            currentCmd = RWCString("set MessagePriority 5 ; PutConfig xcom assign serial ");
                            bool haveSomething=false;
                            bool haveFeeder=false;
                            bool haveLoad=false;
                            bool haveProgram=false;
                            bool haveSplinter=false;
                            bool invalidSPID=false, invalidGEO=false, invalidSub=false,invalidFeeder=false,invalidZip=false;
                            bool invalidUser=false, invalidProgram=false, invalidSplinter=false;
                            RWCString load,program,splinter,serialNum;
                            CHAR buffer[20];
                            int lastLoad=0;
                            ULONG tmpAddress;

                            memset (&buffer, '\0', 20);
                            
                            if (!(tempString1 = cmdLine(",\r\n")).isNull())
                            {
                                currentCmd += tempString1;
                                serialNum = tempString1;
    
                                if (!(tempString1 = cmdLine(",\r\n")).isNull())
                                {
                                    bool continueFlag = true;
                                    while (continueFlag)
                                    {
                                        tempString1 = tempString1.strip(RWCString::both,' ');

                                        if (tempString1.contains("spid"))
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.remove (0,5);
                                            tmpAddress = atoi(tempString1.data());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidSPID = true;
                                            }
                                            else
                                            {
                                                currentCmd += " s " + tempString1;
                                            }
                                        }
                                        else if (tempString1.contains("geo"))
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.remove (0,4);
                                            tmpAddress = atoi(tempString1.data());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidGEO = true;
                                            }
                                            else
                                            {
                                                currentCmd += " g " + tempString1;
                                            }

                                        }
                                        else if (tempString1.contains("sub"))
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.remove (0,4);
                                            tmpAddress = atoi(tempString1.data());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidSub = true;
                                            }
                                            else
                                            {
                                                currentCmd += " b " + tempString1;
                                            }

                                        }
                                        else if (tempString1.contains("feeder"))
                                        {
                                            haveSomething = true;
                                            haveFeeder = true;
                                            tempString1 = tempString1.remove (0,6);
                                            if (atoi (tempString1.data()) != 0)
                                            {
                                                tmpAddress = atoi (tempString1.data());
                                                if ((tmpAddress < 1) || (tmpAddress > 16))
                                                {
                                                    invalidFeeder = true;
                                                }
                                                else
                                                {
                                                    feeder |= (0x0001 << ((atoi (tempString1.data())-1)));
                                                }
                                            }

                                            //feeder |= setExpresscomFeederBit (atoi (tempString1.data()));
                                        }
                                        else if (tempString1.contains("zip"))
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.remove (0,4);
                                            tmpAddress = atoi(tempString1.data());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 16777214)
                                            {
                                                invalidZip = true;
                                            }
                                            else
                                            {
                                                currentCmd += " z " + tempString1;
                                            }
                                        }
                                        else if (tempString1.contains("user"))
                                        {
                                            haveSomething = true;
                                            tempString1 = tempString1.remove (0,5);
                                            tmpAddress = atoi(tempString1.data());
                                            if (tmpAddress < XCOM_ADDRESS_START || tmpAddress > 65534)
                                            {
                                                invalidUser = true;
                                            }
                                            else
                                            {
                                                currentCmd += " u " + tempString1;
                                            }
                                        }
                                        else if (tempString1.contains("load"))
                                        {
                                            haveSomething = true;
                                            RWCString workString;
                                            RWCTokenizer subCmd(tempString1);

                                            while(!(workString = subCmd(" ")).isNull())
                                            {
                                                if (workString.contains("load"))
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

                                                    if (!(workString = subCmd(" ")).isNull())
                                                    {
                                                        if (lastLoad < atoi(workString.data()))
                                                        {
                                                            load += workString;
                                                            lastLoad = atoi(workString.data());

                                                        }
                                                        else
                                                        {
                                                            {
                                                                CtiLockGuard< CtiLogger > guard(dout);
                                                                dout << RWTime() << " ERROR:  Invalid configuration line in " << aFileName <<  " for serial number " << serialNum << endl;
                                                                dout << " --- Loads must be addressed in numerical order " << endl;
                                                                dout << " --- " << input << endl;
                                                                loadCnt--;
                                                            }
                                                        }
                                                    }
                                                }
                                                else if (workString.contains("p"))
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
                                                        if (!(workString = subCmd(" ")).isNull())
                                                        {
                                                            tmpAddress = atoi(workString.data());
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
                                                            memcpy (&buffer, &workString.data()[1],workString.length()-1);
                                                            tmpAddress = atoi(buffer);
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidProgram=true;
                                                            }
                                                            else
                                                            {
                                                                program += RWCString(buffer);
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
                                                else if (workString.contains("s"))
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
                                                        if (!(workString = subCmd(" ")).isNull())
                                                        {
                                                            tmpAddress = atoi(workString.data());
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
                                                            memcpy (&buffer, &workString.data()[1],workString.length()-1);

                                                            tmpAddress = atoi(buffer);
                                                            //check address level
                                                            if ((tmpAddress < XCOM_ADDRESS_START) || (tmpAddress > 254))
                                                            {
                                                                invalidSplinter=true;
                                                            }
                                                            else
                                                            {
                                                                splinter += RWCString(buffer);
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

                                        if ((tempString1 = cmdLine(",\r\n")).isNull())
                                            continueFlag = false;
                                    }

                                    if (!(invalidSPID || invalidGEO || invalidSub || invalidFeeder ||invalidZip ||
                                        invalidUser || invalidProgram || invalidSplinter))
                                    {
                                        if (haveFeeder)
                                        {
                                            currentCmd += " f ";
                                            currentCmd += RWCString (ltoa(feeder,buffer,10));
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
                                                *programming = currentCmd;
                                            }
                                            else
                                            {
                                                {
                                                    CtiLockGuard< CtiLogger > guard(dout);
                                                    dout << RWTime() << " ERROR:  Invalid configuration line in " << aFileName <<  " for serial number " << serialNum << endl;
                                                    dout << " --- Number of addressed loads/splinters/programs must be equal " << endl;
                                                    dout << " --- " << input << endl;
                                                }

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
                                        RWCString address;

                                        if (invalidSPID)
                                            address += RWCString (" Spid ");
                                        if (invalidGEO)
                                            address += RWCString (" Geo ");
                                        if (invalidSub)
                                            address += RWCString (" Substation ");
                                        if (invalidFeeder)
                                            address += RWCString (" Feeder ");
                                        if (invalidZip)
                                            address += RWCString (" Zip ");
                                        if (invalidUser)
                                            address += RWCString (" User ");
                                        if (invalidProgram)
                                            address += RWCString (" Program ");
                                        if (invalidSplinter)
                                            address += RWCString (" Splinter ");
                                        {
                                            CtiLockGuard< CtiLogger > guard(dout);
                                            dout << RWTime() << " ERROR:  Invalid configuration line in " << aFileName <<  " for serial number " << serialNum << endl;
                                            dout << " --- Address is out of range :" << address << endl;
                                            dout << " --- " << input << endl;
                                        }

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

    return retCode;
}   

bool decodeDsm2Lines( RWCString &aFunction, 
                      RWCString &aRoute,
                      RWCString &aSerialNum,
                      RWCString &aCmd,
                      RWCollectableString* programming)
{
	bool retCode = true;
    RWCString route,function,serialNum,cmd;
    
    RWCTokenizer tFunction(aFunction);           
    function = tFunction("\r\n");
    RWCTokenizer tRoute(aRoute);           
    route = tRoute("\r\n");
    RWCTokenizer tSN(aSerialNum);           
    serialNum = tSN("\r\n");
    RWCTokenizer tCmd(aCmd);           
    cmd = tCmd("\r\n");

    route = route.strip(RWCString::both);
    function = function.strip(RWCString::both);
    serialNum = serialNum.strip(RWCString::both);
    cmd = cmd.strip(RWCString::both);

    // just a comment, don't do anything
    switch (atoi (function.data()))
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
                *programming = RWCString ("set MessagePriority 5 ; PutConfig serial ");
                *programming += serialNum;
                *programming += RWCString (" template '");
                *programming += RWCString ("config: ");
                *programming += cmd;
                *programming += RWCString ("'");

                if ((!route.isNull()) && (route.data()[0] != ' '))
                {
                    *programming += RWCString (" select routename '");
                    *programming += route;
                    *programming += RWCString ("'");
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
                *programming = RWCString ("set MessagePriority 7 ; PutConfig versacom serial ");
                *programming += serialNum;
                if (atoi (cmd.data()) ==  0)
                {
                    *programming += " service out";
                }
                else
                {
                    *programming += " service in";
                }

                if ((!route.isNull()) && (route.data()[0] != ' '))
                {
                    *programming += RWCString (" select route name '");
                    *programming += route;
                    *programming += RWCString ("'");
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
                 RWCString &outBuffer)
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


int decodeDSM2VconfigFile(const RWCString& fileName, RWOrdered* commandList)
{
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
	char command;
	RWCString serialNum;
	RWCString programming;
    int lineCnt=0, cmdCnt=0;
    int retVal=NORMAL;

    if( commandList == NULL )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " Invalid use of command decodeDSM2VconfigFile"<< endl;
        dout << "             second parameter commandList cannont be NULL " << endl;
        retVal = TEXT_CMD_FILE_COMMAND_LIST_INVALID;
    }
    else
    {
                                   
        // open file               
        if( (fptr = fopen( fileName.data(), "r")) == NULL )
        {
            retVal = TEXT_CMD_FILE_UNABLE_TO_OPEN_FILE;
        }
        else
        {
            vector<RWCString>     commandVector;

            // load list in the command vector
            while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
            {
                RWCString entry (workBuffer);
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

                while ((lineCnt+4) <= totalLines )
                {
                    /*****************
                    *  ASCII command string built from the text line
                    ******************
                    */
                    RWCollectableString* decodedCommand = new RWCollectableString();

                    if( true == decodeDsm2Lines( commandVector[lineCnt], 
                                                 commandVector[lineCnt+1],
                                                 commandVector[lineCnt+2],
                                                 commandVector[lineCnt+3],
                                                 decodedCommand ))
                    {
                        {
                            CtiLockGuard< CtiLogger > guard(dout);
                            dout << "-- " << *decodedCommand << endl;
                        }
                        commandList->insert(decodedCommand);
                        // set to null for safety sakes
                        decodedCommand = NULL;
                    }
                    else
                    {
                        delete decodedCommand;
                    }

                    lineCnt+=4;
                }
                commandVector.erase(commandVector.begin(), commandVector.end());
            }
        }
    }
    if (retVal == NORMAL)
    {
        DeleteFile (fileName);
    }

    return retVal;
}

