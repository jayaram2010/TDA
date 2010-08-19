package com.tda.presentation.controller;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.tda.model.applicationuser.ApplicationUser;
import com.tda.model.applicationuser.Authority;
import com.tda.persistence.paginator.Paginator;
import com.tda.presentation.params.ParamContainer;
import com.tda.service.api.ApplicationUserService;
import com.tda.service.api.AuthorityService;
import com.tda.service.exception.NoDataFoundException;
import com.tda.service.exception.SingleResultExpectedException;

@Controller
@RequestMapping(value = "/applicationUser")
@SessionAttributes("applicationUser")
public class ApplicationUserController {

	private static final String USER_FORM_DELETE_ERROR = "user.form.deleteError";
	private static final String USER_FORM_NOT_FOUND = "user.form.notFound";
	private static final String USER_FORM_MESSAGE = "message";
	private static final String USER_FORM_ADD_SUCCESSFUL = "user.form.addSuccessful";
	private static final String REDIRECT_TO_USER_LIST = "redirect:/applicationUser";
	private static final String USER_CREATE_FORM = "applicationUser/createForm";
	private static final String USER_LIST = "applicationUser/list";

	private Paginator paginator;
	private ParamContainer params;
	private ApplicationUserService applicationUserService;
	private AuthorityService authorityService;
	
	public ApplicationUserController() {
		params = new ParamContainer();
	}

	@Autowired
	public void setApplicationUserService(
			ApplicationUserService applicationUserService) {
		this.applicationUserService = applicationUserService;
	}

	@Autowired
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	@Autowired
	public void setPaginator(Paginator paginator) {
		this.paginator = paginator;
		paginator.setOrderField("id");
	}

	@ModelAttribute("allAuthorities")
	public List<Authority> populateAuthorities() {
		return authorityService.findAll();
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		model.addAttribute(new ApplicationUser());

		return USER_CREATE_FORM;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(Model model,
			@Valid @ModelAttribute ApplicationUser applicationUser,
			BindingResult result) {
		ModelAndView modelAndView = new ModelAndView();

		// TODO if we're editing and not adding a new item the message
		// seems somewhat... misleading, CHANGE IT :D

		if (result.hasErrors()) {
			modelAndView.setViewName(USER_CREATE_FORM);
		} else {
			modelAndView.setViewName(REDIRECT_TO_USER_LIST);
			modelAndView.addObject(USER_FORM_MESSAGE, USER_FORM_ADD_SUCCESSFUL);
			applicationUserService.save(applicationUser);
		}

		return modelAndView;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String getUpdateForm(@PathVariable Long id, Model model) {
		ApplicationUser applicationUser = applicationUserService.findById(id);
		model.addAttribute("applicationUser", applicationUser);

		return USER_CREATE_FORM;
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public ModelAndView deleteItem(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_USER_LIST);
		ApplicationUser applicationUser = applicationUserService.findById(id);

		if (applicationUser == null) {
			modelAndView.addObject(USER_FORM_MESSAGE, USER_FORM_NOT_FOUND);
		} else {

			try {
				applicationUserService.delete(applicationUser);
			} catch (Exception e) {
				modelAndView.addObject(USER_FORM_MESSAGE,
						USER_FORM_DELETE_ERROR);
			}
		}
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getList(
			@ModelAttribute ApplicationUser aUser,
			BindingResult result,
			@RequestParam(value = "page", required = false) Integer pageNumber,
			@RequestParam(value = "orderField", required = false) String orderField,
			@RequestParam(value = "orderAscending", required = false) Boolean orderAscending) {
		ModelAndView modelAndView = new ModelAndView(USER_LIST);

		List<ApplicationUser> applicationUserList = null;

		// Pagination
		if (pageNumber != null) {
			paginator.setPageIndex(pageNumber);
		}

		// Order
		if (orderField == null || orderAscending == null) {
			orderField = "username";
			orderAscending = true;
		}

		paginator.setOrderAscending(orderAscending);
		paginator.setOrderField(orderField);
		//order params
		params.setParam("orderField", orderField);
		params.setParam("orderAscending", orderAscending.toString());
		
		//filter params
		params.setParam("username", aUser.getUsername());
		params.setParam("password", aUser.getPassword());

		if (aUser.getMyAuthorities() != null)
			params.setParam("myAuthorities", aUser.getMyAuthorities()
					.toString());

		List<String> excludedFields = new ArrayList<String>();
		excludedFields.add("accountNonExpired");
		excludedFields.add("accountNonLocked");
		excludedFields.add("credentialsNonExpired");
		excludedFields.add("enabled");

		applicationUserList = applicationUserService.findByExamplePaged(aUser,
				paginator, excludedFields);
		modelAndView.addObject("applicationUser", new ApplicationUser());
		modelAndView.addObject("applicationUserList", applicationUserList);
		modelAndView.addObject("paginator", paginator);
		modelAndView.addObject("params", params);
		modelAndView.addObject("orderField", orderField);
		modelAndView.addObject("orderAscending", orderAscending.toString());

		return modelAndView;
	}

	@InitBinder
	public void initBinder(WebDataBinder b) {
		b.registerCustomEditor(Authority.class, new AuthorityEditor());
	}

	private class AuthorityEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			try {
				setValue(authorityService.findByAuthority(text));
			} catch (SingleResultExpectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoDataFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public String getAsText() {
			return ((Authority) getValue()).getAuthority();
		}
	}

}
