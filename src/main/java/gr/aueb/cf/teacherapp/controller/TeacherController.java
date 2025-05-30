package gr.aueb.cf.teacherapp.controller;

import gr.aueb.cf.teacherapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.teacherapp.mapper.Mapper;
import gr.aueb.cf.teacherapp.model.Teacher;
import gr.aueb.cf.teacherapp.service.IRegionService;
import gr.aueb.cf.teacherapp.service.ITeacherService;
import gr.aueb.cf.teacherapp.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/school")
@RequiredArgsConstructor
public class TeacherController {

    private final Logger LOGGER = LoggerFactory.getLogger(TeacherController.class);
    private final ITeacherService teacherService;
    private final IRegionService regionService;
    private final Mapper mapper;


    @GetMapping("/teachers/insert")
    public String getTeacherForm(Model model) {
        model.addAttribute("teacherInsertDTO", new TeacherInsertDTO());
        model.addAttribute("regions", regionService.findAllRegions());
        return "teacher-form";
    }

    @PostMapping("/teachers/insert")
    public String saveTeacher(@Valid @ModelAttribute("teacherInsertDTO") TeacherInsertDTO teacherInsertDTO,
                              BindingResult bindingResult, Model model,  RedirectAttributes redirectAttributes) {

        Teacher savedTeacher;

        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionService.findAllRegions()); // Re-populate regions
            return "teacher-form";
        }

        try {
            savedTeacher = teacherService.saveTeacher(teacherInsertDTO);
            LOGGER.info("Teacher with id={} inserted", savedTeacher.getId());
            TeacherReadOnlyDTO teacherReadOnlyDTO = mapper.mapToTeacherReadOnlyDTO(savedTeacher);
            //model.addAttribute("teacher", savedTeacher); -- request scope
            redirectAttributes.addFlashAttribute("teacher", mapper.mapToTeacherReadOnlyDTO(savedTeacher));
            return "redirect:/school/teachers";
        } catch (EntityAlreadyExistsException | EntityInvalidArgumentException e) {
            LOGGER.error("Teacher with vat={} not inserted", teacherInsertDTO.getVat(), e);
            model.addAttribute("regions", regionService.findAllRegions()); // Re-populate
            model.addAttribute("errorMessage", e.getMessage());
            return "teacher-form";
        }
    }

    @GetMapping("/teachers")
    public String getPaginatedTeachers(
            @RequestParam(defaultValue = "0") int page,  // Default to the first page (0-indexed)
            @RequestParam(defaultValue = "5") int size,  // Default page size
            Model model) {

        // Get paginated TeacherReadOnlyDTOs
        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page, size);

        // Add the page of teachers and pagination info to the model
        model.addAttribute("teachersPage", teachersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", teachersPage.getTotalPages());

        return "teachers";  // Return Thymeleaf view (teachers.html)
    }
}
