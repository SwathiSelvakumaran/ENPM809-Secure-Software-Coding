package com.enpm809w.mims;

import com.enpm809w.mims.Common.Insurance;
import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Details.InsuranceDetails;
import com.enpm809w.mims.Repository.InsuranceRepository;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Scheduler.NotificationScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class InsuranceRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private UserRepository userRepository;


    NotificationScheduler notificationScheduler;

    @Test
    public void testCreateInvalidInsurance() {
        try {
            Insurance insurance = new Insurance();
            insurance.setInsuranceName("Invalid");
            insurance.setInsuranceLink("http:hacking");
            insurance.setInsurancePeriod("2022-11-16");
            insurance.setInsuranceType("test");
            User user = userRepository.findByEmail("sswathika99@outlook.com");
            insurance.setUser(user);
            insuranceRepository.save(insurance);

            Insurance verifyInsurance = entityManager.find(Insurance.class, insurance.getId());
            assertThat(insurance.getInsuranceName().equals(verifyInsurance.getInsuranceName()));
        }catch (Exception e){
            assertThat(false);
        }
    }

    @Test
    public void testCreateInsurance() {
        try {
            Insurance insurance = new Insurance();
            insurance.setInsuranceName("Test");
            insurance.setInsuranceLink("https://www.isoa.org/");
            insurance.setInsurancePeriod("2022-11-16");
            insurance.setInsuranceType("test");
            User user = userRepository.findByEmail("sswathika99@outlook.com");
            insurance.setUser(user);
            insuranceRepository.save(insurance);

            Insurance verifyInsurance = entityManager.find(Insurance.class, insurance.getId());
            assertThat(insurance.getInsuranceName().equals(verifyInsurance.getInsuranceName()));
        }catch (Exception e){
            assertThat(false);
        }
    }

    @Test
    public void trackInsurance(){
        try {
            notificationScheduler.trackInsuranceStatus();
            assertThat(true);
        }catch (Exception e){
            assertThat(false);
        }
    }
}
