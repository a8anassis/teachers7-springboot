package gr.aueb.cf.teacherapp.service;

import gr.aueb.cf.teacherapp.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityInvalidArgumentException;
import gr.aueb.cf.teacherapp.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.teacherapp.dto.TeacherEditDTO;
import gr.aueb.cf.teacherapp.dto.TeacherInsertDTO;
import gr.aueb.cf.teacherapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.teacherapp.mapper.Mapper;
import gr.aueb.cf.teacherapp.model.Teacher;
import gr.aueb.cf.teacherapp.model.static_data.Region;
import gr.aueb.cf.teacherapp.repository.RegionRepository;
import gr.aueb.cf.teacherapp.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class TeacherService implements ITeacherService {

    private final TeacherRepository teacherRepository;
    private final RegionRepository regionRepository;
    private final Mapper mapper;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, RegionRepository regionRepository, Mapper mapper) {
        this.teacherRepository = teacherRepository;
        this.regionRepository = regionRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Teacher saveTeacher(TeacherInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        if (teacherRepository.findByVat(dto.getVat()).isPresent()) {
            throw new EntityAlreadyExistsException("Teacher", "Teacher with vat " + dto.getVat() + " already exists");
        }

        Teacher teacher = mapper.mapToTeacherEntity(dto);

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Invalid region id"));

        region.addTeacher(teacher);

        return teacherRepository.save(teacher);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Teacher updateTeacher(TeacherEditDTO dto)
            throws EntityInvalidArgumentException, EntityNotFoundException, EntityAlreadyExistsException {

//        if (teacherRepository.findByVat(dto.getVat()).isPresent()) {
//            throw new EntityAlreadyExistsException("Teacher", "Teacher with vat " + dto.getVat() + " already exists");
//        }

        System.out.println("DTO" + dto);
//        Teacher teacher = mapper.mapToTeacherEntity(dto);
        Teacher teacher = teacherRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new EntityNotFoundException("Teacher", "Teacher not found"));
        System.out.println("Teacher found" + teacher);

        if (!teacher.getVat().equals(dto.getVat())) {
            if (teacherRepository.findByVat(dto.getVat()).isEmpty()) teacher.setVat(dto.getVat());
                    else throw new EntityAlreadyExistsException("Teacher", "Teacher with VAT " + dto.getVat() + " already exists");
        }

        teacher.setFirstname(dto.getFirstname());
        teacher.setLastname(dto.getLastname());

        if (!teacher.getRegion().getId().equals(dto.getRegionId())) {
            Region region = regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Invalid region ID"));

            Region currentRegion = teacher.getRegion();

            // First remove from current region
            if (currentRegion != null) {
                currentRegion.removeTeacher(teacher);
            }

            // Then add to new region
            region.addTeacher(teacher);
            System.out.println("REGION UPDATED");
        }

        return teacherRepository.save(teacher);
    }

    @Override
    @Transactional
    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Teacher> teacherPage = teacherRepository.findAll(pageable);
        return teacherPage.map(mapper::mapToTeacherReadOnlyDTO);
    }

    @Override
    @Transactional
    public void deleteTeacherByUUID(String uuid) throws EntityNotFoundException {
        Teacher teacher = teacherRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Teacher", "Teacher with uuid: " + uuid + " not exists"));
        teacherRepository.deleteById(teacher.getId());
    }
}
