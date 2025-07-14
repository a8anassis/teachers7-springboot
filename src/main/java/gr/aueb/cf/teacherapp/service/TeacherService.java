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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
        try {
            if (teacherRepository.findByVat(dto.getVat()).isPresent()) {
                throw new EntityAlreadyExistsException("Teacher", "Teacher with vat " + dto.getVat() + " already exists");
            }
            Teacher teacher = mapper.mapToTeacherEntity(dto);
            Region region = regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new EntityInvalidArgumentException("Region", "Invalid region id"));
            region.addTeacher(teacher);
            //return teacherRepository.save(teacher);
            teacherRepository.save(teacher);
            log.info("Teacher with vat={} saved.", dto.getVat());
            return teacher;
        } catch (EntityAlreadyExistsException e) {
            // vat={} follows the parameterized placeholder pattern used by most logging (key=value)
            // Log aggregators (ELK, Splunk, Datadog) can automatically extract uuid as a field
            // vat= acts as field identifier. {} is the value placeholder
            log.error("Save failed for teacher with vat={}. Teacher already exists", dto.getVat(), e);
            throw e; // Re-throw to trigger rollback
        } catch (EntityInvalidArgumentException e) {
            log.error("Save failed for teacher with vat={}. Region not found with id={}",
                    dto.getVat(), dto.getRegionId(), e);
            throw e;
        }
    }

    /*
    ERROR, WARN, INFO, DEBUG, TRACE
    Scenario	        Recommended Level
    Entity found	        DEBUG/TRACE
    Entity created/updated	INFO
    Entity delete failed	ERROR
    Login success	        INFO
    Login failed	        WARN (unless brute-force attack suspected → ERROR)

     */

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Teacher updateTeacher(TeacherEditDTO dto)
            throws EntityInvalidArgumentException, EntityNotFoundException, EntityAlreadyExistsException {

        try {
            Teacher teacher = teacherRepository.findByUuid(dto.getUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher", "Teacher not found"));

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

            }
            teacherRepository.save(teacher);
            log.info("Teacher with vat={} updated.", dto.getVat());
            //return teacherRepository.save(teacher);
            return teacher;
        } catch (EntityNotFoundException e) {
            log.error("Update failed for teacher with vat={}. Entity not found.", dto.getVat(), e);
            throw e; // Re-throw to trigger rollback
        } catch (EntityAlreadyExistsException e) {
            log.error("Update failed for teacher with vat={}. Entity already exists.", dto.getVat(), e);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Save failed for teacher with vat={}. Region not found with id={}",
                    dto.getVat(), dto.getRegionId(), e);
            throw e; // Re-throw to trigger rollback
        }
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
        try {
            Teacher teacher = teacherRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Teacher", "Teacher with uuid: " + uuid + " not exists"));
            teacherRepository.deleteById(teacher.getId());
            log.info("Teacher with uuid={} deleted.", uuid);
        } catch (EntityNotFoundException e) {
            log.error("Update failed for teacher with uuid={}. Entity not found.", uuid, e);
            throw e;
        }
    }
}
