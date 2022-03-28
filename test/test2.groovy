stage('Sign Code') {
    steps {
        script {
            try {
                pwd()
                sh pip.sh
            }
            catch (err) {                                        
                unstable(message: "${STAGE_NAME} is unstable")
            }
        }
    }
}
