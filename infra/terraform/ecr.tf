locals {
  service_names = [
    "service-registry", "config-server", "api-gateway",
    "auth-service", "user-service",
    "flight-inventory-service", "flight-booking-service", "flight-status-service",
    "bus-inventory-service", "bus-booking-service", "bus-tracking-service",
    "train-inventory-service", "train-booking-service", "train-availability-service",
    "booking-aggregator-service", "inventory-lock-service",
    "payment-service", "refund-service",
    "document-service", "notification-service",
    "search-service", "analytics-service", "admin-service",
    "audit-service", "fare-engine-service"
  ]
}
resource "aws_ecr_repository" "services" {
  for_each = toset(local.service_names)
  name = "transithub/${each.key}"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration { scan_on_push = true }
  tags = { Name = "transithub-ecr-${each.key}" }
}
resource "aws_ecr_lifecycle_policy" "services" {
  for_each = aws_ecr_repository.services
  repository = each.value.name
  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description = "Keep last 10 images"
      selection = {
        tagStatus = "any"
        countType = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}
