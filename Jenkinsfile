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
                sh '''
                    echo "Running tests with Maven..."
                    mvn test -Dmaven.test.failure.ignore=true
                    
                    echo "Current directory structure:"
                    ls -R
                    
                    echo "Looking for test reports in target directories:"
                    for dir in $(find . -type d -name "target"); do
                        echo "Checking $dir/surefire-reports"
                        ls -la "$dir/surefire-reports" 2>/dev/null || true
                        echo "Checking $dir/failsafe-reports"
                        ls -la "$dir/failsafe-reports" 2>/dev/null || true
                        echo "Checking $dir/test-results"
                        ls -la "$dir/test-results" 2>/dev/null || true
                    done
                    
                    echo "Checking Maven test results:"
                    find . -name "TEST-*.xml" -type f
                '''
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml, **/target/test-results/**/*.xml'
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
        success {
            echo 'Pipeline de desarrollo completado exitosamente!'
            cleanWs()
        }
        failure {
            echo 'Pipeline de desarrollo falló!'
            cleanWs()
        }
    }
}