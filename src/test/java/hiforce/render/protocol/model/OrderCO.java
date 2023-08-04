package hiforce.render.protocol.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Rocky Yu
 * @since 2021/7/31
 */
public class OrderCO extends BaseCO {

    public OrderCO() {
        this.setTag("order");
    }

    @Getter
    @Setter
    private String seller;
}
