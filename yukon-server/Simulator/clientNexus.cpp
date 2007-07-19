/*
    File test_fdrareva.cpp

    Author: Thain Spar
    Date:   5/22/2007

    Test Areva interface

*/
#include "yukon.h"
#include "clientNexus.h"
#include <iostream.h>
#include <windows.h>
#include <iostream>
#include "numstr.h"
#include "cticalls.h"


using namespace std;

int main()
{	
	WSADATA wsaData;
	WSAStartup(MAKEWORD (1,1), &wsaData);

	CTINEXUS * clientSocket;
	clientSocket = new CTINEXUS();

	unsigned char Buffer[38] = "This is a successful test of CTINexus";
	unsigned long bytesWritten;
	unsigned long bytesRead=0;

	if(!clientSocket->CTINexusOpen("127.0.0.1",11235, CTINEXUS_FLAG_READEXACTLY))
		{
		clientSocket->CTINexusWrite(Buffer, 38, &bytesRead, 150); 
	}
	else
		std::cout<<"Cannot open socket"<<endl;

	CTISleep(5000);

	return 0;
}


