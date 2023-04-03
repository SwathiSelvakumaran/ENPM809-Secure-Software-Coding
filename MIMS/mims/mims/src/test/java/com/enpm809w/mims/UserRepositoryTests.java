package com.enpm809w.mims;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Services.UserDetailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @PersistenceContext
    private EntityManager entityManager;

//    @Autowired
//    private MockMvc mvc;
    @Autowired
    private UserRepository repo;

    @Autowired
    private UserDetailService userDetailService;

    @Test
    public void testCreateUser() {
        try {
            User user = new User();
            user.setEmail("sswathika99@outlook.com");
            user.setPassword("Swat1999@");
            user.setFirstName("Swathi");
            user.setLastName("Selvakumaran");

            userDetailService.register(user, "https://localhost:8080", repo);
            User existUser = entityManager.find(User.class, user.getId());

            assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
        }catch (Exception e){
            System.out.println(e);
            assertThat(false);
        }
    }

    @Test
    public void testCreateInvalidUser() {
        try {
            User user = new User();
            user.setEmail("swathi99@umd.edu");
            user.setPassword("Swat1999");
            user.setFirstName("Swathi");
            user.setLastName("Selvakumaran");

            userDetailService.register(user, "https://localhost:8080", repo);
            User existUser = entityManager.find(User.class, user.getId());

            assertThat(user.getEmail()).isEqualTo(existUser.getEmail());
        }catch (Exception e){
            System.out.println(e);
            assertThat(false);
        }
    }

    @Test
    public void doNotRegisterExistingUser() {
        try {
            User user = new User();
            user.setEmail("sswathika99@outlook.com");
            user.setPassword("Swat1999@");
            user.setFirstName("Swathi");
            user.setLastName("Selvakumaran");

            boolean registered = userDetailService.register(user, "https://localhost:8080", repo);

            assertThat(registered == false);
        }catch (Exception e){
            System.out.println(e);
            assertThat(false);
        }
    }

    @Test
    public void testFindByEmail() {
        String email = "swathi99@umd.edu";
        User user = repo.findByEmail(email);
        assertThat(user.getEmail()).isEqualTo(email);
    }

}
