package com.csmy.minyuanplus.model.education;

/**
 * 成绩信息统计实体类
 * Created by Zero on 16/8/8.
 */
public class GradeInfoStatistical {
    //所选学分
    String creditSelect;
    //获得学分
    String creditObtain;
    //重修学分
    String creditRestudy;
    //正考未通过学分
    String creditFail;
    //平均学分绩点
    String gpaAverage;
    //学分绩点总和
    String gpaSum;

    public String getCreditSelect() {
        return creditSelect;
    }

    public void setCreditSelect(String creditSelect) {
        this.creditSelect = creditSelect;
    }

    public String getCreditObtain() {
        return creditObtain;
    }

    public void setCreditObtain(String creditObtain) {
        this.creditObtain = creditObtain;
    }

    public String getCreditRestudy() {
        return creditRestudy;
    }

    public void setCreditRestudy(String creditRestudy) {
        this.creditRestudy = creditRestudy;
    }

    public String getCreditFail() {
        return creditFail;
    }

    public void setCreditFail(String creditFail) {
        this.creditFail = creditFail;
    }

    public String getGpaAverage() {
        return gpaAverage;
    }

    public void setGpaAverage(String gpaAverage) {
        this.gpaAverage = gpaAverage;
    }

    public String getGpaSum() {
        return gpaSum;
    }

    public void setGpaSum(String gpaSum) {
        this.gpaSum = gpaSum;
    }

    @Override
    public String toString() {
        return "GradeInfoStatistical{" +
                "creditSelect='" + creditSelect + '\'' +
                ", creditObtain='" + creditObtain + '\'' +
                ", creditRestudy='" + creditRestudy + '\'' +
                ", creditFail='" + creditFail + '\'' +
                ", gpaAverage='" + gpaAverage + '\'' +
                ", gpaSum='" + gpaSum + '\'' +
                '}';
    }
}
