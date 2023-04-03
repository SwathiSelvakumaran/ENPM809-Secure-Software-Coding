package com.enpm809w.mims.Details;

import java.net.URL;
import java.util.List;

import com.enpm809w.mims.Common.User;
import com.enpm809w.mims.Repository.UserRepository;
import com.enpm809w.mims.Services.InsuranceDetailsService;
import com.enpm809w.mims.Services.UserDetailService;
import com.enpm809w.mims.Common.Insurance;
import com.enpm809w.mims.Repository.InsuranceRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InsuranceDetails implements InsuranceDetailsService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UserRepository userRepository;

    private String userAuthenticated(){
        String userName = "";
        return userName;
    }
    @Override
    public List<Insurance> getAllInsurances() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        return insuranceRepository.findAll();
    }

    @Override
    public void saveInsurance(Insurance insurance) throws Exception {
        if(!isValidUrl(insurance.getInsuranceLink())){
            throw new Exception("Invalid URL");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        insurance.setUser(user);
        this.insuranceRepository.save(insurance);
    }

    boolean isValidUrl(String url) throws Exception {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Insurance getInsuranceById(long id) {
        Optional<Insurance> optional = insuranceRepository.findById(id);
        Insurance insurance = null;
        if (optional.isPresent()) {
            insurance = optional.get();
        } else {
            throw new RuntimeException(" Insurance not found for id :: " + id);
        }
        return insurance;
    }

    @Override
    public void deleteInsuranceById(long id) {
        this.insuranceRepository.deleteById(id);
    }

    @Override
    public List<Insurance> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);

        return this.insuranceRepository.findByUserId(user.getId(),pageable);
    }
}
