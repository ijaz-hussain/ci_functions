def call(dockerRepoName, imageName) {
    pipeline { 
    agent any  
    stages {
        stage('Build') { 
            steps { 
                sh 'pip install -r requirements.txt' 
            } 
        } 
        stage('Python Lint') { 
            steps { 
                sh 'pylint-fail-under --fail_under 5.0 *.py' 
            } 
        } 
        stage('Package') { 
            when { 
                expression { env.GIT_BRANCH == 'origin/main' } 
            } 
            steps { 
                withCredentials([string(credentialsId: 'ijazhussain', variable: 'TOKEN')]) { 
                    sh "docker login -u 'ijazhussain' -p '$TOKEN' docker.io" 
                    sh "docker build -t ${dockerRepoName}:latest --tag ijazhussain/${dockerRepoName}:${imageName} ." 
                    sh "docker push ijazhussain/${dockerRepoName}:${imageName}" 
                } 
            } 
        }
    }
}
}
