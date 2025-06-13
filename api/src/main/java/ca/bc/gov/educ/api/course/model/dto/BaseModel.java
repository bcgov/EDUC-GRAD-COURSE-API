package ca.bc.gov.educ.api.course.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel {
	private String createUser;
	private Date createDate;
	private String updateUser;
	private Date updateDate;
}
