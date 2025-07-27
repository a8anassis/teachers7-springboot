package gr.aueb.cf.teacherapp.repository;

import gr.aueb.cf.teacherapp.model.auth.Capability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapabilityRepository extends JpaRepository<Capability, Long> {
}
