package com.csmy.minyuanplus.support.education;

import com.csmy.minyuanplus.model.education.Grade;
import com.csmy.minyuanplus.model.education.GradeCourseStatistical;
import com.csmy.minyuanplus.model.education.GradeInfoStatistical;
import com.csmy.minyuanplus.support.util.SPUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zero on 16/6/27.
 */
public class EduGrade extends Util {
    public static final String GRADE = "grade";
    public static final String GRADE_HOME = "grade_home";
    public static final String VIEW_STATE = "view_state";
    public static final String GRADE_ACADEMIC_YEAR = "grade_academic_year";
    public static final String GRADE_TERM = "grade_term";
    public static final String GRADE_STATISTICAL = "grade_statistical";

    /**
     * 保存成绩html源码
     */
    public static void saveGradeHtml(String grade) {
        SPUtil.put(GRADE, grade);
    }

    /**
     * @return 获取成绩源码
     */
    public static String getGradeHtml() {
        return SPUtil.get(GRADE, GRADE).toString();
    }

    /**
     * 保存成绩统计html源码
     */
    public static void saveGradeStatisticalHtml(String gradeStatistical) {
        SPUtil.put(GRADE_STATISTICAL, gradeStatistical);
    }

    /**
     * @return 获取成绩统计源码
     */
    public static String getGradeStatisticalHtml() {
        return SPUtil.get(GRADE_STATISTICAL, GRADE_STATISTICAL).toString();
    }


    /**
     * 保存查询成绩的学年
     */
    public static void saveGradeAcadamicYear(String year) {
        SPUtil.put(GRADE_ACADEMIC_YEAR, year);
    }

    /**
     * @return 获取查询成绩的学年
     */
    public static String getGradeAcadamicYear() {
        return SPUtil.get(GRADE_ACADEMIC_YEAR, EduInfo.getCurrentAcademicYear()).toString();
    }


    /**
     * 保存查询成绩的学期
     */
    public static void saveGradeTerm(String term) {
        SPUtil.put(GRADE_TERM, term);
    }

    /**
     * @return 获取查询成绩的学期
     */
    public static String getGradeTerm() {
        return SPUtil.get(GRADE_TERM, EduInfo.getCurrentTerm()).toString();
    }


