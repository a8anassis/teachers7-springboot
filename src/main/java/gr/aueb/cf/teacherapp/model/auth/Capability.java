package gr.aueb.cf.teacherapp.model.auth;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "capabilities")
public class Capability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "capabilities", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // Helper methods to manage relationship
    public void addRole(Role role) {
        this.roles.add(role);
        role.getCapabilities().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getCapabilities().remove(this);
    }
}
