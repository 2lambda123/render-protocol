package hiforce.render.protocol.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
public class Endpoint implements Serializable {

    private static final long serialVersionUID = -1972991257734298414L;

    @Getter
    @Setter
    private EndpointType type = EndpointType.PC;
}
