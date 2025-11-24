package com.chpark.restaurant.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: String,
    val message: String
) {
    MEMBER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "M001", "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "회원 정보를 찾을 수 없습니다."),
    MEMBER_WRONG_CREDENTIAL(HttpStatus.BAD_REQUEST, "M003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "M004", "이메일은 비어 있을 수 없습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "M005", "올바르지 않은 이메일 형식입니다."),

    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 JWT 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A002", "만료된 JWT 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "A003", "지원하지 않는 JWT 토큰입니다."),
    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED, "A004", "JWT 클레임이 비어 있습니다."),
    TOKEN_WRONG_TYPE(HttpStatus.UNAUTHORIZED, "A005", "잘못된 타입의 JWT 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A006", "JWT 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A007", "유효하지 않은 리프레시 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A008", "인증이 필요합니다."),

    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "예약 정보를 찾을 수 없습니다."),
    RESERVATION_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "R002", "이미 취소된 예약입니다."),
    RESERVATION_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "R003", "해당 예약에 대한 권한이 없습니다."),
    RESERVATION_TIME_SLOT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "R004", "선택한 시간대에 예약할 수 없습니다."),
    RESERVATION_INVALID_TIME_SLOT(HttpStatus.BAD_REQUEST, "R005", "잘못된 시간대입니다."),
    RESERVATION_EXCEEDS_CAPACITY(HttpStatus.BAD_REQUEST, "R006", "예약 인원이 예약 대상의 수용 인원을 초과합니다."),
    RESERVATION_ONLY_WAITING_CAN_BE_CONFIRMED(HttpStatus.BAD_REQUEST, "R007", "대기 중인 예약만 확정할 수 있습니다."),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RS001", "예약대상 정보를 찾을 수 없습니다."),
    RESOURCE_INACTIVE(HttpStatus.CONFLICT, "RS002", "비활성화된 예약대상입니다."),
    RESOURCE_CODE_DUPLICATED(HttpStatus.CONFLICT, "RS003", "이미 사용 중인 예약대상 코드입니다."),

    COMMON_INVALID(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    COMMON_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "리소스를 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    COMMON_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 오류가 발생했습니다."),
}