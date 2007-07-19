/*****************************************************************************
*
*    FILE NAME: serverNexus.cpp
*
*    DATE: 7/1/2007
*
*    AUTHOR: Eric Rooney
*
*    PURPOSE: CCU Simulator
*
*    DESCRIPTION: Simulate the behavior of CCUs
*    
*    Copyright (C) 2007 Cannon Technologies, Inc.  All rights reserved.
*****************************************************************************/
#ifndef __RWNET_WINSOCK_H__
#define __RWNET_WINSOCK_H__
#include "yukon.h"
#include <windows.h>
#include <iostream>
#include "cticalls.h"
#include "ctinexus.h"
#include "dsm2.h"
#include "CCU710.h"

int main()
{	
	CCU710 aCCU;

	CTINEXUS * listenSocket; 
	listenSocket = aCCU.getListenSocket();
	listenSocket->CTINexusCreate(11234);

	CTINEXUS * newSocket;
	newSocket = aCCU.getNewSocket();

	for(int i = 0; i<25 && !(newSocket->CTINexusValid()); i++) {
		listenSocket->CTINexusConnect(newSocket, NULL, 1000, CTINEXUS_FLAG_READEXACTLY);
		RWDBDateTime Listening;
		std::cout<<Listening.asString()<<" Listening..."<<std::endl;
	}

	while(newSocket->CTINexusValid()) {
		aCCU.ReceiveMsg();
		aCCU.CreateMsg();
		aCCU.SendMsg();
	}

	CTISleep(5000);

	return 0;
}

#endif
