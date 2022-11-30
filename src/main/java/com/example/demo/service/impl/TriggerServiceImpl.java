package com.example.demo.service.impl;

import com.example.demo.common.BaseServiceImpl;
import com.example.demo.entity.AuthDemo;
import com.example.demo.mapper.TriggerMapper;
import com.example.demo.service.ITriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author elliott
 */
@Service
@Slf4j
public class TriggerServiceImpl extends BaseServiceImpl<TriggerMapper, AuthDemo> implements ITriggerService {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(24, 24, 0L, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>());

    LinkedBlockingQueue<List<AuthDemo>> authListList = new LinkedBlockingQueue<>();
    List<AuthDemo> authList;

    LinkedBlockingQueue<String> field1 = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> field2 = new LinkedBlockingQueue<>();
    LinkedBlockingQueue<Integer> node = new LinkedBlockingQueue<>();

    int count = 0;


    @Override
    public void insert2() throws InterruptedException {
        triggerField1();
        triggerField2();
        triggerField3();
        Thread.sleep(9000);
        triggerField4();
        triggerField5();
    }

    void triggerField1(){
        System.out.println("triggerField1");
        EXECUTOR_SERVICE.submit(this::saveField1);
        EXECUTOR_SERVICE.submit(this::saveField1);
        EXECUTOR_SERVICE.submit(this::saveField1);
    }
    void triggerField2(){
        System.out.println("triggerField2");
        EXECUTOR_SERVICE.submit(this::saveField2);
        EXECUTOR_SERVICE.submit(this::saveField2);
        EXECUTOR_SERVICE.submit(this::saveField2);
    }
    void triggerField3(){
        System.out.println("triggerField3");
        EXECUTOR_SERVICE.submit(this::saveNode);
        EXECUTOR_SERVICE.submit(this::saveNode);
        EXECUTOR_SERVICE.submit(this::saveNode);
    }
    void triggerField4(){
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
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
        EXECUTOR_SERVICE.submit(this::assemblyData);
    }
    void triggerField5(){
        System.out.println("triggerField5");
        EXECUTOR_SERVICE.submit(this::saveList);
    }



    void assemblyData() {
        authList = new ArrayList<>(1000);
        AuthDemo authDemo = new AuthDemo();
        do {
            authDemo.setField2(field1.remove());
            authDemo.setField1(field2.remove());
            authDemo.setNode(node.remove());
            authList.add(authDemo);
        } while (authList.size() < 999);
        authListList.add(authList);
        System.out.println(authListList.size());
    }

    void saveField1() {
        do {
            field1.add(getUUID());
        } while (count < 200000);
    }

    void saveField2() {
        do {
            field2.add(getRandom());
        } while (count < 200000);
    }

    void saveNode() {
        do {
            node.add(getRandomAssign());
        } while (count < 200000);
    }

    void saveList() {
        do {
            if (authListList.size() > 0){
                this.saveBatch(authListList.remove());
                count ++;
                System.out.println("当前插入次数:" + count);
            }
        } while (count < 200000 && authListList.size() > 1);
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
