variable "aws_region" { default = "ap-south-1" description = "AWS region" }
variable "environment" { default = "dev" description = "Environment name" }
variable "vpc_cidr" { default = "10.0.0.0/16" }
variable "public_subnet_cidrs" { default = ["10.0.1.0/24", "10.0.2.0/24"] }
variable "private_subnet_cidrs" { default = ["10.0.10.0/24", "10.0.11.0/24"] }
variable "db_instance_class" { default = "db.t3.medium" }
variable "node_instance_type" { default = "t3.medium" }
variable "eks_min_nodes" { default = 2 }
variable "eks_max_nodes" { default = 5 }
variable "domain_name" { default = "transithub.com" }
