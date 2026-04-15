package com.gymbrut.app.utils;

import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class ChartUtils {
    public static void bindLineChart(LineChart chart, List<Float> values, String label) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) entries.add(new Entry(i, values.get(i)));
        LineDataSet set = new LineDataSet(entries, label);
        set.setColor(Color.parseColor("#D9FF1F"));
        set.setCircleColor(Color.parseColor("#11D8FF"));
        set.setValueTextColor(Color.WHITE);
        set.setLineWidth(2.5f);
        chart.setData(new LineData(set));
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.LTGRAY);
        chart.getAxisLeft().setTextColor(Color.LTGRAY);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }

    public static void bindCompletionChart(PieChart chart, float completed, float remaining) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(remaining, "Remaining"));
        PieDataSet dataSet = new PieDataSet(entries, "Goals");
        dataSet.setColors(Color.parseColor("#D9FF1F"), Color.parseColor("#1A1D21"));
        PieData data = new PieData(dataSet);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
        chart.setCenterText(((int) completed) + "%");
        chart.setCenterTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.invalidate();
    }
}
