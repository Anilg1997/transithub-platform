resource "aws_iam_role" "eks_cluster" {
  name = "transithub-eks-cluster-${var.environment}"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "eks.amazonaws.com" }
    }]
  })
}
resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  role = aws_iam_role.eks_cluster.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}
resource "aws_eks_cluster" "main" {
  name = "transithub-cluster-${var.environment}"
  role_arn = aws_iam_role.eks_cluster.arn
  version = "1.29"
  vpc_config {
    subnet_ids = concat(aws_subnet.public[*].id, aws_subnet.private[*].id)
    endpoint_private_access = true
    endpoint_public_access = true
  }
  depends_on = [aws_iam_role_policy_attachment.eks_cluster_policy]
}
resource "aws_iam_role" "eks_nodes" {
  name = "transithub-eks-nodes-${var.environment}"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}
resource "aws_iam_role_policy_attachment" "eks_nodes_policy" {
  for_each = toset([
    "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy",
    "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy",
    "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly",
  ])
  role = aws_iam_role.eks_nodes.name
  policy_arn = each.key
}
resource "aws_eks_node_group" "main" {
  cluster_name = aws_eks_cluster.main.name
  node_group_name = "transithub-ng-${var.environment}"
  node_role_arn = aws_iam_role.eks_nodes.arn
  subnet_ids = aws_subnet.private[*].id
  scaling_config {
    desired_size = var.eks_min_nodes
    min_size = var.eks_min_nodes
    max_size = var.eks_max_nodes
  }
  instance_types = [var.node_instance_type]
  tags = { Name = "transithub-ng-${var.environment}" }
  depends_on = [aws_iam_role_policy_attachment.eks_nodes_policy]
}
