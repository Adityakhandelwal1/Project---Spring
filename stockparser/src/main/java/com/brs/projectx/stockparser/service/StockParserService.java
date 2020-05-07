package com.brs.projectx.stockparser.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brs.projectx.stockparser.model.StockData;

import redis.clients.jedis.Jedis;

@Service
public class StockParserService {

	@Autowired
	JavaScraperService javaScraperService;

	private Jedis jedis = new Jedis("localhost");;

	public List<StockData> displayData() {
		Set<String> set = jedis.keys("*");
		List<StockData> listStockData = new ArrayList<>();
		for (String temp : set) {
			listStockData.add(convertStringTOModel(jedis.get(temp)));
		}
		return listStockData;
	}

	public List<StockData> getStocksHitting52Low() {
		List<StockData> listStockData = displayData();
		List<StockData> listStockData52Low = new ArrayList<>();
		for (StockData stockData : listStockData) {
			String[] s = stockData.getWeekRange().split(" - ");
			Float lowPrice = Float.parseFloat(s[0].trim());
			Float currPrice = Float.parseFloat(stockData.getPrices().trim());
			if (Math.ceil(currPrice) == Math.ceil(lowPrice)) {
				listStockData52Low.add(stockData);
			}
		}
		return listStockData52Low;
	}

	public List<StockData> getStocksHitting52High() {
		List<StockData> listStockData = displayData();
		List<StockData> listStockData52High = new ArrayList<>();
		for (StockData stockData : listStockData) {
			String[] s = stockData.getWeekRange().split(" - ");
			Float highPrice = Float.parseFloat(s[1].trim());
			Float currPrice = Float.parseFloat(stockData.getPrices().trim());
			if (Math.ceil(currPrice) == Math.ceil(highPrice)) {
				listStockData52High.add(stockData);
			}
		}
		return listStockData52High;
	}

	public List<StockData> displayFivePercentHigh() {
		List<StockData> listStockData = displayData();
		List<StockData> listStockDataFivePercentHigh = new ArrayList<>();
		for (StockData stockData : listStockData) {
			String s = stockData.getPercentageChange();
			if (s == "N/")
				s = "0.00%";
			if (Math.abs(Float.parseFloat(s.substring(0, s.length() - 1))) > 5) {
				listStockDataFivePercentHigh.add(stockData);
			}
		}
		return listStockDataFivePercentHigh;
	}

	public List<StockData> displayFivePercentLow() {
		List<StockData> listStockData = displayData();
		List<StockData> listStockDataFivePercentHigh = new ArrayList<>();
		for (StockData stockData : listStockData) {
			String s = stockData.getPercentageChange();
			if (s == "N/")
				s = "0.00%";
			if (Math.abs(Float.parseFloat(s.substring(0, s.length() - 1))) <= 5) {
				listStockDataFivePercentHigh.add(stockData);
			}
		}
		return listStockDataFivePercentHigh;
	}

	public List<StockData> readCSV() throws IOException {

//		javaScraperService.updateStockCSVTable();
		List<StockData> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("src/main/resources/output.csv"));
		String line;
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] fields = line.split(",");
			StockData stockData = new StockData();
			stockData.setName(fields[0]);
			stockData.setPrices(fields[1]);
			stockData.setChange(fields[2]);
			stockData.setPercentageChange(fields[3]);
			stockData.setMarketCap(fields[4]);
			stockData.setAverageVolume(fields[5]);
			stockData.setVolume(fields[6]);
			stockData.setWeekRange(fields[7]);
			jedis.set(stockData.getName(), stockData.toString());
			list.add(stockData);
		}
		br.close();
		return list;
	}

	private StockData convertStringTOModel(String str) {
		StockData stockData = new StockData();
		if (str.length() > 1) {
			str = str.replace("StockData", "");
			str = str.substring(1, str.length() - 1);
			String[] keyValuePairs = str.split(",");
			String name = keyValuePairs[0].split("=")[1].trim();
			String prices = keyValuePairs[1].split("=")[1].trim();
			String percentageChange = keyValuePairs[3].split("=")[1].trim();
			String marketCap = keyValuePairs[4].split("=")[1].trim();
			String avgVolume = keyValuePairs[5].split("=")[1].trim();
			String volume = keyValuePairs[6].split("=")[1].trim();
			String weekRange = keyValuePairs[7].split("=")[1].trim();
			String change = keyValuePairs[2].split("=")[1].trim();

			stockData.setName(name);
			stockData.setPrices(prices);
			stockData.setChange(change);
			stockData.setPercentageChange(percentageChange);
			stockData.setMarketCap(marketCap);
			stockData.setAverageVolume(avgVolume);
			stockData.setVolume(volume);
			stockData.setWeekRange(weekRange);
		}

		return stockData;

	}

}
