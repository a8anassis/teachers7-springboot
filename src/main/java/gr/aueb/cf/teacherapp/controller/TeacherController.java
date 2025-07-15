package gr.aueb.cf.teacherapp.controller;

import gr.aueb.cf.teacherapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.teacherapp.dto.TeacherEditDTO;
import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.teacherapp.mapper.Mapper;
import gr.aueb.cf.teacherapp.model.Teacher;
import gr.aueb.cf.teacherapp.repository.RegionRepository;
import gr.aueb.cf.teacherapp.repository.TeacherRepository;
import gr.aueb.cf.teacherapp.service.IRegionService;
import gr.aueb.cf.teacherapp.service.ITeacherService;
import gr.aueb.cf.teacherapp.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/school")
//@RequiredArgsConstructor
public class TeacherController {

    private final Logger LOGGER = LoggerFactory.getLogger(TeacherController.class);
    private final ITeacherService teacherService;
    private final IRegionService regionService;
    private final TeacherRepository teacherRepository;
    private final Mapper mapper;
    private final RegionRepository regionRepository;

    @Autowired
    public TeacherController(ITeacherService teacherService, IRegionService regionService,
                             TeacherRepository teacherRepository, Mapper mapper, RegionRepository regionRepository) {
        this.teacherService = teacherService;
        this.regionService = regionService;
        this.teacherRepository = teacherRepository;
        this.mapper = mapper;
        this.regionRepository = regionRepository;
    }

    @GetMapping("/teachers/insert")
    public String getTeacherForm(Model model) {
        model.addAttribute("teacherInsertDTO", new TeacherInsertDTO());
        model.addAttribute("regions", regionRepository.findAll(Sort.by("name")));
        return "teacher-form";
    }

    @PostMapping("/teachers/insert")
    public String saveTeacher(@Valid @ModelAttribute("teacherInsertDTO") TeacherInsertDTO teacherInsertDTO,
                              BindingResult bindingResult, Model model,  RedirectAttributes redirectAttributes) {

        Teacher savedTeacher;

        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionRepository.findAll(Sort.by("name"))); // Re-populate regions regionService.findAllRegions()
            return "teacher-form";
        }

        try {
            savedTeacher = teacherService.saveTeacher(teacherInsertDTO);
            //LOGGER.info("Teacher with id={} inserted", savedTeacher.getId());
            TeacherReadOnlyDTO teacherReadOnlyDTO = mapper.mapToTeacherReadOnlyDTO(savedTeacher);
            //model.addAttribute("teacher", savedTeacher); -- request scope
            redirectAttributes.addFlashAttribute("teacher", mapper.mapToTeacherReadOnlyDTO(savedTeacher));

            // The Post-Redirect-Get (PRG) pattern is a web development design pattern
            // that prevents duplicate form submissions and improves user experience by redirecting
            // after a POST request
            return "redirect:/school/teachers";
        } catch (EntityAlreadyExistsException | EntityInvalidArgumentException e) {
            // LOGGER.error("Teacher with vat={} not inserted", teacherInsertDTO.getVat(), e);
            model.addAttribute("regions", regionRepository.findAll(Sort.by("name"))); // Re-populate
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

    // GET - Show edit form
    @GetMapping("/teachers/edit/{uuid}")
    public String showEditForm(@PathVariable String uuid, Model model) {
        try {
            Teacher teacher = teacherRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Teacher","Teacher not found"));

            model.addAttribute("teacherEditDTO", mapper.mapToTeacherEditDTO(teacher));
            model.addAttribute("regions", regionService.findAllRegions());
            return "teacher-edit-form";
        } catch (EntityNotFoundException e) {
            LOGGER.error("Teacher with uuid={} not inserted", uuid, e);
            model.addAttribute("regions", regionService.findAllRegions()); // Re-populate
            model.addAttribute("errorMessage", e.getMessage());
            return "teacher-edit-form";
        }
    }

    // POST - Process edit form
    @PostMapping("/teachers/edit")
    public String updateTeacher(@Valid @ModelAttribute("teacherEditDTO") TeacherEditDTO teacherEditDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("regions", regionService.findAllRegions());
            System.out.println("ERRORS IN UPDATE");
            return "teacher-edit-form";
        }

        try {
            Teacher updatedTeacher = teacherService.updateTeacher(teacherEditDTO);
            LOGGER.info("Teacher with id={} updated", updatedTeacher.getId());
            TeacherReadOnlyDTO teacherReadOnlyDTO = mapper.mapToTeacherReadOnlyDTO(updatedTeacher);
            //model.addAttribute("teacher", savedTeacher); -- request scope
            redirectAttributes.addFlashAttribute("teacher", mapper.mapToTeacherReadOnlyDTO(updatedTeacher));
            return "redirect:/school/teachers";
        } catch (EntityInvalidArgumentException | EntityNotFoundException | EntityAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("regions", regionRepository.findAll(Sort.by("name")));
            return "teacher-edit-form";
        }
    }

    @GetMapping("/teachers/delete/{uuid}")  // Using GET for simplicity
    public String deleteTeacher(@PathVariable String uuid, Model model) {
        try {
           teacherService.deleteTeacherByUUID(uuid);
            return "redirect:/school/teachers";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("regions", regionRepository.findAll(Sort.by("name")));
            return "teachers";
        }

    }
}
