package org.registrator.community.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.registrator.community.dto.JSON.ResourceNumberDTOJSON;

public class UserDTO extends UserBasicInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    
    private String role;

    private String status;

    @Valid
    private ResourceNumberDTOJSON resourceNumberDTOJSON;

    private WillDocumentDTO willDocument;
    private List<String> otherDocuments;

    public UserDTO() {

    }

    public UserDTO(String firstName, String lastName, String middleName, String role, String login, String email,
            String status, AddressDTO address, PassportDTO passport) {
        super(firstName, lastName, middleName, email, address, passport);
        this.login = login;
        this.role = role;
        this.status = status;
    }

    public UserDTO(String firstName, String lastName, String middleName, String role, String login, String email,
            String status, AddressDTO address, PassportDTO passport, String territorialCommunity,
            ResourceNumberDTOJSON resourceNumberDTOJSON) {
        this(firstName, lastName, middleName, role, login, email, status, address, passport);
        this.resourceNumberDTOJSON = resourceNumberDTOJSON;
    }  

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResourceNumberDTOJSON getResourceNumberDTOJSON() {
        return resourceNumberDTOJSON;
    }

    public void setResourceNumberDTOJSON(ResourceNumberDTOJSON resourceNumberDTOJSON) {
        this.resourceNumberDTOJSON = resourceNumberDTOJSON;
    }

    public WillDocumentDTO getWillDocument() {
        return willDocument;
    }

    public void setWillDocument(WillDocumentDTO willDocument) {
        this.willDocument = willDocument;
    }

    public List<String> getOtherDocuments() {
        return otherDocuments;
    }

    public void setOtherDocuments(List<String> otherDocuments) {
        this.otherDocuments = otherDocuments;
    }

}