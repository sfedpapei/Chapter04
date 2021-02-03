package com.example.Chapter04.batch;

import java.util.Random;

import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;

public class RandomChuckSizePolicy implements CompletionPolicy {
	
	private int chunksize;
	private int totalProcessed;
	private Random random = new Random();

	@Override
	public boolean isComplete(RepeatContext context, RepeatStatus result) {
		if (result == RepeatStatus.FINISHED) {
			return true;
		} else {
			return isComplete(context);
		}
	}

	@Override
	public boolean isComplete(RepeatContext context) {
		return this.totalProcessed>= chunksize;
	}

	@Override
	public RepeatContext start(RepeatContext parent) {
			this.chunksize = random.nextInt(20);
			this.totalProcessed = 0;
			
			System.out.println("The chunk size has been set to " + this.chunksize);
			return parent;
	}

	@Override
	public void update(RepeatContext context) {
		this.totalProcessed++;

	}

}
