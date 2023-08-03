package hiforce.render.protocol.engine.response;

import hiforce.render.protocol.model.RenderData;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/15
 */
public class RenderPageRespDTO implements Serializable {

    private static final long serialVersionUID = 1188856665708037300L;

    @Getter
    @Setter
    private boolean success;

    @Getter
    @Setter
    private String errCode;

    @Getter
    @Setter
    private String errText;

    @Getter
    @Setter
    private RenderData renderData;

    public static RenderPageRespDTO of(boolean success, String errCode, String errText, RenderData data) {
        RenderPageRespDTO result = new RenderPageRespDTO();
        result.success = success;
        result.errCode = errCode;
        result.errText = errText;
        result.renderData = data;
        return result;
    }

    public static RenderPageRespDTO success(RenderData data) {
        return of(true, null, null, data);
    }

    public static RenderPageRespDTO failed(String errCode, String errText, RenderData data) {
        return of(false, null, null, data);
    }

    public static RenderPageRespDTO failed(String errCode, String errText) {
        return of(false, null, null, null);
    }
}
