pipeline {
  agent any

  environment {
    CODERUNNER_PATH = '/root/coderunner'
  }

  stages {
    stage('mvn:install') {
      steps {
        withMaven(maven : 'maven') {
          sh 'mvn clean package'
        }
      }
    }

    stage('deploy:prod') {
      steps {
        sh 'cp -r ${WORKSPACE}/target/coderunner.jar ${CODERUNNER_PATH}'
        sh 'chmod +x ${CODERUNNER_PATH}/coderunner.jar'
      }
    }

    stage('startup') {
      steps {
        sh '${CODERUNNER_PATH}/start.sh'
      }
    }
  }
}