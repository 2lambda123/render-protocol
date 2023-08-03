package hiforce.render.protocol.model.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class StructureConfig implements Serializable {

    private static final long serialVersionUID = 643728948980639970L;

    @Getter
    @Setter
    private long tenantId;

    /**
     * 页面结构的平台类型
     */
    @Getter
    @Setter
    private String platform = "default";

    @Getter
    @Setter
    private StructureItem root;
}
