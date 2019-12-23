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
      when {
        branch 'release/*'
      }
      steps {
        sh 'cp -r ${WORKSPACE}/target/coderunner.jar /root'
      }
    }

    stage('startup') {
      when {
        branch 'release/*'
      }
      steps {
        sh '/root/daemonize.sh 8090 coderunner.sh'
      }
    }
  }
}