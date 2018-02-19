#!/bin/bash
rm -rf target
mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
status=$?
if [ "$status" != "0" ]; then
    tput setaf 1; echo "Build failed with exit status $status"
    tput sgr0; echo "Exiting."
    exit
fi
ls -lh target/AudioPlayerSkill-1.0-SNAPSHOT-jar-with-dependencies.jar
aws s3 cp --acl public-read target/AudioPlayerSkill-1.0-SNAPSHOT-jar-with-dependencies.jar s3://audioplayerskill
aws lambda update-function-code --function-name AudioPlayerSkillJava --s3-bucket audioplayerskill --s3-key AudioPlayerSkill-1.0-SNAPSHOT-jar-with-dependencies.jar

