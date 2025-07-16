package gr.aueb.cf.teacherapp.validator;

import gr.aueb.cf.teacherapp.dto.TeacherEditDTO;
import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.repository.RegionRepository;
import gr.aueb.cf.teacherapp.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeacherEditValidator implements Validator {
    private final TeacherRepository teacherRepository;
    private final RegionRepository regionRepository;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return TeacherEditDTO.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        TeacherEditDTO teacherEditDTO = (TeacherEditDTO) target;

        if (teacherRepository.findByVat(teacherEditDTO.getVat()).isPresent()) {
            log.error("Save failed for teacher with vat={}. Teacher already exists", teacherEditDTO.getVat());
            errors.rejectValue("vat", "Το ΑΦΜ του Καθηγητή υπάρχει ήδη.");
        }

        if (regionRepository.findById(teacherEditDTO.getRegionId()).isEmpty()) {
            log.error("Save failed for teacher with vat={}. Region not found with id={}",
                    teacherEditDTO.getVat(), teacherEditDTO.getRegionId());
            errors.rejectValue("regionId", "Η περιοχή του Καθηγητή δεν μπορεί να είναι κενή.");
        }
    }
}
