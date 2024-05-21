package com.div.service;

import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.div.dao.FormDetailDAO;
import com.div.dto.FormDetailDTO;

@Service
public class SchedulerService {

    @Autowired
    private FormDetailDAO formDetailDAO;

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void checkAndUpdateAccessCategories() {
        Date today = new Date(System.currentTimeMillis());
        List<FormDetailDTO> formDetails = formDetailDAO.findFreeAccessWithExpiry(today);

        for (FormDetailDTO detail : formDetails) {
            if ("free".equals(detail.getAccessCategory()) && detail.getFreeViewExpiry() != null) {
                Date freeViewExpiry = truncateTime(detail.getFreeViewExpiry());
                Date currentDate = truncateTime(today);

                if (freeViewExpiry.equals(currentDate)) {
                    detail.setAccessCategory("premium");
                    detail.setPremium(true);
                    formDetailDAO.update(detail);
                }
            }
        }
    }

    private Date truncateTime(Date date) {
        return Date.valueOf(date.toLocalDate());
    }
}
