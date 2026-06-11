resource "aws_db_subnet_group" "transithub" {
  name = "transithub-db-subnet-${var.environment}"
  subnet_ids = aws_subnet.private[*].id
}
resource "aws_db_instance" "transithub" {
  identifier = "transithub-${var.environment}"
  engine = "postgres"
  engine_version = "16.3"
  instance_class = var.db_instance_class
  allocated_storage = 100
  storage_encrypted = true
  db_name = "transithub"
  username = "transithub_admin"
  password = random_password.db_password.result
  db_subnet_group_name = aws_db_subnet_group.transithub.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  multi_az = var.environment == "prod"
  backup_retention_period = 30
  backup_window = "03:00-04:00"
  maintenance_window = "sun:05:00-sun:06:00"
  skip_final_snapshot = var.environment != "prod"
  deletion_protection = var.environment == "prod"
  tags = { Name = "transithub-rds-${var.environment}" }
}
resource "random_password" "db_password" { length = 24; special = false }
resource "aws_security_group" "rds" {
  name = "transithub-rds-sg-${var.environment}"
  vpc_id = aws_vpc.main.id
  ingress {
    from_port = 5432; to_port = 5432; protocol = "tcp"
    security_groups = [aws_security_group.eks_nodes.id]
  }
}
resource "aws_security_group" "eks_nodes" {
  name = "transithub-eks-node-sg-${var.environment}"
  vpc_id = aws_vpc.main.id
}
