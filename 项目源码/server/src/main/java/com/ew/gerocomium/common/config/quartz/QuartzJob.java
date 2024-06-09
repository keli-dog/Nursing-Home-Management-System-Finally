package com.ew.gerocomium.common.config.quartz;

import com.ew.gerocomium.service.ContractService;
import com.ew.gerocomium.service.ReserveService;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class QuartzJob {
    @Resource
    private ReserveService reserveService;
    @Resource
    private ContractService contractService;

    // 床位预定过期任务
    public class ReserveExpireJob extends QuartzJobBean {
        @SneakyThrows
        @Override
        protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            reserveService.reserveExpireJob();
        }
    }

    // 老人合同到期任务
    public class ContractExpireJob extends QuartzJobBean {
        @SneakyThrows
        @Override
        protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            contractService.contractExpireJob();
        }
    }
}
