package io.julius.chow.domain.model

enum class OrderState(val value: String) {
    ACTIVE("active"), PROCESSED("processed"), REJECTED("rejected")
}