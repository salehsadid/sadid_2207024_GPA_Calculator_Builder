package com.example.sadid_2207024_gpa_calculator_builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GpaCalc{

    private static final Map<String, Double> gradePointMap = new HashMap<>();

    static {
        gradePointMap.put("A+", 4.0);
        gradePointMap.put("A", 3.75);
        gradePointMap.put("A-", 3.5);
        gradePointMap.put("B+", 3.25);
        gradePointMap.put("B", 3.0);
        gradePointMap.put("B-", 2.75);
        gradePointMap.put("C+", 2.5);
        gradePointMap.put("C", 2.25);
        gradePointMap.put("D", 2.0);
        gradePointMap.put("F", 0.0);
    }


    public static double getGradePoint(String grade) {
        return gradePointMap.getOrDefault(grade, 0.0);
    }


    public static double calculateGPA(List<Course> courses) {
        double totalGradePoints = 0.0;
        double totalCredits = 0.0;

        for (Course course : courses) {
            double gradePoint = getGradePoint(course.getGrade());
            totalGradePoints += gradePoint * course.getCredit();
            totalCredits += course.getCredit();
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        return totalGradePoints / totalCredits;
    }


    public static double calculateTotalCredits(List<Course> courses) {
        double total = 0.0;
        for (Course course : courses) {
            total += course.getCredit();
        }
        return total;
    }

    public static String[] getAvailableGrades() {
        return new String[]{"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
    }
}
