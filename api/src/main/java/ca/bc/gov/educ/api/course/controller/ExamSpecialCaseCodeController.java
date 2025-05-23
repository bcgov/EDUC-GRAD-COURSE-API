package ca.bc.gov.educ.api.course.controller;

import ca.bc.gov.educ.api.course.model.dto.ExamSpecialCaseCode;
import ca.bc.gov.educ.api.course.service.ExamSpecialCaseCodeService;
import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.PermissionsConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(EducCourseApiConstants.GRAD_COURSE_API_ROOT_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for Student Course Data.", description = "This API is for Reading Student Course data.", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_STUDENT_COURSE_DATA"})})
public class ExamSpecialCaseCodeController {

    final ExamSpecialCaseCodeService examSpecialCaseCodeService;

    public ExamSpecialCaseCodeController(ExamSpecialCaseCodeService examSpecialCaseCodeService) {
        this.examSpecialCaseCodeService = examSpecialCaseCodeService;
    }

    @GetMapping(EducCourseApiConstants.EXAM_SPECIAL_CASE_CODES_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_EXAM_SPECIAL_CASE_CODE)
    @Operation(summary = "Find All Exam Special Case Codes", description = "Find All Exam Special Case Codes", tags = {"Exam Special Case Code"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<ExamSpecialCaseCode>> getExamSpecialCaseCodes() {
        return ResponseEntity.ok().body(examSpecialCaseCodeService.getExamSpecialCaseCodeList());
    }

    @GetMapping(EducCourseApiConstants.EXAM_SPECIAL_CASE_CODE_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_EXAM_SPECIAL_CASE_CODE)
    @Operation(summary = "Find Exam Special Case Code", description = "Find Exam Special Case Code", tags = {"Exam Special Case Code"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
    public ResponseEntity<ExamSpecialCaseCode> getExamSpecialCaseCode(@PathVariable String examSpecialCaseCode) {
        return ResponseEntity.ok().body(examSpecialCaseCodeService.getExamSpecialCaseCode(examSpecialCaseCode));
    }
}
