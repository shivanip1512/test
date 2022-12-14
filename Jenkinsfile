#!groovy

pipeline {
    agent none
    options {
        preserveStashes()
        timestamps()
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
                            try {
                                if (params.CLEAN_WORKSPACE) {
                                    cleanWs()
                                }
                                env.YUKON_BUILD_RELEASE_NUMBER = "${env.BUILD_NUMBER}"
                                bat 'java -version'
                                def scmVars = checkout([$class: 'GitSCM',
                                                        branches: [[name: 'refs/heads/master']],
                                                        doGenerateSubmoduleConfigurations: false,
                                                        extensions: [],
                                                        submoduleCfg: [],
                                                        userRemoteConfigs: [[refspec: '+refs/heads/master:refs/remotes/origin/master', credentialsId: 'PSPLSoftwareBuildSSH', url: 'ssh://git@bitbucket-prod.tcc.etn.com:7999/easd_sw/yukon.git']]])

                                env.GIT_COMMIT = scmVars.GIT_COMMIT

                                bat './yukon-build/go.bat build-client'

                                stash name: 'yukon-client', excludes: '**/*.java, **/*.class'

                                //junit './yukon-client/*/test/testResults/*.xml'
                            } catch (Exception) {
                                currentBuild.result = 'FAILURE'
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
                            try {
                                if (params.CLEAN_WORKSPACE) {
                                    cleanWs()
                                }
                                checkout([$class: 'GitSCM',
                                          branches: [[name: 'refs/heads/master']],
                                          doGenerateSubmoduleConfigurations: false,
                                          extensions: [],
                                          submoduleCfg: [],
                                          userRemoteConfigs: [[refspec: '+refs/heads/master:refs/remotes/origin/master', credentialsId: 'PSPLSoftwareBuildSSH', url: 'ssh://git@bitbucket-prod.tcc.etn.com:7999/easd_sw/yukon.git']]])

                                bat './yukon-build/go.bat build-server'

                                stash name: 'yukon-server', includes: 'yukon-server/bin/*, yukon-server/pdb/*, yukon-server/Message/Static_Release/ctithriftmsg/I386/*'
                            } catch (Exception) {
                                currentBuild.result = 'FAILURE'
                                //Added sleep so that it capture full log for current stage
                                sleep(5)
                                sendEmailNotification("${env.STAGE_NAME}")
                            }
                        }
                    }
                }

            }
        }
        stage('installer-executetestcase') {
            parallel {
                stage('installer') {
                    agent {
                        label "install"
                    }
                    tools {
                        jdk "jdk-11(18.9)"
                    }
                    steps {
                        // These are checked out clean, of note yukon-build contains the installer which will be wiped out by the UpdateWithCleanUpdater setting
                        script {
                            try {
                                if (params.CLEAN_WORKSPACE) {
                                    cleanWs()
                                }
                                checkout([$class: 'GitSCM',
                                          branches: [[name: "${env.GIT_COMMIT}"]],
                                          doGenerateSubmoduleConfigurations: false,
                                          extensions: [],
                                          submoduleCfg: [],
                                          userRemoteConfigs: [[refspec: '+refs/heads/master:refs/remotes/origin/master', credentialsId: 'PSPLSoftwareBuildSSH', url: 'ssh://git@bitbucket-prod.tcc.etn.com:7999/easd_sw/yukon.git']]])

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

                                bat './yukon-applications/cloud-service/go.bat clean build-cloud'

                                bat './yukon-build/go.bat build-install'

                                bat './yukon-build/go.bat clean build-dist-server'

                                archiveArtifacts artifacts: 'yukon-build/dist/*, yukon-applications/cloud-service/build/*.zip'
                            } catch (Exception) {
                                currentBuild.result = 'FAILURE'
                                //Added sleep so that it capture full log for current stage
                                sleep(5)
                                sendEmailNotification("${env.STAGE_NAME}")
                            }
                        }
                    }
                }
                stage('java-testcase') {
                    agent {
                        label "java"
                    }
                    tools {
                        jdk "jdk-11(18.9)"
                    }
                    steps {
                        script {
                            try {
                                // Running All JUnit Test cases
                                bat './yukon-build/go.bat runUnitTests'
                            } catch (Exception) {
                                currentBuild.result = 'FAILURE'
                                //Added sleep so that it capture full log for current stage
                                sleep(5)
                                sendEmailNotification("${env.STAGE_NAME}")
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            verifyLastBuild()
        }
    }
}
@NonCPS
def verifyLastBuild() {
    if (currentBuild.currentResult == 'SUCCESS') {
        if (currentBuild?.getPreviousBuild()?.result == 'FAILURE') {
            emailext body: " See<${env.BUILD_URL}display/redirect> \n ",
                    to: '$DEFAULT_RECIPIENTS',
                    recipientProviders: [culprits(), requestor(), brokenTestsSuspects(), brokenBuildSuspects()],
                    subject: "Jenkins build is back to normal : ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
@NonCPS
def sendEmailNotification(String stageName) {
    // Code for getting change sets from last build.
    MAX_MSG_LEN = 100
    def changeString = ""
    def changeLogSets = currentBuild.changeSets
    if (changeLogSets.size() > 0) {
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
    for (String line: currentBuild.rawBuild.getLog(100)) {
        if (line?.trim()) {
            logString += "\n" + line
        }
    }
    emailext body: "${stageName} Failed : Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n More info at: ${env.BUILD_URL}\n ${changeString}     ${logString}",
            to: '$DEFAULT_RECIPIENTS',
            recipientProviders: [culprits(), brokenTestsSuspects(), brokenBuildSuspects()],
            subject: "${stageName} failed in Jenkins:  ${env.JOB_NAME}#${env.BUILD_NUMBER}"

}
