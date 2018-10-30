#!groovy

pipeline {
    agent none
    options {
        preserveStashes() 
    }
    stages {
        stage('build') {
            parallel {
                stage('java-build') { 
                    agent {
                        label "java"
                    }
                    environment {
                        JAVA_HOME="${tool 'jdk-8u181'}"
                        PATH="${env.JAVA_HOME}/bin:${env.PATH}"
                    }
                
                    steps {
                       script {
                          if (params.RELEASE_MODE) {
                                 cleanWs()
                            }
                          }
                        bat 'java -version'
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
                            local: 'yukon-help',
                            remote: "${env.SVN_URL}" + '/yukon-help'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-client',
                            remote: "${env.SVN_URL}" + '/yukon-client'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-build',
                            remote: "${env.SVN_URL}" + '/yukon-build'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-shared',
                            remote: "${env.SVN_URL}" + '/yukon-shared']],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                        
                        bat './yukon-build/go.bat build-client'

                        stash name: 'yukon-client', excludes: '**/*.java, **/*.class'
                        
                        //junit './yukon-client/*/test/testResults/*.xml'
                    }
                }
                stage('cpp-build') {
                    agent {
                        label "cpp"
                    }
                    
                    steps {
                       script {
                          if (params.RELEASE_MODE) {
                                 cleanWs()
                            }
                          }
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
                            local: 'yukon-server',
                            remote: "${env.SVN_URL}" + '/yukon-server'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-build',
                            remote: "${env.SVN_URL}" + '/yukon-build'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-client/build/ant/bin',
                            remote: "${env.SVN_URL}" + '/yukon-client/build/ant/bin'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-shared',
                            remote: "${env.SVN_URL}" + '/yukon-shared']],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])

                        bat './yukon-build/go.bat build-server'

                        stash name: 'yukon-server', includes: 'yukon-server/bin/*, yukon-server/pdb/*, yukon-server/Message/Static_Release/ctithriftmsg/I386/*'
                    }
                }
                
            }
        }
        stage('installer') {
            agent {
                label "install"
            }
            steps {
                script {
                   if (params.RELEASE_MODE) {
                           cleanWs()
                       }
                     }
                // The stashed folders are modified during the build, which means a simple
                // unstash leaves data behind. Here we manually wipe these folders before unstashing.
                dir('yukon-client') {
                    deleteDir()
                }                            
                unstash 'yukon-client'
                
                dir('yukon-server') {
                    deleteDir()
                }
                unstash 'yukon-server'
                
                // These are checked out clean, of note yukon-build contains the installer which will be wiped out by the UpdateWithCleanUpdater setting
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
                        local: 'yukon-install',
                        remote: "${env.SVN_URL}" + '/yukon-install'],
                        [cancelProcessOnExternalsFail: true,
                        credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                        depthOption: 'infinity',
                        ignoreExternalsOption: true,
                        local: 'yukon-build',
                        remote: "${env.SVN_URL}" + '/yukon-build']],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateWithCleanUpdater']])
                        
                bat './yukon-build/go.bat build-install'
                
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
                        local: 'yukon-database',
                        remote: "${env.SVN_URL}" + '/yukon-database']],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    
                script {
                    if (params.RELEASE_MODE) {
                         bat './yukon-build/go.bat init clean svn-info-build symstore build-dist'
                    } else {
                         bat './yukon-build/go.bat clean build-dist-pdb'
                    }
				}

                archiveArtifacts artifacts: 'yukon-build/dist/*'
            }
        }
    }
}