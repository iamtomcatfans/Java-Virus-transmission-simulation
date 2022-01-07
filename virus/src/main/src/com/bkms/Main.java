package com.bkms;

import com.bkms.constant.Constants;
import com.bkms.entity.Person;
import com.bkms.entity.PersonPool;

import javax.swing.*;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        //初始化创建新面板
        MyPanel p = new MyPanel();
        Thread panelThread = new Thread(p);
        //构造一个初始时不可见的新窗体
        JFrame frame = new JFrame("病毒传播仿真模拟");
        frame.add(p);
        //设置窗口的属性 窗口位置以及窗口的大小
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        //设置窗口可见
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //线程启动
        panelThread.start();

        List<Person> people = PersonPool.getInstance().getPersonList();
        for (int i = 0; i < Constants.ORIGINAL_COUNT; i++) {
            int index = new Random().nextInt(people.size() - 1);
            Person person = people.get(index);

            while (person.isInfected()) {
                index = new Random().nextInt(people.size() - 1);
                person = people.get(index);
            }
            // 随机感染部分人员
            person.beInfected();
        }
    }
}

//class Test {
//    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            System.out.println(new Random().nextGaussian());
//        }
//    }
//}
