#!groovy

def call(Map pipelineParams) {

// string vmUser, string vmPassword, string vmAddress, string databaseType, string installerProject, string installerSelector ) {

    pipeline {
        agent none
        options {
            preserveStashes()
        }

        environment {
            VM_GUEST_USERID = pipelineParams.vmUser
            VM_GUEST_PASSWORD = pipelineParams.vmPassword
            VM_GUEST_ADDRESS = pipelineParams.vmAddress
            DATABASE_TYPE = pipelineParams.databaseType
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
                    copyArtifacts(projectName: pipelineParams.installerProject, selector: pipelineParams.installerSelector);
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
} 
