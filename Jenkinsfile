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
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Integration Test') {
            steps {
                sh 'mvn failsafe:integration-test failsafe:verify'
            }
        }
		stage('Build docker image'){
            steps {
                sh 'docker build -t hasithajayan/reppido:1.0 .'
            }
        }
        stage('Publish docker image'){
            steps {
				withCredentials([string(credentialsId: 'dockerhub-pwd', variable: 'dockerhubpwd')]) {
					sh 'docker login -u hasithajayan@gmail.com  -p ${dockerhubpwd}'
				}
				sh 'docker push hasithajayan/reppido:1.0'
            }
        }
        stage('Deploy to AWS'){
            steps {
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'reppido-aws', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                	sh 'aws ecs update-service --cluster reppido-cluster --service reppido-ecs-service --force-new-deployment'
                }
            }
        }
    }
}