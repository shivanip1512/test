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
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-help'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-client',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-client'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-build',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-build'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-shared',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-shared']],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                        
                        bat './yukon-build/go.bat build-client'

                        stash name: 'yukon-client', excludes: '**/*.java'
                        
                        //junit './yukon-client/*/test/testResults/*.xml'
                    }
                }
                stage('cpp-build') {
                    agent {
                        label "cpp"
                    }
                    
                    steps {
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
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-server'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-build',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-build'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-client/build/ant/bin',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-client/build/ant/bin'],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: 'yukon-shared',
                            remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-shared']],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])

                        bat './yukon-build/go.bat build-server'

                        stash name: 'yukon-server', includes: 'yukon-server/bin/*, yukon-server/pdb/*'
                    }
                }
                
            }
        }
        stage('installer') {
            agent {
                label "install"
            }
            steps {
                unstash 'yukon-client'
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
                        remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-install'],
                        [cancelProcessOnExternalsFail: true,
                        credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                        depthOption: 'infinity',
                        ignoreExternalsOption: true,
                        local: 'yukon-build',
                        remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-build']],
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
                        remote: 'https://svn.cooperpowereas.net/software/yukon/trunk/yukon-database']],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    
                bat './yukon-build/go.bat clean build-dist-pdb'

                archiveArtifacts artifacts: 'yukon-build/dist/*'
            }
        }
    }
}