package com.binaryalchemist.programondo.models.login;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeRequest {

	private String oldPassword;
	private String newPassword;
	private long tokenId;
}
