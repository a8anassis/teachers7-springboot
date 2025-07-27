package gr.aueb.cf.teacherapp.controller;

import gr.aueb.cf.teacherapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.teacherapp.dto.UserInsertDTO;
import gr.aueb.cf.teacherapp.mapper.Mapper;
import gr.aueb.cf.teacherapp.model.User;
import gr.aueb.cf.teacherapp.repository.RoleRepository;
import gr.aueb.cf.teacherapp.service.UserService;
import gr.aueb.cf.teacherapp.validator.UserInsertValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/school")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserInsertValidator userInsertValidator;
    private final RoleRepository roleRepository;

    @GetMapping("/users/register")
    public String getUserForm(Model model) {
        model.addAttribute("userInsertDTO", new UserInsertDTO());
        model.addAttribute("roles", roleRepository.findAll(Sort.by("name")));
        return "user-form2";
    }

    @PostMapping("/users/register")
    public String insertUser(@Valid @ModelAttribute("userInsertDTO") UserInsertDTO userInsertDTO,
                             BindingResult bindingResult, Model model) {
        userInsertValidator.validate(userInsertDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll(Sort.by("name")));
            return "user-form";
        }
        try {
            userService.saveUser(userInsertDTO);
            return "redirect:/";
        } catch (EntityAlreadyExistsException e) {
            model.addAttribute("roles", roleRepository.findAll(Sort.by("name")));
            model.addAttribute("errorMessage", e.getMessage());
            return "user-form";
        }
    }
}
