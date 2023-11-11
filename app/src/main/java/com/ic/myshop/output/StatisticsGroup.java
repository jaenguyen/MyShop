package com.ic.myshop.output;

import com.ic.myshop.model.Statistics;

import java.util.ArrayList;
import java.util.List;

public class StatisticsGroup {

    private String date;
    private List<Statistics> statisticsList;

    public StatisticsGroup() {
        statisticsList = new ArrayList<>();
    }

    public StatisticsGroup(String date, List<Statistics> statisticsList) {
        this.date = date;
        this.statisticsList = statisticsList;
    }

    public void addStatistics(Statistics statistics) {
        this.statisticsList.add(statistics);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Statistics> getStatisticsList() {
        return statisticsList;
    }

    public void setStatisticsList(List<Statistics> statisticsList) {
        this.statisticsList = statisticsList;
    }
}
