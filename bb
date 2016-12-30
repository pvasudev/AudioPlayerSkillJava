#!/bin/bash
mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
aws s3 cp --acl public-read target/AudioPlayerSkill-1.0-SNAPSHOT-jar-with-dependencies.jar s3://audioplayerskill
aws lambda update-function-code --function-name AudioPlayerSkillJava --s3-bucket audioplayerskill --s3-key AudioPlayerSkill-1.0-SNAPSHOT-jar-with-dependencies.jar

