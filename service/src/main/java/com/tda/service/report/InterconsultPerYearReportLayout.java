package com.tda.service.report;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ChartBuilderException;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import ar.com.fdvs.dj.domain.chart.DJChartOptions;
import ar.com.fdvs.dj.domain.chart.builder.DJAreaChartBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

public class InterconsultPerYearReportLayout {

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public DynamicReport buildReportLayout() throws ColumnBuilderException,
			ClassNotFoundException, ChartBuilderException {

		DynamicReportBuilder drb = new DynamicReportBuilder();

		Style headerStyle = new Style();
		headerStyle.setFont(Font.VERDANA_MEDIUM_BOLD);
		headerStyle.setBorderBottom(Border.PEN_2_POINT);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		headerStyle.setBackgroundColor(Color.DARK_GRAY);
		headerStyle.setTextColor(Color.WHITE);
		headerStyle.setTransparency(Transparency.OPAQUE);

		AbstractColumn columnYear = ColumnBuilder.getNew()
				.setColumnProperty("year", String.class.getName())
				.setTitle("Año").setWidth(new Integer(85)).build();
		AbstractColumn columnQuantity = ColumnBuilder.getNew()
				.setColumnProperty("quantity", Integer.class.getName())
				.setTitle("Cantidad").setWidth(new Integer(85)).build();

		drb.addColumn(columnYear);
		drb.addColumn(columnQuantity);

		drb.setTitle("Interconsultas por año")
				.setSubtitle("Este reporte fue generado en " + new Date())
				.setUseFullPageWidth(true);

		GroupBuilder gb1 = new GroupBuilder();
		DJGroup g1 = gb1.setCriteriaColumn((PropertyColumn) columnYear)
		// .addFooterVariable(columnSexName, DJCalculation.NOTHING)
		// .addVariable("group_sex_name", columnSexName,
		// DJCalculation.)
				.setGroupLayout(GroupLayout.DEFAULT).build();

		drb.addGroup(g1);

		drb.setUseFullPageWidth(true);
		drb.setDefaultStyles(null, null, headerStyle, null);
		DJChart djChart = new DJAreaChartBuilder()
				// chart
				.setX(20)
				.setY(10)
				.setWidth(500)
				.setHeight(250)
				.setCentered(false)
				.setBackColor(Color.LIGHT_GRAY)
				.setShowLegend(false)
				.setPosition(DJChartOptions.POSITION_FOOTER)
				// .setTitle("Necesidades Basicas Insatisfechas por Destino")
				.setTitleColor(Color.DARK_GRAY)
				.setTitleFont(Font.ARIAL_BIG_BOLD)
				// .setSubtitle("Distribucion de nbi por destino")
				.setSubtitleColor(Color.DARK_GRAY)
				.setSubtitleFont(Font.COURIER_NEW_BIG_BOLD)
				.setLegendColor(Color.DARK_GRAY)
				.setLegendFont(Font.COURIER_NEW_MEDIUM_BOLD)
				.setLegendBackgroundColor(Color.WHITE)
				.setLegendPosition(DJChartOptions.EDGE_BOTTOM)
				.setTitlePosition(DJChartOptions.EDGE_TOP)
				.setLineStyle(DJChartOptions.LINE_STYLE_DOTTED).setLineWidth(1)
				.setLineColor(Color.DARK_GRAY).setPadding(5)
				// dataset
				.setCategory((PropertyColumn) columnYear)
				.addSerie(columnQuantity).build();

		drb.addChart(djChart);

		HashMap vars = new HashMap();
		vars.put(columnQuantity, new JRDesignVariable());
		// vars.put(columnSexName, new JRDesignVariable());
		JRDesignGroup group = new JRDesignGroup();
		JRDesignChart chart = djChart.transform(new DynamicJasperDesign(), "",
				group, group, vars, 0);
		DynamicReport dr = drb.build();

		// Return the layout
		return dr;
	}
}