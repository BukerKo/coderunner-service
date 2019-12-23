pipeline {
  agent any

  stages {
    stage('mvn:install') {
      steps {
        withMaven(maven : 'apache-maven-3.6.1') {
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
        sh 'cd /root; ls -la'
      }
    }
  }
}