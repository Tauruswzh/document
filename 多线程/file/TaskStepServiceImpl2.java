package witpdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import witpdp.ProductApi;
import witpdp.constant.CommonConstant;
import witpdp.dto.AddTaskStepDTO;
import witpdp.entity.*;
import witpdp.exception.ExceptionCode;
import witpdp.exception.PdpRuntimeException;
import witpdp.mapper.*;
import witpdp.service.TaskStepService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class TaskStepServiceImpl2 extends ServiceImpl<TaskStepMapper, TaskStep>
        implements TaskStepService {

    @Autowired
    private Executor threadPoolExecutor;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ProductApi productApi;


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void addTaskStep(AddTaskStepDTO addTaskStepDTO){
        int size = addTaskStepDTO.getTaskIds().size();
        //多线程处理
        if (size > CommonConstant.THREAD_DO_SIZE) {
            log.info("method : addTaskStep  多线程执行  start");
            //需要总线程数
            int batch = size % CommonConstant.THREAD_DO_SIZE == 0 ? size / CommonConstant.THREAD_DO_SIZE : size / CommonConstant.THREAD_DO_SIZE + 1;

            final Integer threadCount = batch;

            try {
                CountDownLatch threadLatchs = new CountDownLatch(threadCount);
                AtomicBoolean isError = new AtomicBoolean(false);

                for (int i = 0; i < batch; i++) {
                    int end = (i + 1) * CommonConstant.THREAD_DO_SIZE;
                    if (end > size) {
                        end = size;
                    }
                    //单个线程处理任务数
                    List<Long> subTaskIds = addTaskStepDTO.getTaskIds().subList(i * CommonConstant.THREAD_DO_SIZE, end);
                    AddTaskStepDTO addTaskStep = new AddTaskStepDTO();
                    addTaskStep.setTaskIds(subTaskIds);
                    addTaskStep.setProductId(addTaskStepDTO.getProductId());
                    addTaskStep.setUserId(addTaskStepDTO.getUserId());
                    addTaskStep.setBatchNoMap(addTaskStepDTO.getBatchNoMap());
                    threadPoolExecutor.execute(() -> {

                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        TransactionStatus status = transactionManager.getTransaction(def);
                        try {
                            //执行添加任务步骤
                            this.doAddTaskStep(addTaskStep);
                        } catch (Exception e) {
                            e.printStackTrace();
                            isError.set(true);
                        } finally {
                            threadLatchs.countDown();
                        }

                        try {
                            //倒计时锁 设置超时时间
                            threadLatchs.await();
                            //判断是否异常
                            if (isError.get()) {
                                log.info("rollback  ============  start");
                                transactionManager.rollback(status);
                                log.info("rollback  ============  end");
                            } else {
                                log.info("commit  ============  start");
                                transactionManager.commit(status);
                                log.info("commit  ============  end");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isError.set(true);
                        }
                    });
                }
                threadLatchs.await();
                if (isError.get()) {
                    throw new PdpRuntimeException(ExceptionCode.SAAS_ADD_TASK_STEP_ERROR);
                }
                log.info("method  addTaskStep  多线程执行  end");
            } catch (Exception e) {
                e.printStackTrace();
                throw new PdpRuntimeException(ExceptionCode.SAAS_ADD_TASK_STEP_ERROR);
            }
            //任务较少使用单线程执行
        } else {
            log.info("method  addTaskStep  单线程执行  start");
            this.doAddTaskStep(addTaskStepDTO);
            log.info("method  addTaskStep  单线程执行  end");
        }
    }

}





