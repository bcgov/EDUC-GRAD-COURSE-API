UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Aegrotat', DESCRIPTION='Aegrotat Standing. The student has been granted exemption from writing a Provincial Exam due to unpredictable circumstances that render a student unable to write an exam even at a future session.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='A';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Bogus', DESCRIPTION='When the school reported the same school percentage in more than one session, but the student did not retake the course.For example, if a school reported 65% for both January and April for a given examinable course, TRAX would recognize a duplication, considered the second superfluous and mark that record’s classroom mark with a B.The B course would be ignored for mixing and matching.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='B';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Challenge', DESCRIPTION='Used for students wishing to obtain a scholarship score by challenging a provincially examinable course. After the 1997-98 implementation of the new Course Challenge Policy, this practice was phased out.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2001-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='C';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Deferred', DESCRIPTION='Indicates that the student was granted a deferral for writing the exam because of illness, accident or personal difficulty.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='D';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Special Case F', DESCRIPTION='Unknown. Not applied after 1986.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('1986-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='F';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='None', DESCRIPTION='Indicates no special case for the provincial exam.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='N';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Disqualified', DESCRIPTION='Student is believed to have breached one or more rules during a provincial exam and any result was disqualified.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='Q';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Rewrite', DESCRIPTION='Student re-wrote the exam but did not re-take the course.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='R';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Verified', DESCRIPTION='Student re-took the course and received the exact same school percent as last time. Initially flagged as "B", but changed to V when the student’s classroom mark was confirmed.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('2020-06-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='V';

UPDATE EXAM_SPECIAL_CASE_CODE
SET LABEL='Special Case W', DESCRIPTION='Unknown. Not applied after 1986.',
    EFFECTIVE_DATE=TO_DATE('1984-07-01', 'YYYY-MM-DD'), EXPIRY_DATE=TO_DATE('1986-08-30', 'YYYY-MM-DD'), UPDATE_USER='API_COURSE' , UPDATE_DATE=SYSTIMESTAMP
WHERE EXAM_SPECIAL_CASE_CODE='W';

