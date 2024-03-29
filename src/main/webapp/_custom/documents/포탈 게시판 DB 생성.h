-- 게시판
CREATE TABLE PORTAL.PT_BOARD (
	  BRD_ID INTEGER NOT NULL
	, BRD_NM VARCHAR2(255) NOT NULL
	, BRD_DESC VARCHAR2(1000) 
	, BRD_TYPE VARCHAR2(255) NOT NULL
	, BRD_ADM_USR VARCHAR2(2000) 
	, BRD_CRT_AUTH VARCHAR2(2000) 
	, BRD_VIEW_AUTH VARCHAR2(2000) 
	, POST_TYPE_YN VARCHAR2(1) NOT NULL
	, POST_TAG_YN VARCHAR2(1) NOT NULL 
	, POST_FILE_YN VARCHAR2(1) NOT NULL
	, POST_CMNT_YN VARCHAR2(1) NOT NULL
	, POST_CMNT_FILE_YN VARCHAR2(1) NOT NULL
	, POST_POPUP_YN VARCHAR2(1) NOT NULL
	, POST_SECRET_YN VARCHAR2(1) NOT NULL
	, POST_FIX_YN VARCHAR2(1) NOT NULL
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);
ALTER TABLE PORTAL.PT_BOARD ADD CONSTRAINT BOARD_PK PRIMARY KEY(BRD_ID);
COMMENT ON TABLE PORTAL.PT_BOARD IS '게시판';

COMMENT ON TABLE PORTAL.PT_BOARD IS '게시판';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_ID IS '게시판 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_NM IS '게시판 이름';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_DESC IS '게시판 설명';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_TYPE IS '게시판 타입';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_ADM_USR IS '게시판 관리자';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_CRT_AUTH IS '게시판 작성 권한';
COMMENT ON COLUMN PORTAL.PT_BOARD.BRD_VIEW_AUTH IS '게시판 보기 권한';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_TYPE_YN IS '게시물 분류 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_TAG_YN IS '게시물 TAG 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_FILE_YN IS '게시물 파일 첨부 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_CMNT_YN IS '게시물 댓글 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_CMNT_FILE_YN IS '게시물 댓글 파일 첨부 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_POPUP_YN IS '게시물 팝업 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_SECRET_YN IS '게시물 비밀 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.POST_FIX_YN IS '게시물 상단 고정 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD.MOD_USR_ID IS '수정자 ID';

