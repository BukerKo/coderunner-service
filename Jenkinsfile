pipeline {
  agent any

  environment {
    KEY_LOCATION = '~/Coderunner.pem'
    CODERUNNER_PATH = '/home/ubuntu/coderunner'
    AWS_DNS = 'ubuntu@ec2-18-222-208-138.us-east-2.compute.amazonaws.com'
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
      when {
        branch 'master'
      }
      steps {
        sh 'ssh -i ${KEY_LOCATION} ${AWS_DNS} sudo rm ${CODERUNNER_PATH}/coderunner.jar'
        sh 'scp -i ${KEY_LOCATION} ${WORKSPACE}/target/coderunner.jar ${AWS_DNS}:${CODERUNNER_PATH}/'
      }
    }

    stage('startup') {
      when {
        branch 'master'
      }
      steps {
              sh 'ssh -i ${KEY_LOCATION} ${AWS_DNS} sudo ${CODERUNNER_PATH}/start.sh'
      }
    }
  }

  post {
    success {
      telegramSend 'coderunner-service build status: success'
    }
    failure {
      telegramSend 'coderunner-service build status: failure'
    }
  }
}