resource "aws_iam_role" "ms_orchestrator_irsa" {
  name = "ms-orchestrator-irsa"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Federated = data.terraform_remote_state.kubernetes.outputs.cluster_oidc_provider_arn
        }
        Action = "sts:AssumeRoleWithWebIdentity"
        Condition = {
          StringEquals = {
            "${data.terraform_remote_state.kubernetes.outputs.cluster_oidc_provider_url}:sub" = "system:serviceaccount:default:ms-orchestrator-sa"
            "${data.terraform_remote_state.kubernetes.outputs.cluster_oidc_provider_url}:aud" = "sts.amazonaws.com"
          }
        }
      }
    ]
  })
}
