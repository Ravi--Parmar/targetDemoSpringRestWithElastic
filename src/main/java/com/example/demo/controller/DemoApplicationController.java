package com.example.demo.controller;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.ElasticSearchDao;

@Controller
@RequestMapping("/target")
public class DemoApplicationController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@Autowired
	ElasticSearchDao elasticSearchDao;

	@RequestMapping( method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public @ResponseBody Object addDocument(@RequestBody String input) {
		Object res = null;
		try {
			res = elasticSearchDao.AddDocument(input);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return e;
		}
		return res;
	}

	@RequestMapping(value = "/{index}/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Object getDocument(@PathVariable("index") String index, @PathVariable("id") String id) {
		Object obj = null;
		try {
			obj = elasticSearchDao.getSingalDocument(index,id);
		} catch (Exception ex) {
			return ex;
		}
		return obj;
	}
	
	@RequestMapping(value = "/{index}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Object getDocmentsByIndex(@PathVariable("index") String index) {
		Object obj = elasticSearchDao.getAllDocments(index);
		return obj;
	}

	

	@RequestMapping( method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Object UpdateDocument(@RequestBody String input) {
		Object obj = elasticSearchDao.updateDocment(input);
		return obj;
	}

	@RequestMapping(value = "/{index}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public @ResponseBody Object deleteDocumet(@PathVariable("index") String index,@PathVariable("id") String id) {
		Object obj = elasticSearchDao.deleteDocument(index,id);
		return obj;
	}

}