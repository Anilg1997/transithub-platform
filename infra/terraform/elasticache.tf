resource "aws_elasticache_subnet_group" "redis" {
  name = "transithub-redis-${var.environment}"
  subnet_ids = aws_subnet.private[*].id
}
resource "aws_elasticache_replication_group" "redis" {
  replication_group_id = "transithub-redis-${var.environment}"
  description = "TransitHub Redis cluster"
  node_type = "cache.t3.micro"
  port = 6379
  parameter_group_name = "default.redis7"
  subnet_group_name = aws_elasticache_subnet_group.redis.name
  security_group_ids = [aws_security_group.redis.id]
  automatic_failover_enabled = var.environment == "prod"
  num_cache_clusters = var.environment == "prod" ? 2 : 1
  tags = { Name = "transithub-redis-${var.environment}" }
}
resource "aws_security_group" "redis" {
  name = "transithub-redis-sg-${var.environment}"
  vpc_id = aws_vpc.main.id
  ingress {
    from_port = 6379; to_port = 6379; protocol = "tcp"
    security_groups = [aws_security_group.eks_nodes.id]
  }
}
