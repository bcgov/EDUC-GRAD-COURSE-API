-- EXAMINABLE_COURSE definition

CREATE TABLE "EXAMINABLE_COURSE"
(
    "EXAMINABLE_COURSE_ID" RAW(16)      DEFAULT SYS_GUID()   NOT NULL ENABLE,
    "COURSE_CODE"          VARCHAR2(7)                       NOT NULL ENABLE,
    "COURSE_LEVEL"         VARCHAR2(3),
    "EXAMINABLE_START"     DATE,
    "EXAMINABLE_END"       DATE,
    "OPTIONAL_START"       DATE,
    "OPTIONAL_END"         DATE,
    "CREATE_USER"          VARCHAR2(32) DEFAULT USER         NOT NULL ENABLE,
    "CREATE_DATE"          DATE         DEFAULT SYSTIMESTAMP NOT NULL ENABLE,
    "UPDATE_USER"          VARCHAR2(32) DEFAULT USER         NOT NULL ENABLE,
    "UPDATE_DATE"          DATE         DEFAULT SYSTIMESTAMP NOT NULL ENABLE,
    CONSTRAINT "EXAMINABLE_COURSE_ID_PK" PRIMARY KEY ("EXAMINABLE_COURSE_ID")
        USING INDEX TABLESPACE "API_GRAD_IDX" ENABLE
) SEGMENT CREATION IMMEDIATE
    NOCOMPRESS LOGGING
    TABLESPACE "API_GRAD_DATA" NO INMEMORY;
