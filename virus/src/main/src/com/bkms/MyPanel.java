package com.bkms;

import com.bkms.entity.Hospital;
import com.bkms.entity.Person;
import com.bkms.entity.PersonPool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 面板类
 */
public class MyPanel extends JPanel implements Runnable {
    // 模拟世界时间
    public static int worldTime = 0;

    public MyPanel() {
        // 添加背景颜色
        this.setBackground(new Color(0x444444));
    }

    /**
     * GUI界面绘制方法
     * @param arg0 画笔
     */
    @Override
    public void paint(Graphics arg0) {
        super.paint(arg0);
        arg0.setColor(new Color(0x00ff00));
        // 生成医院格子
        arg0.drawRect(Hospital.getInstance().getX(),
                Hospital.getInstance().getY(),
                Hospital.getInstance().getWidth(),
                Hospital.getInstance().getHeight());

        List<Person> people = PersonPool.getInstance().getPersonList();
        if (people == null) {
            return;
        }

        for (Person person : people) {
            // 人员状态
            switch (person.getState()) {
                case Person.State.NORMAL: {
                    arg0.setColor(new Color(0xdddddd));

                }
                break;
                case Person.State.SHADOW: {
                    arg0.setColor(new Color(0xffee00));

                }
                break;
                case Person.State.CONFIRMED:
                case Person.State.FREEZE: {
                    arg0.setColor(new Color(0xff0000));

                }
                break;
            }
            // 更新当前人员状态
            person.update();
            // 用当前颜色填充由指定矩形界定的椭圆
            arg0.fillOval(person.getX(), person.getY(), 3, 3);
        }
    }

    /**
     * 线程启动
     */
    public void run() {
        while (true) {
            // 开始绘制面板
            this.repaint();
            try {
                // 模拟时间
                Thread.sleep(100);
                worldTime++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

