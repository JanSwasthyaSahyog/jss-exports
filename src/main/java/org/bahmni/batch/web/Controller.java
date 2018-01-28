package org.bahmni.batch.web;

import org.bahmni.batch.BatchConfiguration;
import org.bahmni.batch.form.domain.DateRange;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    BatchConfiguration batchConfiguration;

    @Autowired
    JobLauncher jobLauncher;

    @RequestMapping(path = "/form-export")
    public void runFormExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
        System.out.println("Running the job for date range "+startDate+"-"+endDate);
        DateRange dateRange = new DateRange();
        dateRange.setStartDateString(startDate);
        dateRange.setEndDateString(endDate);
        Job job = batchConfiguration.completeDataExport(dateRange);
        try {
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time",System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(path = "/inpatient-export")
    public void runInPatientExport(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
        System.out.println("Running the job for date range "+startDate+"-"+endDate);
        DateRange dateRange = new DateRange();
        dateRange.setStartDateString(startDate);
        dateRange.setEndDateString(endDate);
        Job job = batchConfiguration.inpatientExport(dateRange);
        try {
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .addLong("time",System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }

    }

}
