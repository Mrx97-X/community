package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl) {
        // 生成文件名
        String fileName = CommunityUtil.generateUUID();

        // 异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        // 返回访问路径
        Map<String, Object> map = new HashMap<>();
//        map.put("shareUrl", domain + contextPath + "/share/image" + fileName);
        map.put("shareUrl", shareBucketUrl + "/" + fileName);
        return CommunityUtil.getJSONString(0, null, map);

    }

}
