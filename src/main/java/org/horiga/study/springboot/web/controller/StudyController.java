package org.horiga.study.springboot.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.horiga.study.springboot.web.service.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/study")
public class StudyController {
	
	private static Logger _log = LoggerFactory.getLogger(StudyController.class);
	
	@Autowired
	private  StudyService studyService;
	
	@RequestMapping("/async/{jobs}")
	@ResponseBody
	String async(@PathVariable("jobs") int jobs) throws Exception {
		
		_log.info(">>> [start] handleRequest !!");
		final long stat = System.currentTimeMillis();
		
		if (jobs <= 0)
			return "done.";
		
		final CountDownLatch latch = new CountDownLatch(jobs);
		List<Future<String>> results = new ArrayList<Future<String>>(jobs);
		for (int i=0; i<jobs; i++) {
			Future<String> res = studyService.processWithAsync(latch, i * 100L);
			results.add(res);
		}
		
		latch.await(1000, TimeUnit.MILLISECONDS);
		
		_log.info(">>> [end] Complete jobs !!: elapsed: {}ms", System.currentTimeMillis() - stat);
		
		StringBuilder sb = new StringBuilder();
		for (Future<String> f : results) {
			sb.append("[");
			if (f.isDone()) {
				sb.append(f.get());
			}
			else {
				sb.append("timeout...");
			}
			sb.append("],");
		}
		
		return sb.toString();
	}
	
	@ResponseStatus(value=HttpStatus.REQUEST_TIMEOUT)
	@ExceptionHandler(value = TimeoutException.class)
	@ResponseBody String handleTimeout() {
		return "timeout";
	}
	
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value=Exception.class)
	@ResponseBody String handleException(HttpServletRequest req, Exception exception) {
		return "internal error";
	}
	
}
