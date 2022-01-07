package com.bkms.entity;

import com.bkms.MyPanel;
import com.bkms.constant.Constants;

import java.util.List;
import java.util.Random;

/**
 * 人类
 */
public class Person {
    private City city;
    private int x;
    private int y;
    private MoveTarget moveTarget;
    int sig = 1;

    double targetXU;
    double targetYU;
    double targetSig = 50;

    int infectedTime = 0; // 被感染的累计时间
    int confirmedTime = 0; // 病人已经被确诊的累计时间
    private float SAFE_DIST = 2f; // 安全距离

    /**
     * 当前人员状态接口
     * 利用数字标记不同人员状态
     */
    public interface State {
        int NORMAL = 0; // 正常
        int SUSPECTED = NORMAL + 1; // 疑似
        int SHADOW = SUSPECTED + 1; // 阴性
        int CONFIRMED = SHADOW + 1; // 确诊
        int FREEZE = CONFIRMED + 1; // 隔离
    }

    public Person(City city, int x, int y) {
        this.city = city;
        this.x = x;
        this.y = y;
        // Random.nextGaussian()遵循正态分布，范围-1 ~ 1，0左右的随机浮点数偏多
        targetXU = 100 * new Random().nextGaussian() + x;
        targetYU = 100 * new Random().nextGaussian() + y;
    }

    /**
     * 人员是否有流动意向
     * @return
     */
    public boolean wantMove() {
        double value = sig * new Random().nextGaussian() + Constants.u;
        // value>0的可能性非常低，大概率返回false
        return value > 0;
    }
    // 默认是正常状态
    private int state = State.NORMAL;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * 人员是否已经感染
     * @return
     */
    public boolean isInfected() {
        return state >= State.SHADOW;
    }

    /**
     * 刷新人员感染病毒时间病毒开始进入潜伏期
     */
    public void beInfected() {
        state = State.SHADOW;
        infectedTime = MyPanel.worldTime;
    }

    /**
     * 感染者与正常人之间的距离
     * @param person 当前流动人员
     * @return
     */
    public double distance(Person person) {
        return Math.sqrt(Math.pow(x - person.getX(), 2) + Math.pow(y - person.getY(), 2));
    }

    private void freezy() {
        state = State.FREEZE;
    }

    /**
     * 人员流动方法
     * @param x 人员位置经度
     * @param y 人员位置纬度
     */
    private void moveTo(int x, int y) {
        this.x += x;
        this.y += y;
    }

    /**
     * 规范移动方向方法
     * @param d
     * @param length
     * @return
     */
    private int disControl(int d,double length) {
        int ud = (int) (d / length);
        if (ud == 0 && d != 0) {
            if (d > 0) {
                ud = 1;
            } else {
                ud = -1;
            }
        }
        return ud;
    }

    /**
     * 模拟人员流动方法
     */
    private void action() {
        if (state == State.FREEZE) {
            return;
        }
        if (!wantMove()) {
            return;
        }
        if (moveTarget == null || moveTarget.isArrived()) {
            double targetX = targetSig * new Random().nextGaussian() + targetXU;
            double targetY = targetSig * new Random().nextGaussian() + targetYU;
            moveTarget = new MoveTarget((int) targetX, (int) targetY);
        }
        // 目的地距离原来位置的距离的横竖线
        int dX = moveTarget.getX() - x;
        int dY = moveTarget.getY() - y;
        // 利用勾股定理求出移动距离
        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (length < 1) {
            moveTarget.setArrived(true);
            return;
        }
        int udX = disControl(dX,length);
        int udY = disControl(dY,length);

        if (x > 700) {
            moveTarget = null;
            if (udX > 0) {
                udX = -udX;
            }
        }
        // 人员开始流动
        // 为保证人员流动速度一致
        // 线程每调用action方法会使流动人员移动1个单位
        moveTo(udX, udY);
    }

    /**
     * 更新人流与病毒传播状态
     */
    public void update() {
        if (state >= State.FREEZE) {
            return;
        }
        if (state == State.CONFIRMED && (MyPanel.worldTime - confirmedTime >=
                Constants.HOSPITAL_RECEIVE_TIME)) {
            Bed bed = Hospital.getInstance().pickBed();
            if (bed == null) {
                System.out.println("隔离区没有空床位");
            } else {
                // 为隔离病人分配床位
                state = State.FREEZE;
                x = bed.getX();
                y = bed.getY();
                bed.setEmpty(false);
            }
        }
        if (MyPanel.worldTime - infectedTime > Constants.SHADOW_TIME && state == State.SHADOW) {
            // 病毒潜伏期已过
            state = State.CONFIRMED;
            confirmedTime = MyPanel.worldTime;
        }
        // 模拟人员流动
        action();

        List<Person> people = PersonPool.getInstance().personList;
        if (state >= State.SHADOW) {
            return;
        }
        for (Person person : people) {
            if (person.getState() == State.NORMAL) {
                continue;
            }
            float random = new Random().nextFloat();
            if (random < Constants.BROAD_RATE && distance(person) < SAFE_DIST) {
                // 模拟人员被感染
                this.beInfected();
            }
        }
    }
}
