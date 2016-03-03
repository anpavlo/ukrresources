package org.registrator.community.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "territorial_community")
public class TerritorialCommunity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "territorialCommunity_id")
    @GeneratedValue
    private Integer territorialCommunityId;
    
    @Column(name = "name", nullable = false)
    private String name;

    public TerritorialCommunity() {
        
    }
    
    

    public TerritorialCommunity(String name) {
		super();
		this.name = name;
	}



	public Integer getTerritorialCommunityId() {
        return territorialCommunityId;
    }
    
    public void setTerritorialCommunityId(Integer territorialCommunityId) {
        this.territorialCommunityId = territorialCommunityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }  
}