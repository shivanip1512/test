*** These scripts are provided to collect data used in analytics performed by the engineering teams. ***
*** Data is queried directly from the Yukon and Network Manager databases.
*** These queries may be performance intensive, so consideration shall be taken regarding potential impact/slowness to running systems.
*** Batch files can be run from command line (Windows).
*** From command line, navigate to C:\Yukon\Tools\RfNetworkData\


*** historicData.bat
-    Output: Generates a collection of csv files covering the last 14 days of data.
             Requires a database user with read access for Network Manager.
-    Usage Syntax: historicData.bat <Server Name> <user_name> <password>
-    Example:      historicData.bat EAS0001111 username password



*** locationData.bat
-    Output: Generates a collection of csv files containing location, network, and gateway data for Meters, Relays and Gateways.
             Requires a database user with read access for both Yukon and Network Manager.
             After running the scripts you will get few csv files listed below
             - GatewayLocationsInYukon.csv
             - GW.csv
             - MeterLocationsInYukon.csv
             - NetworkData.csv
             - NetworkStatus.csv
             - NodeSWVer.csv
             - RelayLocationsInYukon.csv
-    Usage Syntax: locationData.bat <Server Name> <user_name> <password>
-    Example:      locationData.bat EAS0001111 username password


*** networkSnapshot.bat
-    Output: Generates a collection of csv files of network snapshot.
             Requires a database user with read access for both Yukon and Network Manager.
             After running the scripts you will get few csv files listed below
             - descData.csv
             - nmData.csv
             - yukonData.csv
-    Usage Syntax: networkSnapshot.bat <Server Name> <user_name> <password>
-    Example:      networkSnapshot.bat EAS0001111 username password