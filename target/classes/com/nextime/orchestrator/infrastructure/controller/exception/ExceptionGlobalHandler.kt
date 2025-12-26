package com.nextime.orchestrator.infrastructure.controller.exception

//@ControllerAdvice
//class ExceptionGlobalHandler {
//
//    @ExceptionHandler(ValidationException::class)
//    fun handleException(ex: ValidationException): ResponseEntity<ExceptionDetail> {
//        val details = ExceptionDetail(
//            HttpStatus.BAD_REQUEST.value(),
//            ex.message
//        )
//        return ResponseEntity(details, HttpStatus.BAD_REQUEST)
//    }
//}
