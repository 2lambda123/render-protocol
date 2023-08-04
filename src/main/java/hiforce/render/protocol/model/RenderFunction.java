package hiforce.render.protocol.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/16
 */
public class RenderFunction implements Serializable {

    private static final long serialVersionUID = -6818588789004211259L;

    @Getter
    @Setter
    private IRenderParam renderParam;

    @Getter
    @Setter
    private ISubmitParam submitParam;

    @Getter
    @Setter
    private Hierarchy hierarchy;

    @Getter
    @Setter
    private Linkage linkage;

    @Getter
    @Setter
    private Data data;
}
