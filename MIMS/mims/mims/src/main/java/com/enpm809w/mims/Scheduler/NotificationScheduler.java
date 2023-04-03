package com.enpm809w.mims.Scheduler;

import com.enpm809w.mims.Common.Insurance;
import com.enpm809w.mims.Common.SendEmail;
import com.enpm809w.mims.Repository.InsuranceRepository;
import com.enpm809w.mims.Services.InsuranceDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Component
@EnableAsync
public class NotificationScheduler {


    @Autowired
    private InsuranceDetailsService insuranceDetailsService;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private  SendEmail sendEmail;

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void trackInsuranceStatus() throws ParseException, MessagingException, UnsupportedEncodingException, IOException {
        List<Insurance> insuranceList = insuranceRepository.findAll();
        for (Insurance currentInsurance: insuranceList) {
            try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String dateInString = currentInsurance.getInsurancePeriod();


            Date date = formatter.parse(dateInString);

            Date todayDate = formatter.parse(formatter.format(new Date()));

            long time_difference = date.getTime() - todayDate.getTime();

            long days_difference = (time_difference / (1000*60*60*24)) % 365;
            if(days_difference<=3){
                sendEmail.sendNotificationEmail(currentInsurance, days_difference);
            }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
