package hiforce.render.protocol.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class Linkage implements Serializable {

    private static final long serialVersionUID = -7753840657312151198L;

    @Getter
    @Setter
    private boolean compress;

    /**
     * 当前页面的查询参数
     */
    @Getter
    @Setter
    private String queryParams;

    /**
     * 当前页面的提交参数
     */
    @Getter
    @Setter
    private String submitParams;

    /**
     * 当前页面的验证参数（不同的业务有自己不同的验证参数）
     */
    @Getter
    @Setter
    private String validateParams;

    /**
     * 页面上是用户可输入的组件ID，前端自己接管变化
     */
    @Getter
    @Setter
    private List<String> inputs = Lists.newArrayList();

    /**
     * 页面上有变动时需要请求的组件ID，后端服务接管变化
     */
    @Getter
    @Setter
    private List<String> requests = Lists.newArrayList();

    /**
     * 异步请求url
     */
    @Getter
    @Setter
    private String url;
}
