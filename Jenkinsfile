pipeline {
  agent any

  environment {
    CODERUNNER_PATH = '/root/coderunner'
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
        sh 'ssh -i ~/Coderunner.pem ${AWS_DNS} sudo rm ~/coderunner/coderunner.jar'
        sh 'scp -i ~/Coderunner.pem ${WORKSPACE}/target/coderunner.jar ${AWS_DNS}:~/coderunner/'
      }
    }

    stage('startup') {
      when {
        branch 'master'
      }
      steps {
              sh 'ssh -i ~/Coderunner.pem ${AWS_DNS} sudo /home/ubuntu/coderunner/start.sh'
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