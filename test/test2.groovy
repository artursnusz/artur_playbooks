import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

def ROLE_NAME = "dmk-hpc"
def SERVER_ENV = ["test", "development", "acceptance", "production"];

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
                    "{\"key\": \"${SERVER_ENV[3]}\",\"value\": \"${SERVER_ENV[3]}\"}" +
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
        for(int i=0; i<SERVER_ENV.size(); i++) {
            
            stage("${SERVER_ENV[i]}") {
                if (params['Server Environment'].indexOf("${SERVER_ENV[i]}") >= 0) {
                    echo "Test";
                } else {
                    echo "Nothing to do in the stage - stage not selected to run";
                    Utils.markStageSkippedForConditional("${SERVER_ENV[i]}")
                }
            }
        }
    }
}
