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
        sh 'cp -r ${WORKSPACE}/target/coderunner.jar /root'
      }
    }

    stage('startup') {
      when {
        branch 'release/*'
      }
      steps {
        sh 'cd /root; ls -la'
      }
    }
  }
}