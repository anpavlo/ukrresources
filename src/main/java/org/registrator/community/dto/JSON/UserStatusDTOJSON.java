package org.registrator.community.dto.JSON;

public class UserStatusDTOJSON {
	private String login;
	private String status;

	public UserStatusDTOJSON(String login, String status) {
		this.login = login;
		this.status = status;
	}

	public UserStatusDTOJSON() {
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
