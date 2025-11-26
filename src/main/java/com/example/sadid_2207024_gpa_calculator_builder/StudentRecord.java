package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.beans.property.*;
import java.util.List;

public class StudentRecord {
    private final IntegerProperty recordId;
    private final StringProperty studentName;
    private final StringProperty studentId;
    private final IntegerProperty totalCourses;
    private final DoubleProperty totalCredits;
    private final DoubleProperty cgpa;
    private final StringProperty averageGrade;
    private final StringProperty recordDate;
    private List<Course> courses;

    // Constructor for creating new record
    public StudentRecord(String studentName, String studentId, int totalCourses,
                         double totalCredits, double cgpa, String averageGrade, String recordDate) {
        this.recordId = new SimpleIntegerProperty(0);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentId = new SimpleStringProperty(studentId);
        this.totalCourses = new SimpleIntegerProperty(totalCourses);
        this.totalCredits = new SimpleDoubleProperty(totalCredits);
        this.cgpa = new SimpleDoubleProperty(cgpa);
        this.averageGrade = new SimpleStringProperty(averageGrade);
        this.recordDate = new SimpleStringProperty(recordDate);
    }

    // Constructor for loading from database
    public StudentRecord(int recordId, String studentName, String studentId, int totalCourses,
                         double totalCredits, double cgpa, String averageGrade, String recordDate) {
        this.recordId = new SimpleIntegerProperty(recordId);
        this.studentName = new SimpleStringProperty(studentName);
        this.studentId = new SimpleStringProperty(studentId);
        this.totalCourses = new SimpleIntegerProperty(totalCourses);
        this.totalCredits = new SimpleDoubleProperty(totalCredits);
        this.cgpa = new SimpleDoubleProperty(cgpa);
        this.averageGrade = new SimpleStringProperty(averageGrade);
        this.recordDate = new SimpleStringProperty(recordDate);
    }

    // Property getters for JavaFX binding
    public IntegerProperty recordIdProperty() {
        return recordId;
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public StringProperty studentIdProperty() {
        return studentId;
    }

    public IntegerProperty totalCoursesProperty() {
        return totalCourses;
    }

    public DoubleProperty totalCreditsProperty() {
        return totalCredits;
    }

    public DoubleProperty cgpaProperty() {
        return cgpa;
    }

    public StringProperty averageGradeProperty() {
        return averageGrade;
    }

    public StringProperty recordDateProperty() {
        return recordDate;
    }

    // Regular getters
    public int getRecordId() {
        return recordId.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getStudentId() {
        return studentId.get();
    }

    public int getTotalCourses() {
        return totalCourses.get();
    }

    public double getTotalCredits() {
        return totalCredits.get();
    }

    public double getCgpa() {
        return cgpa.get();
    }

    public String getAverageGrade() {
        return averageGrade.get();
    }

    public String getRecordDate() {
        return recordDate.get();
    }

    public List<Course> getCourses() {
        return courses;
    }

    // Setters
    public void setRecordId(int recordId) {
        this.recordId.set(recordId);
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public void setStudentId(String studentId) {
        this.studentId.set(studentId);
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses.set(totalCourses);
    }

    public void setTotalCredits(double totalCredits) {
        this.totalCredits.set(totalCredits);
    }

    public void setCgpa(double cgpa) {
        this.cgpa.set(cgpa);
    }

    public void setAverageGrade(String averageGrade) {
        this.averageGrade.set(averageGrade);
    }

    public void setRecordDate(String recordDate) {
        this.recordDate.set(recordDate);
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "StudentRecord{" +
                "recordId=" + getRecordId() +
                ", studentName='" + getStudentName() + '\'' +
                ", studentId='" + getStudentId() + '\'' +
                ", cgpa=" + getCgpa() +
                '}';
    }
}
