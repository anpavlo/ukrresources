package org.registrator.community.controller.administrator;

import java.util.List;

import javax.validation.Valid;

import org.registrator.community.components.AdminSettings;
import org.registrator.community.components.TableSettingsFactory;
import org.registrator.community.dto.UserDTO;
import org.registrator.community.dto.JSON.ResourceNumberDTOJSON;
import org.registrator.community.dto.JSON.UserStatusDTOJSON;
import org.registrator.community.dto.search.TableSearchRequestDTO;
import org.registrator.community.dto.search.TableSearchResponseDTO;
import org.registrator.community.entity.Role;
import org.registrator.community.entity.TerritorialCommunity;
import org.registrator.community.entity.User;
import org.registrator.community.enumeration.UserStatus;
import org.registrator.community.service.CommunityService;
import org.registrator.community.service.RoleService;
import org.registrator.community.service.UserService;
import org.registrator.community.service.search.BaseSearchService;
import org.registrator.community.validator.CommunityValidator;
import org.registrator.community.validator.ResourceNumberJSONDTOValidator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/administrator/users/")
public class UsersController {

	@Autowired
	private Logger logger;

	@Autowired
	private CommunityService communityService;

	@Autowired
	private UserService userService;

	@Autowired
	private AdminSettings adminSettings;

	@Autowired
	private RoleService roleService;

	@Autowired
	@Qualifier("registerUserSearchService")
	private BaseSearchService<User> userSearchService;

	@Autowired
	private TableSettingsFactory tableSettingsFactory;

	@Autowired
	private CommunityValidator validator;

	@Autowired
	ResourceNumberJSONDTOValidator resourceNumberValidator;

