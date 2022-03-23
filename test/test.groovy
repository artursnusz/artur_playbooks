// Jenkinsfile (Scripted Pipeline)
node("master"){ // node/agent
  stage('Stage 1') {
    echo 'Hello World Artur' // echo Hello World
  }
  stage('Stage 2') {
    echo 'Hello World Artur' // echo Hello World
  }
}
