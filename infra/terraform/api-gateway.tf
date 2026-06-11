resource "aws_apigatewayv2_api" "main" {
  name = "transithub-api-${var.environment}"
  protocol_type = "HTTP"
  cors_configuration {
    allow_origins = ["https://${var.domain_name}", "http://localhost:4200"]
    allow_methods = ["GET", "POST", "OPTIONS"]
    allow_headers = ["content-type", "authorization", "x-service"]
    allow_credentials = true
  }
}
resource "aws_apigatewayv2_stage" "main" {
  api_id = aws_apigatewayv2_api.main.id
  name = "\$default"
  auto_deploy = true
}
resource "aws_apigatewayv2_integration" "eks_alb" {
  api_id = aws_apigatewayv2_api.main.id
  integration_type = "HTTP_PROXY"
  integration_uri = "http://internal-${var.environment}-alb-xxxxxxxxx.ap-south-1.elb.amazonaws.com"
  integration_method = "ANY"
  connection_type = "VPC_LINK"
  connection_id = aws_apigatewayv2_vpc_link.main.id
}
resource "aws_apigatewayv2_vpc_link" "main" {
  name = "transithub-vpc-link-${var.environment}"
  subnet_ids = aws_subnet.private[*].id
  security_group_ids = [aws_security_group.api_gateway.id]
}
resource "aws_security_group" "api_gateway" {
  name = "transithub-api-gw-sg-${var.environment}"
  vpc_id = aws_vpc.main.id
  egress {
    from_port = 0; to_port = 0; protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
resource "aws_apigatewayv2_route" "graphql" {
  api_id = aws_apigatewayv2_api.main.id
  route_key = "POST /graphql"
  target = "integrations/${aws_apigatewayv2_integration.eks_alb.id}"
}
resource "aws_apigatewayv2_route" "ws" {
  api_id = aws_apigatewayv2_api.main.id
  route_key = "GET /graphql-ws"
  target = "integrations/${aws_apigatewayv2_integration.eks_alb.id}"
}
