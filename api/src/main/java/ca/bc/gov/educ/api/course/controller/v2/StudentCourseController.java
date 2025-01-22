package ca.bc.gov.educ.api.course.controller.v2;

import ca.bc.gov.educ.api.course.model.dto.StudentCourse;
import ca.bc.gov.educ.api.course.service.StudentCourseService;
import ca.bc.gov.educ.api.course.util.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController("StudentCourseControllerV2")
@Slf4j
@RequestMapping(EducCourseApiConstants.STUDENT_COURSE_URL_MAPPING_V2)
@OpenAPIDefinition(info = @Info(title = "API for Student Course Data.", description = "This API is for Reading Student Course data.", version = "2"),
        security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_STUDENT_COURSE_DATA"})})
public class StudentCourseController {

    @SuppressWarnings("unused")
    private static final String BEARER = "Bearer ";

    StudentCourseService studentCourseService;

    GradValidation validation;

    ResponseHelper response;

    @Autowired
    public StudentCourseController(StudentCourseService studentCourseService, GradValidation validation, ResponseHelper response) {
        this.studentCourseService = studentCourseService;
        this.validation = validation;
        this.response = response;
    }

    @GetMapping(EducCourseApiConstants.GET_STUDENT_COURSES_BY_STUDENT_ID_MAPPING)
    @PreAuthorize(PermissionsConstants.READ_GRAD_COURSE)
    @Operation(summary = "Find All Student Courses by Student ID", description = "Get All Student Courses by Student ID", tags = {"Student Courses"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "204", description = "NO CONTENT"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "422", description = "UNPROCESSABLE CONTENT")})
    public ResponseEntity<List<StudentCourse>> getStudentCoursesByStudentID(
            @PathVariable UUID studentID, @RequestParam(value = "sortForUI", required = false, defaultValue = "false") boolean sortForUI) {
        validation.requiredField(studentID, "Student ID");
        if (validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.debug("#Get All Student Course by Student ID: {}", studentID);
        List<StudentCourse> studentCourseList = studentCourseService.getStudentCourses(studentID, sortForUI);
        if (studentCourseList.isEmpty()) {
            return response.NO_CONTENT();
        }
        return response.GET(studentCourseList);
    }

    @PostMapping
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Create a Student Course", description = "Create a Student Course", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "422", description = "UNPROCESSABLE CONTENT")
    })
    public ResponseEntity<ApiResponseModel<StudentCourse>> createStudentCourse(
            @NotNull @Valid @RequestBody StudentCourse studentCourse) {
        validation.requiredField(studentCourse.getStudentID(), "Student ID");
        validation.requiredField(studentCourse.getCourseID(), "Course ID");
        if (validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        StudentCourse result = studentCourseService.saveStudentCourse(studentCourse);
        return response.CREATED(result, StudentCourse.class);
    }

    @PutMapping
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Update a Student Course", description = "Update a Student Course", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "422", description = "UNPROCESSABLE CONTENT")
    })
    public ResponseEntity<ApiResponseModel<StudentCourse>> updateStudentCourse(
            @NotNull @Valid @RequestBody StudentCourse studentCourse) {
        validation.requiredField(studentCourse.getId(), "Student Course ID");
        validation.requiredField(studentCourse.getStudentID(), "Student ID");
        validation.requiredField(studentCourse.getCourseID(), "Course ID");
        if (validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        StudentCourse result = studentCourseService.saveStudentCourse(studentCourse);
        return response.UPDATED(result, StudentCourse.class);
    }

    @DeleteMapping(EducCourseApiConstants.STUDENT_COURSE_ID_MAPPING)
    @PreAuthorize(PermissionsConstants.UPDATE_GRAD_COURSE_RESTRICTION)
    @Operation(summary = "Delete a Student Course by Student Course ID", description = "Delete a Student Course by Student Course ID", tags = { "Student Courses" })
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "422", description = "UNPROCESSABLE CONTENT")
    })
    public ResponseEntity<Void> deleteStudentCourse(@PathVariable UUID studentCourseID) {
        validation.requiredField(studentCourseID, "Student Course ID");
        if (validation.hasErrors()) {
            validation.stopOnErrors();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return response.DELETE(studentCourseService.deleteStudentCourse(studentCourseID));
    }

}
