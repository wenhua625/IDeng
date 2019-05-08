package com.novarise.webase.util;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.novarise.webase.framework.HtmlFunction;



public class ChartUtils {
	
	
	
	public static String getPie(String result[][],String chart_title,HttpServletRequest request){
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(int i=0;i<result.length;i++)
		  dataset.setValue(result[i][0], Double.parseDouble(result[i][1]));
		chart_title= HtmlFunction.parseVar(chart_title,request,"");
	      JFreeChart chart = ChartFactory.createPieChart(
	    	 chart_title, // chart title
	         dataset, // data
	         true, // include legend
	         true,
	         false);
	      
	      PiePlot pp = (PiePlot)chart.getPlot();
	      pp.setLabelGenerator(new StandardPieSectionLabelGenerator("{1}"));
	      
	     // pp.setLabelGenerator(null);
	      
	      //pp.setLabelLinksVisible(false);
	      
	      Font titleFont = new Font("黑体", Font.BOLD, 20);
	      TextTitle textTitle = chart.getTitle();
	      textTitle.setFont(titleFont);// 为标题设置上字体
	       
	      Font plotFont = new Font("宋体", Font.PLAIN, 16);
	      PiePlot plot = (PiePlot) chart.getPlot();
	      plot.setLabelFont(plotFont); // 为饼图元素设置上字体
	       
	      Font LegendFont = new Font("楷体", Font.PLAIN, 18);
	      LegendTitle legend = chart.getLegend(0);
	      legend.setItemFont(LegendFont);// 为图例说明设置字体
	      
	         
	      int width = 380; /* Width of the image */
	      int height = 380; /* Height of the image */ 
	      String url = ChartUtils.class.getResource("/").getPath();
			url = url.substring(1, url.length()-16);
			//System.out.println("URL:"+url);
		   String s_filecode = "chart/"+java.util.UUID.randomUUID().toString()+".jpg";
			File pieChart = new File( url+s_filecode);
	      //File pieChart = new File( "PieChart.jpeg" ); 
	      try {
			ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	      String s_url= "http://" + request.getServerName();
			if(request.getServerPort() == 443){
				s_url = "https://" + request.getServerName() + request.getContextPath();
			}else if(request.getServerPort() == 80){
				s_url=s_url+ request.getContextPath();
			}else 
				s_url=s_url+ ":" + request.getServerPort() + request.getContextPath();
			
		String reString = s_url+"/"+s_filecode;
		
		return reString;
	}
	
	
	public static String getBar(String result[][],String chart_title,HttpServletRequest request,String meta_data[],String s_width,String s_height)
	{
		
	      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	     
	      
	      for(int i=0;i<result.length;i++){
	    	 // String s_col[]=result[i];
	    	  for(int col=0;col<meta_data.length;col++){
	    		  dataset.addValue(Double.parseDouble(result[i][col+1]),meta_data[col], result[i][0]);
	    	  }
			  
			  
			 
	      }
	      
	      /*
	         * 解决字体乱码（在创建图表之前设定）
	         */
	        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
	        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
	        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
	        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
	        ChartFactory.setChartTheme(standardChartTheme);
	        
	       

	      JFreeChart barChart = ChartFactory.createBarChart(
	         chart_title, 
	         "", "金额", 
	         dataset,PlotOrientation.VERTICAL, 
	         true, true, false);
	      
	      CategoryPlot plot = barChart.getCategoryPlot();
	      BarRenderer renderer=new BarRenderer();

	      renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	      renderer.setBaseItemLabelsVisible(true);

	      plot.setRenderer(renderer);
	         
	      int width = Integer.parseInt(s_width);
	      int height = Integer.parseInt(s_height); /* Height of the image */ 
	      
	      String url = ChartUtils.class.getResource("/").getPath();
			url = url.substring(1, url.length()-16);
			//System.out.println("URL:"+url);
		   String s_filecode = "chart/"+java.util.UUID.randomUUID().toString()+".jpg";
			File file_BarChart = new File( url+s_filecode);
			
	     // File BarChart = new File( "BarChart.jpeg" ); 
	      try {
			ChartUtilities.saveChartAsJPEG( file_BarChart , barChart , width , height );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      String s_url= "http://" + request.getServerName();
			if(request.getServerPort() == 443){
				s_url = "https://" + request.getServerName() + request.getContextPath();
			}else if(request.getServerPort() == 80){
				s_url=s_url+ request.getContextPath();
			}else 
				s_url=s_url+ ":" + request.getServerPort() + request.getContextPath();
			
		String reString = s_url+"/"+s_filecode;
		
		return reString;
		
	}


	public static void main(String args[]){
		
		
		


		DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue("IPhone 5s", new Double( 20 ) );
	      dataset.setValue("SamSung Grand", new Double( 20 ) );
	      dataset.setValue("MotoG", new Double( 40 ) );
	      dataset.setValue("Nokia Lumia", new Double( 10 ) );

	      JFreeChart chart = ChartFactory.createPieChart(
	         "手机销售情况", // chart title
	         dataset, // data
	         true, // include legend
	         true,
	         false);
	      
	      
	      Font titleFont = new Font("黑体", Font.BOLD, 20);
	      TextTitle textTitle = chart.getTitle();
	      textTitle.setFont(titleFont);// 为标题设置上字体
	       
	      Font plotFont = new Font("宋体", Font.PLAIN, 16);
	      PiePlot plot = (PiePlot) chart.getPlot();
	      plot.setLabelFont(plotFont); // 为饼图元素设置上字体
	       
	      Font LegendFont = new Font("楷体", Font.PLAIN, 18);
	      LegendTitle legend = chart.getLegend(0);
	      legend.setItemFont(LegendFont);// 为图例说明设置字体
	      
	         
	      int width = 640; /* Width of the image */
	      int height = 480; /* Height of the image */ 
	      String url = ChartUtils.class.getResource("/").getPath();
			url = url.substring(1, url.length()-16);
			System.out.println("URL:"+url);
			File pieChart = new File( url+"chart/PieChart.jpg");
	      //File pieChart = new File( "PieChart.jpeg" ); 
	      try {
			ChartUtilities.saveChartAsJPEG( pieChart , chart , width , height );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
