package com.ew.gerocomium.common.config.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Configuration
public class QuartzConfig {
    // 预定过期
    @Bean
    public JobDetail reserveExpireJobDetail() {
        return newJobDetail(QuartzJob.ReserveExpireJob.class, "reserveExpireJobDetail");
    }

    @Bean
    public Trigger reserveExpireTrigger() {
        return newTrigger(reserveExpireJobDetail(), "reserveExpireJobTrigger", "0 0 0 ? * *");
    }

    // 合同到期
    @Bean
    public JobDetail contractExpireJobDetail() {
        return newJobDetail(QuartzJob.ContractExpireJob.class, "contractExpireJobDetail");
    }

    @Bean
    public Trigger contractExpireTrigger() {
        return newTrigger(contractExpireJobDetail(), "contractExpireJobTrigger", "0 0 8 ? * *");
    }

    // 任务封装
    private <T> JobDetail newJobDetail(Class<? extends QuartzJobBean> jobDetailClass, String jobDetailName) {
        return JobBuilder
                // 业务类
                .newJob(jobDetailClass)
                // 任务编号/名称
                .withIdentity(jobDetailName)
                // 每个JobDetail内都有一个Map，包含了关联到这个Job的数据，在Job类中可以通过context获取
                .usingJobData("msg1", "jobDetail1")
                .usingJobData("msg2", "jobDetail2")
                // 即使没有Trigger关联时，也不需要删除该JobDetail
                .storeDurably()
                .build();
    }

    // 触发器封装
    private Trigger newTrigger(JobDetail jobDetail, String triggerName, String dateStr) {
        return TriggerBuilder
                .newTrigger()
                // 关联上述的JobDetail
                .forJob(jobDetail)
                // 给Trigger命名
                .withIdentity(triggerName)
                // 定时
                // 秒 分 时 天  月 年
                .withSchedule(CronScheduleBuilder.cronSchedule(dateStr))
                .build();
    }
}
