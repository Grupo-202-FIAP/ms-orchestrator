output "irsa_role_arn" {
  description = "ARN da role IRSA criada"
  value       = aws_iam_role.ms_orchestrator_irsa.arn
}

output "irsa_role_name" {
  description = "Nome da role IRSA criada"
  value       = aws_iam_role.ms_orchestrator_irsa.name
}
