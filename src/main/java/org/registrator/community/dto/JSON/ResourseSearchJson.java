package org.registrator.community.dto.JSON;

import java.util.List;

public class ResourseSearchJson {
    private List<Integer> discreteParamsIds;
    private List<String> discreteParamsCompares;
    private List<Double> discreteParamsValues;
    private List<Integer> linearParamsIds;
    private List<Double> linearParamsValues;
    private Integer resourceTypeId;
    private Integer page;

    public List<Integer> getDiscreteParamsIds() {
        return discreteParamsIds;
    }

    public void setDiscreteParamsIds(List<Integer> discreteParamsIds) {
        this.discreteParamsIds = discreteParamsIds;
    }

    public List<String> getDiscreteParamsCompares() {
        return discreteParamsCompares;
    }

    public void setDiscreteParamsCompares(List<String> discreteParamsCompares) {
        this.discreteParamsCompares = discreteParamsCompares;
    }

    public List<Double> getDiscreteParamsValues() {
        return discreteParamsValues;
    }

    public void setDiscreteParamsValues(List<Double> discreteParamsValues) {
        this.discreteParamsValues = discreteParamsValues;
    }

    public List<Integer> getLinearParamsIds() {
        return linearParamsIds;
    }

    public void setLinearParamsIds(List<Integer> linearParamsIds) {
        this.linearParamsIds = linearParamsIds;
    }

    public List<Double> getLinearParamsValues() {
        return linearParamsValues;
    }

    public void setLinearParamsValues(List<Double> linearParamsValues) {
        this.linearParamsValues = linearParamsValues;
    }

    public Integer getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Integer resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String toString() {
        return "=============================================\n"
                +"discreteParamsIds: " + discreteParamsIds.toString() + "\n"
                +"discreteParamsCompares: " + discreteParamsCompares.toString() + "\n"
                +"discreteParamsValues: " + discreteParamsValues.toString() + "\n"
                +"linearParamsIds: " + linearParamsIds.toString() + "\n"
                +"linearParamsValues: " + linearParamsValues.toString() + "\n"
                +"resourceTypeId: " + resourceTypeId.toString() +"\n"
                +"==============================================";
    }
}
