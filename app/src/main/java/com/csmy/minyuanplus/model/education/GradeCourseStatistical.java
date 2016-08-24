package com.csmy.minyuanplus.model.education;

/**
 * 课程性质成绩统计实体类
 * Created by Zero on 16/7/2.
 */
public class GradeCourseStatistical {
    //课程性质
    String courseProperty;
    //学分要求
    String creditRequire;
    //获得学分
    String creditObtain;
    //未通过学分
    String creditFail;
    //还需学分
    String creditNeed;


    public String getCourseProperty() {
        return courseProperty;
    }

    public void setCourseProperty(String courseProperty) {
        this.courseProperty = courseProperty;
    }

    public String getCreditRequire() {
        return creditRequire;
    }

    public void setCreditRequire(String creditRequire) {
        this.creditRequire = creditRequire;
    }

    public String getCreditObtain() {
        return creditObtain;
    }

    public void setCreditObtain(String creditObtain) {
        this.creditObtain = creditObtain;
    }

    public String getCreditFail() {
        return creditFail;
    }

    public void setCreditFail(String creditFail) {
        this.creditFail = creditFail;
    }

    public String getCreditNeed() {
        return creditNeed;
    }

    public void setCreditNeed(String creditNeed) {
        this.creditNeed = creditNeed;
    }

    @Override
    public String toString() {
        return "GradeCourseStatistical{" +
                "courseProperty='" + courseProperty + '\'' +
                ", creditRequire='" + creditRequire + '\'' +
                ", creditObtain='" + creditObtain + '\'' +
                ", creditFail='" + creditFail + '\'' +
                ", creditNeed='" + creditNeed + '\'' +
                '}';
    }
}
