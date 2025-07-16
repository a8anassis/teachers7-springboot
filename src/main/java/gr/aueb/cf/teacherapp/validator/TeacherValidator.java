package gr.aueb.cf.teacherapp.validator;

import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class TeacherValidator implements Validator {
    private final TeacherRepository teacherRepository;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return TeacherInsertDTO.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        TeacherInsertDTO teacherInsertDTO = (TeacherInsertDTO) target;

        if (teacherRepository.findByVat(teacherInsertDTO.getVat()).isPresent()) {
            errors.rejectValue("vat", "Το ΑΦΜ του Καθηγητή υπάρχει ήδη.");
        }
    }
}
