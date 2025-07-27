package gr.aueb.cf.teacherapp.repository;

import gr.aueb.cf.teacherapp.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
