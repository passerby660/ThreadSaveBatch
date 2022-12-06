package com.example.demo.service.impl;

import cn.hutool.core.date.DateUtil;
import com.example.demo.common.BaseServiceImpl;
import com.example.demo.entity.AuthDemo;
import com.example.demo.mapper.TriggerMapper;
import com.example.demo.service.ITriggerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(16, 16, 0L, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>());

    LinkedBlockingQueue<List<AuthDemo>> authListList = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<String> field1s = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> field2s = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> nodes = new LinkedBlockingQueue<>();

    int count = 0;


    @Override
    public void insert() throws InterruptedException {
        timeRecord();
        triggerField1();
        triggerField2();
        triggerField3();
        triggerField4();
        triggerField5();
    }

    void triggerField1() {
//        System.out.println("triggerField1");
        EXECUTOR_SERVICE.submit(() -> saveField1());
//        EXECUTOR_SERVICE.submit(this::saveField1);
    }

    void triggerField2() {

//        System.out.println("triggerField2");
        EXECUTOR_SERVICE.submit(() -> saveField2());
//        EXECUTOR_SERVICE.submit(this::saveField2);
    }
    void triggerField3() {
//        System.out.println("triggerField3");
        EXECUTOR_SERVICE.submit(() -> saveNode());
//        EXECUTOR_SERVICE.submit(this::saveNode);
    }
    @SneakyThrows
    void triggerField4() {
        Thread.sleep(1000);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
        EXECUTOR_SERVICE.execute(this::assemblyData);
    }

    void triggerField5() throws InterruptedException {
        System.out.println("triggerField5");
        Thread.sleep(9999);
        EXECUTOR_SERVICE.submit(this::saveList);
        EXECUTOR_SERVICE.submit(this::saveList);
        EXECUTOR_SERVICE.submit(this::saveList);
    }


    @SneakyThrows
    void assemblyData() {
        List<AuthDemo> authList = new Vector<>();
        while (true) {
            authList.clear();
            if (count < 200000) {
                do {
                    AuthDemo authDemo = new AuthDemo();
                    authDemo.setField2(field1s.take());
                    authDemo.setField1(field2s.take());
                    authDemo.setNode(nodes.take());
//                    System.out.println(Thread.currentThread().getName());
                    authList.add(authDemo);
                } while (authList.size() < 1000 && !field1s.isEmpty() && !field2s.isEmpty() && !nodes.isEmpty());
//                System.out.println(Thread.currentThread().getName());
                authListList.offer(authList);
//                saveList2(authList);
            } else {
                log.info("assemblyData + count:{}", count);
            }
        }
    }

    @SneakyThrows
    void timeRecord(){
        BufferedWriter out = new BufferedWriter(new FileWriter("D:\\temp\\temp.txt"));
        out.write(DateUtil.date() + "\\n");
        out.close();
    }
    @SneakyThrows
    void saveField1() {
        while (true) {
            if (count < 200000) {
                field1s.offer(getUUID());
            } else {
                log.info("saveField1 + count:{}", count);
//                Thread.sleep(500);
            }
        }
    }

    @SneakyThrows
    void saveField2() {
        while (true) {
            if (count < 200000) {
                field2s.offer(getRandom());
            } else {
                log.info("saveField2 + count:{}", count);
//                Thread.sleep(500);
            }
        }
    }

    @SneakyThrows
    void saveNode() {
        while (true) {
            if (count < 200000) {
                nodes.offer(getRandomAssign());
            } else {
                log.info("saveNode + count:{}", count);
//                Thread.sleep(500);
            }
        }
    }

    void saveList() {
        while (true) {
            if (count < 200000) {
                if (authListList.size() > 1){
                    this.saveBatch(authListList.remove());
                    System.out.println(Thread.currentThread().getName() + ": saveBatch");
                    count++;
                    System.out.println("===============================================================");
                }
            } else {
                System.out.printf("saveList:%d --- authListList:%d", count, authListList.size());
                System.out.println();
                System.exit(1);
            }
        }
    }

    void saveList2(List<AuthDemo> authList) {
        if (count < 200001) {
            timeRecord();
            this.saveBatch(authList);
            System.out.println(Thread.currentThread().getName() + ": saveBatch");
            count++;
        } else {
            System.exit(1);
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
