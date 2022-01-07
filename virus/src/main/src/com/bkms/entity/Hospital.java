package com.bkms.entity;

import com.bkms.constant.Constants;

import java.util.ArrayList;
import java.util.List;

public class Hospital {
    private int x = 800;
    private int y = 110;
    private int width;
    private int height = 606;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private static Hospital hospital = new Hospital();
    // 医院实例
    public static Hospital getInstance() {
        return hospital;
    }
    // 显示位置
    private Point point = new Point(800, 100);
    // 存放创维的集合
    private List<Bed> beds = new ArrayList<>();

    /**
     * 初始化医院及病房位置
     */
    private Hospital() {
        // 医院停止运营的情况
        if (Constants.BED_COUNT == 0) {
            width = 0;
            height = 0;
        }
        int column = Constants.BED_COUNT / 100;
        width = column * 6;
        for (int i = 0; i < column; i++) {
            for (int j = 10; j <= 610; j += 6) {
                // 模拟床位点位置并添加空床位
                Bed bed = new Bed(point.getX() + i * 6, point.getY() + j);
                beds.add(bed);
            }
        }
    }

    /**
     * 挑选空床位的方法
     *
     * @return 供病人治疗的空床位
     */
    public Bed pickBed() {
        for (Bed bed : beds) {
            if (bed.isEmpty()) {
                return bed;
            }
        }
        return null;
    }
}
