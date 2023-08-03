package hiforce.render.protocol.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 页面渲染数据
 *
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class RenderData implements Serializable {

    private static final long serialVersionUID = 5767601462674625126L;

    /**
     * 页面Linkage信息，包含当前页面的查询参数、提交参数、验证参数
     */
    @Getter
    @Setter
    private Linkage linkage;

    @Getter
    @Setter
    private Hierarchy hierarchy;

    /**
     * 终端信息
     */
    @Getter
    @Setter
    private Endpoint endpoint;

    /**
     * 当前页面的组件数据
     */
    @Getter
    private final Data data = new Data();

    @Getter
    @Setter
    private boolean reload = true;
}
