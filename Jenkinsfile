pipeline {
  agent any

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
        sh 'cp -r ${WORKSPACE}/target/coderunner.jar /root/coderunner'
        sh 'chmod +x /root/coderunner/coderunner.jar'
      }
    }

    stage('startup') {
      steps {
        sh '/root/coderunner/start.sh'
      }
    }
  }
}