	/**
	 * Controller for showing information about user
	 *
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@RequestMapping(value = "/edit-registrated-user", method = RequestMethod.GET)
	public String fillInEditWindow(@RequestParam("login") String login, Model model) {
		logger.info("begin");
		UserDTO userDto = userService.getUserDto(login);
		model.addAttribute("userDto", userDto);
		List<Role> roleList = roleService.getAllRole();
		model.addAttribute("roleList", roleList);
		List<UserStatus> userStatusList = userService.fillInUserStatusforRegistratedUsers();
		model.addAttribute("userStatusList", userStatusList);
		List<TerritorialCommunity> territorialCommunities = communityService.findAll();
		model.addAttribute("territorialCommunities", territorialCommunities);
		logger.info("end");
		return "editWindow";
	}

	/**
	 * Controller for editing user information
	 *
	 */

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@RequestMapping(value = "/edit-registrated-user", method = RequestMethod.POST)
	public String editRegistratedUser(@Valid @ModelAttribute("userDTO") UserDTO userDto, BindingResult result,
			Model model,RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			return fillInEditWindow(userDto.getLogin(), model);
		} else {
			logger.info("begin");
			userService.CreateTomeAndRecourceNumber(userDto);
			UserDTO editUserDto = userService.editUserInformation(userDto);
			model.addAttribute("userDto", editUserDto);
			logger.info("end");
			redirectAttributes.addFlashAttribute("tableSetting", tableSettingsFactory.getTableSetting("registerUser"));
			return "redirect:/administrator/users/get-all-users";
		}
	}

	/**
	 * Controller for showing all inactive user
	 *
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@RequestMapping(value = "/get-all-inactive-users", method = RequestMethod.GET)
	public String getAllInactiveUsers(Model model) {
		logger.info("begin");
		List<UserDTO> inactiveUsers = userService.getAllInactiveUsers();
		model.addAttribute("unregistatedUsers", inactiveUsers);
		List<UserStatus> userStatusList = userService.fillInUserStatusforInactiveUsers();
		model.addAttribute("userStatusList", userStatusList);
		List<Role> roleList = roleService.getAllRole();
		model.addAttribute("roleList", roleList);
		logger.info("end");
		return "InActiveUsers";
	}

	/**
	 * Controller for changing user statur for inactive users
	 *
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@ResponseBody
	@RequestMapping(value = "/get-all-inactive-users", method = RequestMethod.POST)
	public String changeStatus(@RequestBody UserStatusDTOJSON userStatusDto) {
		logger.info("begin");
		userService.changeUserStatus(userStatusDto);
		logger.info("end");
		return "InActiveUsers";
	}

	/**
	 * Controller for showing modal window
	 *
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@ResponseBody
	@RequestMapping(value = "/edit-registrated-user/modal-window", method = RequestMethod.POST)
	public ResponseEntity<String> showModalWindow(@Valid @RequestBody ResourceNumberDTOJSON resourceNumberDtoJson,
			BindingResult result) {
		logger.info("begin");
		resourceNumberValidator.validate(resourceNumberDtoJson, result);
		if (result.hasErrors()) {
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		} else {
			logger.info("end");
			return new ResponseEntity<String>(HttpStatus.OK);
		}
	}

	/**
	 * Controller for get all registrated users
	 * 
	 */

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@RequestMapping(value = "/get-all-users", method = RequestMethod.GET)
	public String getAllUsers(Model model) {
		logger.info("begin");
		model.addAttribute("tableSetting", tableSettingsFactory.getTableSetting("registerUser"));
		logger.info("end");
		return "searchTableTemplate";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_COMMISSIONER')")
	@ResponseBody
	@RequestMapping(value = "registerUser", method = RequestMethod.POST)
	public TableSearchResponseDTO getDataFromDataTable(@Valid @RequestBody TableSearchRequestDTO dataTableRequest) {
		TableSearchResponseDTO dto = userSearchService.executeSearchRequest(dataTableRequest);
		return dto;
	}

	/**
	 * Method for showing administrator settings in order to change registration
	 * method
	 * 
	 * @param model
	 * @return adminSettings.jsp
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public String showSettings(Model model) {
		logger.info("begin: show admin settings");
		model.addAttribute("regMethod", adminSettings.getRegistrationMethod());
		logger.info("end: admin settings are shown");
		return "adminSettings";
	}

	/**
	 * Method for changing administrator settings for one of the possible
	 * options
	 * 
	 * @param optratio
	 *            - one of three possible option for changing registration
	 *            method
	 * @return adminSettings.jsp
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/settings", method = RequestMethod.POST)
	public String changeSettings(@RequestParam String optradio) {
		logger.info("start changing settings");
		adminSettings.changeRegMethod(optradio);
		logger.info("settings are successfully changed");
		return "adminSettings";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/show-all-communities", method = RequestMethod.GET)
	public String showCommunity(Model model) {
		List<TerritorialCommunity> listOfTerritorialCommunity = communityService.findAll();
		model.addAttribute("listOfTerritorialCommunity", listOfTerritorialCommunity);
		return "showAllCommunity";
	}

	/**
	 * Method for loading form for input new territorial community name
	 * 
	 * @param model
	 * @return addNewCommunity.jsp
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/addCommunity", method = RequestMethod.GET)
	public String addNewCommunity(Model model) {
		model.addAttribute("newCommunity", new TerritorialCommunity());
		return "addNewCommunity";
	}

	/**
	 * Method for saving new territorial community in the database. Also there
	 * is validation for checking whether inputing name already exists in
	 * database or not.
	 * 
	 * @param territorialCommunity
	 * @param result
	 * @param model
	 * @return addNewCommunity.jsp (showAllCommunity.jsp page if community name
	 *         is not valid)
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/addCommunity", method = RequestMethod.POST)
	public String addCommunity(@Valid @ModelAttribute("newCommunity") TerritorialCommunity territorialCommunity,
			BindingResult result, Model model) {
		validator.validate(territorialCommunity, result);
		if (result.hasErrors()) {
			logger.info("end method: community name is not valid, "
					+ "return to page for adding new territorial community");
			return "addNewCommunity";

		}
		communityService.addCommunity(territorialCommunity);
		model.addAttribute("territorialCommunity", territorialCommunity);
		return "redirect:/administrator/users/show-all-communities";
	}

	/**
	 * Method for deleting chosen community by territorialCommunityId. If chosen
	 * community has at least one user who is in this community we will get
	 * bad_request and it will not be deleted nor from UI by Ajax neither from
	 * database
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/deleteCommunity/{territorialCommunityId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteCommunity(@PathVariable Integer territorialCommunityId) {
		boolean isDeleted = communityService.deleteCommunity(communityService.findById(territorialCommunityId));
		if (isDeleted) {
			logger.info("end: deleted chosen community");
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		logger.info("end: it's impossible to delete territorial community");
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}

	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/unlockusers")
    public String home() {
		userService.resetAllFailAttempts();
		logger.info("All users atempts are reseted");
		 return "homepage";
    }
	
}