    /**
     * 保存查询成绩用的VIEWSTATE
     */
    public static void saveViewState() {
        Document document = Jsoup.parse(getGradeHomeHtml());
        Elements elements = document.select("input[name=__VIEWSTATE]");
        String view_state = elements.get(0).attr("value");
        try {
            view_state = URLEncoder.encode(view_state, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SPUtil.put(VIEW_STATE, view_state);
    }

    /**
     * @return 获取查询成绩用的VIEWSTATE
     */
    public static String getViewState() {

        return SPUtil.get(VIEW_STATE, VIEW_STATE).toString();
    }


    /**
     * 保存查询成绩主页html源码
     */
    public static void saveGradeHomeHtml(String grade) {
        SPUtil.put(GRADE_HOME, grade);
    }

    /**
     * @return 获取查询成绩主页源码
     */
    public static String getGradeHomeHtml() {

        return SPUtil.get(GRADE_HOME, GRADE_HOME).toString();
    }


    /**
     * @return 学年列表
     */
    public static List<String> getAcademicYear() {
        List<String> academicYearList = new ArrayList<String>();
        Document document = Jsoup.parse(getGradeHtml());
        Elements eAYs = document.select("select#ddlXN");
        for (int i = 0; i < eAYs.size(); i++) {
            Element eAY = eAYs.get(i);
            academicYearList.add(eAY.text());
        }
        return academicYearList;
    }


    /**
     * @return 获取成绩统计数据
     */
    public static List getGradeStatistical(){
        List gsList = new ArrayList();
        Document document = Jsoup.parse(getGradeStatisticalHtml());

        /**
         * 解析成绩信息统计并保存
         */
        GradeInfoStatistical gradeIS = new GradeInfoStatistical();
        String  xftj = document.select("span#xftj").text();
        String[] attrs = xftj.split("；");
        for (String s : attrs) {
        }
        for (int i = 0; i < attrs.length; i++) {
            String[] attr = attrs[i].split("分");
            String value = attr[1];
            switch (i){
                case 0 :
                    gradeIS.setCreditSelect(value);
                    break;
                case 1 :
                    gradeIS.setCreditObtain(value);
                    break;
                case 2 :
                    gradeIS.setCreditRestudy(value);
                    break;
                case 3 :
                    value = value.split("。")[0].trim();
                    gradeIS.setCreditFail(value);
                    break;
            }
        }
        String pjxfjd = document.select("span#pjxfjd").text().split("：")[1];
        String xfjdzh = document.select("span#xfjdzh").text().split("：")[1];
        gradeIS.setGpaAverage(pjxfjd);
        gradeIS.setGpaSum(xfjdzh);
//        Logger.d(xftj);
//        Logger.d(gradeIS.toString());
        gsList.add(gradeIS);
        /**
         * 解析课程学分统计并保存
         */
        Elements trs = document.select("table#Datagrid2").select("tr");
        for (int i = 1; i <=5; i++) {
            Elements tds = trs.get(i).select("td");
            GradeCourseStatistical gradeCS = new GradeCourseStatistical();
            for (int j = 0; j < tds.size(); j++) {
                String td = tds.get(j).text();
                switch (j){
                    case 0:
                        gradeCS.setCourseProperty(td);
                        break;
                    case 1:
                        gradeCS.setCreditRequire(td);
                        break;
                    case 2:
                        gradeCS.setCreditObtain(td);
                        break;
                    case 3:
                        gradeCS.setCreditFail(td);
                        break;
                    case 4:
                        gradeCS.setCreditNeed(td);
                        break;

                }
            }
            gsList.add(gradeCS);
        }
        /**
         * 解析公共任选课信息并保存
         */
        Elements trs2 = document.select("table#DataGrid6").select("tr");
        for (int i = 1; i <trs2.size(); i++) {
            Elements tds = trs2.get(i).select("td");
            GradeCourseStatistical gradeCS = new GradeCourseStatistical();
            for (int j = 0; j < tds.size(); j++) {
                String td = tds.get(j).text();
                switch (j){
                    case 0:
                        gradeCS.setCourseProperty(td);
                        break;
                    case 1:
                        gradeCS.setCreditRequire(td);
                        break;
                    case 2:
                        gradeCS.setCreditObtain(td);
                        break;
                    case 3:
                        gradeCS.setCreditFail(td);
                        break;
                    case 4:
                        gradeCS.setCreditNeed(td);
                        break;

                }
            }
            gsList.add(gradeCS);
        }
        return gsList;
    }


    /**
     *
     * @return 至今未通过成绩集合
     */
    public static List<Grade> getGradeFail(){
        List<Grade> gradeList = new ArrayList<Grade>();
        Document doucument = Jsoup.parse(getGradeHomeHtml());
        Elements elements = doucument.select("table#Datagrid3").select("tr");
        for (int i = 1; i < elements.size(); i++) {
            Element tr = elements.get(i);
            Elements tds = tr.select("td");

            Grade grade = new Grade();

            String courseName = tds.get(1).text();
            String courseType = tds.get(2).text();
            String gpa = tds.get(3).text();
            String level = tds.get(4).text();

            grade.setCourseName(courseName);
            grade.setCourseType(courseType);
            grade.setGpa(gpa);
            grade.setLevel(level);

            gradeList.add(grade);
        }
        return gradeList;
    }

    /**
     *
     * @return 成绩集合
     */
    public static List<Grade> getGrade() {
        List<Grade> gradeList = new ArrayList<Grade>();
        Document doucument = Jsoup.parse(getGradeHtml());
        Elements elements = doucument.select("table#Datagrid1").select("tr");
        for (int i = 1; i < elements.size(); i++) {
            Element tr = elements.get(i);
            Elements tds = tr.select("td");

            Grade grade = new Grade();
            String academicYear = tds.get(0).text();
            String term = tds.get(1).text();
            String courseName = tds.get(3).text();
            String courseType = tds.get(4).text();
            String gpa = tds.get(6).text();
            String ordinaryLevel = tds.get(7).text();
            String terminalLevel = tds.get(9).text();
            String level = tds.get(11).text();
            grade.setAcademicYear(academicYear);
            grade.setTerm(term);
            grade.setCourseName(courseName);
            grade.setCourseType(courseType);
            grade.setGpa(gpa);
            grade.setOrdinaryLevel(ordinaryLevel);
            grade.setTerminalLevel(terminalLevel);
            grade.setLevel(level);

            gradeList.add(grade);
            Logger.d("学年:" + academicYear + " 学期:" + term + " " + courseName + " 绩点：" + gpa + " 平时成绩：" + ordinaryLevel + " 期末成绩:" + terminalLevel + " 总成绩" + level);
        }
        return gradeList;
    }


}
