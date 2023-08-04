package hiforce.render.protocol.model;

import hiforce.render.protocol.model.config.StructureConfig;

import java.util.List;

/**
 * @author Rocky Yu
 * @since 2020/8/15
 */
public interface IPageRenderResult {

    /**
     * 本次页面渲染的页面结构配置
     *
     * @return StructureConfig
     */
    StructureConfig getStructureConfig();

    /**
     * @return 本次会话的自定义组件
     */
    List<? extends BaseCO> getComponents();

    /**
     * @return 获取本次会话的查询参数
     */
    IRenderParam getRenderParam();

    /**
     * 构建本次会话涉及到的提交参数
     *
     * @return ISubmitParam
     */
    ISubmitParam createSubmitParam();

    /**
     * @return 重新全量加载页面
     */
    default boolean isReloadPage() {
        return true;
    }
}
