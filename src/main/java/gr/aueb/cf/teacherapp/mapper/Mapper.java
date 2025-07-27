package gr.aueb.cf.teacherapp.mapper;

//import gr.aueb.cf.teacherapp.core.enums.Role;
import gr.aueb.cf.teacherapp.dto.*;
import gr.aueb.cf.teacherapp.model.Teacher;
import gr.aueb.cf.teacherapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Teacher mapToTeacherEntity(TeacherInsertDTO teacherInsertDTO) {
        Teacher teacher = new Teacher();
        teacher.setFirstname(teacherInsertDTO.getFirstname());
        teacher.setLastname(teacherInsertDTO.getLastname());
        teacher.setVat(teacherInsertDTO.getVat());
        return teacher;
    }

    public TeacherReadOnlyDTO mapToTeacherReadOnlyDTO(Teacher teacher) {
        return new TeacherReadOnlyDTO(teacher.getId(), teacher.getCreatedAt(),
                teacher.getUpdatedAt(), teacher.getUuid(), teacher.getFirstname(),
                teacher.getLastname(), teacher.getVat(), teacher.getRegion().getName());
    }

    public TeacherEditDTO mapToTeacherEditDTO(Teacher teacher) {
        return new TeacherEditDTO(teacher.getUuid(),  teacher.getFirstname(),
                teacher.getLastname(), teacher.getVat(), teacher.getRegion().getId());
    }

    public User mapToUserEntity(UserInsertDTO userInsertDTO) {
        return User.builder().username(userInsertDTO.getUsername())
                .password(userInsertDTO.getPassword()).build();
    }

    public UserReadOnlyDTO mapUserToReadOnlyDTO(User user) {
//        return new UserReadOnlyDTO(user.getUsername(), user.getRole().name());
        return new UserReadOnlyDTO(user.getUsername(), user.getRole().getName());
    }
}


//        return new User(null, userInsertDTO.getUsername(), userInsertDTO.getPassword(),
//         Role.valueOf(userInsertDTO.getRole().toUpperCase()));