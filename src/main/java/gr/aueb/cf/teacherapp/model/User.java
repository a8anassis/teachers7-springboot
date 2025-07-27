package gr.aueb.cf.teacherapp.model;

import gr.aueb.cf.teacherapp.core.enums.UserApplyStatus;
import gr.aueb.cf.teacherapp.model.auth.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

//    @Enumerated(EnumType.STRING)
//    private Role role;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    @ColumnDefault("'APPROVED'")        // Note the single quotes for VARCHAR, PENDING SHOULD BE THE DEFAULT
//    private UserApplyStatus status;     //  UserApplyStatus.PENDING;
//
//    @ColumnDefault("true")
//    @Column(name = "is_active")
//    private Boolean isActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.name()));
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        role.getCapabilities().forEach(capability ->
                grantedAuthorities.add(new SimpleGrantedAuthority(capability.getName()))
        );
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
//    public boolean isEnabled() {
//        return this.getIsActive() == null || this.getIsActive()
//    }
}
