package com.log4j2.plugin;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SL on 2017/2/3.
 * log4j2 阿里云日志服务 appender
 */
@Plugin(name = "LogHub", category = "Core", elementType = "appender", printObject = true)
public class LogHubAppender extends AbstractAppender {

    private String topic;
    private DateFormat dateFormat;
    private String source;
    private String endpoint = "<log_service_endpoint>"; // 选择与上面步骤创建Project所属区域匹配的
     // Endpoint
    private String accessKeyId = "<your_access_key_id>"; // 使用你的阿里云访问密钥AccessKeyId
    private String accessKeySecret = "<your_access_key_secret>"; // 使用你的阿里云访问密钥AccessKeySecret
    private String project = "<project_name>"; // 上面步骤创建的项目名称
    private String logstore = "<logstore_name>"; // 上面步骤创建的日志库名称

    private Client client;
    private LogHubAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions,
                           String source, String topic, String project, String logstore, DateFormat dateFormat, String endpoint, String accessKeyId, String accessKeySecret) {
        super(name, filter, layout, ignoreExceptions);
        this.topic = topic == null ? "" : topic;
        this.project=project;
        this.endpoint=endpoint;
        this.accessKeyId=accessKeyId;
        this.accessKeySecret=accessKeySecret;
        this.logstore = logstore;
        this.dateFormat = dateFormat;
        this.source=source;
    }

    @Override
    public void append(LogEvent event) {

        List<LogItem> logItems = new ArrayList<>();

        LogItem item = new LogItem();
        logItems.add(item);

        item.SetTime((int) (event.getTimeMillis() / 1000L));
        item.PushBack("time", dateFormat.format(new Date(event.getTimeMillis())));
        item.PushBack("level", event.getLevel().toString());
        item.PushBack("thread", event.getThreadName());
        item.PushBack("logger", event.getLoggerName());
        item.PushBack("message", event.getMessage().getFormattedMessage());

        Map<String, String> contextMap = event.getContextMap();
        if(contextMap.containsKey("requestUrl")){
            item.PushBack("requestUrl",contextMap.get("requestUrl"));
        }
        if(contextMap.containsKey("requestData")){
            item.PushBack("requestData",contextMap.get("requestData"));
        }

        if (event.getThrown() != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            event.getThrown().printStackTrace(pw);
            item.PushBack("thrown", sw.getBuffer().toString());
        }

        Map<String, String> properties = event.getContextMap();
        if (properties.size() > 0) {
            Object[] keys = properties.keySet().toArray();
            java.util.Arrays.sort(keys);
            for (Object key : keys) {
                item.PushBack(key.toString(), properties.get(key));
            }
        }

        try {
            client.PutLogs(project,logstore,topic,logItems,"");
        } catch (LogException e) {
            LOGGER.error(e.GetErrorMessage());
        }

    }

    @Override
    public void start() {
        client=new Client(endpoint,accessKeyId,accessKeySecret);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @PluginFactory
    public static LogHubAppender createAppender(@PluginAttribute("name") final String name,
                                                @PluginAttribute("accessKey") final String accessKey,
                                                @PluginAttribute("accessKeyId") final String accessKeyId,
                                                @PluginAttribute("project") final String project,
                                                @PluginAttribute(value = "source") final String source,
                                                @PluginAttribute("logstore") final String logstore,
                                                @PluginAttribute("endpoint") final String endpoint,
                                                @PluginAttribute(value = "timeFormat", defaultString = "yyyy-MM-dd'T'HH:mmZ") final String timeFormat,
                                                @PluginAttribute(value = "timeZone", defaultString = "UTC") final String timeZone,
                                                @PluginAttribute("topic") final String topic,
                                                @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                @PluginElement("Filter") Filter filter,
                                                @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) final boolean ignoreExceptions) {

        if (name == null) {
            LOGGER.error("No name provided for LoghubAppender");
            return null;
        }

        if (project == null) {
            LOGGER.error("No projectName provided for LoghubAppender");
            return null;
        }

        if (logstore == null) {
            LOGGER.error("No logstore provided for LoghubAppender");
            return null;
        }

        if (endpoint == null) {
            LOGGER.error("No endpoint provided for LoghubAppender");
            return null;
        }

        if (accessKeyId == null) {
            LOGGER.error("No accessKeyId provided for LoghubAppender");
            return null;
        }

        if (accessKey == null) {
            LOGGER.error("No accessKey provided for LoghubAppender");
            return null;
        }


        DateFormat dateFormat = null;

        if (timeFormat != null) {
            dateFormat = new SimpleDateFormat(timeFormat);
            if (timeZone != null) {
                dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            }
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new LogHubAppender(name, filter, layout, ignoreExceptions,source, topic, project,  logstore,  dateFormat, endpoint, accessKeyId, accessKey);
    }
}
