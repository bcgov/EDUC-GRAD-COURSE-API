package ca.bc.gov.educ.api.course.model.entity.v2;

import ca.bc.gov.educ.api.course.util.EducCourseApiConstants;
import ca.bc.gov.educ.api.course.util.ThreadLocalStateUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class BaseEntity {

	@Column(name = "CREATE_USER", updatable = false)
	String createUser;

	@Column(name = "CREATE_DATE", updatable = false)
	@PastOrPresent
	LocalDateTime createDate;

	@Column(name = "UPDATE_USER")
	String updateUser;

	@Column(name = "UPDATE_DATE")
	@PastOrPresent
	LocalDateTime updateDate;
	
	@PrePersist
	protected void onCreate() {
		initUserInfo();
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onPersist() {
		initUserInfo();
		this.createDate = (this.createDate == null) ? LocalDateTime.now() : this.createDate;
		this.updateDate = LocalDateTime.now();
	}

	private void initUserInfo() {
		String user = ThreadLocalStateUtil.getCurrentUser();
		if(this.updateUser == null){
			this.updateUser = (StringUtils.isBlank(user)) ? EducCourseApiConstants.DEFAULT_UPDATED_BY : user;
		}
		this.createUser = (StringUtils.isBlank(createUser) && StringUtils.isBlank(user)) ? EducCourseApiConstants.DEFAULT_CREATED_BY : user;
	}

}
