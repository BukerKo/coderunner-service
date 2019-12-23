pipeline {
  agent any
  stages {
    stage('mvn:install') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('deploy:prod') {
      when {
        branch 'release/*'
      }
      steps {
        sh 'cp -r ${WORKSPACE}/target/**/*.jar /root'
      }
    }

    stage('startup') {
      when {
        branch 'release/*'
      }
      steps {
        sh '/root/daemonize.sh -8090 -coderunner.sh'
      }
    }
  }
}