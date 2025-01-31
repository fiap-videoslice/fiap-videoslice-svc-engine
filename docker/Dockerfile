##
# This version of the Dockerfile is to be integrated with the Pipeline and it depends on
# the application being pre-packaged in the ./temp_libs directory at the repository
#
# (for reference, the commands below run at the repo root will prepare the dependencies)
# mvn install dependency:copy-dependencies
# mkdir ./docker/temp_libs && cp ./modules/web-app/target/architect-burgers-webapi-*.jar ./docker/temp_libs/ && cp ./modules/web-app/target/lib/* ./docker/temp_libs/
#
#
FROM openjdk:21-jdk

RUN adduser apprunner -D

RUN mkdir /app

COPY ../temp_libs/* /app/
COPY ./entrypoint.sh /app/

RUN chown -R apprunner /app
RUN chmod a+x /app/entrypoint.sh

USER apprunner

ENV videoslice.integration.sqs.sqsEndpoint=http://localhost:4566
ENV videoslice.integration.s3.s3Endpoint=http://localhost:4566
ENV videosliceapi.integration.aws.overrideAwsEndpoint=http://localhost:4566
ENV videoslice.integration.sqs.videosToBeProcessedQueueName=videosToBeProcessed
ENV videoslice.integration.sqs.videosToBeProcessedQueueUrl=http://localhost:4566/000000000000/videosToBeProcessed
ENV videoslice.integration.sqs.videoStatusQueueName=videoStatus
ENV videoslice.integration.sqs.videoStatusQueueUrl=http://localhost:4566/000000000000/videoStatus
ENV videoslice.integration.s3.bucketName=videoslice-job-requests

EXPOSE 8091

CMD ["/app/entrypoint.sh"]
