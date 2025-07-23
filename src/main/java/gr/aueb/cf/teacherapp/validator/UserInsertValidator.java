package gr.aueb.cf.teacherapp.validator;

import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.dto.UserInsertDTO;
import gr.aueb.cf.teacherapp.repository.RegionRepository;
import gr.aueb.cf.teacherapp.repository.TeacherRepository;
import gr.aueb.cf.teacherapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserInsertValidator implements Validator {
    private final UserRepository userRepository;


    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return UserInsertDTO.class == clazz;
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UserInsertDTO userInsertDTO = (UserInsertDTO) target;

        if (userRepository.findByUsername(userInsertDTO.getUsername()).isPresent()) {
            log.error("Save failed for user with username={}. Teacher already exists", userInsertDTO.getUsername());
            errors.rejectValue("username", "username.user.exists", "Username exists");
        }
    }
}
