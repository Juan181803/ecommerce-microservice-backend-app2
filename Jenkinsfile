pipeline {
    agent any
    
    tools {
        maven 'MAVEN'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Check Environment') {
            steps {
                sh '''
                    echo "Current directory: $(pwd)"
                    echo "PATH: $PATH"
                    which mvn || echo "Maven not found in PATH"
                    mvn -v || echo "Maven command failed"
                    ls -la
                '''
            }
        }
        
        stage('Build Dev') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                sh 'docker-compose -f compose.yml build'
            }
        }
        
        stage('Deploy to Dev') {
            steps {
                sh 'docker-compose -f compose.yml up -d'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline de desarrollo completado exitosamente!'
        }
        failure {
            echo 'Pipeline de desarrollo falló!'
        }
    }
}