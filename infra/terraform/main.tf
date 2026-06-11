terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = { source = "hashicorp/aws", version = "~> 5.0" }
    random = { source = "hashicorp/random", version = "~> 3.5" }
  }
  backend "s3" {
    bucket = "transithub-terraform-state"
    key    = "transithub/terraform.tfstate"
    region = "ap-south-1"
  }
}
provider "aws" {
  region = var.aws_region
  default_tags { tags = { Project = "TransitHub", Environment = var.environment } }
}
