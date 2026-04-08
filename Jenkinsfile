pipeline {
    agent any

    tools {
        maven 'Maven_3.9'
        jdk 'JDK_17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/hasjay/reppido.git'
            }
        }
		stage('Build') {
            steps {
                dir('reppido') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        stage('Unit Test') {
            steps {
                dir('reppido') {
                    sh 'mvn clean test'
                }
            }
        }
        stage('Integration Test') {
            steps {
                dir('reppido') {
                    sh 'mvn failsafe:integration-test failsafe:verify'
                }
            }
        }
    }
}