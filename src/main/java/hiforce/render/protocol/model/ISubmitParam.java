package hiforce.render.protocol.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * @author Rocky Yu
 * @since 2020/8/12
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "$class")
public interface ISubmitParam extends Serializable {
}
