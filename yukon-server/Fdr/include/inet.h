#define INETCHECKTHREADSTACKSIZE    32768
#define INETNULLTHREADSTACKSIZE     32768
#define INETTCPOUTTHREADSTACKSIZE   32768
#define INETINTHREADSTACKSIZE       65536
#define INETOUTTHREADSTACKSIZE      65536

#define BUFFERSIZE 8192
#define INETPORTNUMBER 1000

#define INETRECEIVEONLYFILE "data\\inet.dat"

int EXPENTRY ModuleStart( VOID );
VOID INetInThread( PVOID );
int SizeOfINetMessage( USHORT );
VOID INetOutThread( PVOID );
int SendINetNull( USHORT );
VOID NullMessageThread( PVOID );
VOID INetCheckThread( PVOID );
VOID INetTCPOutThread( PVOID );
VOID APIENTRY INetCleanUp( ULONG );
USHORT InitializeHost( PCHAR hostName, USHORT *Slot );

