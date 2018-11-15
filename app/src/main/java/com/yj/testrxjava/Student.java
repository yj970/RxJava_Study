package com.yj.testrxjava;

import java.util.List;

public class Student {
    String name; // 学生名字
    List<Course> courseList; // 学生选的课程列表

    public Student(String name) {
        this.name = name;
    }

    public Student(String name, List<Course> courseList) {
        this.name = name;
        this.courseList = courseList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}
