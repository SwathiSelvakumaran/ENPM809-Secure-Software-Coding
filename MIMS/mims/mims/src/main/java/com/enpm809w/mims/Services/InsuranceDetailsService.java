package com.enpm809w.mims.Services;

import java.util.List;

import com.enpm809w.mims.Common.Insurance;

public interface InsuranceDetailsService {
    List<Insurance> getAllInsurances();
    void saveInsurance(Insurance insurance) throws Exception;
    Insurance getInsuranceById(long id);
    void deleteInsuranceById(long id);
    List<Insurance> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
