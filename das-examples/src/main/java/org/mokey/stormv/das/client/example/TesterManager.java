package org.mokey.stormv.das.client.example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TesterManager extends Thread{
	private List<Tester> testers = new ArrayList<Tester>();
	private int clientCount;
	private int duration;
	private String host;
	private String logicDb;
	private int port;
	private String sql;
	
	private String configPath = "";
	
	public TesterManager(String configPath) {
		this.configPath = configPath;
	}
	
	public long getDuration() {
		return duration;
	}
	
	private void test() {
		readDbConfig();
		readTesterConfig();
		startTesters();
		waitForDuration();
		stopTesters();
		report();
	}
	
	private void readDbConfig() {
		try {
			System.out.println(this.configPath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.configPath)));
			host = reader.readLine();
			logicDb = reader.readLine();
			sql = reader.readLine();
			
			String portStr = reader.readLine();
			port = Integer.parseInt(portStr);
			
			System.out.println("Host: " + host);
			System.out.println("Port: " + port);
			System.out.println("Logic DB: " + logicDb);
			System.out.println("Test SQL: " + sql);

			System.out.println();
			duration = Math.abs(Integer.parseInt(reader.readLine()));
			System.out.println("Test duration(in second): " + duration);
			duration *= 1000;

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readTesterConfig() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Dal client count: ");
			clientCount = Math.abs(Integer.parseInt(reader.readLine()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void startTesters() {
		System.out.println("Create testers...");
		for(int i = 0; i < clientCount; i++){
			Tester tester = new Tester(host, logicDb, port, sql);
			testers.add(tester); 
			tester.start();
		}
		System.out.println(String.format("All %d testers started", testers.size()));
	}
	
	private void waitForDuration() {
		synchronized (this){
			try {
				this.wait(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void stopTesters() {
		for(Tester tester: testers)
			tester.stopTest();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void report() {
		int i = 0;
		long totalReq = 0;
		long totalDuration = 0;
		System.out.println("Test finished.");
		System.out.println("ID\tReq\tDura\tAvr\tTps");
		double totalTps = 0.0d;
		for(Tester tester: testers) {
			long count = tester.getCount();
			long duration = tester.getDuration();
			double tps = count/(duration/1000d);
			System.out.println(String.format("%d\t%d\t%d\t%f\t%f", i++, count, duration, (double)duration/(double)count, tps));
			totalReq += tester.getCount();
			totalDuration += tester.getDuration();
			totalTps += tps;
		}
		
		System.out.println("Total");
		System.out.println("Req\tDura\tAvr\tTps");
		System.out.println(String.format("%d\t%d\t%f\t%f", totalReq, totalDuration, (double)totalDuration/(double)totalReq, totalTps));
	}
	
	public static void main(String[] args) {
		String filePath = "";
		if(args.length <= 0){
			filePath = ClassLoader.getSystemClassLoader().getResource("stress.conf").getFile();
		}else{
			filePath = args[0];
		}
		TesterManager testManager = new TesterManager(filePath);
		testManager.test();
	}
}
