resource "aws_iam_policy" "ms_orchestrator_ssm" {
  name = "ms-orchestrator-ssm-policy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "ssm:GetParameter",
        "ssm:GetParameters"
      ]
      Resource = "arn:aws:ssm:${var.aws_region}:056075870573:parameter/*"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_attach" {
  role       = aws_iam_role.ms_orchestrator_irsa.name
  policy_arn = aws_iam_policy.ms_orchestrator_ssm.arn
}
