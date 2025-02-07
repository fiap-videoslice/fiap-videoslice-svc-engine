# fiap-videoslice-svc-engine   

Performs the processing of frame extraction. Communicates with the Web API service via queues.


# Setup of local dev environment

Start LocalStack

    docker run --rm -it -d -p 4566:4566 -p 4510-4559:4510-4559 localstack/localstack

Examples of AWS Commands in LocalStack

    aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs list-queues
    
    aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs create-queue --queue-name videoslice_job_requests
    aws --endpoint-url=http://localhost:4566 --region us-east-1 sqs create-queue --queue-name videoslice_job_status

    aws --endpoint-url=http://localhost:4566 --region us-east-1 s3api list-buckets

    aws --endpoint-url=http://localhost:4566 --region us-east-1 s3api create-bucket --bucket videoslice-job-requests
    aws --endpoint-url=http://localhost:4566 --region us-east-1 s3api create-bucket --bucket videoslice-job-results

#### Run locally

    mvn spring-boot:run
