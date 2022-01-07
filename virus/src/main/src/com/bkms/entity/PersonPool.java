package com.bkms.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PersonPool {
    private static PersonPool personPool = new PersonPool();

    public static PersonPool getInstance() {
        return personPool;
    }

    List<Person> personList = new ArrayList<>();

    public List<Person> getPersonList() {
        return personList;
    }

    private PersonPool() {
        // 城市中心
        City city = new City(400, 400);
        // 模拟5000人口
        for (int i = 0; i < 5000; i++) {
            Random random = new Random();
            // 集中分布在城市中心
            int x = (int) (100 * random.nextGaussian() + city.getCenterX());
            int y = (int) (100 * random.nextGaussian() + city.getCenterY());
            if (x > 700) x = 700;
            Person person = new Person(city, x, y);
            personList.add(person);
        }
    }
}
