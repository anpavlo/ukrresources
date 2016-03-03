package org.registrator.community.dto;

import java.util.List;

import org.springframework.util.AutoPopulatingList;

public class PoligonAreaDTO {
    
    private List<PointAreaDTO> points = new AutoPopulatingList<PointAreaDTO>(PointAreaDTO.class);
    
    public PoligonAreaDTO() {
        
    }

    public List<PointAreaDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointAreaDTO> points) {
        this.points = points;
    }
}
