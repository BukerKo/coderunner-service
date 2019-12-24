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
        sh 'cp -r ${WORKSPACE}/target/coderunner.jar /root'
      }
    }

    stage('startup') {
      steps {
        sh '/root/daemonize.sh 8090 coderunner.sh'
      }
    }
  }
}