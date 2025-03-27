package com.pape.timetodo.global.exception;


public enum ExceptionCode {
    SUCCESS("1", 200, "성공"),
	FAIL("-1" , 500, "실패"),

	NON_VALID_PARAMETER("VP001", 400, "파라미터가 유효하지 않습니다."),
	INTERNAL_SERVER_ERROR("ER500", 500, "내부 시스템 오류"),

	NOT_REGIST_ERROR_CODE("ER501", 500, "등록되지 않은 오류 코드"),

	DATA_NOT_FIND("DT001", 400,"데이터를 찾을수 없습니다."),
	DATA_DUPLICATE("DT002", 400, "중복 데이터가 존재합니다."),
	DATA_NO("DT003", 500, "데이터 미존재"),
	EMAIL_DUPLICATE("DT004", 401, "가입된 이메일이 있습니다."),
	EMAIL_NOT_SEND("DT005", 400, "해당 메일로 발신한 메일 내역이 없습니다."),
	DATA_DELETE("DT006", 400, "삭제된 데이터입니다."),

	ALREADY_CERT("CT001", 200, "이미 인증 되어있는 값 입니다."),

	DB_ERROR("DB001", 500, "디비 처리중 오류"),

	TIME_OUT("T001", 400, "인증 시간 초과"),

	WEB_NOT_FOUND("WB001", 404, "찾을 수 없는 URL"),

	FILE_ERROR("F001", 500, "파일 읽어오기 오류"),
	FILE_PARSING_ERROR("F002", 500, "파일 파싱중 오류"),

	NOT_AUTHENTICATION_USER("P001", 400, "인증실패"),
	NOT_AUTHORIZED_USER("P002", 401, "인가되지 않은 사용자"),
	NOT_PERMISSION("P003", 403, "권한이 없는 사용자"),
	USER_DELETE("P004", 400, "탈퇴된 회원"),
	SING_IN_FROM_ANOTHER_DEVICE("P005", 400, "다른기기에서 로그인"),
	NOT_FOUND_USER("P006", 400, "유저 정보를 조회할 수 없습니다."),
	MAIL_NOT_AUTHENTICATION("P007", 400, "메일인증이 필요함"),
	NOT_JOIN_GROUP("P008", 400, "해당 그룹에 속해있지 않습니다."),
	OTHRE_USER_DATA("P009", 403, "다른 유저의 데이터입니다."),
	LOGIN_FAIL("P010", 400, "로그인 실패"),
	

	REFRESH_JWT_EXPIRED("J001", 400, "리프레쉬 만료"),
	INVALID_JWT_SIGN("J002", 400, "잘못된 JWT 서명"),
	EXPRIED_JWT("J003", 400, "만료된 JWT"),
	UNSUPPORTED_JWT("J004", 400, "지원하지 않는 JWT"),
	ERROR_JWT("J005", 400, "잘못된 JWT"),
	NOT_HEADER_JWT("J006", 400, "JWT 토큰이 헤더에 담겨있지 않음"),

	EXTERNAL_API_ERROR("EA500", 500, "외부서버 호출에 실패하였습니다."),
	EXTERNAL_MAIL_SEND("EA501", 500, "메일 발송에 실패했습니다."),

	SYNC_ERROR("S500", 500, "동기화 오류, 로그를 확인하세요."),
	SYNC_ERROR_LOCAL_IDX("S400", 400, "동기화 오류, 기존 인덱스가 없으면 로컬 인덱스는 필수입니다."),
	;

	

	private final String code;

	private final int status;

	private final String message;
	
	private ExceptionCode(String code, int status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}

	public String code() {
		return this.code;
	}

	public String message() {
		return this.message;
	}

	public int status(){
		return this.status;
	}
}
