package hiforce.render.protocol.model;

import hiforce.render.protocol.session.param.BuyRenderParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class ItemCO extends BaseCO {

    public ItemCO() {
        this.setTag("item");
        this.setParentTag("order");
    }

    @Getter
    @Setter
    private String itemId;

    @Getter
    @Setter
    private long unitPrice;

    @Getter
    @Setter
    private int buyQuantity;

    @Override
    public void attachRenderParam(IRenderParam renderParam) {
        BuyRenderParam buyRenderParam = (BuyRenderParam) renderParam;
        buyRenderParam.setBuyQuantity(buyQuantity);
    }
}
