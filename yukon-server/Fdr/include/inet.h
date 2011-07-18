#define INETCHECKTHREADSTACKSIZE    32768
#define INETNULLTHREADSTACKSIZE     32768
#define INETTCPOUTTHREADSTACKSIZE   32768
#define INETINTHREADSTACKSIZE       65536
#define INETOUTTHREADSTACKSIZE      65536

#define BUFFERSIZE 8192
#define INETPORTNUMBER 1000

#define INETRECEIVEONLYFILE "data\\inet.dat"

int EXPENTRY ModuleStart( void );
void INetInThread( PVOID );
int SizeOfINetMessage( USHORT );
void INetOutThread( PVOID );
int SendINetNull( USHORT );
void NullMessageThread( PVOID );
void INetCheckThread( PVOID );
void INetTCPOutThread( PVOID );
void APIENTRY INetCleanUp( ULONG );
USHORT InitializeHost( PCHAR hostName, USHORT *Slot );

