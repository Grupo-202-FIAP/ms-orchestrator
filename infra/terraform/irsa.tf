resource "aws_iam_role" "ms_orchestrator_irsa" {
  name = "ms-orchestrator-irsa"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Federated = var.oidc_provider_arn
      }
      Action = "sts:AssumeRoleWithWebIdentity"
      Condition = {
        StringEquals = {
          "${var.oidc_provider_url}:sub" = "system:serviceaccount:default:ms-orchestrator-sa"
        }
      }
    }]
  })
}
