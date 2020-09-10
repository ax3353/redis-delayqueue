pipeline {
  agent any
  stages {
    stage('pull code') {
      parallel {
        stage('pull code') {
          steps {
            git 'https://github.com/ax3353/eventdrive.git'
            echo 'pulling code'
          }
        }

        stage('build') {
          steps {
            sh 'mvn clean package -DskipTest=true'
          }
        }

      }
    }

  }
}