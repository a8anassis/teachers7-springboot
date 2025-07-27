package gr.aueb.cf.teacherapp.model.auth;

import gr.aueb.cf.teacherapp.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_capabilities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id")
    )
    private Set<Capability> capabilities = new HashSet<>();

    public void addCapability(@NotNull Capability capability) {
        this.capabilities.add(capability);
        capability.getRoles().add(this);
    }

    public void removeCapability(@NotNull Capability capability) {
        this.capabilities.remove(capability);
        capability.getRoles().remove(this);
    }

    public void addUser(User user) {
        this.users.add(user);
        user.setRole(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setRole(null);
    }

    /**
     * Bulk add users with relationship consistency
     */
    public void addUsers(Collection<User> users) {
        users.forEach(this::addUser);
    }
}