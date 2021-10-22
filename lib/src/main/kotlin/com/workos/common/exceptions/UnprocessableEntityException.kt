package com.workos.common.exceptions

import com.workos.common.responses.UnprocessableEntityExceptionResponse.EntityError

class UnprocessableEntityException(val errors: List<EntityError>?, val requestId: String) : Exception("UnprocessableEntityException") {
    val status = 422
}
