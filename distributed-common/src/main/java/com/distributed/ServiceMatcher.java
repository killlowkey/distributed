package com.distributed;

import com.distributed.entity.ServiceData;

import java.util.List;
import java.util.Map;

/**
 * @author Ray
 * @date created in 2021/6/20 9:31
 */
public interface ServiceMatcher {

    ServiceData match(String serviceName);

    void setServices(Map<String, List<ServiceData>> services);

}
