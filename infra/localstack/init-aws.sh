#!/bin/bash
# TransitHub LocalStack Initialization
# Creates S3 bucket and other AWS resources

echo "Initializing LocalStack resources..."

# Create S3 bucket
aws --endpoint-url=http://localhost:4566 s3 mb s3://transithub-documents-dev
aws --endpoint-url=http://localhost:4566 s3api put-bucket-versioning --bucket transithub-documents-dev --versioning-configuration Status=Enabled
aws --endpoint-url=http://localhost:4566 s3api put-bucket-encryption --bucket transithub-documents-dev --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
echo "S3 bucket 'transithub-documents-dev' created and configured"

# Create SNS topics
aws --endpoint-url=http://localhost:4566 sns create-topic --name transithub-notifications
echo "SNS topic 'transithub-notifications' created"

# Create SQS queues
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name transithub-email-queue
echo "SQS queue 'transithub-email-queue' created"

echo "LocalStack initialization complete!"
