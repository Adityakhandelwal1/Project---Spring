package com.brs.projectx.stockparser.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brs.projectx.stockparser.model.StockData;
import com.brs.projectx.stockparser.service.JavaScraperService;
import com.brs.projectx.stockparser.service.StockParserService;

import redis.clients.jedis.Jedis;

@RestController
@CrossOrigin
public class StockParserController {
	
	@Autowired
	StockParserService stockParserService;
	
	@Autowired
	JavaScraperService javaScraperService;

	private Jedis jedis = new Jedis("localhost");;

	@RequestMapping(value = "/displayNames")
	public Set<String> displayAllData() {
		return jedis.keys("*");
	}

	@RequestMapping(value = "/displayData")
	public List<StockData> displayData() {
		return stockParserService.displayData();
	}
	
	@RequestMapping(value = "/getStocksHitting52Low")
	public List<StockData> getStocksHitting52Low() {
		return stockParserService.getStocksHitting52Low();
	}
	
	@RequestMapping(value = "/getStocksHitting52High")
	public List<StockData> getStocksHitting52High() {
		return stockParserService.getStocksHitting52High();
	}
	
	@RequestMapping(value = "/displayFivePercentHigh")
	public List<StockData> displayFivePercentHigh() {
		return stockParserService.displayFivePercentHigh();
	}
	
	@RequestMapping(value = "/displayFivePercentLow")
	public List<StockData> displayFivePercentLow() {
		return stockParserService.displayFivePercentLow();
	}

	@RequestMapping("/addData")
	public List<StockData> readCSV() throws IOException {
		return stockParserService.readCSV();
	}
	
	@RequestMapping("/test")
	public void test()
	{
		javaScraperService.updateStockCSVTable();
	}
}
