#include "yukon.h"

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <process.h>

#include "cticalls.h"
#include "ctinexus.h"
#include "proclog.h"
#include "errserver.h"

static CErrServer  Listener;

void PlogBarf(void)
{
   Listener.CloseMain();
}

void main(void)
{
   atexit(PlogBarf);

   SetConsoleTitle("Process Logger - YUKON v 0.995");

   Listener.Listen();

   Sleep(1000);

   return;
}


