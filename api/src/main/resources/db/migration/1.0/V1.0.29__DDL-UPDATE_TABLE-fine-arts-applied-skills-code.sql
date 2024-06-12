ALTER TABLE FINE_ARTS_APPLIED_SKILLS_CODE MODIFY DESCRIPTION VARCHAR2(500);
UPDATE FINE_ARTS_APPLIED_SKILLS_CODE
SET DESCRIPTION = CASE
        WHEN FINE_ARTS_APPLIED_SKILLS_CODE = 'A' THEN 'Legacy value used to indicate that a Grade 11 BAA course met the 2-credit Applied Skills requirement for the 1995 program. If submitted by a school for a BAA/FNA course session > 2011-06-30, the rules for "B" apply.'
        WHEN FINE_ARTS_APPLIED_SKILLS_CODE = 'F' THEN 'Legacy value used to indicate that a Grade 11 BAA course met the 2-credit Fine Arts requirement for the 1995 program. If submitted by a school for a Grade 11 BAA/FNA course session > 2011-06-30, the rules for "B" apply.'
        WHEN FINE_ARTS_APPLIED_SKILLS_CODE = 'B' THEN 'Where course session > 2011-06-30, indicates that a Grade 11 BAA/FNA course can be used towards Fine Arts and/or Applied Skills (2004 program) or ADST (2018 and 2023 programs) requirement. Where course session =< 2011-06-30, indicates that a Grade 11 BAA course met the both the 2-credit Applied Skills and 2-credit Fine Arts requirements for the 1995 program.'
    END,
    EXPIRY_DATE = NULL
WHERE 1 = 1;