output "vpc_id" { value = aws_vpc.main.id }
output "eks_cluster_endpoint" { value = aws_eks_cluster.main.endpoint }
output "eks_cluster_name" { value = aws_eks_cluster.main.name }
output "rds_endpoint" { value = aws_db_instance.transithub.endpoint }
output "rds_db_name" { value = aws_db_instance.transithub.db_name }
output "s3_bucket_name" { value = aws_s3_bucket.documents.id }
output "opensearch_endpoint" { value = aws_opensearch_domain.search.endpoint }
output "redis_endpoint" { value = aws_elasticache_replication_group.redis.primary_endpoint_address }
output "ecr_repository_urls" { value = { for k, r in aws_ecr_repository.services : k => r.repository_url } }
output "api_gateway_url" { value = aws_apigatewayv2_api.main.api_endpoint }
