#!groovy
upgradeDeploy( vmUser: 'PSPL-SW-NIGHT+Administrator', vmPassword: 'cti123',
               vmAddress: 'PSPL-SW-NIGHT.eatoneaseng.net', databaseType: 'sqlserver',
               installerProject: 'Yukon_Trunk',
               installerSelector: 'lastSuccessful')
