package net.codejava;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import net.codejava.Repository.UserRepository;
import net.codejava.Service.TodoService;
import net.codejava.Service.UserServices;

@Controller
public class AppController {

	@Autowired
	private UserRepository repo;

	@Autowired
	private UserServices service;

	@Autowired
	private TodoService todoService;

	@GetMapping(path = "")
	public String viewHomePage() {
		return "index";
	}

	@GetMapping(path = "/register")
	public String showSignUpForm(Model model) {
		model.addAttribute("user", new User());
		return "signup_form";
	}

	@PostMapping("/process_register")
	public String processRegistration(User user, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		service.register(user, getSiteURL(request));
		return "register_success";
	}

	@GetMapping("/home")
	public String viewHome(Model model) {
		List<User> home = repo.findAll();
		model.addAttribute("home", home);
		return "home";
	}

	@GetMapping("/list_users")
	public String viewUsersList(Model model) {
		List<User> listUsers = repo.findAll();
		model.addAttribute("listUsers", listUsers);
		return "users";
	}

	@GetMapping("/ttuser")
	public String viewTtuser(Model model) {
		List<User> ttuser = repo.findAll();
		model.addAttribute("ttuser", ttuser);
		return "ttuser";
	}

	@RequestMapping("/todo")
	public String viewTodoPage(Model model, @Param("keyword") String keyword) {
		List<Todo> listTodo = todoService.listAll(keyword);

		model.addAttribute("listTodo", listTodo);
		model.addAttribute("keyword", keyword);

		return "todo";
	}

	@RequestMapping("/newtodo")
	public String showNewTodoForm(Model model) {
		List<User> ttuser = repo.findAll();
		System.out.println(ttuser.get(0).getId());
		Todo todo = new Todo(ttuser.get(0));
		model.addAttribute("todo", todo);

		return "new_todo";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveTodo(@ModelAttribute("todo") Todo todo) {

		todoService.save(todo);

		return "redirect:/todo";
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView showEditTodoForm(@PathVariable(name = "id") Long id) {
		ModelAndView mav = new ModelAndView("edit_todo");

		Todo todo = todoService.get(id);
		mav.addObject("todo", todo);

		return mav;
	}

	@RequestMapping("/delete/{id}")
	public String deleteTodo(@PathVariable(name = "id") Long id) {
		todoService.delete(id);

		return "redirect:/todo";
	}

	private String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("code") String code) {
		if (service.verify(code)) {
			return "verify_success";
		} else {
			return "verify_fail";
		}
	}

}
