#!groovy

pipeline {
    agent none
	parameters { 
	         string(name: 'YUKON_CHECKOUT_BASE', defaultValue: '', description: 'This will be the base SVN url to checkout from. This value is parameterizes and different for release and trunk checkout.', trim: true) 
			 string(name: 'YUKON_CLIENT', defaultValue: 'yukon-client', description: 'Yukon client folder .', trim: true) 
			 string(name: 'YUKON_BUILD', defaultValue: 'yukon-build', description: 'Yukon Build folder .', trim: true)
			 string(name: 'YUKON_SHARED', defaultValue: 'yukon-shared', description: 'Yukon Shared folder .', trim: true)
			 string(name: 'YUKON_SERVER', defaultValue: 'yukon-server', description: 'Yukon Server folder .', trim: true)
             string(name: 'YUKON_ANT', defaultValue: 'yukon-client/build/ant/bin', description: 'Yukon Client Ant folder .', trim: true)
             string(name: 'YUKON_DATABASE', defaultValue: 'yukon-database', description: 'Yukon Database folder .', trim: true) 			 
	}
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
                            local:  ${params.YUKON_CLIENT},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_CLIENT}],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: ${params.YUKON_BUILD},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_BUILD}],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local:  ${params.YUKON_SHARED},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_SHARED}]],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                        
                        bat './yukon-build/go.bat build-client'

                        stash name: 'yukon-client', excludes: '*.java'
                        
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
                            local: ${params.YUKON_SERVER},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_SERVER}],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: ${params.YUKON_BUILD},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_BUILD}],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: ${params.YUKON_ANT},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_ANT}],
                            [cancelProcessOnExternalsFail: true,
                            credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                            depthOption: 'infinity',
                            ignoreExternalsOption: true,
                            local: ${params.YUKON_SHARED},
                            remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_SHARED}]],
                        quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])

                        bat './yukon-build/go.bat build-server'

                        stash name: 'yukon-server', includes: 'yukon-server/bin/*'
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
                        local:  ${params.YUKON_INSTALL},
                        remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_INSTALL}],
                        [cancelProcessOnExternalsFail: true,
                        credentialsId: '705036f1-44aa-43f0-8a78-4949f8bcc072',
                        depthOption: 'infinity',
                        ignoreExternalsOption: true,
                        local: ${params.YUKON_BUILD},
                        remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_BUILD}]],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                        
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
                        local: ${params.YUKON_DATABASE},
                        remote: ${params.YUKON_CHECKOUT_BASE} + ${params.YUKON_DATABASE}]],
                    quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
                    
                bat './yukon-build/go.bat build-dist'

                archiveArtifacts artifacts: 'dist/*'
            }
        }
    }
}