--기본 게시판 추가(공지사항, FAQ)
INSERT INTO PORTAL.PT_BOARD
(BRD_ID, BRD_NM, BRD_DESC, BRD_TYPE, BRD_CRT_AUTH, POST_TYPE_YN, POST_TAG_YN, POST_FILE_YN, POST_CMNT_YN, POST_CMNT_FILE_YN, POST_POPUP_YN, POST_SECRET_YN, POST_FIX_YN, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES(1, '공지사항', '공지사항 게시판입니다', 'COMMON', '[]', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', SYSDATE, 'Administrator');
INSERT INTO PORTAL.PT_BOARD
(BRD_ID, BRD_NM, BRD_DESC, BRD_TYPE, BRD_CRT_AUTH, POST_TYPE_YN, POST_TAG_YN, POST_FILE_YN, POST_CMNT_YN, POST_CMNT_FILE_YN, POST_POPUP_YN, POST_SECRET_YN, POST_FIX_YN, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES(2, 'FAQ', '자주 사용하는 질문입니다.', 'FAQ', '[]', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'N', SYSDATE, 'Administrator');
INSERT INTO PORTAL.PT_BOARD
(BRD_ID, BRD_NM, BRD_DESC, BRD_TYPE, BRD_CRT_AUTH, POST_TYPE_YN, POST_TAG_YN, POST_FILE_YN, POST_CMNT_YN, POST_CMNT_FILE_YN, POST_POPUP_YN, POST_SECRET_YN, POST_FIX_YN, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES(3, '자료실', '자료실 게시판입니다.', 'COMMON', '[]', 'N', 'N', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', SYSDATE, 'Administrator');



--데이터 복사
INSERT INTO PORTAL.PT_BOARD 
SELECT
	  BRD_ID
	, BRD_NM
	, BRD_DESC
	, BRD_TYPE
	, BRD_ADM_USR
	, BRD_CRT_AUTH
	, BRD_VIEW_AUTH
	, 'N'
	, 'N' 
	, POST_FILE_YN
	, POST_CMNT_YN
	, POST_CMNT_FILE_YN
	, POST_POPUP_YN
	, POST_SECRET_YN
	, POST_FIX_YN
	, DEL_YN
	, CRT_DT_TM
	, CRT_USR_ID
	, MOD_DT_TM
	, MOD_USR_ID
FROM PORTAL.PT_BOARD_20240216
;



-- 게시물
CREATE TABLE PORTAL.PT_BOARD_POST (
	  POST_ID INTEGER NOT NULL
	, BRD_ID INTEGER NOT NULL
	, POST_TYPE VARCHAR2(255) 
	, POST_TITLE VARCHAR2(255) 
	, POST_CONTENT CLOB 
	, POST_VIEW_AUTH VARCHAR2(2000) 
	, POPUP_YN VARCHAR2(1) 
	, POPUP_START_DT_TM TIMESTAMP 
	, POPUP_END_DT_TM TIMESTAMP 
	, SECRET_YN VARCHAR2(1) 
	, FIX_YN VARCHAR2(1) 
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);
ALTER TABLE PORTAL.PT_BOARD_POST ADD CONSTRAINT BOARD_POST_PK PRIMARY KEY(POST_ID, BRD_ID);
COMMENT ON TABLE PORTAL.PT_BOARD_POST IS '게시물';

COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POST_ID IS '게시물 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.BRD_ID IS '게시판 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POST_TYPE IS '게시물 분류';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POST_TITLE IS '게시물 제목';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POST_CONTENT IS '게시물 내용';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POST_VIEW_AUTH IS '게시물 보기 권한';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POPUP_YN IS '팝업 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POPUP_START_DT_TM IS '팝업 시작 일자';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.POPUP_END_DT_TM IS '팝업 종료 일자';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.SECRET_YN IS '비밀 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.FIX_YN IS '고정 가능 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST.MOD_USR_ID IS '수정자 ID';


--데이터 복사
INSERT INTO PORTAL.PT_BOARD_POST 
SELECT
	  POST_ID
	, BRD_ID
	, ''
	, POST_TITLE
	, POST_CONTENT
	, POST_VIEW_AUTH
	, POPUP_YN
	, POPUP_START_DT_TM
	, POPUP_END_DT_TM
	, SECRET_YN
	, FIX_YN
	, DEL_YN
	, CRT_DT_TM
	, CRT_USR_ID
	, MOD_DT_TM
	, MOD_USR_ID
FROM PORTAL.PT_BOARD_POST_20240216
;




-- 파일
CREATE TABLE PORTAL.PT_BOARD_POST_FILE (
	  FILE_ID INTEGER NOT NULL
	, POST_ID INTEGER 
	, FILE_TYPE VARCHAR2(100) 
	, PARENT_CMNT_ID INTEGER 
	, SRV_FILE_PATH VARCHAR2(255) NOT NULL
	, SRV_FILE_NM VARCHAR2(255) NOT NULL
	, ORG_FILE_NM VARCHAR2(255) NOT NULL
	, FILE_EXT VARCHAR2(255) NOT NULL
	, FILE_SIZE INTEGER NOT NULL
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);
ALTER TABLE PORTAL.PT_BOARD_POST_FILE ADD CONSTRAINT BOARD_POST_FILE_PK PRIMARY KEY(FILE_ID, POST_ID);
COMMENT ON TABLE PORTAL.PT_BOARD_POST_FILE IS '파일';

COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.FILE_ID IS '파일 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.POST_ID IS '게시물 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.FILE_TYPE IS '파일 분류';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.PARENT_CMNT_ID IS '상위 댓글 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.SRV_FILE_PATH IS '서버 파일 경로';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.SRV_FILE_NM IS '서버 파일 명';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.ORG_FILE_NM IS '원본 파일 명';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.FILE_EXT IS '파일 확장자';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.FILE_SIZE IS '파일 사이즈';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_BOARD_POST_FILE.MOD_USR_ID IS '수정자 ID';




--포탈 로그
CREATE TABLE PORTAL.PT_PORTAL_LOG (
	  LOG_ID INTEGER NOT NULL
	, USR_ID VARCHAR2(20) 
	, USR_NM VARCHAR2(100) 
	, USR_DEPT_ID VARCHAR2(20) 
	, USR_DEPT_NM VARCHAR2(100) 
	, USR_POS_ID VARCHAR2(20) 
	, USR_POS_NM VARCHAR2(100) 
	, USR_IP VARCHAR2(50) 
	, SCRN_ID VARCHAR2(100) 
	, SCRN_DET_ID VARCHAR2(100) 
	, USR_ACTN VARCHAR2(20) 
	, DET_INFO_MAP VARCHAR2(2000) 
	, CRT_DT_TM TIMESTAMP NOT NULL
);
ALTER TABLE PORTAL.PT_PORTAL_LOG ADD CONSTRAINT PORTAL_LOG_PK PRIMARY KEY(LOG_ID);
COMMENT ON TABLE PORTAL.PT_PORTAL_LOG IS '포탈 로그';

COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.LOG_ID IS '로그 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_ID IS '사용자 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_NM IS '사용자 이름';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_DEPT_ID IS '사용자 부서 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_DEPT_NM IS '사용자 부서 명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_POS_ID IS '사용자 직위 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_POS_NM IS '사용자 직위 명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_IP IS '사용자 IP';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.SCRN_ID IS '화면 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.SCRN_DET_ID IS '화면 상세 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.USR_ACTN IS '사용자 행동';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.DET_INFO_MAP IS '상세 정보';
COMMENT ON COLUMN PORTAL.PT_PORTAL_LOG.CRT_DT_TM IS '생성 일시';

--포탈 로그 시퀀스
CREATE SEQUENCE PORTAL.PT_PORTAL_LOG_SEQ
	INCREMENT BY 1
	START WITH 1
	MINVALUE 1
	NOMAXVALUE
	NOCYCLE
	NOCACHE
	NOORDER
;




--포탈 관리자
CREATE TABLE PORTAL.PT_PORTAL_ADMIN (
	  ADM_CD VARCHAR2(100) NOT NULL
	, USR_ID VARCHAR2(20) NOT NULL
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);
ALTER TABLE PORTAL.PT_PORTAL_ADMIN ADD CONSTRAINT PORTAL_LOG_PK PRIMARY KEY(ADM_CD, USR_ID);
COMMENT ON TABLE PORTAL.PT_PORTAL_ADMIN IS '포탈 관리자';

COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.ADM_CD IS '관리자 코드';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.USR_ID IS '사용자 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_ADMIN.MOD_USR_ID IS '수정자 ID';

INSERT INTO PORTAL.PT_PORTAL_ADMIN (ADM_CD, USR_ID, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_SYSTEM_ADMIN', 'mksong', 'N', SYSDATE, 'Administrator');




--포탈 코드 분류
CREATE TABLE PORTAL.PT_PORTAL_CODE_TYPE (
	  CD_TYPE_ENG_NM VARCHAR2(100) NOT NULL
	, CD_TYPE_ENG_ABRV_NM VARCHAR2(100) 
	, CD_TYPE_KOR_NM VARCHAR2(200) 
	, CD_TYPE_DESC VARCHAR2(1000) 
	, CD_TYPE_ORD INTEGER 
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);

COMMENT ON TABLE PORTAL.PT_PORTAL_CODE_TYPE IS '포탈 코드 분류 테이블';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CD_TYPE_ENG_NM IS '코드 분류 영문명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CD_TYPE_ENG_ABRV_NM IS '코드 분류 영문 약어명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CD_TYPE_KOR_NM IS '코드 분류 한글명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CD_TYPE_DESC IS '코드 분류 설명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CD_TYPE_ORD IS '코드 분류 순서';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE_TYPE.MOD_USR_ID IS '수정자 ID';

INSERT INTO PORTAL.PT_PORTAL_CODE_TYPE (CD_TYPE_ENG_NM, CD_TYPE_KOR_NM, CD_TYPE_DESC, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_BRD_TYPE', '게시판 분류', '게시판 분류 코드입니다.', 'N', SYSDATE, 'Administrator');




--포탈 코드 테이블
CREATE TABLE PORTAL.PT_PORTAL_CODE (
	  CD_TYPE_ENG_NM VARCHAR2(100) NOT NULL
	, CD_ENG_NM VARCHAR2(100) NOT NULL
	, CD_ENG_ABRV_NM VARCHAR2(100) 
	, CD_KOR_NM VARCHAR2(200) 
	, CD_DESC VARCHAR2(1000) 
	, CD_ORD INTEGER 
	, DEL_YN VARCHAR2(1) NOT NULL
	, CRT_DT_TM TIMESTAMP NOT NULL
	, CRT_USR_ID VARCHAR2(20) NOT NULL
	, MOD_DT_TM TIMESTAMP 
	, MOD_USR_ID VARCHAR2(20) 
);

COMMENT ON TABLE PORTAL.PT_PORTAL_CODE IS '포탈 코드 테이블';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_TYPE_ENG_NM IS '코드 분류 영문명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_ENG_NM IS '코드 영문명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_ENG_ABRV_NM IS '코드 영문 약어명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_KOR_NM IS '코드 한글명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_DESC IS '코드 설명';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CD_ORD IS '코드 순서';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.DEL_YN IS '삭제 여부';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CRT_DT_TM IS '생성 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.CRT_USR_ID IS '생성자 ID';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.MOD_DT_TM IS '수정 일시';
COMMENT ON COLUMN PORTAL.PT_PORTAL_CODE.MOD_USR_ID IS '수정자 ID';

INSERT INTO PORTAL.PT_PORTAL_CODE (CD_TYPE_ENG_NM, CD_ENG_NM, CD_KOR_NM, CD_ORD, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_BRD_TYPE', 'BRD_TYPE_1', '메뉴얼', 1, 'N', SYSDATE, 'Administrator');

INSERT INTO PORTAL.PT_PORTAL_CODE (CD_TYPE_ENG_NM, CD_ENG_NM, CD_KOR_NM, CD_ORD, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_BRD_TYPE', 'BRD_TYPE_2', '용어사전', 2, 'N', SYSDATE, 'Administrator');

INSERT INTO PORTAL.PT_PORTAL_CODE (CD_TYPE_ENG_NM, CD_ENG_NM, CD_KOR_NM, CD_ORD, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_BRD_TYPE', 'BRD_TYPE_3', '동영상교육', 3, 'N', SYSDATE, 'Administrator');

INSERT INTO PORTAL.PT_PORTAL_CODE (CD_TYPE_ENG_NM, CD_ENG_NM, CD_KOR_NM, CD_ORD, DEL_YN, CRT_DT_TM, CRT_USR_ID)
VALUES('PORTAL_BRD_TYPE', 'BRD_TYPE_4', '지점안내', 4, 'N', SYSDATE, 'Administrator');