#!groovy
@Library('jenkins-pipeline-library') _

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
        ansiColor('xterm')
    }

    stages {
        stage('Build') {
            agent {
                docker 'maven:3.3.9-jdk-8'
            }

            steps {
                notifySlack('#bitwise-notify', 'STARTED')

                withMaven(globalMavenSettingsConfig: 'maven-settings') {
                    sh '''
                        mvn --show-version --errors             \
                            --activate-profiles code-quality use-atg-stubs    \
                            --define cpd.failOnViolation=false  \
                            --define findbugs.failOnError=false \
                            --define jacoco.haltOnFailure=false \
                            --define maven.test.failure.ignore  \
                            --define pmd.failOnViolation=false  \
                            clean verify
                    '''
                }
            }

            post {
                always {
                    junit '**/target/surefire-reports/*.xml'

                    step([$class: 'DryPublisher'])
                    step([$class: 'FindBugsPublisher', pattern: '**/findbugsXml.xml'])
                    step([$class: 'JacocoPublisher', exclusionPattern: '**/*_Builder*.class'])
                    step([$class: 'PmdPublisher'])
                    step([$class: 'TasksPublisher', high: 'FIXME', normal: 'TODO', ignoreCase: true])
                    step([$class: 'WarningsPublisher', consoleParsers: [[parserName: 'Java Compiler (javac)'], [parserName: 'Maven']]])
                    // The overall analysis publisher must be declared last in order to collect results from other analyzers
                    step([$class: 'AnalysisPublisher', unstableTotalHigh: '0', failedTotalHigh: '5'])
                }
            }
        }

        stage('Release') {
            agent {
                docker 'maven:3.3.9-jdk-8'
            }

            when {
                branch 'master'

                /*
                expression {
                    // a snapshot or not already tagged
                    def project = readMavenPom()
                    project.version.endsWith('-SNAPSHOT') || !sh(returnStdout: true, script: "git tag --list ${project.version} || exit 0")
                }*/
            }

            steps {
                withMaven(globalMavenSettingsConfig: 'maven-settings') {
                    sh '''
                        mvn --show-version --errors \
                            --define skipTests      \
                            clean deploy
                    '''
                }
            }
        }

        /*
        TODO: re-enable this once it doesn't require generating keypairs and regeristing with github
        see https://issues.jenkins-ci.org/browse/JENKINS-28335
        stage('Tag') {
            when {
                branch 'master'

                expression {
                    // not a snapshot and not already tagged
                    def project = readMavenPom()
                    !project.version.endsWith('-SNAPSHOT') && !sh(returnStdout: true, script: "git tag --list ${project.version} || exit 0")
                }
            }

            steps {
                script {
                    sshagent(['git-spring-wrapper-client']) {
                        def project = readMavenPom()
                        sh "git tag -a '${project.version}' -m 'Released ${project.version}'"
                        sh "git push -v git@github.body.prod:commerce/spring-wrapper-client.git '${project.version}'"
                    }
                }
            }
        }
        */
    }

    post {
        always {
            notifySlack('#bitwise-notify', currentBuild.result)
        }

        aborted {
            deleteDir()
        }

        success {
            deleteDir()
        }
    }
}
