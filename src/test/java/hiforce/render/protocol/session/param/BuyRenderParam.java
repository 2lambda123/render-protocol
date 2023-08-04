package hiforce.render.protocol.session.param;

import hiforce.render.protocol.model.IRenderParam;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class BuyRenderParam implements Serializable, IRenderParam {

    @Getter
    @Setter
    private String seller;

    @Getter
    @Setter
    private String itemId;

    @Getter
    @Setter
    private int buyQuantity;
}
