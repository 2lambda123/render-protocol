package hiforce.render.protocol.engine.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 异步请求渲染页面请求
 *
 * @author Rocky Yu
 * @since 2020/8/15
 */
@ToString
public class RenderPageReqDTO implements Serializable {

    private static final long serialVersionUID = -1839587928821021258L;

    /**
     * 当前异步渲染操作的触发者（比如某个触发异步的某个组件uniqueId）
     */
    @Getter
    @Setter
    private String operator;

    /**
     * 异步提交的组件数据
     */
    @Getter
    @Setter
    private String data;

    /**
     * 页面的Linkage信息
     */
    @Getter
    @Setter
    private String linkage;

    /**
     * 页面的hierarchy信息，异步请求，一般只需要里面的结构信息
     */
    @Getter
    @Setter
    private String hierarchy;
}
