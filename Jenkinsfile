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
                    tools {
                        jdk "jdk-11(18.9)"
                    }
                
                    steps {
                       script {
                          if (params.RELEASE_MODE) {
                                 cleanWs()
                           }
						   try{
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
							}catch(Exception){
								//Added sleep so that it capture full log for current stage
								sleep(5)
								sendEmailNotification("${env.STAGE_NAME}")
							}
						}
                    }
                }
                stage('cpp-build') {
                    agent {
                        label "cpp"
                    }
                    
                    tools {
                        jdk "jdk-11(18.9)"
                    }
                    
                    steps {
                       script {
                          if (params.RELEASE_MODE) {
                                 cleanWs()
                           }
                         try{
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
							}catch(Exception){
								//Added sleep so that it capture full log for current stage
								sleep(5)
								sendEmailNotification("${env.STAGE_NAME}")
							}
						}
                    }
                }
                
            }
        }
        stage('installer') {
            agent {
                label "install"
            }
            tools {
               jdk "jdk-11(18.9)"
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
				script{
					try{
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
							
							if (params.RELEASE_MODE) {
                                 bat 'net use p: \\\\pspl0003.eaton.ad.etn.com\\Public /user:eaton\\psplsoftwarebuild 13aq4xHAB'
								 bat './yukon-build/go.bat init clean svn-info-build symstore build-dist'
                                 bat 'net use p: /delete'
							} else {
								 bat './yukon-build/go.bat clean build-dist-pdb'
							}

						archiveArtifacts artifacts: 'yukon-build/dist/*'
					}catch(Exception){
						currentBuild.result = 'FAILURE'
						//Added sleep so that it capture full log for current stage
						sleep(5)
						sendEmailNotification("${env.STAGE_NAME}")
					}
				}
            }
        }
    }
	post {
        always{
            verifyLastBuild()
        }
    }
}
@NonCPS
def verifyLastBuild(){
    if(currentBuild.currentResult == 'SUCCESS'){
        if(currentBuild?.getPreviousBuild()?.result == 'FAILURE') {
            emailext body: " See<${env.BUILD_URL}display/redirect> \n ",
            to: '$DEFAULT_RECIPIENTS',
            recipientProviders: [culprits(), requestor(), brokenTestsSuspects(), brokenBuildSuspects()],
            subject: "Jenkins build is back to normal : ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
@NonCPS
def sendEmailNotification(String stageName){
	// Code for getting change sets from last build.
	MAX_MSG_LEN = 100
	def changeString = ""
	def changeLogSets = currentBuild.changeSets
	if(changeLogSets.size()>0){
		changeString += "\n Changes : "
		for (int i = 0; i < changeLogSets.size(); i++) {
			def entries = changeLogSets[i].items
			for (int j = 0; j < entries.length; j++) {
				def entry = entries[j]
				truncated_msg = entry.msg.take(MAX_MSG_LEN)
				changeString += " [${entry.author}] - ${truncated_msg} \n"
			}
		}
	}
	//Code for getting logs 
	def logString = "\nLogs : "
	for(String line : currentBuild.rawBuild.getLog(100)){
	    if(line?.trim()){
			logString += "\n"+ line
	    }
	}
	emailext body: "${stageName} Failed : Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}\n ${changeString}     ${logString}",
                to: '$DEFAULT_RECIPIENTS',
                recipientProviders: [culprits(), brokenTestsSuspects(), brokenBuildSuspects()],
                subject: "${stageName} failed in Jenkins:  ${env.JOB_NAME}#${env.BUILD_NUMBER}"

}
