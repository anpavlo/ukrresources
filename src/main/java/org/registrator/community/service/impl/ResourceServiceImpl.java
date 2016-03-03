package org.registrator.community.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import org.registrator.community.dao.DiscreteParameterRepository;
import org.registrator.community.dao.LinearParameterRepository;
import org.registrator.community.dao.PolygonRepository;
import org.registrator.community.dao.ResourceDiscreteValueRepository;
import org.registrator.community.dao.ResourceLinearValueRepository;
import org.registrator.community.dao.ResourceNumberRepository;
import org.registrator.community.dao.ResourceRepository;
import org.registrator.community.dao.ResourceTypeRepository;
import org.registrator.community.dao.TomeRepository;
import org.registrator.community.dao.UserRepository;
import org.registrator.community.dto.*;
import org.registrator.community.dto.JSON.PointJSON;
import org.registrator.community.dto.JSON.PolygonJSON;
import org.registrator.community.dto.JSON.ResourseSearchJson;
import org.registrator.community.entity.*;
import org.registrator.community.enumeration.ResourceStatus;
import org.registrator.community.service.InquiryService;
import org.registrator.community.service.ResourceDiscreteValueService;
import org.registrator.community.service.ResourceLinearValueService;
import org.registrator.community.service.ResourceService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private Logger logger;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private TomeRepository tomeRepository;

    @Autowired
    private ResourceTypeRepository resourceTypeRepository;

    @Autowired
    private PolygonRepository polygonRepository;

    @Autowired
    private ResourceLinearValueRepository linearValueRepository;

    @Autowired
    private ResourceDiscreteValueRepository discreteValueRepository;

    @Autowired
    private LinearParameterRepository linearParameterRepository;

    @Autowired
    private DiscreteParameterRepository discreteParameterRepository;

    @Autowired
    private ResourceDiscreteValueService resourceDiscreteValueService;

    @Autowired
    private ResourceLinearValueService resourceLinearValueService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private ResourceNumberRepository resourceNumberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method parse the resourceDTO into entity objects and save them into
     * database
     * 
     * @param resourceDTO
     * @param registrator
     * @return
     */
    @Override
    @Transactional
    public ResourceDTO addNewResource(ResourceDTO resourceDTO, String ownerLogin, User registrator) {
        logger.info("Method addNewResource");
        logger.info("Owner Login -" + ownerLogin);
        logger.info("Registrator Login -" + registrator.getLogin());

        /* form the resource entity and save in into database */
        Resource resourceEntity = parseToResourseEntity(resourceDTO, registrator);
        resourceEntity = resourceRepository.save(resourceEntity);

        /* sava all additional parameters of given resource */
        saveResourceParameters(resourceDTO, resourceEntity);

        // save data in the table inquiry_list
        if (!ownerLogin.isEmpty()) {
            inquiryService.addInputInquiry(ownerLogin, resourceEntity, registrator);
        }

        /*
         * increment registration number of resource for authenticated registrar if needed
         */
        if(resourceEntity.getIdentifier().equals(getRegistrationNumber(registrator.getLogin()))){           
            incrementRegistrationNumber(registrator.getLogin());
        }
        return findByIdentifier(resourceEntity.getIdentifier());
    }

    /**
     * Find the resource with given identifier and form resourceDTO object
     * 
     * @param identifier
     * @return resourseDTO if exist
     */
    @Override
    public ResourceDTO findByIdentifier(String identifier) {

        logger.info("Method findByIdentifier");
        Resource resourceEntity = resourceRepository.findByIdentifier(identifier);

        if (resourceEntity != null) {
            logger.info("Resource with identifier " + identifier + " was found");

            ResourceDTO resourceDTO = formResourceDTO(resourceEntity);

            return resourceDTO;

        } else {
            logger.info("Resource with identifier " + identifier + " was not found in database");
            return null;
        }

    }

    @Override
    public List<Resource> findByType(ResourceType type) {
        return resourceRepository.findByType(type);
    }

    @Override
    public long count() {
        return resourceRepository.count();
    }

    @Override
    public Set<String> getDescriptionBySearchTag(String searchTag) {
        return resourceRepository.findDescriptionsLikeProposed(searchTag);
    }

    @Override
    public Set<Resource> getAllByAreaLimits(Double minLat, Double maxLat, Double minLng, Double maxLng, String resType, Integer page) {
        Set<Resource> resources = new HashSet<>();
        Pageable pageable = new PageRequest(page, 200);
        List<Polygon> polygons = polygonRepository.findByLimits(minLat, maxLat, minLng, maxLng, pageable);
        for (Polygon polygon : polygons) {
            if ("all".equals(resType)) {
                resources.add(polygon.getResource());
            } else if (resType.equals(polygon.getResource().getType().getTypeName())) {
                resources.add(polygon.getResource());
            }
        }

        return resources;
    }

    @Override
    public Set<Resource> getAllByPoint(Double lat, Double lng, Integer page) {
        Set<Resource> resources = new HashSet<>();
        Pageable pageable = new PageRequest(page, 200);
        List<Polygon> polygons = polygonRepository.findByPoint(lat, lng, pageable);
        for (Polygon polygon : polygons) {
            resources.add(polygon.getResource());
        }
        return resources;
    }

    @Override
    public ParameterSearchResultDTO getAllByParameters(ResourseSearchJson parameters) {
        ParameterSearchResultDTO result = new ParameterSearchResultDTO();

        JPAQuery query = new JPAQuery(entityManager);
        QResource qResource = QResource.resource;
        BooleanExpression expression = null;
        BooleanExpression rdvExpression = null;
        BooleanExpression rlvExpression = null;
        int pageSize = 200;
        int pageOffset = parameters.getPage()*pageSize;

        if (parameters.getDiscreteParamsIds().size() > 0) {
            rdvExpression = resourceDiscreteValueExpression(
                    parameters.getDiscreteParamsIds(),
                    parameters.getDiscreteParamsValues(),
                    parameters.getDiscreteParamsCompares());
        }

        if (parameters.getLinearParamsIds().size() > 0) {
            rlvExpression = resourceLinearValueExpression(
                    parameters.getLinearParamsIds(),
                    parameters.getLinearParamsValues());
        }

        if (rdvExpression != null) {
            expression = rdvExpression;
        }
        if (rlvExpression != null) {
            if (expression == null) {
                expression = rlvExpression;
            }
            else {
                expression = expression.and(rlvExpression);
            }
        }

        query = query.from(qResource).where(expression);

        long count = query.count();
        List<Resource> resources = query.limit(pageSize).offset(pageOffset).list(qResource);

        result.setCount(count);
        result.setResources(resources);

        return result;
    }

    @Override
    public List<PolygonJSON> createPolygonJSON(Resource resource, int i) {
        List<PolygonJSON> polygonsJSON = new ArrayList<>();

        List<Polygon> polygons = polygonRepository.findByResource(resource);

        for (Polygon polygon : polygons) {
            PolygonJSON polygonJSON = new PolygonJSON();
            List<PointJSON> points = new ArrayList<>();
            Gson gson = new Gson();
            List<PointDTO> pointDTOs = gson.fromJson(polygon.getCoordinates(), new TypeToken<List<PointDTO>>() {}.getType());

            for (PointDTO pointDTO: pointDTOs) {
                PointJSON point = new PointJSON();
                point.setLatitude(pointDTO.getLat());
                point.setLongitude(pointDTO.getLng());
                points.add(point);
            }

            polygonJSON.setResourceDescription(resource.getDescription());
            polygonJSON.setIdentifier(resource.getIdentifier());
            polygonJSON.setResourceType(resource.getType().getTypeName());
            polygonJSON.setDate(new SimpleDateFormat("dd.MM.yyyy").format(resource.getDate()));
            polygonJSON.setDT_RowId("row" + i);
            polygonJSON.setPoints(points);

            polygonsJSON.add(polygonJSON);
            i++;
        }
        return polygonsJSON;
    }

    @Override
    public String getRegistrationNumber(String login) {
        final int MAXIMAL_NUMBER_LENGTH = 4;
        User user = userRepository.findUserByLogin(login);
        ResourceNumber resourceNumber = resourceNumberRepository.findResourceNumberByUser(user);
        if (resourceNumber != null) {
            int numberOfDigits = resourceNumber.getNumber().toString().length();
            StringBuilder resourceNumberPattern = new StringBuilder();
            for (int i = numberOfDigits; i < MAXIMAL_NUMBER_LENGTH; i++) {
                resourceNumberPattern.append("0");
            }
            resourceNumberPattern.append(resourceNumber.getNumber());

            return resourceNumber.getRegistratorNumber() + resourceNumberPattern.toString();
        }

        return null;
    }

    /**
     * Method create the Resource object from given resourceDTO
     * 
     * @param resourceDTO
     * @param registrator
     * @return
     */

    private Resource parseToResourseEntity(ResourceDTO resourceDTO, User registrator) {
        Resource resourceEntity = new Resource();
        resourceEntity.setIdentifier(resourceDTO.getIdentifier());
        resourceEntity.setDescription(resourceDTO.getDescription());
        resourceEntity.setReasonInclusion(resourceDTO.getReasonInclusion());
        resourceEntity.setTome(tomeRepository.findTomeByRegistrator(registrator));
        resourceEntity.setRegistrator(registrator);
        resourceEntity.setStatus(ResourceStatus.ACTIVE);
        ResourceType resourceType = resourceTypeRepository.findByName(resourceDTO.getResourceType());
        resourceEntity.setType(resourceType);
        resourceEntity.setDate(resourceDTO.getDate());
        return resourceEntity;
    }

    /**
     * Save additional parameters of resource entity in database
     * 
     * @param resourceDTO
     * @param resourceEntity
     */
    private void saveResourceParameters(ResourceDTO resourceDTO, Resource resourceEntity) {
        /* save list of area points */
        for (PoligonAreaDTO poligonAreaDTO : resourceDTO.getResourceArea().getPoligons()) {

            Polygon polygonEntity = getPolygonEntity(resourceEntity, poligonAreaDTO);
            polygonEntity.setCoordinates(createCoordinatesJson(poligonAreaDTO));
            polygonEntity = polygonRepository.save(polygonEntity);
        }

        /* save list of resource linear values if exist */
        if (!resourceDTO.getResourceLinear().isEmpty()) {
            logger.info("save linear values of resource");
            List<ResourceLinearValue> resourceLinearValues = parseToLinearValueList(resourceDTO, resourceEntity);
            linearValueRepository.save(resourceLinearValues);
        }

        /* save list of resource discrete values if exist */
        if (!resourceDTO.getResourceDiscrete().isEmpty()) {
            logger.info("save discrete values of resource");
            List<ResourceDiscreteValue> resourceDiscreteValues = parseToDiscreteValueList(resourceDTO, resourceEntity);
            discreteValueRepository.save(resourceDiscreteValues);
        }
    }

    /**
     * Create Polygon object from poligonAreaDTO
     * 
     * @param resourceEntity
     * @param poligonAreaDTO
     * @return Polygon
     */
    private Polygon getPolygonEntity(Resource resourceEntity, PoligonAreaDTO poligonAreaDTO) {
        Polygon polygonEntity = new Polygon();
        Double minLat = 90.0;
        Double maxLat = -90.0;
        Double minLng = 180.0;
        Double maxLng = -180.0;

        for (PointAreaDTO point : poligonAreaDTO.getPoints()) {
            if (point.getOrderNumber() != 0) {
                if (minLat > point.getDecimalLatitude()) {
                    minLat = point.getDecimalLatitude();
                }
                if (maxLat < point.getDecimalLatitude()) {
                    maxLat = point.getDecimalLatitude();
                }
                if (minLng > point.getDecimalLongitude()) {
                    minLng = point.getDecimalLongitude();
                }
                if (maxLng < point.getDecimalLongitude()) {
                    maxLng = point.getDecimalLongitude();
                }
            }
        }

        polygonEntity.setMinLat(minLat);
        polygonEntity.setMaxLat(maxLat);
        polygonEntity.setMinLng(minLng);
        polygonEntity.setMaxLng(maxLng);
        polygonEntity.setResource(resourceEntity);

        return polygonEntity;
    }

    /**
     * Create list of Area objects from poligonAreaDTO
     * 
     * @param poligonAreaDTO
     * @return List<Area>
     */

    private String createCoordinatesJson(PoligonAreaDTO poligonAreaDTO) {
        String coordinates;
        List<PointDTO> pointDTOs = new ArrayList<>();
        for (PointAreaDTO point : poligonAreaDTO.getPoints()) {
            if (point.getOrderNumber() != 0) {
                PointDTO pointDTO = new PointDTO();
                pointDTO.setLat(point.getDecimalLatitude());
                pointDTO.setLng(point.getDecimalLongitude());
                pointDTOs.add(pointDTO);
            }
        }
        Gson gson = new Gson();
        coordinates = gson.toJson(pointDTOs);
        return coordinates;
    }

    /**
     * Create list of ResourceDiscreteValue objects given in resourceDTO
     * 
     * @param resourceDTO
     * @param resourceEntity
     * @return List<ResourceDiscreteValue>
     */
    private List<ResourceDiscreteValue> parseToDiscreteValueList(ResourceDTO resourceDTO, Resource resourceEntity) {
        List<ResourceDiscreteValue> resourceDiscreteValues = new ArrayList<ResourceDiscreteValue>();
        for (ResourceDiscreteValueDTO discValueDTO : resourceDTO.getResourceDiscrete()) {
            DiscreteParameter discreteParameter = discreteParameterRepository
                    .findByResourceAndName(discValueDTO.getDiscreteParameterDescription(), resourceEntity.getType());
            for (ValueDiscreteDTO valueDiscrete : discValueDTO.getValueDiscretes()) {
                ResourceDiscreteValue discreteValue = new ResourceDiscreteValue();
                discreteValue.setResource(resourceEntity);
                discreteValue.setDiscreteParameter(discreteParameter);
                discreteValue.setValue(valueDiscrete.getValue());
                discreteValue.setComment(valueDiscrete.getComment());
                resourceDiscreteValues.add(discreteValue);
            }
        }
        return resourceDiscreteValues;
    }

    /**
     * Create list of ResourceLinearValue objects given in resourceDTO
     * 
     * @param resourceDTO
     * @param resourceEntity
     * @return List<ResourceLinearValue>
     */
    private List<ResourceLinearValue> parseToLinearValueList(ResourceDTO resourceDTO, Resource resourceEntity) {
        List<ResourceLinearValue> resourceLinearValues = new ArrayList<ResourceLinearValue>();
        for (ResourceLinearValueDTO linValueDTO : resourceDTO.getResourceLinear()) {
            LinearParameter linParameter = linearParameterRepository
                    .findByResourceAndName(linValueDTO.getLinearParameterDescription(), resourceEntity.getType());
            for (SegmentLinearDTO segment : linValueDTO.getSegments()) {
                ResourceLinearValue linearValue = new ResourceLinearValue();
                linearValue.setResource(resourceEntity);
                linearValue.setLinearParameter(linParameter);
                linearValue.setMinValue(segment.getBegin());
                linearValue.setMaxValue(segment.getEnd());
                resourceLinearValues.add(linearValue);
            }
        }
        return resourceLinearValues;
    }

    /**
     * Method return ResourseDTO object with all fields based on resourceEntity
     * 
     * @param resourceEntity
     * @return resourceDTO
     */
    private ResourceDTO formResourceDTO(Resource resourceEntity) {
        /* fill standard resource fields */
        ResourceDTO resourceDTO = fillBasicParametersResourceDTO(resourceEntity);

        /* fill linear values of resource */
        resourceDTO = fillLinearValuesDTO(resourceDTO, resourceEntity);

        /* fill discrete values of resource */
        resourceDTO = fillDiscreteValuesDTO(resourceDTO, resourceEntity);

        /* fill area values of resource */
        resourceDTO = fillAreaDTO(resourceDTO, resourceEntity);
        return resourceDTO;
    }

    /**
     * Method create ResourceDTO with basics parameters from resoureEntity
     * 
     * @param resourceEntity
     * @return ResourceDTO
     */
    private ResourceDTO fillBasicParametersResourceDTO(Resource resourceEntity) {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setDescription(resourceEntity.getDescription());
        resourceDTO.setDate(resourceEntity.getDate());
        resourceDTO.setIdentifier(resourceEntity.getIdentifier());
        resourceDTO.setReasonInclusion(resourceEntity.getReasonInclusion());
        resourceDTO.setRegistratorName(
                resourceEntity.getRegistrator().getFirstName() + " " + resourceEntity.getRegistrator().getMiddleName()
                        + " " + resourceEntity.getRegistrator().getLastName());
        resourceDTO.setTomeIdentifier(resourceEntity.getTome().getIdentifier());
        resourceDTO.setResourceType(resourceEntity.getType().getTypeName());
        return resourceDTO;
    }

    /**
     * Fill LinearValues list in ResourceDTO object
     * 
     * @param resourceDTO
     * @param resourceEntity
     * @return ResourceDTO
     */
    private ResourceDTO fillLinearValuesDTO(ResourceDTO resourceDTO, Resource resourceEntity) {
        /* find linear parameters of resource type */
        List<LinearParameter> linearParameters = linearParameterRepository.findByResourceType(resourceEntity.getType());
        /* find linear values of given resource */
        List<ResourceLinearValue> linearValues = linearValueRepository.findByResource(resourceEntity);
        List<ResourceLinearValueDTO> resLinDTOs = new ArrayList<ResourceLinearValueDTO>();
        for (LinearParameter lp : linearParameters) {
            ResourceLinearValueDTO resLinDTO = new ResourceLinearValueDTO();
            resLinDTO.setLinearParameterDescription(lp.getDescription());
            resLinDTO.setLinearParameterUnit(lp.getUnitName());
            List<SegmentLinearDTO> segments = new ArrayList<SegmentLinearDTO>();
            for (ResourceLinearValue linearValue : linearValues) {
                if (linearValue.getLinearParameter().getDescription().equals(lp.getDescription())) {
                    SegmentLinearDTO segment = new SegmentLinearDTO();
                    segment.setBegin(linearValue.getMinValue());
                    segment.setEnd(linearValue.getMaxValue());
                    segments.add(segment);
                }
            }
            resLinDTO.setSegments(segments);
            resLinDTOs.add(resLinDTO);
        }
        resourceDTO.setResourceLinear(resLinDTOs);
        return resourceDTO;
    }

    /**
     * Fill DiscreteValues list in ResourceDTO object
     * 
     * @param resourceDTO
     * @param resourceEntity
     * @return ResourceDTO
     */
    private ResourceDTO fillDiscreteValuesDTO(ResourceDTO resourceDTO, Resource resourceEntity) {
        /* find discrete parameters of resource type */
        List<DiscreteParameter> discreteParameters = discreteParameterRepository
                .findAllByResourceType(resourceEntity.getType());
        /* find discrete values of given resource */
        List<ResourceDiscreteValue> discreteValues = discreteValueRepository.findByResource(resourceEntity);
        List<ResourceDiscreteValueDTO> resDiscDTOs = new ArrayList<ResourceDiscreteValueDTO>();
        for (DiscreteParameter dp : discreteParameters) {
            ResourceDiscreteValueDTO resDisDTO = new ResourceDiscreteValueDTO();
            resDisDTO.setDiscreteParameterDescription(dp.getDescription());
            resDisDTO.setDiscreteParameterUnit(dp.getUnitName());

            List<ValueDiscreteDTO> valuediscretes = new ArrayList<>();
            for (ResourceDiscreteValue dv : discreteValues) {
                if (dv.getDiscreteParameter().getDiscreteParameterId().equals(dp.getDiscreteParameterId())) {
                    ValueDiscreteDTO valueDiscrete = new ValueDiscreteDTO();
                    valueDiscrete.setValue(dv.getValue());
                    valueDiscrete.setComment(dv.getComment());
                    valuediscretes.add(valueDiscrete);
                }

            }
            resDisDTO.setValueDiscretes(valuediscretes);
            resDiscDTOs.add(resDisDTO);
        }
        resourceDTO.setResourceDiscrete(resDiscDTOs);
        return resourceDTO;
    }

    /**
     * Fill Area in ResourceDTO object
     * 
     * @param resourceDTO
     * @param resourceEntity
     * @return ResourceDTO
     */
    private ResourceDTO fillAreaDTO(ResourceDTO resourceDTO, Resource resourceEntity) {
        List<Polygon> polygons = polygonRepository.findByResource(resourceEntity);
        /* fill additional resource fields */
        ResourceAreaDTO resourceArea = new ResourceAreaDTO();
        List<PoligonAreaDTO> poligonsDTO = new ArrayList<PoligonAreaDTO>();

        for (Polygon polygon : polygons) {
//            List<Area> areas = new ArrayList<>();
//            areas.addAll(areaRepository.findByPolygon(polygon));

            Gson gson = new Gson();

            PoligonAreaDTO poligon = new PoligonAreaDTO();
            List<PointAreaDTO> pointDTOs = new ArrayList<>();
            List<PointDTO> coordinates = gson.fromJson(polygon.getCoordinates(), new TypeToken<List<PointDTO>>(){}.getType());

            for (PointDTO coordinate : coordinates) {
                PointAreaDTO pointDTO = new PointAreaDTO();
//                pointDTO.setOrderNumber(area.getOrderNumber());
                pointDTO.setLatitudeValues(coordinate.getLat());
                pointDTO.setLongitudeValues(coordinate.getLng());
                pointDTOs.add(pointDTO);
            }
            poligon.setPoints(pointDTOs);
            poligonsDTO.add(poligon);
            System.out.println("==========================================");
            System.out.println("Gson list: " + coordinates);
        }
        resourceArea.setPoligons(poligonsDTO);
        resourceDTO.setResourceArea(resourceArea);
        return resourceDTO;
    }

    /**
     * Method increment registration number of resource for registrator which
     * add new resource
     * 
     * @param login
     *            - login of registrator authorized
     */
    private void incrementRegistrationNumber(String login) {
        User user = userRepository.findUserByLogin(login);
        ResourceNumber resourceNumber = resourceNumberRepository.findResourceNumberByUser(user);
        Integer incrementedNumber = resourceNumber.getNumber() + 1;
        resourceNumber.setNumber(incrementedNumber);
        resourceNumberRepository.save(resourceNumber);
    }

    @Override
    public Integer countAllByAreaLimits(Double minLat, Double maxLat, Double minLng, Double maxLng) {
        Integer count = polygonRepository.countByLimits(minLat, maxLat, minLng, maxLng);
        return count;
    }

    @Override
    public Integer countAllByPoint(Double lat,Double lng) {
        Integer count = polygonRepository.countByPoint(lat, lng);
        return count;
    }

    /**
     * Creates boolean expression for JPAQuery for resource search depending on resource discrete values
     * @param idList - List with discrete parameters ids
     * @param valueList - List with search values
     * @param signList - List with compare signs
     * @return BooleanExpression
     */
    private BooleanExpression resourceDiscreteValueExpression (List<Integer> idList, List<Double> valueList, List<String> signList) {
        BooleanExpression expression = null;
        QResourceDiscreteValue rdv = QResourceDiscreteValue.resourceDiscreteValue;
        QResource qResource = QResource.resource;

        for (int i=0; i<idList.size(); i++) {
            if(valueList.get(i) != null) {
                DiscreteParameter dp = discreteParameterRepository.findByDiscreteParameterId(idList.get(i));
                BooleanExpression exp = rdv.discreteParameter.eq(dp);

                switch (signList.get(i)){
                    case "less": exp = exp.and(rdv.value.lt(valueList.get(i))); break;
                    case "equal": exp = exp.and(rdv.value.eq(valueList.get(i))); break;
                    case "greater": exp = exp.and(rdv.value.gt(valueList.get(i))); break;
                }

                if(expression == null) {
                    expression = qResource.in(new JPASubQuery().from(rdv).where(exp).list(rdv.resource));
                }
                else {
                    expression = expression.and(qResource.in(new JPASubQuery().from(rdv).where(exp).list(rdv.resource)));
                }
            }
        }

        return expression;
    }

    /**
     * Creates boolean expression for JPAQuery for resource search depending on resource linear values
     * @param idList - List with Linear Parameters ids
     * @param valueList - List with search values
     * @return BooleanExpression
     */
    private BooleanExpression resourceLinearValueExpression (List<Integer> idList, List<Double> valueList) {
        BooleanExpression expression = null;
        QResourceLinearValue rlv = QResourceLinearValue.resourceLinearValue;
        QResource qResource = QResource.resource;

        for (int i = 0; i < idList.size(); i++) {
            if (valueList.get(i) != null) {
                double val = valueList.get(i);
                LinearParameter lp = linearParameterRepository.findByLinearParameterId(idList.get(i));
                BooleanExpression exp = rlv.linearParameter.eq(lp).and(rlv.minValue.lt(val)).and(rlv.maxValue.gt(val));
                if (expression == null) {
                    expression = qResource.in(new JPASubQuery().from(rlv).where(exp).list(rlv.resource));
                }
                else {
                    expression = expression.and(qResource.in(new JPASubQuery().from(rlv).where(exp).list(rlv.resource)));
                }
            }
        }
        return expression;
    }


}
