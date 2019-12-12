package com.lkh.tool.kafka;

import java.util.Properties;

//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaDemo {
//	public static void main(String[] args) {
//		 Properties props = new Properties();
//		 props.put("bootstrap.servers", "192.168.10.69:9092,192.168.10.70:9092");
//		 props.put("acks", "all");
//		 props.put("retries", 0);
//		 props.put("batch.size", 16384);
//		 props.put("linger.ms", 1);
//		 props.put("buffer.memory", 33554432);
//		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//		 Producer<String, String> producer = new KafkaProducer<>(props);
////		 Demo d = new Demo("Tim",26,true);
//		 for(int i = 100; i < 120; i++)
//		     producer.send(new ProducerRecord<String, String>("tim","{\"Name\":"+Integer.toString(i)+"}"));
//		 producer.close();
//		 System.err.println("ok");
//	}
//	static class Demo {
//		private String name;
//		private int age;
//		private boolean sex;
//
//		public Demo(String name, int age, boolean sex) {
//			super();
//			this.name = name;
//			this.age = age;
//			this.sex = sex;
//		}
//		public String getName() {
//			return name;
//		}
//		public void setName(String name) {
//			this.name = name;
//		}
//		public int getAge() {
//			return age;
//		}
//		public void setAge(int age) {
//			this.age = age;
//		}
//		public boolean isSex() {
//			return sex;
//		}
//		public void setSex(boolean sex) {
//			this.sex = sex;
//		}
//
//	}
}
