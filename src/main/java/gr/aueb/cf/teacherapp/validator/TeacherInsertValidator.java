package gr.aueb.cf.teacherapp.validator;

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
public class TeacherInsertValidator implements Validator {
    private final TeacherRepository teacherRepository;
    private final RegionRepository regionRepository;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return TeacherInsertDTO.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        TeacherInsertDTO teacherInsertDTO = (TeacherInsertDTO) target;

        if (teacherRepository.findByVat(teacherInsertDTO.getVat()).isPresent()) {
            log.error("Save failed for teacher with vat={}. Teacher already exists", teacherInsertDTO.getVat());
            errors.rejectValue("vat", "Το ΑΦΜ του Καθηγητή υπάρχει ήδη.");
        }

        if (regionRepository.findById(teacherInsertDTO.getRegionId()).isEmpty()) {
            log.error("Save failed for teacher with vat={}. Region not found with id={}",
                    teacherInsertDTO.getVat(), teacherInsertDTO.getRegionId());
            errors.rejectValue("regionId", "Η περιοχή του Καθηγητή δεν μπορεί να είναι κενή.");
        }
    }
}
