
#ifdef USE_ANSI

#define SET_FOREGROUND_BLACK            {printf ("[0;30m");}
#define SET_FOREGROUND_RED              {printf ("[0;31m");}
#define SET_FOREGROUND_GREEN            {printf ("[0;32m");}
#define SET_FOREGROUND_YELLOW           {printf ("[0;33m");}
#define SET_FOREGROUND_BLUE             {printf ("[0;34m");}
#define SET_FOREGROUND_MAGNETA          {printf ("[0;35m");}
#define SET_FOREGROUND_CYAN             {printf ("[;36m");}
#define SET_FOREGROUND_WHITE            {printf ("[0;37m");}
#define SET_FOREGROUND_BRIGHT_BLACK     {printf ("[1;30m");}
#define SET_FOREGROUND_BRIGHT_RED       {printf ("");}
#define SET_FOREGROUND_BRIGHT_GREEN     {printf ("[1;32m");}
#define SET_FOREGROUND_BRIGHT_YELLOW    {printf ("[1;33m");}
#define SET_FOREGROUND_BRIGHT_BLUE      {printf ("[1;34m");}
#define SET_FOREGROUND_BRIGHT_MAGNETA   {printf ("[1;35m");}
#define SET_FOREGROUND_BRIGHT_CYAN      {printf ("[1;36m");}
#define SET_FOREGROUND_BRIGHT_WHITE     {printf ("[1;37m");}
#define SET_BACKGROUND_BLACK            {printf ("[0;40m");}
#define SET_BACKGROUND_RED              {printf ("[0;41m");}
#define SET_BACKGROUND_GREEN            {printf ("[0;42m");}
#define SET_BACKGROUND_YELLOW           {printf ("[0;43m");}
#define SET_BACKGROUND_BLUE             {printf ("[0;44m");}
#define SET_BACKGROUND_MAGNETA          {printf ("[0;45m");}
#define SET_BACKGROUND_CYAN             {printf ("[0;46m");}
#define SET_BACKGROUND_WHITE            {printf ("[0;47m");}
#define SET_SCREEN_NORMAL               {printf ("");}
#define CLEAR_SCREEN                    {printf ("[2J");}

#else

#define SET_FOREGROUND_BLACK            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , 0);}
#define SET_FOREGROUND_RED              {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_RED);}
#define SET_FOREGROUND_GREEN            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_GREEN);}
#define SET_FOREGROUND_YELLOW           {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_GREEN | FOREGROUND_RED);}
#define SET_FOREGROUND_BLUE             {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE);}
#define SET_FOREGROUND_CYAN             {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN);}
#define SET_FOREGROUND_WHITE            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);}
#define SET_FOREGROUND_BRIGHT_BLACK     {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY);}
#define SET_FOREGROUND_BRIGHT_RED       {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_RED);}
#define SET_FOREGROUND_BRIGHT_YELLOW    {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_GREEN | FOREGROUND_RED);}
#define SET_FOREGROUND_BRIGHT_BLUE      {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_BLUE);}
#define SET_FOREGROUND_BRIGHT_MAGNETA   {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_BLUE | FOREGROUND_RED);}
#define SET_FOREGROUND_BRIGHT_CYAN      {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_BLUE | FOREGROUND_GREEN);}
#define SET_FOREGROUND_BRIGHT_WHITE     {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);}
#define SET_BACKGROUND_BLACK            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | 0);}
#define SET_BACKGROUND_RED              {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_RED);}
#define SET_BACKGROUND_GREEN            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_GREEN);}
#define SET_BACKGROUND_YELLOW           {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_GREEN | BACKGROUND_RED);}
#define SET_BACKGROUND_BLUE             {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_BLUE);}
#define SET_BACKGROUND_MAGNETA          {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_BLUE | BACKGROUND_RED);}
#define SET_BACKGROUND_CYAN             {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_BLUE | BACKGROUND_GREEN);}
#define SET_BACKGROUND_WHITE            {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , BACKGROUND_BLUE | BACKGROUND_GREEN | BACKGROUND_RED);}

#define SET_FOREGROUND_BRIGHT_GREEN     {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_GREEN);}
#define SET_FOREGROUND_BRIGHT_MAGNETA   {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_INTENSITY | FOREGROUND_BLUE | FOREGROUND_RED);}
#define SET_FOREGROUND_MAGNETA          {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_RED);}
#define SET_SCREEN_NORMAL               {SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE) , FOREGROUND_BLUE | FOREGROUND_GREEN | FOREGROUND_RED);}
#define CLEAR_SCREEN

#endif
