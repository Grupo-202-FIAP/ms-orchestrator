resource "aws_ecr_repository" "ms_orchestrator" {
  name                 = "ms-orchestrator"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
