package com.example.demo.service.impl;

import com.example.demo.common.BaseServiceImpl;
import com.example.demo.entity.AuthDemo;
import com.example.demo.mapper.TriggerMapper;
import com.example.demo.service.ITriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * @author elliott
 */
@Service
@Slf4j
public class TriggerServiceImpl extends BaseServiceImpl<TriggerMapper, AuthDemo> implements ITriggerService {

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(15, 15, 0L, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>());

    LinkedBlockingQueue<List<AuthDemo>> authListList = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> field1s = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> field2s = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> nodes = new LinkedBlockingQueue<>();

    int count = 0;


    @Override
    public void insert() throws InterruptedException {
        triggerField1();
        triggerField2();
        triggerField3();
        triggerField4();
        triggerField5();
    }

    void triggerField1() {
        System.out.println("triggerField1");
        EXECUTOR_SERVICE.submit(this::saveField1);
    }

    void triggerField2() {

        System.out.println("triggerField2");
        EXECUTOR_SERVICE.submit(this::saveField2);
    }

    void triggerField3() {
        System.out.println("triggerField3");
        EXECUTOR_SERVICE.submit(this::saveNode);
    }

    void triggerField4() {
        System.out.println("triggerField4");
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
    }

    void triggerField5() throws InterruptedException {
        System.out.println("triggerField5");
        Thread.sleep(9999);
        EXECUTOR_SERVICE.submit(this::saveList);
        EXECUTOR_SERVICE.submit(this::saveList);
        EXECUTOR_SERVICE.submit(this::saveList);
    }


    void assemblyData() {
        List<AuthDemo> authList = new Vector<>();
        while (true) {
            authList.clear();
            if (count < 200000) {
                do {
                    AuthDemo authDemo = new AuthDemo();
                    authDemo.setField2(field1s.remove());
                    authDemo.setField1(field2s.remove());
                    authDemo.setNode(nodes.remove());
                    authList.add(authDemo);
//                    System.out.println(Thread.currentThread().getName() + ": assemblyData");
                } while (authList.size() < 999);
                authListList.offer(authList);
            } else {
                log.info("assemblyData + count:{}", count);
            }
        }

    }

    void saveField1() {
        while (true) {
            if (count < 200000) {
                field1s.offer(getUUID());
            } else {
                log.info("saveField1 + count:{}", count);
            }
        }
    }

    void saveField2() {
        while (true) {
            if (count < 200000) {
                field2s.offer(getRandom());
            } else {
                log.info("saveField2 + count:{}", count);
            }
        }
    }

    void saveNode() {
        while (true) {
            if (count < 200000) {
                nodes.offer(getRandomAssign());
            } else {
                log.info("saveNode + count:{}", count);
            }
        }
    }

    void saveList() {
        while (true) {
            if (count < 200000 && authListList.size() > 1) {
                this.saveBatch(authListList.remove());
                System.out.println(Thread.currentThread().getName() + ": saveBatch");
                count++;
                System.out.println("===============================================================");
            } else {
                System.out.printf("saveList:%d --- authListList:%d", count, authListList.size());
            }
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private int getRandomAssign() {
        return ThreadLocalRandom.current().nextInt(0, 111112);
    }

    private int getRandom() {
        return ThreadLocalRandom.current().nextInt();
    }
}
