package hiforce.render.protocol.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 页面结构等信息
 *
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class Hierarchy implements Serializable {

    private static final long serialVersionUID = 6227631578375963498L;

    /**
     * 根节点组件TAG+ID
     */
    @Getter
    @Setter
    private String root;

    /**
     * 当前页面上的所有组件TAG列表
     */
    @Getter
    private final List<String> components = Lists.newArrayList();

    /**
     * 页面结构信息
     */
    @Getter
    @Setter
    private Map<String, List<String>> structure = Maps.newHashMap();

    /**
     * 组件UniqueId和签名Map，用于异步请求时做数据内容是否变化的检查用
     */
    @Getter
    private final Map<String, String> signatureMap = Maps.newHashMap();
}
