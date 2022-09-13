package com.mysite.sbb.answer;

import javax.validation.constraints.NotEmpty;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {
	@NotEmpty(message="내용을 필수입력 항목입니다.")
	private String content;

}
