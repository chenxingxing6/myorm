package org.apache.ibatis;

import javafx.util.Pair;

import java.util.Map;

/**
 * @Author: cxx
 * @Date: 2019/9/18 13:16
 */
public class Function {
    private String sql;
    private String sqlType;
    private String functionName;
    private String resultType;
    private String parameterType;
    private Map<Integer/*位置序号*/, Pair<String/*key*/, String/*type*/>> indexParamMap;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public Map<Integer, Pair<String, String>> getIndexParamMap() {
        return indexParamMap;
    }

    public void setIndexParamMap(Map<Integer, Pair<String, String>> indexParamMap) {
        this.indexParamMap = indexParamMap;
    }
}
