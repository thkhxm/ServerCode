package com.lkh.tool.log;
//package com.ftkj.tool.log;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.TimeZone;
//
//import org.apache.log4j.AppenderSkeleton;
//import org.apache.log4j.spi.LoggingEvent;
//import org.apache.log4j.spi.ThrowableInformation;
//
//import com.aliyun.openservices.log.common.LogItem;
//import com.aliyun.openservices.log.producer.LogProducer;
//import com.aliyun.openservices.log.producer.ProducerConfig;
//import com.aliyun.openservices.log.producer.ProjectConfig;
//
//public class LoghubAppender extends AppenderSkeleton {
//	private ProducerConfig config = new ProducerConfig();
//	private LogProducer producer;
//	private ProjectConfig projectConfig = new ProjectConfig();
//	private String logstore;
//	private String topic = "";
//	private String timeZone = "UTC";
//	private String timeFormat = "yyyy-MM-dd'T'HH:mmZ";
//	private SimpleDateFormat formatter;
//
//	@Override
//	public void activateOptions() {
//		super.activateOptions();
//		formatter = new SimpleDateFormat(timeFormat);
//		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
//		producer = new LogProducer(config);
//		producer.setProjectConfig(projectConfig);
//	}
//
//	public String getTimeFormat() {
//		return timeFormat;
//	}
//
//	public void setTimeFormat(String timeFormat) {
//		this.timeFormat = timeFormat;
//		formatter = new SimpleDateFormat(timeFormat);
//		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
//	}
//
//	public void close() {
//		producer.flush();
//		producer.close();
//	}
//
//	public boolean requiresLayout() {
//		return false;
//	}
//
//	public String getLogstore() {
//		return logstore;
//	}
//
//	public void setLogstore(String logstore) {
//		this.logstore = logstore;
//	}
//
//	public String getTopic() {
//		return topic;
//	}
//
//	public void setTopic(String topic) {
//		this.topic = topic;
//	}
//
//	public String getTimeZone() {
//		return timeZone;
//	}
//
//	public void setTimeZone(String timeZone) {
//		this.timeZone = timeZone;
//		formatter = new SimpleDateFormat(timeFormat);
//		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
//	}
//
//	@Override
//	protected void append(LoggingEvent event) {
//		List<LogItem> logItems = new ArrayList<LogItem>();
//		LogItem item = new LogItem();
//		logItems.add(item);
//		item.SetTime((int) (event.getTimeStamp() / 1000));
//		item.PushBack("time", formatter.format(new Date(event.getTimeStamp())));
//		item.PushBack("level", event.getLevel().toString());
//		item.PushBack("thread", event.getThreadName());
//		item.PushBack("location",
//				event.getLocationInformation().fullInfo.toString());
//		String message = event.getMessage().toString();
//		ThrowableInformation throwable = event.getThrowableInformation();
//		if(throwable != null){
//			for(String s: throwable.getThrowableStrRep()){
//				message += System.lineSeparator() + s;
//			}
//		}
//		item.PushBack("message", message);
//		Map properties = event.getProperties();
//		if (properties.size() > 0) {
//			Object[] keys = properties.keySet().toArray();
//			Arrays.sort(keys);
//			for (int i = 0; i < keys.length; i++) {
//				item.PushBack(keys[i].toString(), properties.get(keys[i])
//						.toString());
//			}
//		}
//		producer.send(projectConfig.projectName, logstore, topic, null,
//				logItems);
//	}
//
//	public String getProjectName() {
//		return projectConfig.projectName;
//	}
//
//	public void setProjectName(String projectName) {
//		projectConfig.projectName = projectName;
//	}
//
//	public String getEndpoint() {
//		return projectConfig.endpoint;
//	}
//
//	public void setEndpoint(String endpoint) {
//		projectConfig.endpoint = endpoint;
//	}
//
//	public String getAccessKeyId() {
//		return projectConfig.accessKeyId;
//	}
//
//	public void setAccessKeyId(String accessKeyId) {
//		projectConfig.accessKeyId = accessKeyId;
//	}
//
//	public String getAccessKey() {
//		return projectConfig.accessKey;
//	}
//
//	public void setAccessKey(String accessKey) {
//		projectConfig.accessKey = accessKey;
//	}
//
//	public String getStsToken() {
//		return projectConfig.stsToken;
//	}
//
//	public void setStsToken(String stsToken) {
//		projectConfig.stsToken = stsToken;
//	}
//
//	public int getPackageTimeoutInMS() {
//		return config.packageTimeoutInMS;
//	}
//
//	public void setPackageTimeoutInMS(int packageTimeoutInMS) {
//		config.packageTimeoutInMS = packageTimeoutInMS;
//	}
//
//	public int getLogsCountPerPackage() {
//		return config.logsCountPerPackage;
//	}
//
//	public void setLogsCountPerPackage(int logsCountPerPackage) {
//		config.logsCountPerPackage = logsCountPerPackage;
//	}
//
//	public int getLogsBytesPerPackage() {
//		return config.logsBytesPerPackage;
//	}
//
//	public void setLogsBytesPerPackage(int logsBytesPerPackage) {
//		config.logsBytesPerPackage = logsBytesPerPackage;
//	}
//
//	public int getMemPoolSizeInByte() {
//		return config.memPoolSizeInByte;
//	}
//
//	public void setMemPoolSizeInByte(int memPoolSizeInByte) {
//		config.memPoolSizeInByte = memPoolSizeInByte;
//	}
//
//	public int getIoThreadsCount() {
//		return config.maxIOThreadSizeInPool;
//	}
//
//	public void setIoThreadsCount(int ioThreadsCount) {
//		config.maxIOThreadSizeInPool = ioThreadsCount;
//	}
//
//	public int getShardHashUpdateIntervalInMS() {
//		return config.shardHashUpdateIntervalInMS;
//	}
//
//	public void setShardHashUpdateIntervalInMS(int shardHashUpdateIntervalInMS) {
//		config.shardHashUpdateIntervalInMS = shardHashUpdateIntervalInMS;
//	}
//
//	public int getRetryTimes() {
//		return config.retryTimes;
//	}
//
//	public void setRetryTimes(int retryTimes) {
//		config.retryTimes = retryTimes;
//	}
//}