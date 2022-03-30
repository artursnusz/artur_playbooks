import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

def ROLE_NAME = "dmk-hpc"
def SERVER_ENV = ["test", "development", "acceptance", "production", "test2", "test3"];

node {
    properties([
        disableConcurrentBuilds(),
        parameters([
            // requires plugin "Custom Checkbox Parameter"
            checkboxParameter(
                name: 'Server Environment', 
                format: 'JSON',
                pipelineSubmitContent: "{\"CheckboxParameter\": [" +
                    "{\"key\": \"${SERVER_ENV[0]}\",\"value\": \"${SERVER_ENV[0]}\"}," +
                    "{\"key\": \"${SERVER_ENV[1]}\",\"value\": \"${SERVER_ENV[1]}\"}," +
                    "{\"key\": \"${SERVER_ENV[2]}\",\"value\": \"${SERVER_ENV[2]}\"}," +
                    "{\"key\": \"${SERVER_ENV[3]}\",\"value\": \"${SERVER_ENV[3]}\"}," +
                    "{\"key\": \"${SERVER_ENV[4]}\",\"value\": \"${SERVER_ENV[4]}\"}," +
                    "{\"key\": \"${SERVER_ENV[5]}\",\"value\": \"${SERVER_ENV[5]}\"}" +
                "]}", 
                description: 'Select for which environment pipeline should run'
            )
        ]),
        pipelineTriggers([
            // schedule execution every 30 minutes, required plugin "Parameterized Scheduler"
            parameterizedCron('H/30 * * * * %Server Environment=' + SERVER_ENV.join(","))
        ])
    ])
    // requires plugin "AnsiColor"
    ansiColor('xterm') {
        def isError = false;
        for(int i=0; i<SERVER_ENV.size(); i++) {
        try{
            stage("${SERVER_ENV[i]}") {
                if (params['Server Environment'].indexOf("${SERVER_ENV[i]}") >= 0) {
                    if(SERVER_ENV[i] == "test" || SERVER_ENV[i] == "test2" ){
                        echo SERVER_ENV[i]
                        sh 'exit 1';
                        echo currentBuild.result + "test"
                    }
                    else{
                        echo "test"
                    }
                } else {
                    echo "Nothing to do in the stage - stage not selected to run";
                    Utils.markStageSkippedForConditional("${SERVER_ENV[i]}")
                }
            }
        }
        catch(e){
            isError = true;
            def info = e.toString();
            echo e.toString() + "${SERVER_ENV[i]}"
            echo info;
            if(info.indexOf("script returned") >= 0)
            {
                echo "dupa";
                echo e.toString();
                Utils.markStageSkippedForConditional("${SERVER_ENV[i]}")
                //isError = true
            }
            //else{
            //Utils.markStageSkippedForConditional("${SERVER_ENV[i]}")
            //}

        }
        if(isError){
            currentBuild.result = "SUCCESS"

        }
        else{
            currentBuild.result = "FAILURE"
        }
        }
    }
}
