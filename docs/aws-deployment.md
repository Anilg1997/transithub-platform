# TransitHub AWS Deployment Guide

## Prerequisites
1. AWS Free Tier account
2. AWS CLI installed and configured
3. kubectl installed
4. Helm 3 installed

## Deployment Steps
1. Run Terraform: `cd infra/terraform && terraform apply`
2. Build Docker images and push to ECR
3. Deploy to EKS using Helm charts
4. Configure GitHub Actions secrets for CI/CD
5. Push to main branch to trigger automated deployment

See the main README.md for detailed step-by-step instructions.
