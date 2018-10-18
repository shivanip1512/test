#!groovy

pipeline {
    agent none
    options {
        preserveStashes()
    }
    parameters {
        string(name: 'VM_GUEST_USERID', defaultValue: 'PSPL-SW-NIGHT+Administrator', description: 'The username. If unspecified the scripts will automatically try VM_GUEST_ADDRESS+VM_GUEST_USERID and VM_GUEST_USERID. In this case the capitalization of VM_GUEST_ADDRESS may matter')
        string(name: 'VM_GUEST_PASSWORD', defaultValue: 'cti123', description: 'Who should I say hello to?')
        string(name: 'VM_GUEST_ADDRESS', defaultValue: 'PSPL-SW-NIGHT.eatoneaseng.net', description: 'The name of the server, such as PSPL-SW-NIGHT.EATONEASENG.NET. Case may matter here if the guest userID needs this to be appended to it. See VM_GUEST_USERID.')
        string(name: 'DATABASE_TYPE', defaultValue: 'sqlserver', description: 'For Oracle, set to oracle or oracle12, all other values use sqlserver')
    }
    
    environment {
        VM_GUEST_USERID = "${VM_GUEST_USERID}"
        VM_GUEST_PASSWORD = "${VM_GUEST_PASSWORD}"
        VM_GUEST_ADDRESS = "${VM_GUEST_ADDRESS}"
        DATABASE_TYPE = "${DATABASE_TYPE}"
    }

    stages {
        stage('upgrade') {
            agent {
                label '!master'
            }

            steps {
                dir('yukon-deploy') {
                    deleteDir()
                }
                cleanWs()
                copyArtifacts(projectName: 'Yukon_Trunk');
                checkout([$class: 'SubversionSCM',
                    additionalCredentials: [],
                    excludedCommitMessages: '',
                    excludedRegions: '',
                    excludedRevprop: '',
                    excludedUsers: '',
                    filterChangelog: false,
                    ignoreDirPropChanges: false,
                    includedRegions: '',
                    locations: [[cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-deploy',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-deploy']],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])

                bat './yukon-deploy/vm-install/go.bat copy_support_files remote_upgrade_powershell verify_webserver verify_eim'
                
            }
        }
    }
}
