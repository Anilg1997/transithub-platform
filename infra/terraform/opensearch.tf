resource "aws_opensearch_domain" "search" {
  domain_name = "transithub-search-${var.environment}"
  engine_version = "OpenSearch_2.11"
  cluster_config {
    instance_type = "t3.small.search"
    instance_count = 1
    dedicated_master_enabled = false
  }
  ebs_options {
    ebs_enabled = true
    volume_size = 20
    volume_type = "gp3"
  }
  encrypt_at_rest { enabled = true }
  node_to_node_encryption { enabled = true }
  domain_endpoint_options {
    enforce_https = true
    tls_security_policy = "TLS_1_2"
  }
  access_policies = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = ["es:*"]
      Principal = { AWS = aws_eks_cluster.main.role_arn }
      Effect = "Allow"
      Resource = "arn:aws:es:${var.aws_region}:*:domain/transithub-search-${var.environment}/*"
    }]
  })
  tags = { Name = "transithub-opensearch-${var.environment}" }
}
