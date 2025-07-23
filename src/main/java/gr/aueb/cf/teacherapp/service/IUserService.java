package gr.aueb.cf.teacherapp.service;

import gr.aueb.cf.teacherapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.teacherapp.dto.TeacherEditDTO;
import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.teacherapp.dto.UserInsertDTO;
import gr.aueb.cf.teacherapp.model.Teacher;
import org.springframework.data.domain.Page;

public interface IUserService {
    void saveUser(UserInsertDTO userInsertDTO) throws EntityAlreadyExistsException;
}
