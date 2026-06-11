resource "aws_cloudwatch_log_group" "services" {
  for_each = toset([
    "auth-service", "user-service", "flight-inventory-service", "flight-booking-service",
    "bus-inventory-service", "bus-booking-service", "train-inventory-service",
    "train-booking-service", "payment-service", "notification-service",
    "search-service", "analytics-service", "admin-service"
  ])
  name = "/transithub/${each.key}"
  retention_in_days = 30
}
resource "aws_cloudwatch_metric_alarm" "booking_failure_rate" {
  alarm_name = "transithub-booking-failure-rate-${var.environment}"
  alarm_description = "High booking failure rate detected"
  namespace = "TransitHub/booking-service"
  metric_name = "booking.failure"
  statistic = "Sum"
  period = 300
  evaluation_periods = 2
  threshold = 10
  comparison_operator = "GreaterThanThreshold"
  alarm_actions = []
}
resource "aws_cloudwatch_metric_alarm" "s3_upload_failures" {
  alarm_name = "transithub-s3-upload-failures-${var.environment}"
  namespace = "TransitHub/document-service"
  metric_name = "s3.upload.failure"
  statistic = "Sum"
  period = 300
  evaluation_periods = 2
  threshold = 5
  comparison_operator = "GreaterThanThreshold"
}
