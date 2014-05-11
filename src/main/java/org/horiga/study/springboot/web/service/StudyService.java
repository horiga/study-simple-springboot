package org.horiga.study.springboot.web.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class StudyService {
	
	private static Logger _log = LoggerFactory.getLogger(StudyService.class);
	
	@Async
	public Future<String> processWithAsync(final CountDownLatch latch, final Long waitMillis) {
		
		try {
			_log.info("[start] processWithAsync(wait={}ms)!!", waitMillis);
			try {
				Thread.sleep(waitMillis);
			} catch (InterruptedException e) {}
			_log.info("[ end ] processWithAsync(wait={}ms)!!", waitMillis);
			return new AsyncResult<String>(
					new StringBuilder("ID-").append(Thread.currentThread().getName())
					.append(Thread.currentThread().getId()).toString());
		} finally {
			latch.countDown();
		}
	}
	
}
