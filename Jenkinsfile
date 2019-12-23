pipeline {
  agent {
     docker {
       image 'maven:3-alpine'
       args '-v /root/.m2:/root/.m2'
     }
  }

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
        sh '/root/daemonize.sh'
      }
    }
  }
}