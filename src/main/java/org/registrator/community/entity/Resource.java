package org.registrator.community.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.registrator.community.enumeration.ResourceStatus;

@Entity
@Table(name = "list_of_resouces")
public class Resource implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "resources_id")
    @GeneratedValue
    private Integer resourcesId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "resource_type_id", nullable = false)
    private ResourceType type;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;
    
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "registrator_id", nullable = false)
    private User registrator;
    
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResourceStatus status;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "tome_id", nullable = false)
    private Tome tome;
    
    @Column(name = "reason_inclusion", nullable = false)
    private String reasonInclusion;
    
    //for deleting the resource with its childs
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy="resource")
    public List<Polygon> polygons;
    
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy="resource")
    public List<ResourceDiscreteValue> resourceDiscreteValues;
    
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy="resource")
    public List<ResourceLinearValue> resourceLinearValues;
    
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy="resource")
    public List<Inquiry> inquiries;
    
    
    public Resource() {
        
    }
    
    public Resource(ResourceType type, String identifier, String description, User registrator, Date date,
            String status, Tome tome, String reasonInclusion) {
        this.type = type;
        this.identifier = identifier;
        this.description = description;
        this.registrator = registrator;
        this.date = date;
        this.status = ResourceStatus.valueOf(status.toUpperCase());
        this.tome = tome;
        this.reasonInclusion = reasonInclusion;
    }

    public Integer getResourcesId() {
        return resourcesId;
    }


    public void setResourcesId(Integer resourcesId) {
        this.resourcesId = resourcesId;
    }


    public ResourceType getType() {
        return type;
    }


    public void setType(ResourceType type) {
        this.type = type;
    }


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getRegistrator() {
        return registrator;
    }

    public void setRegistrator(User registrator) {
        this.registrator = registrator;
    }

    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public ResourceStatus getStatus() {
        return status;
    }


    public void setStatus(ResourceStatus status) {
        this.status = status;
    }


    public Tome getTome() {
        return tome;
    }


    public void setTome(Tome tome) {
        this.tome = tome;
    }


    public String getReasonInclusion() {
        return reasonInclusion;
    }


    public void setReasonInclusion(String reasonInclusion) {
        this.reasonInclusion = reasonInclusion;
    }

//    Created for testing
    @Override
    public String toString() {
        return "id: " + getResourcesId()
                + ",typeId: " + getType().getTypeId()
                + ",identifier: " + getIdentifier()
                + ",description: " + getDescription()
                + ",registrator: " + getRegistrator().getUserId()
                + ",date: " + getDate().toString()
                + ",status: " + getStatus().toString()
                + ",tome: " + getTome().getTomeId()
                + ",reason: " + getReasonInclusion();
    }

    @Override
    public int hashCode() {
        return resourcesId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Resource other = (Resource) obj;
        if (resourcesId != other.resourcesId)
            return false;
        return true;
    }
}


